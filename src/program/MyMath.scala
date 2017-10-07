package program

/**
 * Created by weijiayi on 15/2/24.
 */
object MyMath {

  val deg = math.Pi/180
  val twoPi = math.Pi * 2

  def wrapInRange(x:Double,lower:Double,upper:Double):Double ={
    val length=upper-lower
    if(x > upper) wrapInRange(x-length,lower,upper)
    else if(x < lower) wrapInRange(x+length,lower,upper)
    else x
  }

  def wrapInMinusPiPositivePi(angle:Double) = wrapInRange(angle,-math.Pi,math.Pi)

  def radToDeg(rad:Double):Int = if(rad>0) (rad/deg).toInt else 360+(rad/deg).toInt

}

case class Vec2(x:Double,y:Double){
  def modularSquared = x*x+y*y
  def modular = math.sqrt(modularSquared)

  def - (that:Vec2) = Vec2(x-that.x,y-that.y)
}
object Vec2{
  val zero = Vec2(0,0)
}
