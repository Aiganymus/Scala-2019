package week2.startup

import scala.util.Random

case class KaspiClient() extends BankClient {

  def transferMoney(sourceAcc: String, destinationAccount: String, amount: Int): String = {
    if (Random.nextBoolean()) "Successful Kaspi Transaction"
    else "Failed to transfer money"
  }

  override def transfer(sourceAccount: String, destinationAccount: String, amount: Int): String = {
    transferMoney(sourceAccount, destinationAccount, amount)
  }

}
