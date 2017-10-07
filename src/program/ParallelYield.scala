package program

import actors.Actor._
import actors._
import scala.collection.parallel.mutable

/**
 * Created by weijiayi on 3/9/15.
 */
class ParallelYield[T](start:Int,end:Int,step:Int,f:Int=>T,remind:Double=>Unit,val processors:Int){
  def this(start:Int,end:Int,step:Int,f:Int=>T, processors:Int = 8) = this(start,end,step,f,p=>{},processors)

  var results = IndexedSeq[T]()
  var index = start
  var workingProcessors = 0
  private var finished = 0

  def getResult():IndexedSeq[T] ={
    val slaves = spawnSlaves()
    slaves.foreach(_.start())
    workingProcessors = slaves.length
    waitForRequest()
  }

  def waitForRequest(): IndexedSeq[T] ={
    var ended = false
    while(!ended) {
      receive {
        case RequestWork(slave) =>
          val newIndex = index + step
          if (newIndex < end) {
            index = newIndex
            slave ! Work(index)
          } else {
            slave ! Stop
            workingProcessors -= 1
            if (workingProcessors == 0) {
              ended=true
            }
          }
        case result: T =>
          finished += 1
          remind(finished.toDouble/(end-start)*step)
          results = results :+ result
      }
    }
    results
  }

  private def spawnSlaves() ={
    (0 until processors).map(i=>new Slave(self,i))
  }

  class Slave(val master:Actor,val id:Int) extends Actor{
    def act(): Unit ={
      println(s"Slave $id Started")
      master ! RequestWork(this)
      waitForWork()
    }

    def waitForWork(): Unit ={
      receive{
        case Work(n)=> {
          val result = f(n)
          master ! result
          master ! RequestWork(this)
          waitForWork()
        }
        case Stop => println(s"Slave $id Stopped")
      }
    }
  }

  case class Work(n:Int)
  object Stop
  case class RequestWork(slave: Slave)
}
