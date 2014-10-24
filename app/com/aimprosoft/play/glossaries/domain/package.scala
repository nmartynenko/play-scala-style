package com.aimprosoft.play.glossaries

import play.api.libs.json._
import play.api.libs.functional.syntax._

package object domain {

  implicit def pageFormat[T: Format]: Format[PageResponse[T]] = (
    (__ \ "content").format[Seq[T]] and
        (__ \ "startRow").format[Int] and
        (__ \ "pageSize").format[Int] and
        (__ \ "totalElements").format[Int]
    )(PageResponse.apply[T], unlift(PageResponse.unapply[T]))

}
