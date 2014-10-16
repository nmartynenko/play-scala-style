package controllers

import com.aimprosoft.play.glossaries.security.{GlossaryUserSubject, SecurityUserService}
import play.api.Logger
import play.api.Play.current
import play.api.cache.Cache
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._

object LoginController extends SecuredController {

  //define login form
  val loginForm = Form(
    tuple(
      "j_username" -> nonEmptyText,
      "j_password" -> text //can be empty
    ) verifying (error = "Invalid email or password", constraint = {
      case (username, password) =>
        check(username, password)
      case _ =>
        false
    })
  )

  private def check(username: String, password: String) = {
    val maybeAuth = SecurityUserService.authenticate(username, password)

    maybeAuth.fold(false) { auth =>
      val identifier = GlossaryUserSubject.generateIdentifier(username)

      Logger.debug(s"Put value $identifier for $username")

      //put auth in the session
      Cache.set(identifier, auth)

      true
    }
  }

  def login = notAuthenticated {
    Action { implicit request =>
      Ok(views.html.login(loginForm))
    }
  }


  def authenticate = notAuthenticated {
    Action { implicit request =>
      loginForm.bindFromRequest.fold(
        hasErrors = {form => BadRequest(views.html.login(form))},
        success = {
          case (username, _) =>
            HOME.withSession(Security.username -> GlossaryUserSubject.generateIdentifier(username))
        }
      )
    }
  }

  def logout = Action {
    Redirect(routes.LoginController.login()).withNewSession.flashing(
      "success" -> "You are now logged out."
    )
  }

}
