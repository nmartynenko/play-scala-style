package com.aimprosoft.play.glossaries.listeners

import com.aimprosoft.play.glossaries.persistence.impl.{GlossaryPersistence, UserPersistence}
import play.api.Logger
import play.api.Play.current
import play.api.db.slick.Config.driver
import play.api.db.slick.DB

object ApplicationDDLCreator extends Listener{

  import driver.simple._

  //it has to be at first place
  override def order: Int = Listener.HIGHEST_PRECEDENCE

  def init() {
    DB.withSession {implicit session: Session =>
      //check whether we need to create DDL
      if (needsDdlCreation){
        Logger.info("Start updating DDL for application")
        //perform DB schema creation
        createDdl

        Logger.info("Updating DDL for application has ended")
      }
    }
  }

  //specific implementations
  private def createDdl(implicit session: Session) {
    (UserPersistence.tableQuery.ddl ++ GlossaryPersistence.tableQuery.ddl).create
  }

  @inline
  private def needsDdlCreation(implicit session: Session): Boolean = {
    driver.getTables.list.isEmpty
  }
}
