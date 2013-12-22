package nu.rinu.util

trait Utils {
  @deprecated("https://github.com/jsuereth/scala-arm")
  def using[A <: AutoCloseable, B](closable: A)(f: A => B): B = {
    try {
      f(closable)
    } finally {
      closable.close()
    }
  }
}

object Utils extends Utils