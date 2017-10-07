package program

/**
 * Created by weijiayi on 15/2/24.
 */
import math._

case class Arc(center:Vec2,startAngle:Double,endAngle:Double) {

  def angleInRange(angle:Double): Boolean ={
    if(endAngle>startAngle) angle>startAngle && angle<endAngle
    else angle>startAngle || angle<endAngle
  }

  def intersects(arc:Arc):Boolean = {
    def bothArcMet(a1: Double, a2: Double):Boolean = {
      this.angleInRange(a1) && arc.angleInRange(a2)
    }

    val relativeVec = arc.center - center
    val halfDisSquared = relativeVec.modularSquared/4

    if(halfDisSquared<1){
      val baselineAngle = atan2(relativeVec.y,relativeVec.x)
      val angleToBaseline = acos(sqrt(halfDisSquared))
      val upperAngle1 = MyMath.wrapInMinusPiPositivePi(baselineAngle + angleToBaseline)
      val upperAngle2 = MyMath.wrapInMinusPiPositivePi(baselineAngle+math.Pi - angleToBaseline)
      if(bothArcMet(upperAngle1, upperAngle2)){
        return true
      }else{
        val lowerAngle1 = MyMath.wrapInMinusPiPositivePi(baselineAngle - angleToBaseline)
        val lowerAngle2 = MyMath.wrapInMinusPiPositivePi(baselineAngle+math.Pi + angleToBaseline)
        return bothArcMet(lowerAngle1, lowerAngle2)
      }
    }else return false
  }
}

abstract class Entry{
  def intersects(arc: Arc):Boolean
}

class HorizontalEntry (pos:Double) extends Entry{
  override def intersects(arc: Arc): Boolean = {
    val dy = pos-arc.center.y
    if(math.abs(dy)<1){
      val angle = acos(dy)
      val intersectAngle1 = Pi/2-angle
      val intersectAngle2 = Pi/2+angle
      return arc.angleInRange(intersectAngle1) || arc.angleInRange(intersectAngle2)
    }
    return false
  }
}