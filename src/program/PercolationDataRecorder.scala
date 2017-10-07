package program

import java.io._

import scala.util.Random

/**
 * Created by weijiayi on 15/2/27.
 * export data to files
 */
object PercolationDataRecorder {
  val checkNumberPerCondition = 20
  val region = RecRegion(50, 50)
  val checkFunc = checkPercolation(region,new Random())_

  def main(args: Array[String]) {

    val dataToExport = for(arcAngle<- 180 to 360 by 20; sampleN <- 600 to 2000 by 25) yield {
      val hit = (0 until checkNumberPerCondition).count(_=>checkFunc(arcAngle,sampleN))
      val percolationRatio = hit.toDouble/checkNumberPerCondition
      val dataPoint = (sampleN,arcAngle,percolationRatio)
      println(dataPoint)
      dataPoint
    }

    println("Program Started!")
    exportData(dataToExport)
    println("All data exported!")
  }

  def exportData(data:IndexedSeq[(Int,Int,Double)]): Unit ={
    val info = s"Rec[${region.width},${region.height}],PointPerData:$checkNumberPerCondition"
    val file = new FileWriter(info+".txt")
    def writeData(d:(Int,Int,Double)) = file.write(s"${d._1} ${d._2} ${d._3}\n")

    data.foreach(writeData)
    file.close()
  }


  def checkPercolation(region:RecRegion,random: Random)(arcAngle:Double,sampleNumber:Int):Boolean= {
    val network = {
      val experiment = new PercolationExperiment(region, arcAngle * MyMath.deg, sampleNumber, random)
      val arcs = experiment.sampleArcs()
      val builder = new NetworkBuilder(region, 2)
      builder.makeNetwork(arcs)
    }
    network.visitNode(network.top)
    network.bottom.visited
  }
}
