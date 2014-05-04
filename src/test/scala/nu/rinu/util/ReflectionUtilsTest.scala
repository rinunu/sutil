package nu.rinu.util

import org.specs2.mutable.Specification

class ReflectionUtilsTest extends Specification {

  import ReflectionUtils._

  class Foo {
    def method1() = 1

    def method2() = 2

    val val1 = 1
    val val2 = 1
  }

  "method" >> {
    val foo = new Foo

    val fooMirror = mirrorAndType(foo)

    fooMirror.method("method1")() === 1
    fooMirror.method("val1")() === 1
  }

  "method" >> {
    val foo = new Foo

    val fooMirror = mirrorAndType(foo)
    val sym = fooMirror.methodSymbol("method1")

    fooMirror.method(sym)() === 1
  }

  "methods" >> {
    val foo = new Foo

    val fooMirror = mirrorAndType(foo)

    fooMirror.methodSymbols.foreach(println)
    pending
  }

}
