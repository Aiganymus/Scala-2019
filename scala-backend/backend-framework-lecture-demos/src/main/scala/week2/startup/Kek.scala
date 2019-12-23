package week2.startup

object Kek {

  val kekAccount = "Kek account"

  val halykAccount = 1234D

  val kaspiClient = KaspiClient() // Remove after migration to Halyk

  val halykClient = HalykBank()

  val fo = FinancialOperations(kaspiClient)

  val fo2 = FinancialOperations(halykClient)



  // Remove after migration
  def transferClientMoney(clientId: String, clientPhone: String, clientEmail: String, clientBankAccout: String, amount: Int): Unit = {

    val transferResult = kaspiClient.transferMoney(kekAccount, clientBankAccout, amount)

    println(s"Sent notification: $transferResult to $clientPhone")
    println(s"Sent notification: $transferResult to $clientEmail")

  }


  def transferClientMoney2(clientId: String, clientPhone: String, clientEmail: String, clientBankAccout: String, amount: Int): Unit = {

    val transferResult = halykClient.internalTransaction(halykAccount, clientBankAccout.toDouble, BigDecimal(amount))

    println(s"Sent notification: $transferResult to $clientPhone")
    println(s"Sent notification: $transferResult to $clientEmail")

  }

  def transferClientMoney3(clientId: String, clientPhone: String, clientEmail: String, clientBankAccout: String, amount: Int): Unit = {

    val transferResult = fo.cashOut(clientBankAccout, amount)

    println(s"Sent notification: $transferResult to $clientPhone")
    println(s"Sent notification: $transferResult to $clientEmail")

  }
}
