package week4

import scala.concurrent.Future

object Boot extends App with Conversions {


  // Option:
  // i) Some(value) - has some value
  // ii) None - no value
  val optNum1: Option[Int] = Some(9)

  val optNum2: Option[Int] = None


  def parseNumber(n: String): Option[Int] = {
    try {
      Some(n.toInt)
    } catch {
      case e: Throwable => None
    }
  }

  println(parseNumber("123"))
  println(parseNumber("123xp"))
  println(parseNumber("123xp909"))


  val listNum: List[String] = List("143535", "454", "449o4", "12", "piu4")

  // list map String => Option[Int]
  // Alt + Enter to see type
  val listNumOpt: List[Option[Int]] = listNum.map { x => parseNumber(x) }



  // implicit

  implicit val config: Config = Config(60, "qwrq9wer0qwer", "ads0f87as9df", "192.168.1.1:49854")
  implicit val config2: Config = Config(120, "qwrq9a6sd45fa6s8dfwer", "89a79dsfs0f87as9df", "192.168.1.1:49854")

  val config3: Config = Config(120, "qwrq9a6sd45fa6s8dfwer", "89a79dsfs0f87as9df", "192.168.1.1:49854")

  def connectToDb(config: Config): String = {
    s"postgre://config.dbConnetion"
  }


  // explicit use of parameter
  val httpClient = HttpClient("asdf")(config2, 10)

  val httpClient2 = HttpClient("asdf")(config3, 8)



  // implicit conversions


  val fahrenheit = Fahrenheit(52)

  // Bad practice
  println(isWarmWeather(fahrenheit))

  // ok
  println(isWarmWeather(fahrenheitToCelcius(Fahrenheit(80))))


  def isWarmWeather(celciusDegree: Celcius): Boolean = {
    celciusDegree.degree > 25 && celciusDegree.degree < 35
  }



  // Collections (immutable)
  // append
  val numbers: Seq[Int] = Seq(1, 4, 3, 2, 5, -6)

  val nums2 = numbers :+ 9

  println(nums2)

  val nums3 = numbers :+ 7 :+ 10 :+ 45


  // prepend

  val nums4 = 10 +: numbers

  println(nums4)


  // filter
  println(nums3.filter(x => x % 2 == 0))

  val isEven = (x: Int) => x % 2 == 0

  println(nums3.filter(isEven))



  // sortWith
  println(numbers.sortWith((a, b) => a > b))


  println(numbers.sortWith((a, b) => a < b))

  // find

  val find1: Option[Int] = numbers.find(x => x == 3)

  find1 match {
    case Some(value) => println(s"Found 3: $value")

    case None => println("3 not Found")
  }


  val find2 = numbers.find(isEven)

  find2 match {
    case Some(even) => println(s"Found: $even")

    case None => println("Could not find even")
  }


  // contains
  println(numbers.contains(9))

  println(numbers.isEmpty)
  println(numbers.nonEmpty)
  println(numbers.exists(x => x == 100 || x == 99))



  numbers.foreach(x => println(x))

  // _ = any
  numbers.foreach(println(_))

  numbers.foreach(println)


  // map


  println(numbers.map(x => x * 2 + 5))


  val l2: Seq[Celcius] = numbers.map(x => Celcius(x))
  println(l2)


  numbers
    .filter(isEven)
    .map(x => Fahrenheit(x))
    .sortWith((a, b) => a.degree < b.degree)
    .foreach(print)


  /**
    * Student st = new Student
    * st.setName("Ivan") // void
    * st.setAge(29) // void
    */





  // Future

  //  def add(x: Int, y: Int): Future[Int] = Future {
  //    x + y
  //  }



}
