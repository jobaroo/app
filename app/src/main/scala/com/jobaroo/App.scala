package com.jobaroo

import scala.scalajs.js.annotation.*
import org.scalajs.dom.window
import tyrian.*
import tyrian.Html.*
import cats.effect.*
import scala.concurrent.duration.*
import core.*
import tyrian.TyrianApp
import com.jobaroo.components.Header
import com.jobaroo.pages.Page

@JSExportTopLevel("JobarooApp")
class App extends TyrianApp[App.Msg, App.Model]:

  import App.*

  override def init(flags: Map[String, String]): (Model, Cmd[IO, Msg]) =
    val location            = window.location.pathname
    val page                = Page(location)
    val pageCmd             = page.initCmd
    val (router, routerCmd) = Router.startAt(location)
    val session             = Session()
    val sessionCmd          = session.initCmd
    (Model(router, page, session), routerCmd |+| pageCmd |+| sessionCmd)

  override def subscriptions(model: Model): Sub[IO, Msg] =
    Sub.make(
      "urlChange",
      model.router.history.state.discrete.map(_.get).map(newLocation => Router.ChangeLocation(newLocation, true))
    )

  override def update(model: Model): Msg => (Model, Cmd[IO, Msg]) =
    case msg: Router.Msg  =>
      val (newRouter, newRouterCmd) = model.router.update(msg)
      if model.router == newRouter then
        (model, Cmd.None)
      else
        val newPage    = Page(newRouter.location)
        val newPageCmd = newPage.initCmd
        (model.copy(router = newRouter, page = newPage), newRouterCmd |+| newPageCmd)
    case msg: Session.Msg =>
      val (newSession, newSessionCmd) = model.session.update(msg)
      (model.copy(session = newSession), newSessionCmd)
    case msg: App.Msg     =>
      val (newPage, cmd) = model.page.update(msg)
      (model.copy(page = newPage), cmd)

  override def view(model: Model): Html[Msg] =
    div(
      Header.view,
      model.page.view
    )

object App:

  trait Msg
  final case class Model(router: Router, page: Page, session: Session)
