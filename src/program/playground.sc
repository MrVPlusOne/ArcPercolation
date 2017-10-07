import program.ParallelYield
import actors.Actor._

new ParallelYield[Int](1,1000,1,i=>i*i,self).act()
receive{
  case r=>println(r)
}