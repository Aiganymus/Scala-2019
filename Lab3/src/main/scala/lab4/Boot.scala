package lab4

object Boot extends App {
  case class Film( name: String,
                   yearOfRelease: Int,
                   imdbRating: Double)
  case class Director( firstName: String,
                       lastName: String,
                       yearOfBirth: Int,
                       films: Seq[Film])


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

  def task1(numberOfFilms: Int): Seq[Director] = {
    directors.filter(d => d.films.size > numberOfFilms)
  }

  def task2(year: Int): Seq[Director] = {
    directors.filter(d => d.yearOfBirth < year)
  }

  def task3(year: Int, numberOfFilms: Int): List[Director] = {
    directors.filter(d => d.films.size > numberOfFilms && d.yearOfBirth < year).toList
  }

  def task4(ascending: Boolean = true): Seq[Director] = {
    if(ascending) directors.sortBy(_.yearOfBirth)
    else directors.sortBy(_.yearOfBirth).reverse
  }

  def task5(): List[String] = {
    nolan.films.map(_.name).toList
  }

  def task6(): List[String] = {
    directors.flatten(_.films.map(_.name)).toList
  }

  def task7(): Int = {
    mcTiernan.films.map(_.yearOfRelease).min
  }

  def task8(): Seq[Film] = {
    directors.flatten(_.films).sortBy(_.imdbRating).reverse
  }

  def task9(): Double = {
    directors.flatten(_.films).map(_.imdbRating).sum / directors.flatten(_.films).length
  }

  def task10(): Unit = {
    directors.foreach(d => d.films.foreach(f => println(s"Tonight only! ${f.name} by ${d.lastName}")))
  }

  def task11(): Int = {
    directors.flatMap(_.films).map(_.yearOfRelease).min
  }
}
