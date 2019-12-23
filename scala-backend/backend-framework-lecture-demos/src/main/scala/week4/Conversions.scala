package week4

trait Conversions {

  implicit def fahrenheitToCelcius(f: Fahrenheit): Celcius = {
    Celcius((f.degree - 32) * 5 / 9)
  }
}
