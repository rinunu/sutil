package nu.rinu.util

import java.util.{Date, Calendar}
import java.text.{DateFormat, SimpleDateFormat}


class RichDateString(val impl: String) extends AnyVal {
  @deprecated
  def jst: Date = JSTParser(impl)
}

class RichDate(val impl: Date) extends AnyVal with Ordered[Date] {
  /**
   * thread safe
   */
  def format(implicit f: DateFormat): String =
    f.synchronized {
      f.format(impl)
    }

  def format(formatText: String): String =
    new SimpleDateFormat(formatText).format(impl)

  def isToday: Boolean = {
    val todayCal = Calendar.getInstance()
    todayCal.setTime(new Date())

    val cal = Calendar.getInstance()
    cal.setTime(impl)

    Seq(
      Calendar.YEAR,
      Calendar.MONTH,
      Calendar.DAY_OF_MONTH
    ).forall {
      field =>
        todayCal.get(field) == cal.get(field)
    }
  }

  def isYesterday: Boolean = {
    import DateImplicits._
    date == (new Date).date.plusDays(-1)
  }

  def date: Date = {
    val cal = Calendar.getInstance()
    cal.setTime(impl)

    val result = Calendar.getInstance()
    result.clear()

    Seq(
      Calendar.YEAR,
      Calendar.MONTH,
      Calendar.DAY_OF_MONTH
    ).foreach {
      field =>
        result.set(field, cal.get(field))
    }
    result.getTime
  }

  def days: Int = getField(Calendar.DAY_OF_MONTH)

  def hours: Int = getField(Calendar.HOUR)

  def minutes: Int = getField(Calendar.MINUTE)

  def month: Int = getField(Calendar.MONTH)

  def seconds: Int = getField(Calendar.SECOND)

  def year: Int = getField(Calendar.YEAR)

  private def getField(field: Int): Int = {
    val cal = Calendar.getInstance()
    cal.setTime(impl)
    cal.get(field)
  }

  private def plus(field: Int, n: Int): Date = {
    val cal = Calendar.getInstance()
    cal.setTime(impl)
    cal.add(field, n)
    cal.getTime
  }

  def plusDays(n: Int): Date = plus(Calendar.DAY_OF_MONTH, n)

  def plusHours(n: Int): Date = plus(Calendar.HOUR, n)

  def plusMillis(n: Int): Date = plus(Calendar.MILLISECOND, n)

  def plusMinutes(n: Int): Date = plus(Calendar.MINUTE, n)

  def plusMonths(n: Int): Date = plus(Calendar.MONTH, n)

  def plusSeconds(n: Int): Date = plus(Calendar.SECOND, n)

  def plusYears(n: Int): Date = plus(Calendar.YEAR, n)

  def until(to: Date): DateRange = {
    DateRange(impl, to)
  }

  @deprecated("until")
  def to(to: Date): Stream[Date] = {
    DateUtils.daysTo(impl, to)
  }

  def compare(that: Date): Int = {
    impl.compareTo(that)
  }
}

trait DateUtils {
  /**
   * days of [begin, end)
   */
  def daysUntil(begin: Date, end: Date): Stream[Date] = {
    val beginCal = Calendar.getInstance()
    beginCal.setTime(begin)

    val cal = Calendar.getInstance()
    cal.clear()
    Seq(Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH).foreach {
      field =>
        cal.set(field, beginCal.get(field))
    }

    new Iterator[Date] {
      def hasNext: Boolean = {
        val t = cal.getTime
        t.getTime < end.getTime
      }

      def next(): Date = {
        val t = cal.getTime
        cal.add(Calendar.DAY_OF_MONTH, 1)
        t
      }
    }.toStream
  }

  /**
   * days of [begin, end]
   */
  def daysTo(begin: Date, end: Date): Stream[Date] = {
    val cal = Calendar.getInstance()
    cal.setTime(begin)

    new Iterator[Date] {
      def hasNext: Boolean = {
        val t = cal.getTime
        t.getTime <= end.getTime
      }

      def next(): Date = {
        val t = cal.getTime
        cal.add(Calendar.DAY_OF_MONTH, 1)
        t
      }
    }.toStream
  }
}

/**
 * [from,  to)
 */
case class DateRange(from: Date, to: Date) {
  /**
   * get days
   */
  def days: Stream[Date] = {
    DateUtils.daysUntil(from, to)
  }

  def contains(d: Date): Boolean = {
    import DateImplicits._
    from <= d && d < to
  }
}

trait DateParser {
  def apply(s: String): Date
}

@deprecated
object JSTParser extends DateParser {
  def apply(s: String): Date = {
    // TODO JST
    val formats = Seq(
      new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"),
      new SimpleDateFormat("yyyy-MM-dd HH:mm"),
      new SimpleDateFormat("yyyy-MM-dd"),
      new SimpleDateFormat("yyyyMMdd")
    )

    val dateOptSeq = formats.map {
      format =>
        try {
          Some(format.parse(s))
        } catch {
          case _: Exception =>
            None
        }
    }

    dateOptSeq.flatten.headOption.getOrElse {
      sys.error("parse error: " + s)
    }
  }
}

/**
 * path"xxx/xxx"
 */
class DateStringContext(val sc: StringContext) extends AnyVal {
  def date(args: Any*)(implicit f: DateFormat): Date = {
    f.synchronized(f.parse(sc.parts(0)))
  }
}

class RichDateFormat(val impl: DateFormat) extends AnyVal {
  def apply(s: String): Date = impl.parse(s)

  def apply(d: Date): String = impl.format(d)
}

class RichDateInt(val impl: Int) extends AnyVal {
  def seconds = impl * 1000

  def second = seconds

  def milliseconds = impl

  def millisecond = milliseconds

  def millis = milliseconds

  def minutes = impl * 1000 * 60

  def minute = minutes

  def hours = impl * 1000 * 60 * 60

  def hour = hours

  def days = impl * 1000 * 60 * 60 * 24

  def day = days
}

trait DateImplicits {
  implicit def dateStringContext(sc: StringContext) = new DateStringContext(sc)

  implicit def richDateFormat(a: DateFormat) = new RichDateFormat(a)

  implicit def richDateInt(a: Int) = new RichDateInt(a)

  implicit def stringToRichDate(a: String)(implicit f: DateFormat): RichDate = {
    f.synchronized {
      new RichDate(f.parse(a))
    }
  }

  implicit def stringToDate(a: String)(implicit f: DateFormat): Date = {
    f.synchronized {
      f.parse(a)
    }
  }

  implicit def richDateString(impl: String): RichDateString = new RichDateString(impl)

  implicit def richDate(impl: Date): RichDate = new RichDate(impl)
}

object DateImplicits extends DateImplicits

object DateUtils extends DateUtils with DateImplicits {

}