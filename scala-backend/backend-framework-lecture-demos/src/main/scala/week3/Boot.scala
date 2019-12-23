package week3

import week3.model.{LinkedListNil, Node, Student}


object Boot extends App {


  val tuple2: (Int, String) = (12, "Ivan")

  println(tuple2._2)

  val tuple3 = (12, "Ivan", true, "12-08-1998")

  println(tuple3._4)

  // use case1: return statements

  def myFunction(x: Int): (Int, Int) = {
    (x * (x + 1) / 2, x / 5)
  }
  val (sumUntil, divisionResult) = myFunction(10)

  // use case2: Map

  val map: Map[Int, Int] = Map(1 -> 5, 2 -> 10) + (3 -> 15)




  // Higher-order functions
  // type: (Int, Int) => Int
  val sum: (Int, Int) => Int = (x: Int, y: Int) => x + y

  val division = (x: Int, y: Int) => x / y

  val calculateSum = (x: Int, y: Int) => x + y

  // input: operand1, operand2, operation
  // type: (Int, Int, (Int, Int) => Int) => Int
  val calculate: (Int, Int, (Int, Int) => Int) => Int = (x: Int, y: Int, operation: (Int, Int) => Int) => operation(x, y)

  println(calculate(2, 5, sum))
  println(calculate(10, 5, division))


  // Tour example
  def urlBuilder(ssl: Boolean, domainName: String): (String, String) => String = {
    val schema = if (ssl) "https://" else "http://"
    (endpoint: String, query: String) => s"$schema$domainName/$endpoint?$query"
  }

  val domainName = "www.example.com"
  def getURL: (String, String) => String = urlBuilder(ssl=true, domainName)
  // function (https || http) and domain
  val endpoint = "users"
  val query = "id=1"

  val url = getURL(endpoint, query) // https://www.example.com/users?id=1
  val url2 = getURL("dashboard", "name=Daulet") // https://www.example.com/dashboard?name=Daulet
  val url3 = getURL("history", "date=2019-09-14") // https://www.example.com/history?date=2019-09-14



  // Case class

  case class Book(author: String, ISBN: String)

  // no `new` keyword
  val book1 = Book("Voina i Mir", "1234")
  val book2 = Book("Voina i Mir", "1234")

  // equals implemented
  println(book1 == book2)

  // toString implemented
  println(book1)

  // Math.abs(10 - 25)


  // companion object
  object Book {

    def apply(author: String, ISBN: String): Book = new Book(author, ISBN)

    def apply(ISBN: String) = new Book("Gogol", ISBN)

    override def toString: String = "This is a book" // implement
  }

  val book3 = Book.apply("Voina i Mir", "1234")

  val book4 = Book("123456")
  println(book4)


  // Case class immutable => object parameter cannot be changed
  // no setters

  val book5: Book = book3.copy(ISBN = "091828341")

  println(book3)
  println(book5)


  // case class Prof() extends Student // Student is sealed


  // stacktrace

//  try {
//    8 / 0
//  } catch {
//    case e: Throwable => e.printStackTrace()
//  }


  val sumUntil5 = Recursion.sumUntil(5)
  println(sumUntil5)

  val list1 = Node(1, Node(2, Node(3, Node(4, LinkedListNil))))

//  list1.append(5)
  println(list1)

  println(list1.find(4))
  println(list1.find(10))


}
