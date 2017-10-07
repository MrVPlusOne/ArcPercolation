package program

import java.io._

import scala.util.Random

/**
 * Created by weijiayi on 3/9/15.
 */
object SteepnessStudy {
  val random = new Random()

  val arcAngle = 200.0

  def checkFunc(region:RecRegion,sampleNum:Int) = PercolationDataRecorder.checkPercolation(region,random)(arcAngle,sampleNum)

  def findCurveRange(recRegion: RecRegion,startSampleNumber:Int,
                     sampleNumberStepSize:Int, experTimePerPoint:Int):Range={

    var number = startSampleNumber
    var percolationTime = 0
    var percolationHappened = false
    var startPoint = 0

    while (percolationTime<2){
      val hit = (0 until experTimePerPoint).count(_=>checkFunc(recRegion,number))
      if(!percolationHappened && hit!=0){
        percolationHappened = true
        startPoint = number - sampleNumberStepSize
      }
      val ratio = hit.toDouble/experTimePerPoint
      if(ratio>0.8){
        percolationTime += 1
      }
      number += sampleNumberStepSize
    }
    val endPoint = number

    Range(startPoint,endPoint)
  }

  def curvePoints(recRegion: RecRegion,
                  startNumber:Int,endNumber:Int,stepSize:Int,experTimePerPoint:Int)={
    new ParallelYield[(Int,Double)](startNumber,endNumber,stepSize,number=>{
      val hit = (0 until experTimePerPoint).count(_=>checkFunc(recRegion,number))
      val ratio = hit.toDouble/experTimePerPoint
      (number,ratio)
    },p=>println(s"Progress: ${p}"),processors = 8).getResult()
  }

  def intPower(base:Int,p:Int):Int = (0 until p).map(_=>base).product
  def main(args: Array[String]) {
    val fileName = "curve_data.txt"
    val stream = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(fileName)))
    val regionBaseSize = 10
    val growthFactor = 2
    val stepBaseSize = 5
    for(i <- 0 to 5){
      println(s"i = $i")
      val scaleFactor = intPower(growthFactor,i)
      val regionSize = regionBaseSize*scaleFactor
      val region = RecRegion(regionSize,regionSize)
      val area = scaleFactor*scaleFactor
      val bigStepSize = stepBaseSize*area
      val smallStepSize = bigStepSize/5
      val curveRange = findCurveRange(region,bigStepSize,bigStepSize,experTimePerPoint = 10)
      println(s"Curve Range[${curveRange.start.toDouble/area},${curveRange.end.toDouble/area}}]")
      val curve = curvePoints(region,curveRange.start,curveRange.end,smallStepSize,experTimePerPoint = 100)
      exportCurve(stream,regionSize,curve)
    }

    println("Program Finished!")
    stream.close()
  }

  def exportCurve(writer:OutputStreamWriter,regionSize:Int,points:IndexedSeq[(Int,Double)]): Unit ={
    def writeData(point:(Int,Double)) = writer.write(s" ${point._1},${point._2}")
    writer.write(regionSize.toString)
    points.foreach(writeData)
    writer.write("\n")
  }
}
