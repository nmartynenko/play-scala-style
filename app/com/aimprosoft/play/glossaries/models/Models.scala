package com.aimprosoft.play.glossaries.models

//Models
case class Glossary(id: Option[Long] = None, name: String, description: Option[String] = None)

case class User(id: Option[Long] = None, email: String, password: String, name: String, role: String)