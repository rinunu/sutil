package nu.rinu.util

import org.specs2.mutable.Specification

class IteratorsTest extends Specification {
  "" >> {
    var i = 0
    val it = Iterators.iterator {
      i += 1
      i match {
        case 1 => Some(1)
        case 2 => Some(2)
        case 3 => None
      }
    }

    it.toList === List(1, 2)
  }
}
