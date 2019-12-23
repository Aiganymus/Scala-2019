package week4

case class HttpClient(input: String)(implicit config: Config, num: Int) {
  def makeHttpRequest() = ???

  def getHeaders() = ???
}
