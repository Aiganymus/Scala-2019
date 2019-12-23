package week3.model

sealed trait Student

case class Undergrad() extends Student
case class Grad() extends Student