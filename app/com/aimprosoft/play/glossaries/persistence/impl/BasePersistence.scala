package com.aimprosoft.play.glossaries.persistence.impl

import com.aimprosoft.play.glossaries.persistence.Persistence
import play.api.db.slick.Config.driver.simple._
import scala.language.reflectiveCalls

//abstract table entity
abstract class SlickBaseTable[T, ID](tag: Tag, tableName: String) extends Table[T](tag, tableName){
  def id: Column[ID]
}

abstract class SlickBasePersistence[T <: {val id: Option[ID]}, ID: BaseColumnType]
  extends Persistence[T, ID] {

  //shortcut for convenience
  type TQ = SlickBaseTable[T, ID]

  //Macro expansion value
  val tableQuery: TableQuery[_ <: TQ] /* = TableQuery[TQ] */

  //helper methods
  def byId(id: ID)(implicit session: Session): Query[TQ, TQ#TableElementType, Seq] = tableQuery.filter(_.id === id)

  def byId(idOpt: Option[ID])(implicit session: Session): Query[TQ, TQ#TableElementType, Seq] = {
    idOpt map {id => byId(id)} getOrElse {
      throw new IllegalArgumentException("ID option should not be None")
    }
  }

  protected def autoInc = tableQuery returning tableQuery.map(_.id)

  //base methods
  def exists(id: ID)(implicit session: Session): Boolean = byId(id).length.run > 0

  def get(id: ID)(implicit session: Session): Option[T] = byId(id).firstOption

  def list(startRow: Int = 0, pageSize: Int = -1)(implicit session: Session): Seq[T] = {
    //create query for retrieving of all entities
    var q = tableQuery.map(e => e)

    //if it needs to be started from certain row
    if (startRow > 0) {
      q = q.drop(startRow)
    }

    //if we need to get only certain number of rows
    if (pageSize >= 0) {
      q = q.take(pageSize)
    }

    //perform query
    q.list
  }

  def insert(entity: T)(implicit session: Session): ID = {
    autoInc.insert(entity)
  }

  def insertAll(entities: Seq[T])(implicit session: Session) {
    autoInc.insertAll(entities: _*)
  }

  def update(entity: T)(implicit session: Session) {
    byId(entity.id).update(entity)
  }

  def delete(id: ID)(implicit session: Session) {
    byId(id).delete
  }

  def count(implicit session: Session): Int = {
    tableQuery.length.run
  }

}