package week3.model


// Algebraic Data Type: Sum type
sealed trait LinkedList {

//  def append(num: Int): LinkedList = this match {
//    case node: Node =>
//      node.append(num)
//    case LinkedListNil =>
//      Node(num, LinkedListNil)
//  }

  def find(elem: Int): Option[LinkedList] = this match {
    case node: Node =>
      if (elem == node.value) Some(node) else node.tail.find(elem)
    case LinkedListNil => None
  }
}

case class Node(value: Int, tail: LinkedList) extends LinkedList {

}
case object LinkedListNil extends LinkedList


// head => tail (LinkedList)
