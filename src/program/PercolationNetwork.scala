package program

import scala.collection.mutable

abstract class Node{
  def connectTo = connections
  private var connections = List[Node]()
  def addConnection(node: Node): Unit ={
    connections = node::connections
  }
  var visited = false
}

case class ArcNode(arc: Arc) extends Node{
  val id = ArcNode.allocateId()

  override def toString = s"Node(id: ${id})"
}
object ArcNode{
  private var currentId=0
  def allocateId():Int = {
    currentId+=1
    currentId-1
  }
}
case class EntryNode(edge: Entry) extends Node

case class PercolationNetwork(nodes:List[ArcNode],top:EntryNode,bottom:EntryNode,recRegion: RecRegion) {
//  def visitNode(node: Node): Unit ={
//    assert(node.visited == false, "Every node should only be visited once")
//    node.visited=true
//    node.connectTo.foreach(n=>if(!n.visited) visitNode(n))
//  }

  def visitNode(node:Node): Unit ={
    node.visited = true
    val stack = mutable.Stack[Node]()
    stack.push(node)

    while(stack.nonEmpty){
      val next = stack.pop()
      next.connectTo.foreach(n=>{
        if(!n.visited){
          n.visited = true
          stack.push(n)
        }
      })
    }
  }
}

class NetworkBuilder(recRegion: RecRegion, preferredDivideSize:Double){
  val columnDivide = (recRegion.width/preferredDivideSize).toInt
  val rowDivide = (recRegion.height/preferredDivideSize).toInt
  private val dx = recRegion.width/columnDivide
  private val dy = recRegion.height/rowDivide
  assert(dx>1)
  assert(dy>1)
  private val cellArray = Range(0,rowDivide*columnDivide).map(new RecCell(_))

  def getCellAt(row:Int,col:Int) = cellArray(row*columnDivide+col)

  def getCellOptional(row:Int,col:Int):Option[RecCell] = {
    if(row<0 || col<0 || row>=rowDivide || col>=columnDivide) None
    else Some(getCellAt(row,col))
  }
  def getAdjacentCells(row:Int,col:Int):List[RecCell]={
    val list = for(r<- row-1 to row+1;c<- col-1 to col+1) yield getCellOptional(r,c)
    def loop(acc:List[RecCell],list:List[Option[RecCell]]):List[RecCell] = list match{
      case h::t=> h match{
        case Some(rec)=> loop(rec::acc,t)
        case None=> loop(acc,t)
      }
      case Nil=>acc
    }
    loop(Nil,list.toList)
  }

  class RecCell(val id:Int){

    def addNode(node: ArcNode): Unit = {
      arcNodes = node::arcNodes
    }

    var arcNodes = List[ArcNode]()

    override def toString = {
      val row = id/columnDivide
      val col = id%columnDivide
      s"Cell($id, r = ${row}, c = ${col}})"
    }
  }

  def addArc(arc: Arc): Unit ={
    val row = (arc.center.y/dy).toInt
    val col = (arc.center.x/dx).toInt
    val adjacentCells = getAdjacentCells(row=row,col=col)
    val arcNode = new ArcNode(arc)
    for(rec<- adjacentCells; node<-rec.arcNodes){
      if(arc.intersects(node.arc)){
        node.addConnection(arcNode)
        arcNode.addConnection(node)
      }
    }
    getCellOptional(row,col).map(_.addNode(arcNode))
  }

  def makeNetwork: PercolationNetwork = {
    val nodes = cellArray.map(cell=>cell.arcNodes).flatten.toList
    val topEntry = new HorizontalEntry(recRegion.height)
    val topNode = EntryNode(topEntry)
    val bottomEntry = new HorizontalEntry(0)
    val bottomNode = EntryNode(bottomEntry)
    for(n<-nodes){
      if(topEntry.intersects(n.arc)){
        n.addConnection(topNode)
        topNode.addConnection(n)
      }else if(bottomEntry.intersects(n.arc)){
        n.addConnection(bottomNode)
        bottomNode.addConnection(n)
      }
    }
    PercolationNetwork(nodes,topNode,bottomNode,recRegion = recRegion)
  }

  def makeNetwork(arcs:List[Arc]): PercolationNetwork = {
    arcs.foreach(addArc)
    this.makeNetwork
  }
}