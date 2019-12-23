package week2.startup

trait BankClient {

  def transfer(sourceAccount: String, destinationAccount: String, amount: Int): String

  def add(x: Int, y: Int): Int = x + y
}
