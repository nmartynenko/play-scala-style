package com.aimprosoft.play.glossaries

import com.aimprosoft.play.glossaries.domain._
import play.api.libs.json.Json

package object models {

  object JsonFormatImplicits {
    //Glossary
    implicit lazy val glossaryFormat = Json.format[Glossary]
    implicit lazy val glossaryPageFormat = pageFormat[Glossary]
    //User
    implicit lazy val userFormat = Json.format[User]
    implicit lazy val userPageFormat = pageFormat[User]
  }

}
