package com.aimprosoft.play.glossaries.listeners

trait Listener {
  def order: Int = Int.MaxValue
  def init(): Unit
}
