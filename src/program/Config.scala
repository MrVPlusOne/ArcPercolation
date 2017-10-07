package program

import scala.io.Source

case class Config(map: Map[String, String]){
  def getInt(key: String): Int = {
    val v = map.getOrElse(key, throw new Exception(s"Key not exist in config: '$key'"))
    try{
      v.toInt
    } catch {
      case _: Exception => throw new Exception(s"Can't read int key $key")
    }
  }
  def getDouble(key: String): Double = {
    val v = map.getOrElse(key, throw new Exception(s"Key not exist in config: '$key'"))
    try {
      v.toDouble
    } catch {
      case _: Exception => throw new Exception(s"Can't read float key $key")
    }
  }
}

object Config {
  def readFromFile(file: String): Config = {
    val lines = Source.fromFile(file).getLines().map(_.trim).filterNot(_.isEmpty)
    val map = lines.map{ line =>
      val Array(left, right) = line.split(raw"\s*=\s*")
      left -> right
    }.toMap
    Config(map)
  }
}
