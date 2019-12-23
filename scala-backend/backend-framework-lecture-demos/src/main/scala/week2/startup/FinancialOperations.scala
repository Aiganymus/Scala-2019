package week2.startup

case class FinancialOperations(bankClient: BankClient) {
  val sourceAcc = "1234" // KEK account
  def cashOut(clientAccount: String, amount: Int) = bankClient.transfer(sourceAcc, clientAccount, amount)
}
