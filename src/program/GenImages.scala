package program

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import scala.util.Random

object GenImages {
  def main(args: Array[String]): Unit = {
    val config = Config.readFromFile("config.txt")
    val width = config.getInt("width")
    val height = config.getInt("height")

    val region = RecRegion(width, height)
    val sampleNumber: Int = config.getInt("sampleNumber")
    val arcAngle: Int = config.getInt("arcAngle")
    val pixelPerUnit: Double = config.getDouble("pixelPerUnit")
    val gridSize: Double = config.getDouble("gridSize")
    val seed = config.getInt("seed")
    val imageNum = config.getInt("imageNum")

    val random = new Random(seed)

    createIfNotExist("positive")
    createIfNotExist("negative")

    for(i <- 0 until imageNum){
      val network = {
        val experiment = new PercolationExperiment(region, arcAngle * MyMath.deg, sampleNumber, random)
        val arcs = experiment.sampleArcs()
        val builder = new NetworkBuilder(region, gridSize)
        builder.makeNetwork(arcs)
      }
      network.visitNode(network.top)

      val percolated = network.bottom.visited

      val plot = PercolationPlot(network, pixelPerUnit, arcAngle,
        visitedColor = Color.black,
        normalColor = Color.black)

      val image = plotImage(plot)

      val dir = if(percolated) "positive" else "negative"
      val path = s"$dir/$i.png"
      val file = new File(path)
      ImageIO.write(image, "png", file)
      println(s"[$i/$imageNum] image write to $path")
    }
  }

  def plotImage(plot: PercolationPlot): BufferedImage = {
    val imageWidth = plot.imageWidth
    val imageHeight = plot.imageHeight
    val image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_BYTE_GRAY)
    val g2d = image.createGraphics()
    g2d.setPaint(Color.white)
    g2d.fillRect(0, 0, image.getWidth, image.getHeight)
    plot.plotToGraphics(g2d)
    image
  }

  def createIfNotExist(name: String): Unit = {
    val file = new File(name)
    if(!file.exists())
      file.mkdir()
  }
}
