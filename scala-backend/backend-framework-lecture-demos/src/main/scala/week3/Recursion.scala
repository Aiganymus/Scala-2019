package week3

object Recursion {



  def fibonacci(n: Int): Int = {
    if (n == 0 || n == 1) 1
    else fibonacci(n - 1) + fibonacci(n - 2)
  }

  // 0 + 1 + 2 + 3 + 4 + 5 + ... + n

  def sumUntil(n: Int): Int = {

    // internal (local) method
    def sum(n: Int, acc: Int): Int = {
      if (n == 0) acc
      else sum(n - 1, acc + n)
    }

    sum(n, 0)
  }



}
