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
import com.jobaroo.tyrianui.core.UiAttrs
import com.jobaroo.tyrianui.daisy.Badge
import com.jobaroo.tyrianui.daisy.Card
import com.jobaroo.tyrianui.daisy.Feedback
import com.jobaroo.tyrianui.html.Tags.{div, form, h3, h4, img, li, p, ul}
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
    if !Session.isActive then
      AppLayout.pageContainer(
        AppLayout.hero(
          title = "Post a job with a cleaner hiring workflow.",
          subtitle = "You need to be logged in before creating a listing.",
          eyebrow = "Recruiter Console"
        ),
        div(UiAttrs.classes(Jobaroo.state.centeredShort))(
          Feedback.alert("You need to be logged in to post a job.", Feedback.Tone.Warning)
        )
      )
    else
      AppLayout.pageContainer(
        AppLayout.hero(
          title = "Publish a role that candidates actually want to read.",
          subtitle = "The submit flow stays wired to the current backend. This page is being rebuilt as a disciplined UI layer with a live preview and cleaner composition.",
          eyebrow = "Recruiter Console",
          actions = Seq(
            Badge.render(s"Promoted listing $$${constants.jobAdvertPriceUSD}", Badge.Tone.Primary)
          )
        ),
        div(UiAttrs.classes(Jobaroo.shell.splitWide))(
          renderComposer,
          renderPreviewRail
        )
      )

  override protected def renderFormContent(): List[Html[App.Msg]] =
    if !Session.isActive then return List(p()(text("You need to be logged in to post a job.")))

    List(
      renderInput("Company", "company", "text", true, company, UpdateCompany(_)),
      renderInput("Title", "title", "text", true, title, UpdateTitle(_)),
      renderTextArea("Description", "description", true, description, UpdateDescription(_)),
      renderInput("External URL", "externalUrl", "url", true, externalUrl, UpdateExternalUrl(_)),
      renderInput("Location", "location", "text", true, location, UpdateLocation(_)),
      renderToggle(
        name = "Remote",
        uid = "remote",
        isRequired = true,
        checkedValue = remote,
        onChange = _ => ToggleRemote,
        hint = Some("Keep enabled when candidates can work from anywhere.")
      ),
      renderInput(
        "Salary Low",
        "salaryLow",
        "number",
        false,
        salaryLow.fold("")(_.toString),
        amount => UpdateSalaryLow(Try(amount.toInt).getOrElse(0))
      ),
      renderInput(
        "Salary High",
        "salaryHigh",
        "number",
        false,
        salaryHigh.fold("")(_.toString),
        amount => UpdateSalaryHigh(Try(amount.toInt).getOrElse(0))
      ),
      renderInput("Currency", "currency", "text", false, currency.getOrElse(""), UpdateCurrency(_)),
      renderInput("Country", "country", "text", false, country.getOrElse(""), UpdateCountry(_)),
      renderInput("Tags", "tags", "text", false, tags.getOrElse(""), UpdateTags(_)),
      renderImageUploadInput("Logo", "logo", image, UpdateImageFile(_)),
      renderInput("Seniority", "seniority", "text", false, seniority.getOrElse(""), UpdateSeniority(_)),
      renderInput("Other", "other", "text", false, other.getOrElse(""), UpdateOther(_)),
      renderPrimaryAction(s"Post Job - $$${constants.jobAdvertPriceUSD}", PostJob)
    )

  private def renderComposer: Html[App.Msg] =
    Card.surface(UiAttrs.classes(Jobaroo.surface.card))(
      Card.body(UiAttrs.classes(Jobaroo.surface.bodyComfortable))(
        AppLayout.sectionTitle(
          eyebrow = "Role details",
          title = "Compose the listing",
          subtitle = "Keep the structure tight: title, compensation, location, signal, and a strong description."
        ),
        renderStatusAlert,
        form(
          UiAttrs(name := "job-post", id := "form") |+|
            UiAttrs.classes(Jobaroo.form.composeGrid) |+|
            UiAttrs(
              onEvent(
                "submit",
                e =>
                  e.preventDefault()
                  App.NoOp
              )
            )
        )(renderFormContent()*)
      )
    )

  private def renderPreviewRail: Html[App.Msg] =
    div(UiAttrs.classes(Jobaroo.post.rail))(
      Card.surface(UiAttrs.classes(Jobaroo.surface.card |+| Jobaroo.surface.stickyRail))(
        Card.body(UiAttrs.classes(Jobaroo.surface.bodyCompact))(
          div(UiAttrs.classes(Jobaroo.post.previewHeader))(
            p(UiAttrs.classes(Jobaroo.post.previewEyebrow))(text("Live preview")),
            h3(UiAttrs.classes(Jobaroo.post.previewTitle))(text("How candidates will see it"))
          ),
          renderPreviewCard,
          renderChecklist
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
        p(UiAttrs.classes(Jobaroo.post.applyValue))(text(fallback(externalUrl, "External URL not set")))
      )
    )

  private def renderChecklist: Html[App.Msg] =
    div(UiAttrs.classes(Jobaroo.post.checklist))(
      p(UiAttrs.classes(Jobaroo.post.checklistTitle))(text("Readiness")),
      ul(UiAttrs.classes(Jobaroo.post.checklistList))(
        checklistItem("Company name is set", company.nonEmpty),
        checklistItem("Job title is set", title.nonEmpty),
        checklistItem("Description is set", description.nonEmpty),
        checklistItem("External URL is set", externalUrl.nonEmpty)
      )
    )

  private def checklistItem(label: String, isReady: Boolean): Html[App.Msg] =
    li(UiAttrs.classes(Jobaroo.post.checklistItem))(
      span()(text(label)),
      Badge.render(if isReady then "Ready" else "Missing", if isReady then Badge.Tone.Primary else Badge.Tone.Outline)
    )

  private def companyOrPlaceholder: String = fallback(company, "Company")
  private def titleOrPlaceholder: String   = fallback(title, "Role title")

  private def descriptionPreview: String =
    fallback(
      PreviewText.fromMarkdown(description),
      "Add a concise, candidate-first description. This live preview updates from the current Tyrian state."
    )

  private def locationPreview: String =
    val place = fallback(location, "Location")
    country.fold(place)(selectedCountry => s"$selectedCountry, $place")

  private def salaryPreview: String =
    (salaryLow, salaryHigh, currency.map(_.trim).filter(_.nonEmpty)) match
      case (Some(low), Some(high), Some(curr)) => s"$curr $low-$high"
      case (Some(low), Some(high), None)       => s"$low-$high"
      case (Some(low), None, Some(curr))       => s"$curr $low+"
      case (Some(low), None, None)             => s"$low+"
      case (None, Some(high), Some(curr))      => s"$curr up to $high"
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
