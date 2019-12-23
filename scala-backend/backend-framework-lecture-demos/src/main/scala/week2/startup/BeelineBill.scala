package week2.startup

case class BeelineBill() extends PhoneBill with BankClient {
  override def transfer(sourceAccount: String, destinationAccount: String, amount: Int): String = {
    "payed to Beeline"
  }
}
