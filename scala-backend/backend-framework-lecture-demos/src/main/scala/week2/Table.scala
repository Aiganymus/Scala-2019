package week2

class Table(legs: Int, var name: String) {
  def getLegs: Int = this.legs


  def setName(newName: String) = {
    this.name = newName
  }
}
