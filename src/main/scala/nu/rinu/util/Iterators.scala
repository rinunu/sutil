package nu.rinu.util

trait Iterators {
  def iterator[T](fetch: => Option[T]): Iterator[T] = {
    new Iterator[T] {
      var nextOpt: Option[T] = None
      var completed = false

      // hasNext, next, hasNext, ... 

      def hasNext: Boolean = {
        nextOpt match {
          case Some(n) =>
            true
          case None if completed =>
            false
          case None =>
            nextOpt = fetch
            nextOpt match {
              case Some(_) =>
                true
              case None =>
                completed = true
                false
            }
        }
      }

      def next(): T = {
        nextOpt match {
          case Some(n) =>
            nextOpt = None
            n
          case None =>
            sys.error("invalid state")
        }
      }
    }
  }

}

object Iterators extends Iterators