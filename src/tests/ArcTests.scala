package tests

import program.{HorizontalEntry, MyMath, Vec2, Arc}
import MyMath._
/**
 * Created by weijiayi on 15/2/24.
 */
class ArcTests extends MyTest{

  val pi = math.Pi
  val arcA = Arc(Vec2.zero,startAngle = -pi, endAngle =  pi)
  val arcB = Arc(Vec2(1,0),startAngle = -pi, endAngle = pi)
  val halfCircleLeft = Arc(Vec2(-1,0),-pi/2,pi/2)
  val halfCircleRight = Arc(Vec2(1,0),-pi/2,pi/2)
  val antiHalfCircleLeft = Arc(Vec2(-1,0),pi/2,-pi/2)

  "angle should in range" in {
    arcA.angleInRange(0)
    arcA.angleInRange(math.Pi/2)
  }

  "two arc" should {
    "intersect" in {
      val intersects = List(
        arcA.intersects(arcB),
        arcA.intersects(halfCircleLeft))
      intersects map (_ shouldBe true)
    }
    "not intersect" in {
      val notIntersects = List(
        halfCircleRight.intersects(halfCircleLeft),
        arcA.intersects(antiHalfCircleLeft))
      notIntersects map (_ shouldBe false)
    }
  }

  val baseline = new HorizontalEntry(0)
  val halfOneLine = new HorizontalEntry(0.5)
  "an arc and a vertical entry" should{
    "intersect" in{
      baseline.intersects(arcA) shouldBe true
      baseline.intersects(halfCircleLeft) shouldBe true
      halfOneLine.intersects(arcA) shouldBe true
    }
    "not intersect" in{
      val lowerHalfArc = Arc(Vec2.zero,-pi,0)
      halfOneLine.intersects(lowerHalfArc) shouldBe false
    }

  }
}
