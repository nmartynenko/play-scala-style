package com.aimprosoft.play.glossaries.security

import be.objectify.deadbolt.core.models.Subject
import be.objectify.deadbolt.scala.{DeadboltHandler, DynamicResourceHandler}
import play.api.Logger
import play.api.Play.current
import play.api.cache.Cache
import play.api.mvc.Results._
import play.api.mvc.{Request, Result, Security}


import com.aimprosoft.play.glossaries.util.TemplateUtils._

import scala.concurrent.Future

trait GlossaryUserDeadboltHandler extends DeadboltHandler {

  protected val handlerOption = Some(GlossaryDynamicResourceHandler)

  def getSubject[A](request: Request[A]): Future[Option[Subject]] = {
    Future.successful {
      //get username from session
      request.session.get(Security.username) flatMap {username =>

        val fromCache = Cache.getAs[Subject](username)

        Logger.debug(s"Getting value for $username, and it returns $fromCache")

        fromCache
      }
    }
  }

  def onAuthFailure[A](request: Request[A]): Future[Result] = Future.successful(Forbidden)

  def getDynamicResourceHandler[A](request: Request[A]): Option[DynamicResourceHandler] = handlerOption
}

object SubjectPresentGlossaryUserDeadboltHandler extends GlossaryUserDeadboltHandler{

  private val redirect: Result = Redirect("/login.html")

  def beforeAuthCheck[A](request: Request[A]): Option[Future[Result]] = {
  val subject = getSubjectImmediately(this)(request)
    //do nothing, if subject is defined
    if (subject.isDefined) {
      None
    } else {
      //redirect to login page if there is no subject
      Some(Future.successful(redirect))
    }
  }

}

object SubjectNotPresentGlossaryUserDeadboltHandler extends GlossaryUserDeadboltHandler{

  private val redirect: Result = Redirect("/index.html")

  def beforeAuthCheck[A](request: Request[A]): Option[Future[Result]] = {
    getSubjectImmediately(this)(request) map { _ =>
        //redirect to home page if there is a subject
        Future.successful(redirect)
    }
  }

}
