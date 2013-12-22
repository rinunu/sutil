package nu.rinu.util

import java.io._
import java.nio.charset.Charset

trait IOUtils {
  /**
   * @param r close yourself.
   * @return
   */
  def lines(r: BufferedReader): Stream[String] = {
    val line = r.readLine()
    if (line != null) {
      line #:: lines(r)
    } else {
      Stream.Empty
    }
  }

  def using[A <: AutoCloseable, B](closable: A)(f: A => B): B = {
    Utils.using(closable)(f)
  }

  def bufferedReader(input: InputStream): BufferedReader = {
    new BufferedReader(new InputStreamReader(input))
  }

}

class RichReader(val impl: Reader) extends AnyVal {
  def lines(): Stream[String] = {
    val buf = impl match {
      case r: BufferedReader => r
      case r: Reader => new BufferedReader(r)
    }
    IOUtils.lines(buf)
  }
}


class RichInputStream(val impl: InputStream) extends AnyVal {
  /**
   * read and close.
   */
  def reader[T](f: BufferedReader => T)(implicit charset: Charset): T = {
    val r = new BufferedReader(new InputStreamReader(impl, charset))
    IOUtils.using(r)(f)
  }
}

trait IOImplicits extends PathImplicits {
  implicit def richReader(impl: Reader) = new RichReader(impl)

  implicit def richInputStream(impl: InputStream) = new RichInputStream(impl)
}

object IOImplicits extends IOImplicits

object IOUtils extends IOUtils