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
import com.jobaroo.tyrianui.daisy.Card
import com.jobaroo.tyrianui.daisy.Field
import com.jobaroo.tyrianui.daisy.Feedback
import com.jobaroo.tyrianui.html.Tags.{div, h2, input, p}
import com.jobaroo.ui.core.UiId
import com.jobaroo.ui.preset.Jobaroo

final case class FilterPanel(
  jobFilters: JobFilter = JobFilter(),
  selectedFilters: Map[String, Set[String]] = Map.empty,
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
    case UpdateCheckbox(groupName, value, isChecked) =>
      val prevValues         = selectedFilters.getOrElse(groupName, Set.empty)
      val newValues          = if isChecked then prevValues + value else prevValues - value
      val newSelectedFilters = selectedFilters + (groupName -> newValues)
      (this.copy(selectedFilters = newSelectedFilters, isDirty = true), Cmd.None)

  override def view: Html[App.Msg] =
    Card.surface(UiAttrs.classes(Jobaroo.surface.card |+| Jobaroo.surface.stickyRail))(
      Card.body(UiAttrs.classes(Jobaroo.surface.bodyPanel))(
        div(UiAttrs.classes(Jobaroo.filter.header))(
          p(UiAttrs.classes(Jobaroo.section.eyebrow))(text("Search filters")),
          h2(UiAttrs.classes(Jobaroo.post.previewTitle))(text("Refine results")),
          p(UiAttrs.classes(Jobaroo.filter.intro))(text("Filter by salary, remote status, company, location, country, tag, and seniority."))
        ),
        optError.fold(div())(error => Feedback.alert(error, Feedback.Tone.Error)),
        salaryFilter,
        remoteFilter,
        checkboxGroup("Companies", jobFilters.companies),
        checkboxGroup("Locations", jobFilters.locations),
        checkboxGroup("Countries", jobFilters.countries),
        checkboxGroup("Tags", jobFilters.tags),
        checkboxGroup("Seniorities", jobFilters.seniorities),
        div(UiAttrs.classes(Jobaroo.filter.actionGrid))(
          Button.render(
            Button.props[App.Msg]("Update Results").copy(
              width = Button.Width.Full,
              disabled = !isDirty,
              onPress = Some(TriggerFilter)
            )
          )
        )
      )
    )

  private def salaryFilter: Html[App.Msg] =
    renderFilterGroup(
      title = "Salary",
      content = Field.textInput(
        meta = Field.Meta.static("filter-salary", "Minimum salary", hint = Some("Set the minimum visible salary floor.")),
        currentValue = if maxSalary == 0 then "" else maxSalary.toString,
        onValue = value => UpdateSalary(value.toIntOption.getOrElse(0)),
        kind = Field.InputKind.Number,
        fieldAttrs = UiAttrs.classes(Jobaroo.form.compactFieldset),
        labelAttrs = UiAttrs.classes(Jobaroo.form.fieldLabel),
        hintAttrs = UiAttrs.classes(Jobaroo.form.fieldHint)
      )
    )

  private def remoteFilter: Html[App.Msg] =
    renderFilterGroup(
      title = "Remote",
      content = Field.toggleField(
        meta = Field.Meta.static("filter-remote", "Remote only", hint = Some("Only show fully remote roles.")),
        checkedValue = remote,
        onChangeValue = UpdateRemoteCheckbox(_),
        wrapperAttrs = UiAttrs.classes(Jobaroo.form.fileLabel),
        copyAttrs = UiAttrs.classes(Jobaroo.form.fileCopy),
        titleAttrs = UiAttrs.classes(Jobaroo.form.fileTitle),
        hintAttrs = UiAttrs.classes(Jobaroo.form.fileDescription)
      )
    )

  private def checkboxGroup(groupName: String, values: List[String]): Html[App.Msg] =
    val checkedValues = selectedFilters.getOrElse(groupName, Set.empty)

    renderFilterGroup(
      title = groupName,
      content = div(UiAttrs.classes(Jobaroo.filter.groupContent))(
        values.map { value =>
          Field.checkboxField(
            meta = Field.Meta.dynamic(
              id = UiId.slug("filter", groupName, value),
              label = value
            ),
            checkedValue = checkedValues(value),
            onChangeValue = checked => UpdateCheckbox(groupName, value, checked),
            wrapperAttrs = UiAttrs.classes(Jobaroo.form.fileLabel),
            copyAttrs = UiAttrs.classes(Jobaroo.form.fileCopy),
            titleAttrs = UiAttrs.classes(Jobaroo.form.fileTitle),
            hintAttrs = UiAttrs.classes(Jobaroo.form.fileDescription)
          )
        }*
      )
    )

  private def renderFilterGroup(title: String, content: Html[App.Msg]): Html[App.Msg] =
    div(UiAttrs.classes(Jobaroo.filter.collapse))(
      input(UiAttrs(`type` := "checkbox", checked(true))),
      div(UiAttrs.classes(Jobaroo.filter.collapseTitle))(text(title)),
      div(UiAttrs.classes(Jobaroo.filter.collapseBody))(content)
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
