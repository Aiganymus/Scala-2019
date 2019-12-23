package lab4

case class Film(name: String,
                yearOfRelease: Int,
                imdbRating: Double)

case class Director(firstName: String,
                    lastName: String,
                    yearOfBirth: Int,
                    films: Seq[Film])

object Driver extends App {

  val memento = new Film("Memento", 2000, 8.5)
  val darkKnight = new Film("Dark Knight", 2008, 9.0)
  val inception = new Film("Inception", 2010, 8.8)
  val highPlainsDrifter = new Film("High Plains Drifter", 1973, 7.7)
  val outlawJoseyWales = new Film("The Outlaw Josey Wales", 1976, 7.9)
  val unforgiven = new Film("Unforgiven", 1992, 8.3)
  val granTorino = new Film("Gran Torino", 2008, 8.2)
  val invictus = new Film("Invictus", 2009, 7.4)
  val predator = new Film("Predator", 1987, 7.9)
  val dieHard = new Film("Die Hard", 1988, 8.3)
  val huntForRedOctober = new Film("The Hunt for Red October", 1990, 7.6)
  val thomasCrownAffair = new Film("The Thomas Crown Affair", 1999, 6.8)
  val eastwood = new Director("Clint", "Eastwood", 1930,
    Seq(highPlainsDrifter, outlawJoseyWales, unforgiven, granTorino, invictus))
  val mcTiernan = new Director("John", "McTiernan", 1951,
    Seq(predator, dieHard, huntForRedOctober, thomasCrownAffair))
  val nolan = new Director("Christopher", "Nolan", 1970,
    Seq(memento, darkKnight, inception))
  val someGuy = new Director("Just", "Some Guy", 1990,
    Seq())
  val directors = Seq(eastwood, mcTiernan, nolan, someGuy)

  val filter1 = (numberOfFilms: Int) => directors.filter(d => d.films.size > numberOfFilms) // task 1
  println(filter1(2))

  val filter2 = (year: Int) => directors.filter(d => d.yearOfBirth < year) // task 2
  println(filter2(1968))

  val filter3 = (year: Int, numberOfFilms: Int) => directors.filter(d => d.yearOfBirth < year && d.films.size > numberOfFilms) // task 3
  println(filter3(1970, 2))

  def sortWith1(ascending: Boolean = true) = ascending match { // task 4
    case true => directors.sortWith((a, b) => a.yearOfBirth < b.yearOfBirth)
    case false => directors.sortWith((a, b) => a.yearOfBirth > b.yearOfBirth)
  }

  println(sortWith1())
  println(sortWith1(false))

  val map1 = nolan.films.map(f => f.name) // task 5
  println(map1)

  val map2 = directors.flatMap(d => d.films.map(f => f.name)) // task 6
  println(map2)

  val sortWith2 = mcTiernan.films.sortWith((a, b) => a.yearOfRelease < b.yearOfRelease).headOption // task 7
  println(sortWith2)

  val sortWith3 = directors.flatten(d => d.films).sortWith((a, b) => a.imdbRating > b.imdbRating) // task 8

  def averageScore(): Double = { // task 9
    var sum, cnt: Double = 0
    directors.foreach(d => d.films.foreach(f => {
      cnt += 1
      sum += f.imdbRating
    }))
    sum / cnt
  }
  println(averageScore())

  directors.foreach(d => d.films.foreach(f => println(s"Tonight only! ${f.name} by ${d.firstName} ${d.lastName}!"))) // task 10

  println(directors.flatten(d => d.films).sortWith((a, b) => a.yearOfRelease < b.yearOfRelease).headOption) // task 11



}