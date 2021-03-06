package com.aimprosoft.play.glossaries.test

import com.aimprosoft.play.glossaries.domain.PageResponse
import com.aimprosoft.play.glossaries.models._
import com.aimprosoft.play.glossaries.security.GlossaryUserSubject
import com.aimprosoft.play.glossaries.service.GlossaryService
import org.junit.runner._
import org.specs2.execute.{AsResult, Result}
import org.specs2.mutable._
import org.specs2.runner._
import play.api.cache.Cache
import play.api.http.HeaderNames
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.Security
import play.api.test.Helpers._
import play.api.test._

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */
@RunWith(classOf[JUnitRunner])
class ApplicationSpec extends Specification {

  import com.aimprosoft.play.glossaries.models.JsonFormatImplicits._

  "Application" should {

    "redirect from / into index.html" in new WithApplication {
      var rootRoute = route(
        FakeRequest(GET, "/")
      ).get

      status(rootRoute) must equalTo(SEE_OTHER)
      headers(rootRoute) must havePair(HeaderNames.LOCATION -> "/index.html")
    }

    "send 404 on a bad request" in new WithApplication {
      route(FakeRequest(GET, "/boum")) must beNone
    }

  }

  "Application for unauthenticated users" should {

    "show login page" in new WithApplication{
      val loginUrl = route(
        FakeRequest(GET, "/login.html")
      ).get

      status(loginUrl) must equalTo(OK)
      contentType(loginUrl) must beSome.which(_ == "text/html")

      val content: String = contentAsString(loginUrl)

      Seq("form", "j_username", "j_password", "submit") foreach {
        content must contain(_)
      }
    }

    "do not show all other pages/routes" in new WithApplication {
      Seq(
        ("/index.html", GET),
        ("/glossaries", GET),
        ("/glossaries/1", GET),
        ("/glossaries", PUT),
        ("/glossaries", POST),
        ("/glossaries/1", DELETE),
        ("/logout", GET)
      ) foreach {

        case (url, method) =>
          val forbiddenRoute = route(
            FakeRequest(method, url)
              //for POST/PUT requests, for others it's ignored either way
              .withJsonBody(JsObject(List()))
          ).get

          status(forbiddenRoute) must equalTo(SEE_OTHER)
          headers(forbiddenRoute) must havePair(HeaderNames.LOCATION -> "/login.html")
      }
    }

  }

  "Application for authenticated users" should {
    val adminUsername:String = "fakeAdmin"
    val adminSubject: GlossaryUserSubject = new GlossaryUserSubject(
      User(Some(1), "admin", "", "admin", "admin")
    )
    val userUsername: String = "fakeUser"
    val userSubject: GlossaryUserSubject = new GlossaryUserSubject(
      User(Some(2), "user", "", "user", "user")
    )
    val users: Seq[(String, GlossaryUserSubject)] = Seq(
      (adminUsername, adminSubject),
      (userUsername, userSubject)
    )

    def forAllUsers(f: => (String, GlossaryUserSubject) => Unit): Unit = {
      users foreach {
        case (username, subject) => f(username, subject)
      }
    }

    class WithAuthApplication extends WithApplication {
      override def around[T: AsResult](t: => T): Result = {
        //authenticate all users
        forAllUsers {Cache.set(_, _)}
        //implement block
        super.around(t)
      }
    } 

    "show the index page for all roles" in new WithAuthApplication {
      forAllUsers {
        (username, subject) =>
          //make sure that cache has actual data
          Cache.get(username) must beSome(subject)

          val home = route(
            FakeRequest(GET, "/index.html")
              .withSession(Security.username -> username)
          ).get

          status(home) must equalTo(OK)
          contentType(home) must beSome.which(_ == "text/html")
          contentAsString(home) must contain("script")
      }
    }

    "return all glossaries for all users" in new WithAuthApplication {
      forAllUsers {
        (username, subject) =>
          //make sure that cache has actual data
          Cache.get(username) must beSome(subject)

          val glossariesPage = route(
            FakeRequest(GET, "/glossaries")
              .withSession(Security.username -> username)
          ).get

          status(glossariesPage) must equalTo(OK)
          contentType(glossariesPage) must beSome.which(_ == "application/json")

          val response = contentAsJson(glossariesPage).
            as[PageResponse[Glossary]]
          //there is no pagination
          response.content must haveLength(response.totalElements)
      }
    }

    "return particular glossary for all users" in new WithAuthApplication {
      forAllUsers {
        (username, subject) =>
          //make sure that cache has actual data
          Cache.get(username) must beSome(subject)

          val id = 1

          val glossariesPage = route(
            FakeRequest(GET, s"/glossaries/$id")
              .withSession(Security.username -> username)
          ).get

          status(glossariesPage) must equalTo(OK)
          contentType(glossariesPage) must beSome.which(_ == "application/json")

          val response = contentAsJson(glossariesPage).as[Glossary]

          response.id must beSome.which(_ == id)
      }
    }

    "reject removal of particular glossary for regular users" in new WithAuthApplication {
      //make sure that cache has actual data
      Cache.get(userUsername) must beSome(userSubject)

      val id: Long = 1

      //this glossary must be present in DB
      GlossaryService.exists(id) must beTrue

      val glossariesPage = route(
        FakeRequest(DELETE, s"/glossaries/$id")
          .withSession(Security.username -> userUsername)
      ).get

      status(glossariesPage) must equalTo(FORBIDDEN)

      //this glossary must be still present in DB
      GlossaryService.exists(id) must beTrue
    }

    "allow removal of particular glossary for admin users" in new WithAuthApplication {
      //make sure that cache has actual data
      Cache.get(adminUsername) must beSome(adminSubject)

      val id: Long = 2

      //this glossary must be present in DB
      GlossaryService.exists(id) must beTrue

      val glossariesPage = route(
        FakeRequest(DELETE, s"/glossaries/$id")
          .withSession(Security.username -> adminUsername)
      ).get

      status(glossariesPage) must equalTo(OK)

      //this glossary must be not present in DB
      GlossaryService.exists(id) must beFalse
    }

    "reject adding of particular glossary for regular users" in new WithAuthApplication {
      //make sure that cache has actual data
      Cache.get(userUsername) must beSome(userSubject)

      //initial number of glossaries
      val initialCount = GlossaryService.count

      val glossary = Glossary(name = "Try to add")

      val glossariesPage = route(
        FakeRequest(PUT, "/glossaries")
          .withSession(Security.username -> userUsername)
          .withJsonBody(Json.toJson(glossary))
      ).get

      status(glossariesPage) must equalTo(FORBIDDEN)

      //number of glossaries must remain the same
      GlossaryService.count must equalTo(initialCount)
    }

    "allow adding of particular glossary for admin users with no glossary's ID" in new WithAuthApplication {
      //make sure that cache has actual data
      Cache.get(adminUsername) must beSome(adminSubject)

      //initial number of glossaries
      val initialCount = GlossaryService.count

      val glossary = Glossary(name = "Try to add")

      val glossariesPage = route(
        FakeRequest(PUT, "/glossaries")
          .withSession(Security.username -> adminUsername)
          .withJsonBody(Json.toJson(glossary))
      ).get

      status(glossariesPage) must equalTo(OK)

      //number of glossaries must be increase by one
      GlossaryService.count must equalTo(initialCount + 1)
    }

    "allow adding of particular glossary for admin users with no predefined ID" in new WithAuthApplication {
      //make sure that cache has actual data
      Cache.get(adminUsername) must beSome(adminSubject)

      //initial number of glossaries
      val initialCount = GlossaryService.count

      val id = 100500

      val glossary = Glossary(id = Some(id), name = "Try to add")

      //this glossary must not be present in DB
      GlossaryService.exists(id) must beFalse

      val glossariesPage = route(
        FakeRequest(PUT, "/glossaries")
          .withSession(Security.username -> adminUsername)
          .withJsonBody(Json.toJson(glossary))
      ).get

      status(glossariesPage) must equalTo(OK)

      //this glossary must not be present in DB
      GlossaryService.exists(id) must beFalse

      //number of glossaries must be increase by one
      GlossaryService.count must equalTo(initialCount + 1)
    }

    "reject updating of particular glossary for regular users" in new WithAuthApplication {
      //make sure that cache has actual data
      Cache.get(userUsername) must beSome(userSubject)

      //particular glossary's id
      val id = 1

      //this glossary must be present in DB
      GlossaryService.exists(id) must beTrue

      //initial number of glossaries
      val dbGlossary = GlossaryService.getById(id).get

      val newGlossary = dbGlossary.copy(name = "Try to update")

      //this should be different glossaries
      newGlossary mustNotEqual dbGlossary

      val glossariesPage = route(
        FakeRequest(POST, "/glossaries")
          .withSession(Security.username -> userUsername)
          .withJsonBody(Json.toJson(newGlossary))
      ).get

      status(glossariesPage) must equalTo(FORBIDDEN)

      //stored glossary must remain the same
      GlossaryService.getById(id).get must equalTo(dbGlossary)
    }

    "allow updating of particular glossary for admin users with valid id" in new WithAuthApplication {
      //make sure that cache has actual data
      Cache.get(adminUsername) must beSome(adminSubject)

      //particular glossary's id
      val id = 3

      //this glossary must be present in DB
      GlossaryService.exists(id) must beTrue

      //initial number of glossaries
      val dbGlossary = GlossaryService.getById(id).get

      val newGlossary = dbGlossary.copy(name = "Try to update")

      //this should be different glossaries
      newGlossary mustNotEqual dbGlossary

      val glossariesPage = route(
        FakeRequest(POST, "/glossaries")
          .withSession(Security.username -> adminUsername)
          .withJsonBody(Json.toJson(newGlossary))
      ).get

      status(glossariesPage) must equalTo(OK)

      //stored glossary must be updated
      GlossaryService.getById(id).get must equalTo(newGlossary)
    }

    "reject updating of particular glossary for admin users with invalid id" in new WithAuthApplication {
      //make sure that cache has actual data
      Cache.get(adminUsername) must beSome(adminSubject)

      //particular glossary's id
      val id = 100500

      //this glossary must be present in DB
      GlossaryService.exists(id) must beFalse

      val dbCount = GlossaryService.count

      //initial number of glossaries
      val newGlossary = Glossary(id = Some(id), name = "Try to update")

      val glossariesPage = route(
        FakeRequest(POST, "/glossaries")
          .withSession(Security.username -> adminUsername)
          .withJsonBody(Json.toJson(newGlossary))
      ).get

      status(glossariesPage) must equalTo(OK)

      //glossary must be not saved
      GlossaryService.getById(id) must beNone

      //number of elements must remain the same
      GlossaryService.count must beEqualTo(dbCount)
    }

    "logout page should redirect to login page" in new WithAuthApplication {
      forAllUsers {
        (username, subject) =>
          //make sure that cache has actual data
          Cache.get(username) must beSome(subject)

          val logoutUrl = route(
            FakeRequest(GET, "/logout")
              .withSession(Security.username -> username)
          ).get

          status(logoutUrl) must equalTo(SEE_OTHER)
          headers(logoutUrl) must havePair(HeaderNames.LOCATION -> "/login.html")
      }
    }
  }

}
