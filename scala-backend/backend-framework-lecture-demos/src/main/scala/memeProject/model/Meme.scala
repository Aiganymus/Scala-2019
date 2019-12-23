package memeProject.model

import akka.http.scaladsl.model.DateTime

/**
  *
  * @param id
  * @param title
  * @param description
  * @param author
  * @param rating
  */
case class Meme(id: String, title: String, description: String, author: Author, rating: Int = 0)
