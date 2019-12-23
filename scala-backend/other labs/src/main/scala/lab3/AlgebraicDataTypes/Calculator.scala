package lab3.AlgebraicDataTypes

sealed trait Calculator

case class Success(result: Int) extends Calculator
case class Failure(result: String) extends Calculator