package model

// TODO: yearOfRelease to year (DateTime)

/**
 * Model of a movie
 * @param id unique ID of a movie
 * @param title
 * @param director
 * @param yearOfRelease
 */
case class Movie(id: String, title: String, director: Director, yearOfRelease: Int)
