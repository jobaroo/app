package com.jobaroo.components

import io.circe.syntax.*
import io.circe.parser.*
import io.circe.generic.auto.*
import tyrian.*
import tyrian.http.*
import tyrian.Html.*
import cats.effect.IO
import com.jobaroo.App
import com.jobaroo.common.*
import com.jobaroo.pages.Page
import com.jobaroo.common.Endpoint
import com.jobaroo.domain.job.*
import com.jobaroo.pages.Page.urls
import com.jobaroo.core.Session
import com.jobaroo.pages.Page.Kind
import com.jobaroo.common.constants.endpoints
import org.scalajs.dom.HTMLInputElement
import tyrian.cmds.Logger

final case class FilterPanel(
  jobFilters     : JobFilter = JobFilter(),
  selectedFilters: Map[String, Set[String]] = Map.empty,
  optError       : Option[String] = None,
  maxSalary      : Int = 0,
  remote         : Boolean = false,
  isDirty        : Boolean = false,
  filterAction   : Map[String, Set[String]] => App.Msg = _ => App.NoOp
) extends Component[App.Msg, FilterPanel]:

  import FilterPanel.*

  override def initCmd: Cmd[IO, App.Msg] = commands.getFilters

  override def update(msg: App.Msg): (FilterPanel, Cmd[IO, App.Msg]) = msg match
    case FilterPanelError(error)                     => (this.copy(optError = Some(error)), Cmd.None)
    case FilterPanelSuccess(jobFilters)              => (this.copy(jobFilters = jobFilters), Cmd.None)
    case UpdateSalary(salary)                        => (this.copy(maxSalary = salary, isDirty = true), Cmd.None)
    case TriggerFilter                               => (this.copy(isDirty = false), Cmd.Emit(filterAction(this.selectedFilters)))
    case UpdateRemoteCheckbox(remote)                => (this.copy(remote = remote, isDirty = true), Cmd.None)
    case UpdateCheckbox(groupName, value, isChecked) =>
      val prevValues         = selectedFilters.getOrElse(groupName, Set.empty)
      val newValues          = if isChecked then prevValues + value else prevValues - value
      val newSelectedFilters = selectedFilters + (groupName -> newValues)
      (
        this.copy(selectedFilters = newSelectedFilters, isDirty = true),
        Logger.consoleLog[IO](s"New filters: $newSelectedFilters"))

  override def view: Html[App.Msg] =
    div(`class` := "filter-panel-container")(
      div(`class` := "filter-panel-error")(
        optError.fold(div())(div(_))
      ),
      salaryFilter,
      renderRemoteCheckbox,
      checkboxGroup("Companies", jobFilters.companies),
      checkboxGroup("Locations", jobFilters.locations),
      checkboxGroup("Countries", jobFilters.countries),
      checkboxGroup("Tags", jobFilters.tags),
      checkboxGroup("Seniorities", jobFilters.seniorities),
      button(`type` := "button", disabled(!isDirty), onClick(TriggerFilter))("Apply Filters")
    )

  private def salaryFilter: Html[App.Msg] =
    div(`class` := "filter-group")(
      h6(`class` := "filter-group-header")("Salary"),
      div(`class` := "filter-group-content")(
        label(`for` := "filter-salary")("Min (in local currency)"),
        input(`type` := "number", id := "filter-salary", onInput(s => UpdateSalary(if s.isEmpty then 0 else s.toInt)))
      )
    )

  private def renderRemoteCheckbox: Html[App.Msg] =
    div(`class` := "filter-group-content")(
      label(`for` := "filter-checkbox")("Remote"),
      input(
        `type` := "checkbox",
        id     := s"filter-checkbox",
        checked(remote),
        onEvent("change", e => UpdateRemoteCheckbox(e.target.asInstanceOf[HTMLInputElement].checked))
      )
    )

  private def checkboxGroup(groupName: String, values: List[String]): Html[App.Msg] =
    def checkBox(value: String, isChecked: Boolean): Html[App.Msg] =
      div(`class` := "filter-group-content")(
        label(`for` := s"filter-$groupName-$value")(value),
        input(
          `type` := "checkbox",
          id     := s"filter-$groupName-$value",
          checked(isChecked),
          onEvent("change", e => UpdateCheckbox(groupName, value, e.target.asInstanceOf[HTMLInputElement].checked))
        )
      )

    val checkedValues = selectedFilters.getOrElse(groupName, Set.empty)
    div(`class` := "filter-group")(
      h6(`class` := "filter-group-header")(groupName),
      div(`class` := "filter-group-content")(values.map(v => checkBox(v, checkedValues(v))))
    )

object FilterPanel:

  trait Msg                                                                             extends App.Msg
  final case class FilterPanelError(error: String)                                      extends Msg
  final case class FilterPanelSuccess(jobFilters: JobFilter)                            extends Msg
  final case class UpdateSalary(salary: Int)                                            extends Msg
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
