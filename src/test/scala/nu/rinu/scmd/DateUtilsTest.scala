package nu.rinu.scmd

import org.specs2.mutable.Specification
import java.util.Calendar
import nu.rinu.util.DateUtils
import java.text.SimpleDateFormat

class DateUtilsTest extends Specification {

  import DateUtils._

  "jst" >> {
    val date = "2013-01-02".jst
    val cal = Calendar.getInstance()
    cal.setTimeInMillis(0)
    cal.set(2013, 0, 2, 0, 0, 0)
    date.getTime === cal.getTime.getTime
  }

  "date" >> {
    implicit val dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    date"2013-01-02 00:00:00".date == date"2013-01-02 10:10:10".date
  }

  "duration" >> {
    //    1.days
    //    4.hours
  }

  "plus" >> {
    implicit val dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    "seconds" >> {
      val t = date"2013-01-02 00:00:00"
      t.plusSeconds(1).format === "2013-01-02 00:00:01"
      t.plusSeconds(-1).format === "2013-01-01 23:59:59"
    }

    "days" >> {
      val t = date"2013-01-01 00:00:01"
      t.plusDays(1).format === "2013-01-02 00:00:01"
      t.plusDays(-1).format === "2012-12-31 00:00:01"
    }
  }

  "field" >> {
    implicit val dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val t = date"2013-01-01 01:02:03"
    "hours" >> {
      t.hours == 1
    }
  }

  "days" >> {
    "date" >> {
      implicit val f = new SimpleDateFormat("yyyy-MM-dd")

      val days = ("2013-01-02" until "2013-01-05").days.toList
      days === List(date"2013-01-02", date"2013-01-03", date"2013-01-04")
    }

    "date time" >> {
      implicit val dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
      val d = new SimpleDateFormat("yyyy-MM-dd")

      val days = ("2013-01-02 01:00:00" until "2013-01-03 00:00:00").days.toList
      days === List(d.parse("2013-01-02"))
    }

    "date time 2" >> {
      implicit val dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
      val d = new SimpleDateFormat("yyyy-MM-dd")

      val days = ("2013-01-02 01:00:00" until "2013-01-03 00:00:01").days.toList
      days === List(d("2013-01-02"), d("2013-01-03"))
    }
  }

  "context" >> {
    implicit val f = new SimpleDateFormat("yyyyMMdd")
    "2013-01-02".jst.format === "20130102"
  }
}
