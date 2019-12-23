package week5

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success, Try}
import scala.concurrent.duration._

object Boot extends App {

  // =============== Options ==================
  def readInt(str: String): Option[Int] =
    if(str matches "\\d+") Some(str.toInt) else None

  val list = List(1, 2, 6, 190, 2)

  val find1: Option[Int] = list.find(x => x == 190)   // Some or None

  find1 match {
    case Some(number) => println(s"Number: $number is found")
    case None => println("Could not find the number")
  }

  list.find(x => x == 1000) match {
    case Some(number) => println(s"Number: $number is found")
    case None => println("Could not find the number")
  }

  // example 3 input String values
  // goal: return sum of 3 input values (if all values are numbers)

  val input1: Option[Int] = readInt("56")
  val input2: Option[Int] = readInt("123")
  val input3: Option[Int] = readInt("10")

  val opt: Option[Int] = None

  // if (opt.isDefined) opt.get...   // if (opt != null) opt....

  val result: Option[Int] = input1 match {
    case Some(num1) => input2 match {
      case Some(num2) => input3 match {
        case Some(num3) => Some(num1 + num2 + num3)
        case None => None // Some(num1 + num2)
      }
      case None => None
    }
    case None => None
  }

  println(result)

  // for comprehension
  val result2: Option[Int] = for {
    num1 <- input1
    num2 <- input2
    num3 <- input3
  } yield num1 + num2 + num3

  println(result2)

  // get or else

  val opt2: Option[Int] = None
  println(opt2.getOrElse(0))

  // Bonus: map, filter, foreach .... work for Monads too!
  println(result2.map(x => x * 2))

  // FUTURE
  def sum(x: Int, y: Int) = Future {
    println("sum called")
    x + y
  }

  def subtract(x: Int, y: Int) = Future {
    println("subtract called")
    x - y
  }

  def multiply(x: Int, y: Int) = Future {
    println("multiply called")
    x * y
  }

  def divide(x: Int, y: Int): Future[Int] = Future {
    println("division called")
    x / y
  }

  println(sum(10, 30))

  sum(10, 30).onComplete {
    case Success(value) => println(s"Success: $value")
    case Failure(fail) => println("Failed")
  }


  // goal: Future[Int]
  val resultFuture: Future[Future[Future[Future[Int]]]] = sum(20, 34).map { res1 =>
    multiply(res1, 10).map { res2 => // Int => Future[Future[Int]
      subtract(res2, 2).map { res3 =>     // Int => Future[Int]
        divide(res3, 2)
      }
    }
  }


  // flatten
  val listOfLists: List[List[Int]] = List(List(1, 9, 0), List(9, 0, 4), List(1, 5, 3), List(0, 2, 3, 4))

  val flatLists: List[Int] = listOfLists.flatten

  println(flatLists)
  println(List(List(List(1, 2, 3, 4), List(1, 0, 2, 3)), List(List(1, 9, 0, 3), List(1, 2, 3, 4))).flatten)
  println(List(List(List(1, 2, 3, 4), List(1, 0, 2, 3)), List(List(1, 9, 0, 3), List(1, 2, 3, 4))).flatten.flatten)


  // goal: Future[Int]
  val resultFuture2: Future[Int] = sum(20, 34).map { res1 =>
    multiply(res1, 10).map { res2 => // Int => Future[Future[Int]
      subtract(res2, 2).map { res3 =>     // Int => Future[Int]
        divide(res3, 2)
      }.flatten
    }.flatten
  }.flatten

  // goal: Future[Int]
  // note: map and then flatten

  val resultFuture3: Future[Int] = sum(20, 34).flatMap { res1 =>       // 54
    multiply(res1, 10).flatMap { res2 => // Int => Future[Future[Int]  // 540
      subtract(res2, 2).flatMap { res3 =>     // Int => Future[Int]    // 538
        divide(res3, 2)                                                // 269
      }
    }
  }

  // BLOCKING
  // ANTI-PATTERN
  // val result3 = Await.result(resultFuture3, 5.seconds)

  // executed async
  resultFuture3.onComplete {
    case Success(value) => println(s"Future result: $value")
    case Failure(fail) => println(s"Future failed: ${fail.getMessage}")
  }

  Thread.sleep(1000)

  println("----------- example 4 -------------")

  sum(10, 30).flatMap { res1 =>       // 54
    divide(res1, 4).flatMap { res2 => // Int => Future[Future[Int]  // 540
      divide(res2, 0).flatMap { res3 =>     // Int => Future[Int]    // 538
        multiply(res3, 8)                                                // 269
      }
    }
  }.onComplete {
    case Success(value) => println(s"Future result: $value")
    case Failure(fail) => println(s"Future failed: ${fail.getMessage}")
  }

  Thread.sleep(1000)

  // TRY
  val try1: Try[Int] = Try(190 / 0)

  try {
    val value = 190 / 0
    println(s"Success: $value")
  } catch {
    case e: Throwable => println(s"Fail: ${e.getMessage}")
  }

  try1 match {
    case Success(value) => println(s"Success: $value")
    case Failure(failure) => println(s"Fail: ${failure.getMessage}")
  }

  // Either
  val either1: Either[String, Int] = Right(90)
  val either2: Either[String, Int] = Left("Wrong number") // instead of None in Option

  val resultEither: Either[String, Int] = either1.flatMap(res1 => either2.map(res2 => res1 + res2))

  resultEither match {
    case Right(value) => println(s"Either right: $value")
    case Left(error) => println(s"Either left: $error")
  }



  // Converting monads

  // option => either
  val either3: Either[String, Int] = opt2.toRight("Wrong number")

  // either => option
  val eitherToOpt = resultEither.toOption

  // try => option
  val tryToOption = try1.toOption

  // option => try N/A

  println(resultEither.map(x => x + 4).toOption.getOrElse(0))

}
