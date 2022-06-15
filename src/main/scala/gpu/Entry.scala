package gpu

import chisel3._

object Main extends App {
  println("Creating a new GPU...")

  (new chisel3.stage.ChiselStage)
    .emitVerilog(new GPU(), Array("--target-dir", "generated"))
}

class GPU extends Module {}
