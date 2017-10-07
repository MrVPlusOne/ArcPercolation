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
class PercolationPlot(network: PercolationNetwork,pixelPerUnit:Double,arcAngle:Int) extends BoxPanel(Orientation.Vertical){
  val topBottomConnected = network.bottom.visited
  val visitedColor = if(topBottomConnected) Color.red else Color.blue
  val normalColor = Color.black

  preferredSize = new Dimension((network.recRegion.width*pixelPerUnit).toInt,
    (network.recRegion.height*pixelPerUnit).toInt)

  override def paint(g: Graphics2D): Unit = {
    super.paint(g)

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

class CanvasController{
  val canvasFrame = new MainFrame {
    title = "Percolation"
    contents = new FlowPanel()
  }
  canvasFrame.visible = true

  def generateNetwork(region: RecRegion,sampleNumber:Int, arcAngle:Int, pixelPerUnit:Double,
                      gridSize:Double,timeLabel:JLabel):Unit ={
    val startTime = System.currentTimeMillis()
    val network = {
      val experiment = new PercolationExperiment(region,arcAngle*MyMath.deg,sampleNumber,new Random())
      val arcs = experiment.sampleArcs()
      val builder = new NetworkBuilder(region,gridSize)
      builder.makeNetwork(arcs)
    }
    network.visitNode(network.top)

    val endTime = System.currentTimeMillis()
    val calcTimeUse = endTime - startTime
    timeLabel.setText(calcTimeUse+"ms")

    val plot = new PercolationPlot(network,pixelPerUnit,arcAngle){background = Color.white}
    canvasFrame.contents=plot
  }
}

object PlotProgram extends SimpleSwingApplication{

  val controlPanel = new BoxPanel(Orientation.Vertical){
    def numField(showText:String)= new TextField{
      text=showText; preferredSize = new Dimension(100,30);maximumSize = preferredSize}

    def mkLabel(text:String) = new Label(text){xAlignment=Alignment.Left}

    val sampleNumberField = numField("500")
    val angleField = numField("200")
    val pixelField = numField("12.0")
    val paintButton = new Button(){text = "Paint"}
    val timeUseLabel = mkLabel("Calc Time: ")
    val widthField = numField("50.0")
    val heightField = numField("50.0")
    contents += mkLabel("Region")
    contents += new BoxPanel(Orientation.Horizontal){
      contents += mkLabel("W:")
      contents += widthField
      contents += mkLabel("  H:")
      contents += heightField
    }
    contents += mkLabel("Sample Number: ")
    contents += sampleNumberField
    contents += mkLabel("Arc Angle: ")
    contents += angleField
    contents += mkLabel("Pixel Per Unit")
    contents += pixelField
    contents += paintButton
    contents += timeUseLabel

    listenTo(paintButton)

    reactions += {
      case ButtonClicked(`paintButton`)=>{
        val sampleNumber = sampleNumberField.text.toInt
        val arcAngle = angleField.text.toInt
        val pixelPerUnit = pixelField.text.toDouble
        val regionWidth = widthField.text.toDouble
        val regionHeight = heightField.text.toDouble
        val region = RecRegion(regionWidth,regionHeight)
        val calcTimeUseHandler = (milliSec:Long)=>{
          timeUseLabel.text = s"$milliSec milliseconds"
        }
        generateNetwork(region,sampleNumber,arcAngle,pixelPerUnit = pixelPerUnit,calcTimeUseHandler)
      }
    }
  }

  val canvasFrame = new MainFrame {
    title = "Percolation"
    contents = new FlowPanel()
  }
  canvasFrame.visible = true

  val mainFrame = new MainFrame{
    title = "Percolation"
    contents = controlPanel
  }
  override def top: Frame = mainFrame

  def generateNetwork(region: RecRegion,sampleNumber:Int, arcAngle:Int, pixelPerUnit:Double, timeUseHandler:Long=>Unit):Unit ={
    val startTime = System.currentTimeMillis()
    val network = {
      val experiment = new PercolationExperiment(region,arcAngle*MyMath.deg,sampleNumber,new Random())
      val arcs = experiment.sampleArcs()
      val builder = new NetworkBuilder(region,2.01)
      builder.makeNetwork(arcs)
    }
    network.visitNode(network.top)

    val endTime = System.currentTimeMillis()
    val calcTimeUse = endTime - startTime
    timeUseHandler(calcTimeUse)

    val plot = new PercolationPlot(network,pixelPerUnit,arcAngle){background = Color.white}
    canvasFrame.contents=plot
    mainFrame.pack()
  }
}