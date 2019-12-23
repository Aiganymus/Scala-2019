package week2.startup

import scala.util.Random

case class HalykBank() extends BankClient {


  def internalTransaction(source: Double, dest: Double, amount: BigDecimal): Boolean = {
    Random.nextBoolean()
  }

  override def transfer(sourceAccount: String, destinationAccount: String, amount: Int): String = {
    // TODO: account validation isDouble

    internalTransaction(sourceAccount.toDouble, destinationAccount.toDouble, BigDecimal(amount))

    "FIXME"
  }

}
