package week2

import week2.startup.Kek

// object singleton
object HelloWorld extends App {
  val divisor = 0

//  println(180 / divisor) // Runtime exception

  1 + 1

  println("KBTU is " + "the best")



  val number: Int = 5 // value //immutable

  // number = 4 // ERROR


  var varNumber = 8

  varNumber = 80

  val str = "Hello"

  var strVariable = "This is a string"

  // strVariable = 80 // Error: wrong type

  println({
    val part1 = "Hello "
    val part2 = "KBTU"
    part1 + part2
  }) // blocks are for more than 1 code of line



  // f(x, y) = x + y
  // (Int, Int) => Int: type
  // (x: Int, y: Int) => x + y : implementation
  val add: (Int, Int) => Int = (x: Int, y: Int) => x + y
  println(s"Function add: ${add(5, 14)}")

  val meaningOfEverything = () => 42

  println(meaningOfEverything())

  def addMethod(x: Int, y: Int): Int = {
    x + y
  }


  // multiple parameter list
  def addThenMultiply(x: Int, y: Int)(multiplier: Int): Int = {
    multiplier * (x + y)
  }



  val table1: Table = new Table(4, "IKEA")

  // how many strings are created?
  // String is an immutable Data Structure
  val str1 = "my " + "name " + "is " + "Daulet"

  println(s"My table has ${table1.getLegs} legs")

  val table2 = new Table(4, "IKEA")

  val table3 = table1

  // checked by reference
  println(s"table1 and table2 are equal: ${table1 == table2}")
  println(s"table1 and table3 are equal: ${table1 == table3}")

  // toString
  println(table1)

  val bottle1 = Bottle("Nestle")
  val bottle2 = Bottle("Nestle")

  // checked by value
  println(s"bottle1 and bottle2: ${bottle1 == bottle2}")

  // toString
  println(bottle1)

  // x ^ x - 5x + 6 = 0
  // x = x + 10 => nonsense
  // y = x + 10 // OK
  // (x - 3)(x - 2) = 0




  Kek.transferClientMoney("12310", "87077777777", "daulet@gmail.com", "kaspi-91234", 10000)


  Kek.transferClientMoney2("12310", "87077777777", "daulet@gmail.com", "91234", 10000)



}
