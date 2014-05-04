package nu.rinu.util

import scala.reflect.runtime.{universe => u}

class RichType(val impl: u.Type) extends AnyVal {
  def methods: Seq[u.MethodSymbol] =
    impl.members.toSeq.filter(_.isMethod).map(_.asMethod)

  def method(name: u.TermName): u.MethodSymbol = {
    impl.member(name).asMethod
  }
}

class MirrorAndType(val mirror: u.InstanceMirror, val tpe: u.Type) {

  import ReflectionImplicits._

  def method(name: u.TermName): u.MethodMirror = {

    val symbol = tpe.method(name)
    mirror.reflectMethod(symbol)
  }

  def method(symbol: u.MethodSymbol): u.MethodMirror = {
    mirror.reflectMethod(symbol)
  }

  def methodSymbols = tpe.methods

  def methodSymbol(name: u.TermName) = tpe.method(name)
}

trait ReflectionImplicits {
  implicit def richType(a: u.Type): RichType = new RichType(a)

  implicit def stringToTermName(a: String): u.TermName = {
    u.newTermName(a)
  }

  implicit def stringToTypeName(a: String): u.TypeName = {
    u.newTypeName(a)
  }
}

trait ReflectionUtils {
  def reflect(instance: Any) = {
    val m = u.runtimeMirror(instance.getClass.getClassLoader)
    m.reflect(instance)
  }

  def mirrorAndType[A: u.TypeTag](a: A): MirrorAndType = {
    val tt = implicitly[u.TypeTag[A]]
    new MirrorAndType(reflect(a), tt.tpe)
  }
}

object ReflectionImplicits extends ReflectionImplicits

object ReflectionUtils extends ReflectionUtils with ReflectionImplicits

