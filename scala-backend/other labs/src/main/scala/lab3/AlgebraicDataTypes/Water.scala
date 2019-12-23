package lab3.AlgebraicDataTypes

sealed trait Source

case class Well(message: String) extends Source
case class Spring(message: String) extends Source
case class Tap(message: String) extends Source // sum type pattern


case class BottledWater(size: Int, source: Source, carbonated: Boolean) // p-type