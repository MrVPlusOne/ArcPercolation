package program

object CommonEntrance {
  def main(args: Array[String]): Unit = {
    if(args.length == 1){
      GenImages.main(args)
    }else{
      ControlPanel.main(args)
    }
  }
}
