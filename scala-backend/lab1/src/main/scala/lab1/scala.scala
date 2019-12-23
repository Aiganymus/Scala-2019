package lab1

// Theoretical questions: why do we need abstraction
// How `traits` in Scala are used?

trait Animal {
  // Is this abstract or concrete (implemented) member?
  // this is abstract member
  def name: String

  // Is this abstract or concrete (implemented) member?
  // this is abstract member
  def makeSound(): String
}

trait Walks {

  // What does this line mean?
  // it means self-typing
  this: Animal =>

  // Is this abstract or concrete (implemented) member?
  // this is concrete member
  // Why `name` parameter is available here?
  // because we are mixing traits with self-typing
  def walk: String = s"$name is walking"

}


// Can Dog only extend from `Walks`?
// nope, Dog class also must extend from Animal because of self-type
// Try to fix Dog, so it extends proper traits
// Implement Dog class so it passes tests
case class Dog(nameF: String, soundF: String = "Whoooof") extends Walks with Animal {
  override def name: String = nameF;

  override def makeSound(): String = soundF;
}

// Implement Cat class so it passes tests
case class Cat(nameF: String, soundF: String = "Miiyaaau") extends Animal with Walks {
  override def name: String = nameF;

  override def makeSound(): String = soundF;
}

object Lab2 extends App {

  // Here we will test Dog and Cat classes

  val dog1 = Dog("Ceasar")
  val dog2 = Dog("Laika")

  println(dog1.name)
  println(dog1.makeSound())
  println(dog1.walk)

  //  assert(dog1.name == "Ceasar")
  //  assert(dog2.name == "Laika")
  //
  //  assert(dog1.makeSound() == "Whooof")
  //  assert(dog2.makeSound() == "Whooof")
  //
  //  assert(dog1.walk == "Ceasar is walking")
  //  assert(dog2.walk == "Laika is walking")


  val cat1 = Cat("Tosha")
  val cat2 = Cat("Chocolate")

  println(cat1.name)
  println(cat1.makeSound())
  println(cat1.walk)

  //  assert(cat1.name == "Tosha")
  //  assert(cat2.name == "Chocolate")
  //
  //  assert(cat1.makeSound() == "Miiyaaau")
  //  assert(cat2.makeSound() == "Miiyaaau")
  //
  //  assert(cat1.walk == "Tosha is walking")
  //  assert(cat2.walk == "Chocolate is walking")

}