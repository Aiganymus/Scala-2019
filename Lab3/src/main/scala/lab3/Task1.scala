package lab3

sealed trait Shape {
  def sides: Int
  def perimeter: Double
  def area: Double
}

case class Circle(radius: Int) extends Shape {
  override def sides: Int = 0

  override def perimeter: Double = 2 * radius * math.Pi

  override def area: Double = 2 * math.Pi * math.pow(radius, 2)
}

trait Rectangular extends Shape {
  var height: Int

  var width: Int

  override def sides = 4

  override def perimeter: Double = 2.0 * (height + width)

  override def area: Double = height * width
}

case class Rectangle(a: Int, b: Int) extends Rectangular {
  override var height: Int = a

  override var width: Int = b
}

case class Square(a: Int) extends Rectangular {
  override var height: Int = a

  override var width: Int = a
}

object Draw {
  def apply(shape: Shape): String = shape match {
    case r: Rectangle => s"A rectangle of width ${r.width}cm and height ${r.height}cm"
    case s: Square => s"A square of side ${s.width}cm"
    case c: Circle => s"A circle of radius ${c.radius}cm"
  }
}

object Task1 extends App {
  println(Draw(Circle(10)))

  println(Draw(Rectangle(3, 4)))

  println(Draw(Square(4)))
}

