package lab3.Shapes

object Driver extends App {
  Draw(Circle(4))
  Draw(Rectangle(4, 2))
  Draw(Square(9))
}

object Draw {
  def apply(shape: Shape): Unit = {
    shape match {
      case Circle(side) => println(s"This is a circle with such radius: ${side}")
      case Square(radius) => println(s"This is a square with such side: ${radius}")
      case Rectangle(length, width) => println(s"This is a rectangle with such length and width respectively: ${length}, ${width}")
      case _ => println("Sorry. Can not recognize the shape type.")
    }
  }
}

sealed trait Shape {
  def getSides(): Int

  def getPerimeter(): Double

  def getArea(): Double
}

trait Rectangular extends Shape {
  override def getSides(): Int = 4
}

case class Square(side: Double) extends Rectangular {
  override def getPerimeter(): Double = side * 4

  override def getArea(): Double = side * side
}

case class Rectangle(length: Double, width: Double) extends Rectangular {
  override def getPerimeter(): Double = 2 * (length * width)

  override def getArea(): Double = length * width
}

case class Circle(radius: Double) extends Shape {
  override def getSides(): Int = 0

  override def getPerimeter(): Double = 2 * math.Pi * radius

  override def getArea(): Double = math.Pi * math.pow(radius, 2)
}

