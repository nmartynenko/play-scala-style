package com.aimprosoft.play.glossaries.listeners

trait Listener {
  def order: Int = Listener.LOWEST_PRECEDENCE
  def init(): Unit
}

object Listener {
  val HIGHEST_PRECEDENCE = Int.MinValue
  val LOWEST_PRECEDENCE = Int.MaxValue
}