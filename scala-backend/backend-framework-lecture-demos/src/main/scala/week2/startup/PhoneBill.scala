package week2.startup

trait PhoneBill {

  // who extends PhoneBill must extend BankClient
  this: BankClient =>

  def payForPhone(phoneNumber: String, amount: Int): String = {
    transfer("3456", "2345298", amount)
  }

}
