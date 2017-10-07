package program
import program.ParallelYield
import actors.Actor._

object TestProgram{

  def main(args: Array[String]) {
    val result = new ParallelYield[Int](1,1001,1,i=>i*i).getResult()
    println(result)
  }

}