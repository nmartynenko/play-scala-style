package com.aimprosoft.play.glossaries.service

import be.objectify.deadbolt.core.models.Role
import com.aimprosoft.play.glossaries.domain.PageResponse
import com.aimprosoft.play.glossaries.models.{User, Glossary}
import com.aimprosoft.play.glossaries.service.impl.{UserServiceImpl, GlossaryServiceImpl}
import scala.language.reflectiveCalls

trait BaseCrudService[T <: {def id: Option[ID]}, ID] {

  def getCurrentPage(startRow: Int = 0, pageSize: Int = -1): PageResponse[T]

  def exists(id: ID): Boolean

  def count: Int

  def getFirst: Option[T]

  def getById(id: ID): Option[T]

  def add(entity: T): Unit

  def addAll(entities: T*): Unit

  def update(entity: T): Unit

  def remove(entity: T): Boolean

  def removeById(id: ID): Boolean
}

trait GlossaryService extends BaseCrudService[Glossary, Long]

trait UserService extends BaseCrudService[User, Long]{

  def getByEmail(username: String): Option[User]

  def countByRole(role: Role): Int

}

object GlossaryService extends GlossaryServiceImpl

object UserService extends UserServiceImpl

