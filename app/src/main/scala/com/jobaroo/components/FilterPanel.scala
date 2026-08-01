package com.jobaroo.components

import io.circe.generic.auto.*
import cats.effect.IO
import cats.syntax.semigroup.*
import tyrian.*
import tyrian.http.*
import tyrian.Html.*
import com.jobaroo.App
import com.jobaroo.common.*
import com.jobaroo.common.Endpoint
import com.jobaroo.domain.job.*
import com.jobaroo.tyrianui.core.UiAttrs
import com.jobaroo.tyrianui.daisy.Button
import com.jobaroo.tyrianui.daisy.Feedback
import com.jobaroo.tyrianui.daisy.Toggle
import com.jobaroo.tyrianui.html.Tags.{div, h2, h3, input, label, p, span, Children}
import com.jobaroo.tyrianui.icons.Icons
import com.jobaroo.ui.core.Css
import com.jobaroo.ui.core.UiId
import com.jobaroo.ui.preset.Jobaroo
import com.jobaroo.ui.syntax.all.*

final case class FilterPanel(
  jobFilters     : JobFilter = JobFilter(),
  selectedFilters: Map[String, Set[String]] = Map.empty,
  searchTerms    : Map[String, String] = Map.empty,
  optError       : Option[String] = None,
  maxSalary      : Int = 0,
  remote         : Boolean = false,
  isDirty        : Boolean = false,
  filterAction   : JobFilter => App.Msg = _ => App.NoOp
) extends Component[App.Msg, FilterPanel]:

  import FilterPanel.*

  override def initCmd: Cmd[IO, App.Msg] = commands.getFilters

  override def update(msg: App.Msg): (FilterPanel, Cmd[IO, App.Msg]) = msg match
    case FilterPanelError(error)                                       => (this.copy(optError = Some(error)), Cmd.None)
    case FilterPanelSuccess(jobFilters)                                => (this.copy(jobFilters = jobFilters), Cmd.None)
    case UpdateSalary(salary)                                          => (this.copy(maxSalary = salary, isDirty = true), Cmd.None)
    case TriggerFilter                                                 => (this.copy(isDirty = false), Cmd.Emit(filterAction(currentFilter)))
    case ResetFilters                                                  => (resetState, Cmd.Emit(filterAction(JobFilter())))
    case UpdateRemoteCheckbox(isRemote)                                =>
      val nextSelectedFilters = if isRemote then selectedFilters - Groups.Locations else selectedFilters
      val nextSearchTerms     = if isRemote then searchTerms - Groups.Locations else searchTerms
      (
        this.copy(
          remote = isRemote,
          selectedFilters = nextSelectedFilters,
          searchTerms = nextSearchTerms,
          isDirty = true
        ),
        Cmd.None
      )
    case UpdateSearch(groupName, _) if isGroupDisabled(groupName)      =>
      (this, Cmd.None)
    case UpdateSearch(groupName, query)                                =>
      (this.copy(searchTerms = searchTerms + (groupName -> query), isDirty = true), Cmd.None)
    case UpdateCheckbox(groupName, _, _) if isGroupDisabled(groupName) =>
      (this, Cmd.None)
    case UpdateCheckbox(groupName, value, isChecked)                   =>
      val previous          = selectedValues(groupName)
      val updatedValues     = if isChecked then previous + value else previous - value
      val updatedSelections = selectedFilters + (groupName -> updatedValues)
      (this.copy(selectedFilters = updatedSelections, isDirty = true), Cmd.None)

  override def view: Html[App.Msg] =
    div(UiAttrs.classes(Jobaroo.filter.sidebar))(Children.concat(viewSections))

  private def viewSections: List[Html[App.Msg]] =
    List(filterHeader, filterIntro) ++
      optError.toList.map(error => Feedback.alert(error, Feedback.Tone.Error, UiAttrs.classes(Css.literal("mb-4")))) ++
      List(remoteFilter, salaryFilter) ++
      sections.map(renderSection) ++
      List(actionButtons)

  private def filterHeader: Html[App.Msg] =
    div(UiAttrs.classes(Jobaroo.filter.header))(
      div(UiAttrs.classes(Css.literal("flex items-center gap-2")))(
        span(UiAttrs.classes(Css.literal("text-primary")))(Icons.funnel(Jobaroo.icon.regular)),
        h2(UiAttrs.classes(Css.literal("text-lg font-semibold")))(text("Filter Jobs"))
      )
    )

  private def filterIntro: Html[App.Msg] =
    p(UiAttrs.classes(Jobaroo.filter.intro))(text("Filter by remote status, salary, company, location, country, tags, and seniority."))

  private def remoteFilter: Html[App.Msg] =
    div(UiAttrs.classes(Css.literal("mb-5")))(
      div(UiAttrs.classes(Css.literal("flex items-center justify-between gap-3")))(
        label(UiAttrs.classes(Css.literal("text-sm font-medium")))(
          text("Remote Only")
        ),
        Toggle.render(
          Toggle.props[App.Msg](checked = remote, onToggle = UpdateRemoteCheckbox.apply).copy(
            ariaLabel = Some("Remote Only"),
            attrs = UiAttrs.classes(Jobaroo.filter.remoteSwitch),
            checkedAttrs = UiAttrs.classes(Jobaroo.filter.remoteSwitchOn),
            uncheckedAttrs = UiAttrs.classes(Jobaroo.filter.remoteSwitchOff),
            stateAttrs = UiAttrs.classes(Jobaroo.filter.remoteState),
            thumbAttrs = UiAttrs.classes(Jobaroo.filter.remoteThumb)
          )
        )
      )
    )

  private def salaryFilter: Html[App.Msg] =
    div(UiAttrs.classes(Css.literal("mb-5")))(
      h3(UiAttrs.classes(Jobaroo.filter.sectionTitle))(text("Maximum Salary")),
      input(
        UiAttrs(
          `type`      := "number",
          value       := (if maxSalary == 0 then "" else maxSalary.toString),
          placeholder := "e.g. 120000"
        ) |+|
          UiAttrs.classes(Css.literal("input input-bordered input-sm w-full mb-2")) |+|
          UiAttrs(onInput(value => UpdateSalary(value.toIntOption.getOrElse(0))))
      ),
      div(UiAttrs.classes(Jobaroo.filter.salaryBox))(text(if maxSalary == 0 then "No salary limit"
      else s"Up to $$${maxSalary}"))
    )

  private def sections: List[SectionSpec] = List(
    SectionSpec.checkbox(Groups.Companies, jobFilters.companies, searchable = true),
    SectionSpec.checkbox(
      Groups.Locations,
      jobFilters.locations,
      searchable = true,
      disabled = remote,
      helper = Option.when(remote)("Location filters are unavailable while Remote Only is enabled.")
    ),
    SectionSpec.checkbox(Groups.Countries, jobFilters.countries, searchable = true),
    SectionSpec.pills(Groups.Tags, jobFilters.tags, searchable = true),
    SectionSpec.checkbox(Groups.Seniorities, jobFilters.seniorities)
  )

  private def renderSection(section: SectionSpec): Html[App.Msg] =
    val filtered = filteredValues(section)

    div(UiAttrs.classes(Jobaroo.filter.groupWrap |+| Jobaroo.filter.sectionDisabled.when(section.disabled)))(
      Children.concat(
        Children.one(h3(UiAttrs.classes(Jobaroo.filter.sectionTitle))(text(section.groupName))),
        Children.fromOption(searchField(section)),
        Children.fromOption(section.helper.map(renderHelperText)),
        Children.one(renderSectionContent(section, filtered)),
        Children.one(emptyState(section.groupName, filtered))
      )
    )

  private def renderSectionContent(section: SectionSpec, values: List[String]): Html[App.Msg] =
    section.kind match
      case SectionKind.Checkboxes => renderCheckboxes(section, values)
      case SectionKind.Pills      => renderPills(section, values)

  private def renderCheckboxes(section: SectionSpec, values: List[String]): Html[App.Msg] =
    val checkedValues = selectedValues(section.groupName)

    div(UiAttrs.classes(Jobaroo.filter.groupContent))(
      values.map { value =>
        label(UiAttrs.classes(Jobaroo.filter.rowOption |+| Jobaroo.filter.rowOptionDisabled.when(section.disabled)))(
          input(checkboxAttrs(section, value, checkedValues(value))),
          span(UiAttrs.classes(Css.literal("text-sm")))(text(value))
        )
      }*
    )

  private def renderPills(section: SectionSpec, values: List[String]): Html[App.Msg] =
    val checkedValues = selectedValues(section.groupName)

    div(UiAttrs.classes(Jobaroo.filter.pillGroup))(
      values.map { value =>
        label(UiAttrs.classes(Css.literal("cursor-pointer") |+| Jobaroo.filter.rowOptionDisabled.when(section.disabled)))(
          input(
            UiAttrs(
              `type` := "checkbox",
              id     := fieldId(section.groupName, value).value,
              disabled(section.disabled)
            ) |+|
              UiAttrs.booleanDomProperty("checked", checkedValues(value)) |+|
              UiAttrs.classes(Css.literal("hidden peer")) |+|
              checkboxChangeHandler(section.groupName, value)
          ),
          span(
            UiAttrs.classes(
              Css.literal("badge badge-sm badge-outline peer-checked:badge-primary peer-checked:text-primary-content")
            )
          )(text(value))
        )
      }*
    )

  private def checkboxAttrs(section: SectionSpec, value: String, isChecked: Boolean): UiAttrs[App.Msg] =
    UiAttrs(
      `type` := "checkbox",
      id     := fieldId(section.groupName, value).value,
      disabled(section.disabled)
    ) |+|
      UiAttrs.booleanDomProperty("checked", isChecked) |+|
      UiAttrs.classes(Css.literal("checkbox checkbox-primary checkbox-xs")) |+|
      checkboxChangeHandler(section.groupName, value)

  private def checkboxChangeHandler(groupName: String, value: String): UiAttrs[App.Msg] =
    UiAttrs(
      onEvent(
        "change",
        event => UpdateCheckbox(groupName, value, event.target.asInstanceOf[org.scalajs.dom.HTMLInputElement].checked)
      )
    )

  private def searchField(section: SectionSpec): Option[Html[App.Msg]] =
    Option.when(section.searchable)(
      input(
        UiAttrs(
          `type`      := "search",
          value       := queryFor(section.groupName),
          placeholder := s"Search ${section.groupName.toLowerCase}...",
          disabled(section.disabled)
        ) |+|
          UiAttrs.classes(Jobaroo.filter.searchInput) |+|
          UiAttrs(onInput(UpdateSearch(section.groupName, _)))
      )
    )

  private def renderHelperText(copy: String): Html[App.Msg] =
    p(UiAttrs.classes(Jobaroo.filter.helperText))(text(copy))

  private def actionButtons: Html[App.Msg] =
    div(UiAttrs.classes(Jobaroo.filter.actionGrid))(
      Button.render(
        Button.props[App.Msg]("Apply Filters").copy(
          width = Button.Width.Full,
          disabled = !isDirty,
          onPress = Some(TriggerFilter)
        )
      ),
      Button.render(
        Button.props[App.Msg]("Reset Filters").copy(
          tone = Button.Tone.Outline,
          width = Button.Width.Full,
          disabled = !hasResettableState,
          onPress = Some(ResetFilters)
        )
      )
    )

  private def queryFor(groupName: String): String =
    searchTerms.getOrElse(groupName, "")

  private def filteredValues(section: SectionSpec): List[String] =
    val query = queryFor(section.groupName).trim.toLowerCase

    if query.isEmpty then section.values
    else section.values.filter(_.toLowerCase.contains(query))

  private def emptyState(groupName: String, values: List[String]): Html[App.Msg] =
    if values.nonEmpty then div()
    else p(UiAttrs.classes(Jobaroo.filter.emptyState))(text(s"No ${groupName.toLowerCase} match the current search."))

  private def selectedValues(groupName: String): Set[String] =
    selectedFilters.getOrElse(groupName, Set.empty)

  private def currentFilter: JobFilter =
    JobFilter(
      companies = selectedValues(Groups.Companies).toList.sorted,
      locations = if remote then Nil else selectedValues(Groups.Locations).toList.sorted,
      countries = selectedValues(Groups.Countries).toList.sorted,
      seniorities = selectedValues(Groups.Seniorities).toList.sorted,
      tags = selectedValues(Groups.Tags).toList.sorted,
      maxSalary = Option.when(maxSalary > 0)(maxSalary),
      remote = remote
    )

  private def isGroupDisabled(groupName: String): Boolean =
    remote && groupName == Groups.Locations

  private def hasResettableState: Boolean =
    remote || maxSalary > 0 || selectedFilters.values.exists(_.nonEmpty) || searchTerms.values.exists(_.trim.nonEmpty)

  private def resetState: FilterPanel =
    this.copy(selectedFilters = Map.empty, searchTerms = Map.empty, maxSalary = 0, remote = false, isDirty = false)

  private def fieldId(groupName: String, value: String): UiId =
    UiId.slug("filter", groupName, value)

object FilterPanel:

  object Groups:

    val Companies   = "Companies"
    val Locations   = "Locations"
    val Countries   = "Countries"
    val Tags        = "Tags"
    val Seniorities = "Seniorities"

  enum SectionKind:
    case Checkboxes, Pills

  final case class SectionSpec(
    groupName : String,
    values    : List[String],
    kind      : SectionKind,
    searchable: Boolean = false,
    disabled  : Boolean = false,
    helper    : Option[String] = None
  )

  object SectionSpec:

    def checkbox(
      groupName: String,
      values: List[String],
      searchable: Boolean = false,
      disabled: Boolean = false,
      helper: Option[String] = None
    ): SectionSpec =
      SectionSpec(groupName, values, SectionKind.Checkboxes, searchable, disabled, helper)

    def pills(
      groupName: String,
      values: List[String],
      searchable: Boolean = false,
      disabled: Boolean = false,
      helper: Option[String] = None
    ): SectionSpec =
      SectionSpec(groupName, values, SectionKind.Pills, searchable, disabled, helper)

  trait Msg                                                                             extends App.Msg
  final case class FilterPanelError(error: String)                                      extends Msg
  final case class FilterPanelSuccess(jobFilters: JobFilter)                            extends Msg
  final case class UpdateSalary(salary: Int)                                            extends Msg
  final case class UpdateSearch(groupName: String, query: String)                       extends Msg
  final case class UpdateCheckbox(groupName: String, value: String, isChecked: Boolean) extends Msg
  final case class UpdateRemoteCheckbox(remote: Boolean)                                extends Msg
  case object TriggerFilter                                                             extends Msg
  case object ResetFilters                                                              extends Msg

  object endpoints:

    val getJobFilters = new Endpoint[Msg](
      location = constants.endpoints.jobFilters,
      method = Method.Get,
      onError = e => FilterPanelError(e.toString),
      onResponse = Endpoint.onResponse[JobFilter, Msg](onError = FilterPanelError(_), onSuccess = FilterPanelSuccess(_))
    ) {}

  object commands:

    def getFilters: Cmd[IO, Msg] = endpoints.getJobFilters.call()
