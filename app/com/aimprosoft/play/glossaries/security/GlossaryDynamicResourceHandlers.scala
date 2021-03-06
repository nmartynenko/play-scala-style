package com.aimprosoft.play.glossaries.security

import be.objectify.deadbolt.scala.{DeadboltHandler, DynamicResourceHandler}
import com.aimprosoft.play.glossaries.util.TemplateUtils._
import play.api.mvc.Request

sealed trait RejectAllDynamicResourceHandler extends DynamicResourceHandler {
  def isAllowed[A](name: String, meta: String, deadboltHandler: DeadboltHandler, request: Request[A]): Boolean = false

  def checkPermission[A](permissionValue: String, deadboltHandler: DeadboltHandler, request: Request[A]): Boolean = false
}

sealed trait AllowAllDynamicResourceHandler extends DynamicResourceHandler {
  def isAllowed[A](name: String, meta: String, deadboltHandler: DeadboltHandler, request: Request[A]): Boolean = true

  def checkPermission[A](permissionValue: String, deadboltHandler: DeadboltHandler, request: Request[A]): Boolean = true
}

object RejectAllDynamicResourceHandler extends RejectAllDynamicResourceHandler

object AllowAllDynamicResourceHandler extends AllowAllDynamicResourceHandler

object RoleBasedDynamicResourceHandler extends RejectAllDynamicResourceHandler {

  //Java2Scala conversions and vice versa
  import scala.collection.JavaConversions._

  override def isAllowed[A](name: String, meta: String, deadboltHandler: DeadboltHandler, request: Request[A]): Boolean = {
    getSubjectImmediately(deadboltHandler)(request).fold(false) {
        _.getRoles exists {
          _.getName == name.toLowerCase
        }
    }
  }
}

object GlossaryDynamicResourceHandler extends RejectAllDynamicResourceHandler {

  private val handlers: Map[String, DynamicResourceHandler] = Map(

      "user" -> RoleBasedDynamicResourceHandler,

      "admin" -> RoleBasedDynamicResourceHandler

    ).withDefaultValue(RejectAllDynamicResourceHandler)

  override def isAllowed[A](name: String, meta: String, deadboltHandler: DeadboltHandler, request: Request[A]): Boolean = {
    handlers(name.toLowerCase).isAllowed(name, meta, deadboltHandler, request)
  }
}