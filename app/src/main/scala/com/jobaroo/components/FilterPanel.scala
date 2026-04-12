package com.jobaroo.components

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
import com.jobaroo.domain.job.*
import com.jobaroo.tyrianui.core.UiAttrs
import com.jobaroo.tyrianui.daisy.Button
import com.jobaroo.tyrianui.daisy.Feedback
import com.jobaroo.tyrianui.html.Tags.{Children, div, h2, h3, input, label, p, span}
import com.jobaroo.tyrianui.icons.Icons
import com.jobaroo.ui.core.Css
import com.jobaroo.ui.core.UiId
import com.jobaroo.ui.preset.Jobaroo

final case class FilterPanel(
  jobFilters: JobFilter = JobFilter(),
  selectedFilters: Map[String, Set[String]] = Map.empty,
  searchTerms: Map[String, String] = Map.empty,
  optError: Option[String] = None,
  maxSalary: Int = 0,
  remote: Boolean = false,
  isDirty: Boolean = false,
  filterAction: Map[String, Set[String]] => App.Msg = _ => App.NoOp
) extends Component[App.Msg, FilterPanel]:

  import FilterPanel.*

  override def initCmd: Cmd[IO, App.Msg] = commands.getFilters

  override def update(msg: App.Msg): (FilterPanel, Cmd[IO, App.Msg]) = msg match
    case FilterPanelError(error)                     => (this.copy(optError = Some(error)), Cmd.None)
    case FilterPanelSuccess(jobFilters)              => (this.copy(jobFilters = jobFilters), Cmd.None)
    case UpdateSalary(salary)                        => (this.copy(maxSalary = salary, isDirty = true), Cmd.None)
    case TriggerFilter                               => (this.copy(isDirty = false), Cmd.Emit(filterAction(this.selectedFilters)))
    case UpdateRemoteCheckbox(remote)                => (this.copy(remote = remote, isDirty = true), Cmd.None)
    case UpdateSearch(groupName, query)              => (this.copy(searchTerms = searchTerms + (groupName -> query)), Cmd.None)
    case UpdateCheckbox(groupName, value, isChecked) =>
      val prevValues         = selectedFilters.getOrElse(groupName, Set.empty)
      val newValues          = if isChecked then prevValues + value else prevValues - value
      val newSelectedFilters = selectedFilters + (groupName -> newValues)
      (this.copy(selectedFilters = newSelectedFilters, isDirty = true), Cmd.None)

  override def view: Html[App.Msg] =
    div(UiAttrs.classes(Jobaroo.filter.sidebar))(
      div(UiAttrs.classes(Jobaroo.filter.header))(
        div(UiAttrs.classes(Css.literal("flex items-center gap-2")))(
          span(UiAttrs.classes(Css.literal("text-primary")))(Icons.funnel(Jobaroo.icon.regular)),
          h2(UiAttrs.classes(Css.literal("text-lg font-semibold")))(text("Filter Jobs"))
        )
      ),
      p(UiAttrs.classes(Jobaroo.filter.intro))(text("Filter by remote status, salary, company, location, country, tags, and seniority.")),
      optError.fold(div())(error => Feedback.alert(error, Feedback.Tone.Error, UiAttrs.classes(Css.literal("mb-4")))),
      remoteFilter,
      salaryFilter,
      checkboxGroup("Companies", jobFilters.companies),
      checkboxGroup("Locations", jobFilters.locations),
      checkboxGroup("Countries", jobFilters.countries),
      pillGroup("Tags", jobFilters.tags),
      checkboxGroup("Seniorities", jobFilters.seniorities),
      div(UiAttrs.classes(Jobaroo.filter.actionGrid))(
        Button.render(
          Button.props[App.Msg]("Apply Filters").copy(
            width = Button.Width.Full,
            disabled = !isDirty,
            onPress = Some(TriggerFilter)
          )
        )
      )
    )

  private def remoteFilter: Html[App.Msg] =
    div(UiAttrs.classes(Css.literal("mb-5")))(
      label(UiAttrs.classes(Css.literal("flex items-center justify-between cursor-pointer")))(
        span(UiAttrs.classes(Css.literal("text-sm font-medium")))(text("Remote Only")),
        input(
          UiAttrs(`type` := "checkbox", checked(remote)) |+|
            UiAttrs.classes(Css.literal("toggle toggle-primary toggle-sm")) |+|
            UiAttrs(
              onEvent(
                "change",
                event => UpdateRemoteCheckbox(event.target.asInstanceOf[org.scalajs.dom.HTMLInputElement].checked)
              )
            )
        )
      )
    )

  private def salaryFilter: Html[App.Msg] =
    div(UiAttrs.classes(Css.literal("mb-5")))(
      h3(UiAttrs.classes(Jobaroo.filter.sectionTitle))(text("Maximum Salary")),
      input(
        UiAttrs(`type` := "number", value := (if maxSalary == 0 then "" else maxSalary.toString), placeholder := "e.g. 120000") |+|
          UiAttrs.classes(Css.literal("input input-bordered input-sm w-full mb-2")) |+|
          UiAttrs(onInput(value => UpdateSalary(value.toIntOption.getOrElse(0))))
      ),
      div(UiAttrs.classes(Jobaroo.filter.salaryBox))(text(if maxSalary == 0 then "No salary limit" else s"Up to $$${maxSalary}"))
    )

  private def checkboxGroup(groupName: String, values: List[String]): Html[App.Msg] =
    val checkedValues = selectedFilters.getOrElse(groupName, Set.empty)
    val filtered      = filteredValues(groupName, values)

    filterSection(groupName)(
      div(UiAttrs.classes(Jobaroo.filter.groupContent))(
        filtered.map { value =>
          val fieldId = UiId.slug("filter", groupName, value)
          label(UiAttrs.classes(Jobaroo.filter.rowOption))(
            input(
              UiAttrs(`type` := "checkbox", id := fieldId.value, checked(checkedValues(value))) |+|
                UiAttrs.classes(Css.literal("checkbox checkbox-primary checkbox-xs")) |+|
                UiAttrs(
                  onEvent(
                    "change",
                    event => UpdateCheckbox(groupName, value, event.target.asInstanceOf[org.scalajs.dom.HTMLInputElement].checked)
                  )
                )
            ),
            span(UiAttrs.classes(Css.literal("text-sm")))(text(value))
          )
        }*
      ),
      emptyState(groupName, filtered)
    )

  private def pillGroup(groupName: String, values: List[String]): Html[App.Msg] =
    val checkedValues = selectedFilters.getOrElse(groupName, Set.empty)
    val filtered      = filteredValues(groupName, values)

    filterSection(groupName)(
      div(UiAttrs.classes(Jobaroo.filter.pillGroup))(
        filtered.map { value =>
          val fieldId = UiId.slug("filter", groupName, value)
          label(UiAttrs.classes(Css.literal("cursor-pointer")))(
            input(
              UiAttrs(`type` := "checkbox", id := fieldId.value, checked(checkedValues(value))) |+|
                UiAttrs.classes(Css.literal("hidden peer")) |+|
                UiAttrs(
                  onEvent(
                    "change",
                    event => UpdateCheckbox(groupName, value, event.target.asInstanceOf[org.scalajs.dom.HTMLInputElement].checked)
                  )
                )
            ),
            span(
              UiAttrs.classes(
                Css.literal(
                  "badge badge-sm badge-outline peer-checked:badge-primary peer-checked:text-primary-content"
                )
              )
            )(text(value))
          )
        }*
      ),
      emptyState(groupName, filtered)
    )

  private def filterSection(groupName: String)(children: Html[App.Msg]*): Html[App.Msg] =
    div(UiAttrs.classes(Jobaroo.filter.groupWrap))(
      Children.concat(
        Children.one(h3(UiAttrs.classes(Jobaroo.filter.sectionTitle))(text(groupName))),
        Children.fromOption(searchField(groupName)),
        children
      )
    )

  private def searchField(groupName: String): Option[Html[App.Msg]] =
    Option.when(isSearchable(groupName))(
      input(
        UiAttrs(`type` := "search", value := queryFor(groupName), placeholder := s"Search ${groupName.toLowerCase}...") |+|
          UiAttrs.classes(Jobaroo.filter.searchInput) |+|
          UiAttrs(onInput(UpdateSearch(groupName, _)))
      )
    )

  private def queryFor(groupName: String): String =
    searchTerms.getOrElse(groupName, "")

  private def filteredValues(groupName: String, values: List[String]): List[String] =
    val query = queryFor(groupName).trim.toLowerCase

    if query.isEmpty then values
    else values.filter(_.toLowerCase.contains(query))

  private def emptyState(groupName: String, values: List[String]): Html[App.Msg] =
    if values.nonEmpty then div()
    else p(UiAttrs.classes(Jobaroo.filter.emptyState))(text(s"No ${groupName.toLowerCase} match the current search."))

  private def isSearchable(groupName: String): Boolean =
    Set("Companies", "Locations", "Countries", "Tags").contains(groupName)

object FilterPanel:

  trait Msg                                                                             extends App.Msg
  final case class FilterPanelError(error: String)                                      extends Msg
  final case class FilterPanelSuccess(jobFilters: JobFilter)                            extends Msg
  final case class UpdateSalary(salary: Int)                                            extends Msg
  final case class UpdateSearch(groupName: String, query: String)                       extends Msg
  final case class UpdateCheckbox(groupName: String, value: String, isChecked: Boolean) extends Msg
  final case class UpdateRemoteCheckbox(remote: Boolean)                                extends Msg
  case object TriggerFilter                                                             extends Msg

  object endpoints:

    val getJobFilters = new Endpoint[Msg](
      location = constants.endpoints.jobFilters,
      method = Method.Get,
      onError = e => FilterPanelError(e.toString),
      onResponse = Endpoint.onResponse[JobFilter, Msg](onError = FilterPanelError(_), onSuccess = FilterPanelSuccess(_))
    ) {}

  object commands:

    def getFilters: Cmd[IO, Msg] = endpoints.getJobFilters.call()
