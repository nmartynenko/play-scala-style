package com.aimprosoft.play.glossaries.listeners

import com.aimprosoft.play.glossaries.models.Glossary
import com.aimprosoft.play.glossaries.service.GlossaryService
import play.api.Logger
import scala.util.Random

object CreateGlossaryDataListener extends Listener {
  
  private def tear(separator: String)(string: String) = {
    string
      //remove lines breaks and big spaces
      .replaceAll("(\n|\\ {2,})", " ")
      //split it wit
      .split(separator)
  }
  
  private val TITLES = tear(",") {
    """Lorem ipsum dolor sit amet, consectetur adipisicing elit,
    sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."""
  }

  private val DESCRIPTIONS = tear("[\\.\\?]") {
    """Sed ut perspiciatis unde omnis iste natus
    error sit voluptatemaccusantium doloremque laudantium, totam rem aperiam,
    eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae
    vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas
    sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores
    eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est,
    qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit,
    sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam
    aliquam quaerat voluptatem. Ut enim ad minima veniam, quis nostrum exercitationem
    ullam corporis suscipit laboriosam, nisi ut aliquid ex ea commodi consequatur?
    Quis autem vel eum iure reprehenderit qui in ea voluptate velit esse quam nihil
    molestiae consequatur, vel illum qui dolorem eum fugiat quo voluptas nulla pariatur?"""
  }

  def init(): Unit = {
    //it can't be too much glossaries
    Logger.info("Start adding sample glossaries")

    val random = new Random()

    for (i <- 0 until TITLES.length) {

      val descIndex = random.nextInt(DESCRIPTIONS.length)

      val glossary = new Glossary(
        name = TITLES(i),
        description = Some(DESCRIPTIONS(descIndex))
      )

      GlossaryService.add(glossary)
    }

    Logger.info("End adding sample glossaries")
  }
}
