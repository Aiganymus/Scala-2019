package memeProject.model

/**
  *
  * @param id
  * @param firstName
  * @param middleName
  * @param lastName
  * @param numberOfPublications
  */
case class Author(id: String, firstName: String, middleName: Option[String], lastName: String, numberOfPublications: Int = 0)
