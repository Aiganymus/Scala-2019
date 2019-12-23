package lab3.TailRecursion

// Linked List
sealed trait IntList {
  def length(): Int = {
    def nestedLength(node: IntList, n: Int): Int = node match {
      case Node(_, tail) => nestedLength(tail, n + 1)
      case End => n
    }

    nestedLength(this, 0)
  }

  def product(): Int = {
    def nestedProduct(node: IntList, n: Int): Int = node match {
      case Node(head, tail) => nestedProduct(tail, n * head)
      case End => n
    }

    nestedProduct(this, 1)
  }

  def double(): IntList = {
    def nestedDouble(node: IntList): IntList = node match {
      case Node(head, tail) => Node(2 * head, nestedDouble(tail))
      case End => End
    }

    nestedDouble(this)
  }

  def map(n: Int => Int): IntList = {
    def nestedMap(node: IntList): IntList = node match {
      case Node(head, tail) => Node(n(head), nestedMap(tail))
      case End => End
    }

    nestedMap(this)
  }
}

case object End extends IntList
case class Node(head: Int, tail: IntList) extends IntList

object DriverIntList extends App {
  val intList = Node(1, Node(2, Node(3, Node(4, End))))

  // IntList length method checking
  println(intList.length())
  println(intList.tail.length())
  println(End.length())

  // IntList product method checking
  println(intList.product())
  println(intList.tail.product())
  println(End.product())

  // IntList double method checking
  println(intList.double())
  println(intList.tail.double())
  println(End.double)

  // IntList map method checking
  println(intList.map(x => x * 3))
  println(intList.map(x => 5 - x))
  println(intList.tail.map(x => x * 3))
  println(intList.tail.map(x => 5 - x))
  println(End.map(x => x * 3))


}

