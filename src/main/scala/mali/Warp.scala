package mali

import chisel3._
import chisel3.util._

// Block scheduler. Takes in threads which becomes blocks?
class BlockScheduler(len: Int = 100) extends Module {
  val io = IO(new Bundle {
    val in_warp = Input(new Warp)
    val out_warp = Output(new Warp)
  })

  // a fifo that takes in 1 warp per tick and outputs 1 warp per tick. A cicrcular buffer with up to 100 queued warps
  val fifo = Module(new Fifo(new Warp, len))

  io.out_warp := fifo.io.deq
  fifo.io.enq := io.in_warp
}

// A warp is a group with N threads, e.g. 32. A block is simply a queue of warps
class Warp extends Bundle {
  val prio = UInt(32.W)
}

// @Len: Size of FIFO scheduler per SM. On an SM, schedules a warp to be executed by all of its cores
class WarpScheduler(len: Int) extends Module {
  val io = IO(new Bundle {
    val in_warp = Input(new Warp)
    val out_warp = Output(new Warp)
  })
  val thread = UInt(len.W)
}

class DecoupledIO[T <: Data](data: T) extends Bundle {
  val ready = Input(Bool())
  val valid = Output(Bool())
  val bits = Output(data)
}

class Fifo[T <: Data](data: T, n: Int) extends Module {
  val io = IO(new Bundle {
    val enq = Flipped(new DecoupledIO(data))
    val deq = new DecoupledIO(data)
  })

  val enqPtr = RegInit(0.U((log2Up(n)).W))
  val deqPtr = RegInit(0.U((log2Up(n)).W))
  val isFull = RegInit(false.B)
  val doEnq = io.enq.ready && io.enq.valid
  val doDeq = io.deq.ready && io.deq.valid
  val isEmpty = !isFull && (enqPtr === deqPtr)
  val deqPtrInc = deqPtr + 1.U
  val enqPtrInc = enqPtr + 1.U

  val isFullNext = Mux(
    doEnq && ~doDeq && (enqPtrInc === deqPtr),
    true.B,
    Mux(doDeq && isFull, false.B, isFull)
  )

  enqPtr := Mux(doEnq, enqPtrInc, enqPtr)
  deqPtr := Mux(doDeq, deqPtrInc, deqPtr)
  isFull := isFullNext

  // it was "gen"
  val ram = Mem(n, data)
  when(doEnq) {
    ram(enqPtr) := io.enq.bits
  }

  io.enq.ready := !isFull
  io.deq.ready := !isEmpty
  ram(deqPtr) <> io.deq.bits
}
