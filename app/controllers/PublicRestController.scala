package controllers

import play.api.Play.current
import play.api.http.ContentTypes
import play.api.i18n.Messages
import play.api.libs.json.Json
import play.api.mvc._

object PublicRestController extends Controller {

  //calculate one time only
  lazy val allMessages = Json.toJson(Messages.messages)

  def messages = Action {
    Ok(allMessages).as(ContentTypes.JSON)
  }

}
