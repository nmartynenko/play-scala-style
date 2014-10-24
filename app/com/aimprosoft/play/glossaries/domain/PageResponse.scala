package com.aimprosoft.play.glossaries.domain

//domain generic class for pageable requests
case class PageResponse[T](content: Seq[T],
                      startRow: Int = 0,
                      pageSize: Int = -1,
                      totalElements: Int)