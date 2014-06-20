package com.aimprosoft.play.glossaries.persistence

import scala.slick.jdbc.JdbcBackend
import scala.language.reflectiveCalls

//abstract persistence trait
trait Persistence[T <: {def id: Option[ID]}, ID] {

  def exists(id: ID)(implicit session: JdbcBackend#Session): Boolean

  def get(id: ID)(implicit session: JdbcBackend#Session): Option[T]

  def list(startRow: Int = 0, pageSize: Int = -1)(implicit session: JdbcBackend#Session): Seq[T]

  def insert(entity: T)(implicit session: JdbcBackend#Session): ID

  def insertAll(entities: Seq[T])(implicit session: JdbcBackend#Session): Unit

  def update(entity: T)(implicit session: JdbcBackend#Session): Unit

  def delete(id: ID)(implicit session: JdbcBackend#Session): Unit

  def count(implicit session: JdbcBackend#Session): Int
}

