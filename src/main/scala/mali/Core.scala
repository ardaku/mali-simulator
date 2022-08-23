package mali

import chisel3._
import mali.RegisterFile
import mali.Memory

// Analogous to an SM
class Core(len: Int) extends Module {
  val registers = new RegisterFile(32, 32)

  val fifo = Module(new Fifo(new Warp, len))

  // fetch
  val program_counter = UInt(32.W)
  // send a request to the fetch unit, which interfaces with VRAM?
  val fetch = new Fetch()

  // decode and execute x32 (ALU)

  // do again at next tick
}

// should fetch have a pointer to ram? maybe a wire to ram
class Fetch extends Module {
  val link_to_vram = new Memory()
}
