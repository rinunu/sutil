package nu.rinu.util

import scala.util.control.NonFatal

trait Utils {
  @deprecated("https://github.com/jsuereth/scala-arm")
  def using[A <: AutoCloseable, B](closable: A)(f: A => B): B = {
    try {
      f(closable)
    } finally {
      try {
        closable.close()
      } catch {
        case NonFatal(e) =>
        // 無視
      }
    }
  }
}

object Utils extends Utils