package controllers

import com.aimprosoft.play.glossaries.models._
import com.aimprosoft.play.glossaries.service.{GlossaryService => service}
import play.api.data.validation.ValidationError
import play.api.http.ContentTypes
import play.api.i18n.{Lang, Messages}
import play.api.libs.json._
import play.api.mvc._

object GlossariesRestController extends SecuredController {

  import JsonFormatImplicits._

  def getGlossaries(startRow: Int, pageSize: Int) = authenticated {
    Action {
      //get generic page
      val page = service.getCurrentPage(startRow, pageSize)

      Ok(Json.toJson(page)).as(ContentTypes.JSON)
    }
  }

  def getGlossary(id: Long) = authenticated {
    Action {
      service.getById(id) map {glossary =>
          Ok(Json.toJson(glossary)).as(ContentTypes.JSON)
      } getOrElse {
        BadRequest(Messages("sample.error.glossary.not.found", id))
      }
    }
  }

  def removeGlossary(glossaryId: Long) =  asAdmin {
    Action {
      service.removeById(glossaryId)

      Ok
    }
  }

  def updateGlossary = asAdmin {
    saveUpdate {service.update}
  }

  def saveGlossary = asAdmin {
    saveUpdate {service.add}
  }

  private def saveUpdate[Ignore](action: Glossary => Ignore) = Action(parse.json) {
    implicit request =>
      request.body.validate[Glossary] match {
        case JsSuccess(glossary, _) =>
          action(glossary)
          Ok
        case JsError(errors) =>
          val validationResponse = handleErrors(errors)

          BadRequest(Json.toJson(validationResponse))
      }
  }

  private def handleErrors(errors: Seq[(JsPath, Seq[ValidationError])])(implicit lang: Lang): Map[String, Seq[String]] = {
    (errors map {
      case (jsPath, validationErrors) =>
        val key = jsPath.toString()
        val value = validationErrors map {
          valError => Messages(valError.message)
        }

        //return tuple, which naturally transforms into map
        key -> value
    }).toMap
  }

}