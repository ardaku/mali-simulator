package gpu

import chisel3._

object Main extends App {
  println("Creating a new GPU...")

  (new chisel3.stage.ChiselStage)
    .emitVerilog(new GPU(), Array("--target-dir", "generated"))
}

// A control list is variable length. How to implement? sequential or stream based input?
class ControlListExecutor extends Module {
  val io = IO {
    new Bundle {
      val control_code = Bits(256.W)
      // control list = variable length command
      // next part => set of vertex attributes + primitives (triangles)
      val control_list = UInt(32.W)
    }
  }

  // use it to figure out what to do
  io.control_list := 0.U
}

class DMAWriter extends Module {}

class VertexCacher extends Module {}

class GPU extends Module {}
