package com.jobaroo.pages

import io.circe.generic.auto.*
import io.circe.parser.*
import io.circe.syntax.*
import cats.effect.IO
import cats.syntax.semigroup.*
import tyrian.*
import tyrian.http.*
import tyrian.Html.*
import com.jobaroo.App
import com.jobaroo.common.*
import com.jobaroo.common.Endpoint
import com.jobaroo.common.constants
import com.jobaroo.components.AppLayout
import com.jobaroo.components.PreviewText
import com.jobaroo.core.Router
import com.jobaroo.core.Session
import com.jobaroo.domain.job.*
import com.jobaroo.pages.Page.urls
import com.jobaroo.tyrianui.core.UiAttrs
import com.jobaroo.tyrianui.daisy.Badge
import com.jobaroo.tyrianui.daisy.Button
import com.jobaroo.tyrianui.daisy.Card
import com.jobaroo.tyrianui.daisy.Feedback
import com.jobaroo.tyrianui.html.Tags.{Children, div, form, h1, h2, h3, h4, img, li, p, ul}
import com.jobaroo.tyrianui.icons.Icons
import com.jobaroo.ui.preset.Jobaroo
import org.scalajs.dom.CanvasRenderingContext2D
import org.scalajs.dom.File
import org.scalajs.dom.FileReader
import org.scalajs.dom.HTMLCanvasElement
import org.scalajs.dom.HTMLImageElement
import org.scalajs.dom.document
import scala.util.Try
import tyrian.cmds.Logger

final case class PostJobPage(
  company    : String = "",
  title      : String = "",
  description: String = "",
  externalUrl: String = "",
  location   : String = "",
  remote     : Boolean = false,
  salaryLow  : Option[Int] = None,
  salaryHigh : Option[Int] = None,
  currency   : Option[String] = None,
  country    : Option[String] = None,
  tags       : Option[String] = None,
  image      : Option[String] = None,
  seniority  : Option[String] = None,
  other      : Option[String] = None,
  status     : Option[Page.Status] = None
) extends FormPage("Post Job", status):

  import PostJobPage.*

  override def view: Html[App.Msg] =
    AppLayout.pageContainer(
      renderHero,
      if !Session.isActive then
        div(UiAttrs.classes(Jobaroo.state.centeredShort))(
          Feedback.alert("You need to be logged in to post a job.", Feedback.Tone.Warning)
        )
      else
        div(UiAttrs.classes(Jobaroo.shell.splitWide))(
          renderComposer,
          renderPreviewRail
        )
    )

  override protected def renderFormContent(): List[Html[App.Msg]] = Nil

  private def renderHero: Html[App.Msg] =
    div(UiAttrs.classes(Jobaroo.post.heroSection))(
      div(UiAttrs.classes(Jobaroo.post.heroWrap))(
        div(UiAttrs.classes(Jobaroo.post.heroBackRow))(
          AppLayout.backLink(fallback = urls.jobs, classes = Jobaroo.section.backLinkInverse)
        ),
        p(UiAttrs.classes(Jobaroo.section.eyebrow))(text("Recruiter Console")),
        h1(UiAttrs.classes(Jobaroo.post.heroTitle))(text("Hire your next star.")),
        p(UiAttrs.classes(Jobaroo.post.heroText))(
          text("Shape the role exactly as candidates will see it, then publish from one focused posting workflow.")
        ),
        div(UiAttrs.classes(Jobaroo.post.heroStats))(
          heroStat("12,000+", "Java/Scala applicants"),
          heroStat("500+", "Companies hiring"),
          heroStat("48 hrs", "Average time to first response")
        )
      )
    )

  private def renderComposer: Html[App.Msg] =
    Card.surface(UiAttrs.classes(Jobaroo.form.formCard))(
      Card.body(UiAttrs.classes(Jobaroo.surface.bodySpacious))(
        form(
          UiAttrs(name := "job-post", id := "form") |+|
            UiAttrs.classes(Jobaroo.post.editorForm) |+|
            UiAttrs(
              onEvent(
                "submit",
                e =>
                  e.preventDefault()
                  App.NoOp
              )
            )
        )(
          editorSection(
            icon = Icons.building(Jobaroo.icon.regular),
            title = "Basic information",
            copy = "This is the first impression candidates get from the card grid and the job detail page."
          )(
            renderInput("Company", "company", "text", true, company, UpdateCompany(_)),
            renderInput("Job Title", "title", "text", true, title, UpdateTitle(_)),
            renderTextArea("Job Description", "description", true, description, UpdateDescription(_)),
            renderInput("Application URL", "externalUrl", "url", true, externalUrl, UpdateExternalUrl(_))
          ),
          divider,
          editorSection(
            icon = Icons.mapPin(Jobaroo.icon.regular),
            title = "Location",
            copy = "Clarify whether the role is remote and where the team is anchored."
          )(
            renderToggle(
              name = "Fully Remote Position",
              uid = "remote",
              isRequired = true,
              checkedValue = remote,
              onChange = _ => ToggleRemote,
              hint = Some("Toggle on when candidates can apply from anywhere.")
            ),
            div(UiAttrs.classes(Jobaroo.post.editorColumns2))(
              renderInput("City / Region", "location", "text", true, location, UpdateLocation(_)),
              renderInput("Country", "country", "text", false, country.getOrElse(""), UpdateCountry(_))
            )
          ),
          divider,
          editorSection(
            icon = Icons.banknotes(Jobaroo.icon.regular),
            title = "Compensation",
            copy = "Strong salary signals improve conversion and reduce low-intent applications."
          )(
            div(UiAttrs.classes(Jobaroo.post.editorColumns3))(
              renderInput(
                "Minimum Salary",
                "salaryLow",
                "number",
                false,
                salaryLow.fold("")(_.toString),
                amount => UpdateSalaryLow(Try(amount.toInt).getOrElse(0))
              ),
              renderInput(
                "Maximum Salary",
                "salaryHigh",
                "number",
                false,
                salaryHigh.fold("")(_.toString),
                amount => UpdateSalaryHigh(Try(amount.toInt).getOrElse(0))
              ),
              renderInput("Currency", "currency", "text", false, currency.getOrElse(""), UpdateCurrency(_))
            )
          ),
          divider,
          editorSection(
            icon = Icons.tag(Jobaroo.icon.regular),
            title = "Additional details",
            copy = "Use short labels for skill tags and seniority, then add any final context recruiters care about."
          )(
            div(UiAttrs.classes(Jobaroo.post.editorColumns2))(
              renderInput("Tags", "tags", "text", false, tags.getOrElse(""), UpdateTags(_)),
              renderInput("Seniority", "seniority", "text", false, seniority.getOrElse(""), UpdateSeniority(_))
            ),
            renderTextArea("Additional Information", "other", false, other.getOrElse(""), UpdateOther(_))
          ),
          divider,
          editorSection(
            icon = Icons.photo(Jobaroo.icon.regular),
            title = "Company logo",
            copy = "Add a recognizable mark so the card and preview feel credible."
          )(
            renderImageUploadInput("Logo", "logo", image, UpdateImageFile(_))
          ),
          divider,
          div(UiAttrs.classes(Jobaroo.form.submitPanel))(
            div(UiAttrs.classes(Jobaroo.form.submitRow))(
              p(UiAttrs.classes(Jobaroo.form.fileTitle))(text("Job posting")),
              p(UiAttrs.classes(Jobaroo.form.submitPrice))(text(s"$$${constants.jobAdvertPriceUSD}"))
            ),
            ul(UiAttrs.classes(Jobaroo.form.submitList))(
              checklistLine("Featured placement for the default 7-day run."),
              checklistLine("Posted to the live job board once payment completes."),
              checklistLine("Editing stays within the existing application flow.")
            ),
            renderStatusAlert,
            renderPrimaryAction(s"Post Job - $$${constants.jobAdvertPriceUSD}", PostJob)
          )
        )
      )
    )

  private def renderPreviewRail: Html[App.Msg] =
    div(UiAttrs.classes(Jobaroo.post.rail))(
      Card.surface(UiAttrs.classes(Jobaroo.surface.card |+| Jobaroo.surface.stickyRail))(
        Card.body(UiAttrs.classes(Jobaroo.surface.bodyCompact))(
          div(UiAttrs.classes(Jobaroo.post.previewHeader))(
            p(UiAttrs.classes(Jobaroo.post.previewEyebrow))(text("Candidate preview")),
            h3(UiAttrs.classes(Jobaroo.post.previewTitle))(text("How the listing reads"))
          ),
          renderPreviewCard
        )
      ),
      Card.surface(UiAttrs.classes(Jobaroo.post.signalsCard))(
        Card.body(UiAttrs.classes(Jobaroo.surface.bodyCompact))(
          p(UiAttrs.classes(Jobaroo.section.eyebrow))(text("Listing signals")),
          h3(UiAttrs.classes(Jobaroo.post.previewTitle))(text("What candidates will notice first")),
          ul(UiAttrs.classes(Jobaroo.post.signalsList))(
            signalLine(Icons.mapPin(Jobaroo.icon.small), locationPreview),
            signalLine(Icons.banknotes(Jobaroo.icon.small), salaryPreview),
            signalLine(Icons.briefcase(Jobaroo.icon.small), fallback(seniority.getOrElse(""), "Open seniority")),
            signalLine(if remote then Icons.globe(Jobaroo.icon.small) else Icons.building(Jobaroo.icon.small), if remote then "Remote friendly" else "On-site")
          )
        )
      ),
      Card.surface(UiAttrs.classes(Jobaroo.surface.card))(
        Card.body(UiAttrs.classes(Jobaroo.surface.bodyCompact))(
          renderChecklist
        )
      ),
      Card.surface(UiAttrs.classes(Jobaroo.post.tipsCard))(
        Card.body(UiAttrs.classes(Jobaroo.surface.bodyCompact))(
          p(UiAttrs.classes(Jobaroo.section.eyebrow))(text("Tips for a great listing")),
          h3(UiAttrs.classes(Jobaroo.post.previewTitle))(text("Increase response quality")),
          ul(UiAttrs.classes(Jobaroo.post.tipsList))(
            tipLine("Use an outcome-driven title instead of an internal level code."),
            tipLine("Mention salary ranges when you can to improve candidate trust."),
            tipLine("Highlight the team scope and impact in the first two description paragraphs.")
          )
        )
      )
    )

  private def renderStatusAlert: Html[App.Msg] =
    status.fold(div()) { currentStatus =>
      val tone = currentStatus.kind match
        case Page.Kind.SUCCESS => Feedback.Tone.Success
        case Page.Kind.ERROR   => Feedback.Tone.Error
        case Page.Kind.LOADING => Feedback.Tone.Info

      Feedback.alert(currentStatus.message, tone)
    }

  private def renderPreviewCard: Html[App.Msg] =
    div(UiAttrs.classes(Jobaroo.post.previewCard))(
      div(UiAttrs.classes(Jobaroo.post.previewTop))(
        div(UiAttrs.classes(Jobaroo.jobs.avatarWrap))(
          div(UiAttrs.classes(Jobaroo.jobs.avatarFrame))(
            img(UiAttrs(src := image.getOrElse(constants.fallbackImage), alt := titleOrPlaceholder) |+| UiAttrs.classes(
              Jobaroo.jobs.avatarImage
            ))
          )
        ),
        div(UiAttrs.classes(Jobaroo.post.previewCopy))(
          p(UiAttrs.classes(Jobaroo.jobs.company))(text(companyOrPlaceholder)),
          h4(UiAttrs.classes(Jobaroo.jobs.title))(text(titleOrPlaceholder)),
          div(UiAttrs.classes(Jobaroo.jobs.metaRow))(
            Badge.render(locationPreview, Badge.Tone.Outline),
            Badge.render(salaryPreview, Badge.Tone.Outline),
            Badge.render(if remote then "Remote" else "On-site", Badge.Tone.Primary)
          )
        )
      ),
      p(UiAttrs.classes(Jobaroo.post.previewDescription))(text(descriptionPreview)),
      div(UiAttrs.classes(Jobaroo.post.previewTags))(previewTags*),
      div(UiAttrs.classes(Jobaroo.post.applyBox))(
        p(UiAttrs.classes(Jobaroo.post.applyEyebrow))(text("Apply destination")),
        p(UiAttrs.classes(Jobaroo.post.applyValue))(text(fallback(externalUrl, "External URL not set"))),
        p(UiAttrs.classes(Jobaroo.jobs.footerLink))(text("View Job →"))
      )
    )

  private def renderChecklist: Html[App.Msg] =
    div(UiAttrs.classes(Jobaroo.post.checklist))(
      p(UiAttrs.classes(Jobaroo.section.eyebrow))(text("Checklist")),
      p(UiAttrs.classes(Jobaroo.post.checklistTitle))(text("Listing checklist")),
      ul(UiAttrs.classes(Jobaroo.post.checklistList))(
        checklistItem("Company name is set", company.nonEmpty),
        checklistItem("Job title is set", title.nonEmpty),
        checklistItem("Description is set", description.nonEmpty),
        checklistItem("External URL is valid", validExternalUrl)
      )
    )

  private def checklistItem(label: String, isReady: Boolean): Html[App.Msg] =
    li(UiAttrs.classes(Jobaroo.post.checklistItem))(
      span()(text(label)),
      Badge.render(if isReady then "Ready" else "Missing", if isReady then Badge.Tone.Primary else Badge.Tone.Outline)
    )

  private def signalLine(icon: Html[App.Msg], label: String): Html[App.Msg] =
    li(UiAttrs.classes(Jobaroo.post.signalsItem))(
      icon,
      p()(text(label))
    )

  private def editorSection(
    icon: Html[App.Msg],
    title: String,
    copy: String
  )(children: Html[App.Msg]*): Html[App.Msg] =
    val header =
      div(UiAttrs.classes(Jobaroo.post.editorHeader))(
        div(UiAttrs.classes(Jobaroo.post.editorTitleRow))(
          div(UiAttrs.classes(Jobaroo.post.editorIcon))(icon),
          h2(UiAttrs.classes(Jobaroo.post.editorTitle))(text(title))
        ),
        p(UiAttrs.classes(Jobaroo.post.editorCopy))(text(copy))
      )

    div(UiAttrs.classes(Jobaroo.post.editorSection))(
      Children.concat(Children.one(header), children)
    )

  private def heroStat(value: String, label: String): Html[App.Msg] =
    div(UiAttrs.classes(Jobaroo.post.heroStat))(
      p(UiAttrs.classes(Jobaroo.post.heroValue))(text(value)),
      p(UiAttrs.classes(Jobaroo.post.heroLabel))(text(label))
    )

  private def checklistLine(label: String): Html[App.Msg] =
    li(UiAttrs.classes(Jobaroo.post.tipsItem))(
      Icons.check(Jobaroo.icon.small),
      span()(text(label))
    )

  private def tipLine(label: String): Html[App.Msg] =
    li(UiAttrs.classes(Jobaroo.post.tipsItem))(
      Icons.info(Jobaroo.icon.small),
      span()(text(label))
    )

  private def divider: Html[App.Msg] =
    div(UiAttrs.classes(Jobaroo.form.sectionDivider))()

  private def companyOrPlaceholder: String = fallback(company, "Company")
  private def titleOrPlaceholder: String   = fallback(title, "Role title")

  private def descriptionPreview: String =
    fallback(
      PreviewText.fromMarkdown(PreviewText.withoutLeadingTitle(description, title)),
      "Add a concise, candidate-first description that explains the role, the impact, and why someone should care."
    )

  private def locationPreview: String =
    val place = fallback(location, "Location")
    country.fold(place)(selectedCountry => s"$place, $selectedCountry")

  private def salaryPreview: String =
    (salaryLow, salaryHigh, currency.map(_.trim).filter(_.nonEmpty)) match
      case (Some(low), Some(high), Some(curr)) => s"$curr $low - $curr $high"
      case (Some(low), Some(high), None)       => s"$low - $high"
      case (Some(low), None, Some(curr))       => s"$curr $low+"
      case (Some(low), None, None)             => s"$low+"
      case (None, Some(high), Some(curr))      => s"Up to $curr $high"
      case (None, Some(high), None)            => s"Up to $high"
      case _                                   => "Salary TBD"

  private def previewTags: List[Html[App.Msg]] =
    tags
      .toList
      .flatMap(_.split(",").map(_.trim).toList)
      .filter(_.nonEmpty)
      .take(5)
      .map(tag => Badge.render(tag, Badge.Tone.Outline))

  private def fallback(value: String, default: String): String =
    Option(value).map(_.trim).filter(_.nonEmpty).getOrElse(default)

  private def trimmed(value: String): String =
    Option(value).map(_.trim).getOrElse("")

  private def validExternalUrl: Boolean =
    trimmed(externalUrl).matches("""https?://.+""")

  private def validationError: Option[String] =
    if trimmed(company).isEmpty then Some("Company is required.")
    else if trimmed(title).isEmpty then Some("Job title is required.")
    else if trimmed(description).isEmpty then Some("Job description is required.")
    else if !validExternalUrl then Some("Application URL must start with http:// or https://.")
    else if trimmed(location).isEmpty then Some("Location is required.")
    else if salaryLow.exists(_ < 0) || salaryHigh.exists(_ < 0) then Some("Salary values must be zero or greater.")
    else if salaryLow.zip(salaryHigh).exists((low, high) => low > high) then
      Some("Maximum salary must be greater than or equal to minimum salary.")
    else None

  override def update(msg: App.Msg): (Page, Cmd[IO, App.Msg]) = msg match
    case UpdateCompany(company)         => (this.copy(company = company), Cmd.None)
    case UpdateTitle(title)             => (this.copy(title = title), Cmd.None)
    case UpdateDescription(description) => (this.copy(description = description), Cmd.None)
    case UpdateExternalUrl(externalUrl) => (this.copy(externalUrl = externalUrl), Cmd.None)
    case UpdateLocation(location)       => (this.copy(location = location), Cmd.None)
    case ToggleRemote                   => (this.copy(remote = !this.remote), Cmd.None)
    case UpdateSalaryLow(salaryLow)     => (this.copy(salaryLow = Some(salaryLow)), Cmd.None)
    case UpdateSalaryHigh(salaryHigh)   => (this.copy(salaryHigh = Some(salaryHigh)), Cmd.None)
    case UpdateCurrency(currency)       => (this.copy(currency = Some(currency)), Cmd.None)
    case UpdateCountry(country)         => (this.copy(country = Some(country)), Cmd.None)
    case UpdateTags(tags)               => (this.copy(tags = Some(tags)), Cmd.None)
    case UpdateImage(optImage)          => (this.copy(image = optImage), Cmd.None)
    case UpdateImageFile(optFile)       => (this, commands.loadFile(optFile))
    case UpdateSeniority(seniority)     => (this.copy(seniority = Some(seniority)), Cmd.None)
    case UpdateOther(other)             => (this.copy(other = Some(other)), Cmd.None)
    case PostJobFailure(error)          => (setErrorStatus(error), Cmd.None)
    case PostJobSuccess(jobId)          => (setSuccessStatus("Job created"), Logger.consoleLog[IO](s"jobId: $jobId"))
    case PostJob                        =>
      validationError.fold[(Page, Cmd[IO, App.Msg])](
        (
          this,
          commands.postJob(
            company = this.company,
            title = this.title,
            description = this.description,
            externalUrl = this.externalUrl,
            location = this.location,
            remote = this.remote,
            salaryLow = this.salaryLow,
            salaryHigh = this.salaryHigh,
            currency = this.currency,
            country = this.country,
            tags = this.tags,
            image = this.image,
            seniority = this.seniority,
            other = this.other
          )(promoted = true)
        )
      )(message => (setErrorStatus(message), Cmd.None))

  private def setErrorStatus(message: String): Page   = this.copy(status = Some(Page.Status(message, Page.Kind.ERROR)))
  private def setSuccessStatus(message: String): Page = this.copy(status = Some(Page.Status(message, Page.Kind.SUCCESS)))

object PostJobPage:

  trait Msg                                               extends App.Msg
  final case class UpdateCompany(company: String)         extends Msg
  final case class UpdateTitle(title: String)             extends Msg
  final case class UpdateDescription(description: String) extends Msg
  final case class UpdateExternalUrl(externalUrl: String) extends Msg
  final case class UpdateLocation(location: String)       extends Msg
  case object ToggleRemote                                extends Msg
  final case class UpdateSalaryLow(salaryLow: Int)        extends Msg
  final case class UpdateSalaryHigh(salaryHigh: Int)      extends Msg
  final case class UpdateCurrency(currency: String)       extends Msg
  final case class UpdateCountry(country: String)         extends Msg
  final case class UpdateTags(tags: String)               extends Msg
  final case class UpdateImageFile(optFile: Option[File]) extends Msg
  final case class UpdateImage(optImage: Option[String])  extends Msg
  final case class UpdateSeniority(seniority: String)     extends Msg
  final case class UpdateOther(other: String)             extends Msg
  case object PostJob                                     extends Msg
  final case class PostJobSuccess(jobId: String)          extends Msg
  final case class PostJobFailure(error: String)          extends Msg

  object endpoints:

    val createJob = new Endpoint[Msg](
      location = constants.endpoints.createJob,
      method = Method.Post,
      onError = e => PostJobFailure(e.toString),
      onResponse = Endpoint.onResponse(onError = PostJobFailure(_), onSuccess = PostJobSuccess(_))
    ) {}

    val createJobPromoted = new Endpoint[App.Msg](
      location = constants.endpoints.createJobPromoted,
      method = Method.Post,
      onError = e => PostJobFailure(e.toString),
      onResponse = Endpoint.onResponse(onError = PostJobFailure(_), onSuccess = Router.ExternalRedirect(_))
    ) {}

  object commands:

    import cats.syntax.traverse.*

    def loadFile(optFile: Option[File]) = Cmd.Run[IO, Option[String], Msg](
      optFile.traverse { file =>
        IO.async_ { cb =>
          val reader = new FileReader

          reader.onload = _ =>
            val img = document.createElement("img").asInstanceOf[HTMLImageElement]

            img.addEventListener(
              `type` = "load",
              listener = _ =>
                val canvas          = document.createElement("canvas").asInstanceOf[HTMLCanvasElement]
                val ctx             = canvas.getContext("2d").asInstanceOf[CanvasRenderingContext2D]
                val (width, height) = computeImageDimensions(img.width, img.height)
                canvas.width = width
                canvas.height = height

                ctx.drawImage(img, 0, 0, canvas.width, canvas.height)

                cb(Right(canvas.toDataURL(file.`type`)))
            )

            img.src = reader.result.toString

          reader.readAsDataURL(file)
        }
      }
    )(UpdateImage(_))

    private def computeImageDimensions(width: Int, height: Int): (Int, Int) =
      if width >= height then
        val ratio     = width * 1.0 / 256
        val newWidth  = width / ratio
        val newHeight = height / ratio
        (newWidth.toInt, newHeight.toInt)
      else computeImageDimensions(width = height, height = width).swap

    def postJob(
      company: String,
      title: String,
      description: String,
      externalUrl: String,
      location: String,
      remote: Boolean,
      salaryLow: Option[Int],
      salaryHigh: Option[Int],
      currency: Option[String],
      country: Option[String],
      tags: Option[String],
      image: Option[String],
      seniority: Option[String],
      other: Option[String]
    )(promoted: Boolean = true): Cmd[IO, App.Msg] =

      val newJobInfo = JobInfo(
        company = company,
        title = title,
        description = description,
        externalUrl = externalUrl,
        location = location,
        remote = remote,
        salaryLow = salaryLow,
        salaryHigh = salaryHigh,
        currency = currency,
        country = country,
        tags = tags.map(_.split(",").map(_.trim).toList),
        image = image,
        seniority = seniority,
        other = other
      )

      val endpoint = if promoted then endpoints.createJobPromoted else endpoints.createJob
      endpoint.callAuthorized(newJobInfo)
