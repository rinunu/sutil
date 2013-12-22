package nu.rinu.util

import org.specs2.mutable.Specification
import java.nio.file.Files

class IOUtilsTest extends Specification {
  implicit val c = Charsets.UTF8
  "read lines from path simply" >> {
    import IOImplicits._

    path"test.txt".reader {
      _.lines().foreach(println)
    }
  }

  "read lines from stream simply" >> {
    import IOImplicits._

    Files.newInputStream(path"test.txt").reader {
      _.lines().foreach(println)
    }
  }

}
