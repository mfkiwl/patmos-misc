// Process emulation output for generation of bar charts

import scala.sys.process._
import java.io._
import scala.collection.mutable
import scala.io.Source

// TODO make it dependent on args
val log = false

val types = List("np", "sp")
val cache = List("mcache", "icache", "pref")

val np = Map("mcache" -> mutable.Map[String, Int](), "icache" -> mutable.Map[String, Int](), "pref" -> mutable.Map[String, Int]())
val sp = Map("mcache" -> mutable.Map[String, Int](), "icache" -> mutable.Map[String, Int](), "pref" -> mutable.Map[String, Int]())

val all = Map("np" -> np, "sp" -> sp)

val bench = mutable.Set[String]()

val files = new File(".").listFiles
val txtFiles = files.filter( f => f.getName.endsWith(".txt") )
txtFiles.map{ file => addResult(file.getName) }

def addResult(f: String) {
  val s = Source.fromFile(f)
  val l = s.getLines()
  val v = l.next().split(" ")
  bench += v(0)
  val cycles = l.next().split(" ")(1).toInt
  if (log) println(v.toList + ": " + cycles)
  all(v(2))(v(1)) += (v(0) -> cycles)
}

if (log) println()
if (log) println(bench)

def printStat(t: String) {
  println("Results for " + t + ": mcache icache")
  for (b <- bench) {
    val v1 = all(t)("mcache")(b)
    val v2 = all(t)("icache")(b)
    val fac = v2.toDouble / v1
    println(b + " " + v1 + " " + v2 + " " + fac)
  }
}


def printData(t: String) {
  println("sym y")
  val sortedBench = bench.toSeq.sorted
  for (b <- sortedBench) {
    val v1 = all(t)("mcache")(b)
    val v2 = all(t)("icache")(b)
    val fac = v2.toDouble / v1
    val n = b.flatMap { case '_' => "\\_" case c => s"$c" }
    println(n + " " + fac)
  }
}

// TODO use args and dependent on args just spill out stats or the right data
if (log) types.map{ t => printStat(t) }

printData("np")