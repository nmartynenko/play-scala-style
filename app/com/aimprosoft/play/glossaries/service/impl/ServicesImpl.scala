package com.aimprosoft.play.glossaries.service.impl

import be.objectify.deadbolt.core.models.Role
import com.aimprosoft.play.glossaries.domain.PageResponse
import com.aimprosoft.play.glossaries.models.{User, Glossary}
import com.aimprosoft.play.glossaries.persistence.Persistence
import com.aimprosoft.play.glossaries.persistence.impl.{UserPersistence, GlossaryPersistence}
import com.aimprosoft.play.glossaries.service._
import org.mindrot.jbcrypt.BCrypt
import play.api.Play.current
import play.api.db.slick._
import scala.language.reflectiveCalls

trait SlickTransactional {

  def readOnly[T]: (Session => T) => T = DB.withSession

  def transactional[T]: (Session => T) => T = DB.withTransaction

}

trait BaseCrudServiceImpl[T <: {val id: Option[ID]}, ID] extends BaseCrudService[T, ID]
  with SlickTransactional{

  protected def persistence: Persistence[T, ID]

  def getCurrentPage(startRow: Int = 0, pageSize: Int = -1): PageResponse[T] = readOnly {
    implicit session: Session => {
      //list of entities
      val content = persistence.list(startRow, pageSize)

      //number of all elements in DB
      val te = {
        //quick check for not working with DB
        if (startRow <= 0 && pageSize < 0)
          content.size
        //check in DB otherwise
        else
          persistence.count
      }

      //adjust start row
      val sr = startRow max 0
      //adjust page size
      val ps = if (pageSize > 0) pageSize else te

      new PageResponse(content, sr, ps, te)
    }
  }


  def exists(id: ID): Boolean = readOnly {
    implicit session: Session =>
      persistence.exists(id)
  }

  def count: Int = readOnly {
    implicit session: Session =>
      persistence.count
  }

  def getFirst: Option[T] = readOnly {
    implicit session: Session =>
      persistence.list(0, 1).headOption
  }

  def getById(id: ID): Option[T] = readOnly {
    implicit session: Session =>
      persistence.get(id)
  }

  def add(entity: T): Unit = transactional {
    implicit session: Session =>
      persistence.insert(entity)
  }

  def update(entity: T): Unit = transactional {
    implicit session: Session =>
      persistence.update(entity)
  }

  def remove(entity: T): Unit = entity.id foreach { id =>
    removeById(id)
  }

  def removeById(id: ID): Unit = transactional {
    implicit session: Session =>
      persistence.delete(id)
  }

}

class GlossaryServiceImpl extends GlossaryService
  with BaseCrudServiceImpl[Glossary, Long] {
  protected def persistence = GlossaryPersistence
}

class UserServiceImpl extends UserService
  with BaseCrudServiceImpl[User, Long] {

  protected def persistence = UserPersistence

  override def add(user: User): Unit = {
    //hash plain text password
    val hashedPassword = BCrypt.hashpw(user.password, BCrypt.gensalt())

    super.add(user.copy(password = hashedPassword))
  }

  def getByEmail(email: String): Option[User] = transactional {
    implicit session: Session =>
      persistence.findByEmail(email)
  }

  def countByRole(role: Role): Int = readOnly {
    implicit session: Session =>
      persistence.countByRole(role.getName)
  }
}