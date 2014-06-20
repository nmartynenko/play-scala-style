package com.aimprosoft.play.glossaries

import com.aimprosoft.play.glossaries.listeners._
import com.aimprosoft.play.glossaries.mapper.ErrorResponses._
import org.reflections.Reflections
import play.api.mvc.Results._
import play.api.mvc._
import play.api.{Application, GlobalSettings, Logger}

import scala.concurrent.Future

object Global extends GlobalSettings{

  override def onStart(app: Application): Unit = {
    Logger.info("Pre-fill some data")

    scanListeners foreach { _.init() }

    Logger.info("Initialization has ended")
  }

  private def scanListeners: Iterable[Listener] = {
    import scala.collection.JavaConversions._
    import scala.reflect.runtime.universe

    val runtimeMirror = universe.runtimeMirror(this.getClass.getClassLoader)

    new Reflections(this.getClass.getPackage.getName).getSubTypesOf(classOf[Listener]).toList map { clazz =>
      val module = runtimeMirror.staticModule(clazz.getName)

      val obj = runtimeMirror.reflectModule(module)

      obj.instance.asInstanceOf[Listener]

    } sortWith {_.order < _.order}
  }

  override def onHandlerNotFound(request: RequestHeader): Future[Result] = {
    Future.successful(NotFound(views.html.page404()))
  }

  override def onError(request: RequestHeader, ex: Throwable): Future[Result] = {

    //onError original exceptions are always wrapped with Play ones
    ex.getCause match {
      case e: Exception => handleException(e, request)
      case _ =>
        super.onError(request, ex)
    }
  }

}
