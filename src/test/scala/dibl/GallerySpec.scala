package dibl

import java.io.{BufferedReader, FileReader}

import org.scalatest.{FlatSpec, Matchers}

import scala.collection.JavaConverters._

class GallerySpec extends FlatSpec with Matchers {
  "each gallery pattern" should "succeed, except one" in {
    Matrices.values.filter { l =>
      val f = l.split(";")
      val cols = if (f.size > 3) f(3).toInt else 9
      println(l)
      PairDiagram.create(f(0), f(1), absRows = 9, absCols = cols).isFailure
    }.toSeq shouldBe Seq("B-B- -B-B C-C- -C-C;bricks")
  }

  ignore should "work for some set of parameters" in {
    val x: Seq[String] = for{
      shiftLeft <- 0 until 16
      shiftUp <- 0 until 16
      cols <- 8 until 32
    } yield {
      val result = PairDiagram.create("B-B- -B-B C-C- -C-C", "bricks", absRows = 9, cols, shiftLeft, shiftUp)
      s"shiftLeft=$shiftLeft shiftUp=$shiftUp cols=$cols ${result.isSuccess}"
    }
    println (x)
    x.mkString should include ("true")
  }
}