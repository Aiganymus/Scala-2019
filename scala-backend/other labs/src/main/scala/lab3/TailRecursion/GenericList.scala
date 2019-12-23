package lab3.TailRecursion

sealed trait GenericList[A] {
  def length(): Int = {
    def nestedLength(node: GenericList[A], n: Int): Int = node match {
      case GenericNode(_, tail) => nestedLength(tail, n + 1)
      case GenericEnd() => n
    }

    nestedLength(this, 0)
  }

  def map[B](n: A => B): GenericList[B] = {
    def nestedMap(node: GenericList[A]): GenericList[B] = node match {
      case GenericNode(head, tail) => GenericNode(n(head), nestedMap(tail))
      case GenericEnd() => GenericEnd()
    }

    nestedMap(this)
  }
}

case class GenericEnd[A]() extends GenericList[A]
case class GenericNode[A](head: A, tail: GenericList[A]) extends GenericList[A]

object DriverGeneticList extends App {
  val genericList: GenericList[Int] = GenericNode(1, GenericNode(2, GenericNode(3, GenericEnd())))
  val genericList1: GenericList[String] = GenericNode("one", GenericNode("two", GenericNode("three", GenericEnd())))

  // GenericList[Int] length method checking
  println(genericList.length())
  println(genericList.map(x => x * 5))
  println(genericList.map(x => x.toString))

  // GenericList[String] length method checking
  println(genericList1.length())
  println(genericList1.map(x => concatWithAsterisk(x)))

  def concatWithAsterisk(s: String): String = {
    s.concat("*")
  }



}
