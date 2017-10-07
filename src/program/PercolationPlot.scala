package program

import java.awt.{Color, RenderingHints, BasicStroke}
import java.util.Date
import javax.swing.{JLabel, BorderFactory}
import javax.swing.border.EmptyBorder
import scala.swing.Dimension
import scala.swing._
import scala.swing.event.ButtonClicked
import scala.util.Random

/**
 * Created by weijiayi on 15/2/25.
 */
case class PercolationPlot(network: PercolationNetwork,pixelPerUnit:Double,arcAngle:Int,
                           visitedColor: Color, normalColor: Color) {
  val topBottomConnected = network.bottom.visited

  val imageWidth = (network.recRegion.width*pixelPerUnit).toInt
  val imageHeight = (network.recRegion.height*pixelPerUnit).toInt

  def plotToGraphics(g: Graphics2D): Unit = {
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON)
    g.setStroke(new BasicStroke(1.25f))
    network.nodes.foreach(node=>{
      val color = if(node.visited) visitedColor else normalColor
      g.setColor(color)
      drawArc(node.arc ,g)
    })
  }

  val plotHeight = network.recRegion.height
  def drawArc(arc: Arc, g: Graphics2D): Unit ={

    val x = ((arc.center.x-1)*pixelPerUnit).toInt
    val y = ((plotHeight-arc.center.y-1)*pixelPerUnit).toInt
    val startDeg = MyMath.radToDeg(arc.startAngle)
    val diameter = (2*pixelPerUnit).toInt
    g.drawArc(x,y,diameter,diameter,startDeg,arcAngle)
  }
}

class PercolationPlotPane(plot: PercolationPlot) extends BoxPanel(Orientation.Vertical){
  import plot._
  preferredSize = new Dimension(imageWidth, imageHeight)

  override def paint(g: Graphics2D): Unit = {
    super.paint(g)

    plotToGraphics(g)
  }
}

class CanvasController{
  val canvasFrame = new MainFrame {
    title = "Percolation"
    contents = new FlowPanel()
  }
  canvasFrame.visible = true

  def generateNetwork(region: RecRegion,sampleNumber:Int, arcAngle:Int, pixelPerUnit:Double,
                      gridSize:Double,timeLabel:JLabel):Unit = {
    val startTime = System.currentTimeMillis()
    val network = {
      val experiment = new PercolationExperiment(region, arcAngle * MyMath.deg, sampleNumber, new Random())
      val arcs = experiment.sampleArcs()
      val builder = new NetworkBuilder(region, gridSize)
      builder.makeNetwork(arcs)
    }
    network.visitNode(network.top)

    val endTime = System.currentTimeMillis()
    val calcTimeUse = endTime - startTime
    timeLabel.setText(calcTimeUse + "ms")

    val plotPane = new PercolationPlotPane(
      PercolationPlot(network, pixelPerUnit, arcAngle,
        visitedColor = if (network.bottom.visited) Color.red else Color.blue,
        normalColor = Color.black
      )
    ) {
      background = Color.white
    }
    canvasFrame.contents = plotPane
  }
}

