package com.aimprosoft.play.glossaries.util

import be.objectify.deadbolt.core.models.Subject
import be.objectify.deadbolt.scala.DeadboltHandler
import play.api.mvc.Request

import scala.concurrent.Await
import scala.concurrent.duration._

object TemplateUtils {

  def getSubjectImmediately(handler: DeadboltHandler)(implicit request: Request[_]): Option[Subject] = {
    Await.result(handler.getSubject(request), 1.second)
  }

}
