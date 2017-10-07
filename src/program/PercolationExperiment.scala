package program

import scala.util.Random

/**
 * Created by weijiayi on 15/2/24.
 */

case class RecRegion(width:Double,height:Double){

  def randomPickPoint(random: Random):Vec2 = {
    val x = random.nextDouble()*width
    val y = random.nextDouble()*height
    Vec2(x,y)
  }
}

class PercolationExperiment(region:RecRegion,arcAngle:Double,arcNumber:Int,random:Random) {
  def sampleArcs(): List[Arc] ={
    (for(i<- 0 until arcNumber) yield {
      val center = region.randomPickPoint(random)
      val startAngle = MyMath.wrapInMinusPiPositivePi(random.nextDouble()*MyMath.twoPi)
      val endAngle = MyMath.wrapInMinusPiPositivePi(startAngle+arcAngle)-1e-6
      Arc(center,startAngle,endAngle)
    }).toList
  }
}


