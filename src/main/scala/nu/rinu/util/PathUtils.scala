package nu.rinu.util

import java.nio.file.{Files, Paths, Path}
import java.io.{InputStream, OutputStream, BufferedReader, BufferedWriter}
import java.nio.charset.Charset

class PathString(val impl: String) extends AnyVal {
  def /(sub: String): Path = {
    Paths.get(impl, sub)
  }
}

class RichPath(val impl: Path) extends AnyVal {
  def /(sub: String): Path = {
    impl.resolve(sub)
  }

  def /(sub: Path): Path = {
    impl.resolve(sub)
  }

  /**
   * read lines and close.
   */
  def eachLine[T](f: String => T)(implicit charset: Charset) {
    IOUtils.using(Files.newBufferedReader(impl, charset)) { r =>
      IOUtils.lines(r).foreach(f)
    }
  }

  def lines(implicit charset: Charset): Seq[String] = {
    withReader(r => IOUtils.lines(r).toList)
  }

  @deprecated
  def writer[T](f: BufferedWriter => T)(implicit charset: Charset): T = {
    withWriter(f)
  }

  /**
   * read and close.
   */
  @deprecated
  def reader[T](f: BufferedReader => T)(implicit charset: Charset): T = {
    withReader(f)
  }

  def withWriter[T](f: BufferedWriter => T)(implicit charset: Charset): T = {
    val w = Files.newBufferedWriter(impl, charset)
    IOUtils.using(w)(f)
  }

  /**
   * read and close.
   */
  def withReader[T](f: BufferedReader => T)(implicit charset: Charset): T = {
    val r = Files.newBufferedReader(impl, charset)
    IOUtils.using(r)(f)
  }

  def outputStream[A](): OutputStream = {
    Files.newOutputStream(impl)
  }

  def inputStream[A](): InputStream = {
    Files.newInputStream(impl)
  }

  def withOutputStream[A](f: OutputStream => A): A = {
    IOUtils.using(outputStream())(f)
  }

  def withInputStream[A](f: InputStream => A): A = {
    IOUtils.using(inputStream())(f)
  }

  def write(s: String)(implicit charset: Charset) {
    withWriter { w => w.write(s)}
  }

  def exists(): Boolean = {
    Files.exists(impl)
  }

  def isDirectory(): Boolean = {
    Files.isDirectory(impl)
  }
}

/**
 * path"xxx/xxx"
 */
class PathStringContext(val sc: StringContext) extends AnyVal {
  def path(args: Any*): Path = {
    Paths.get(sc.parts(0))
  }
}

trait PathImplicits {

  implicit def pathStringContext(sc: StringContext) = new PathStringContext(sc)

  implicit def pathString(impl: String): PathString = {
    new PathString(impl)
  }

  implicit def richPath(impl: Path): RichPath = {
    new RichPath(impl)
  }

  /**
   * '/' separated String to Path
   *
   * unix only
   */
  implicit def path(s: String): Path = {
    Paths.get(s)
  }
}

object PathImplicits extends PathImplicits

object PathUtils {
  def home: Path = {
    Paths.get(System.getProperty("user.home"))
  }
}
