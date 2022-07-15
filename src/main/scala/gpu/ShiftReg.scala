package gpu

import chisel3._
import chisel3.util.switch

/** Serial in, parallel out
  *
  * @param width
  *   >= 1
  */
class SIPO(width: Int) extends Module {
  val io = IO(new Bundle {   
    val in = Input(Bool())
    val parallel_out = Output(UInt(width.W))
    val out = Output(Bool())
  })

  // maybe in_true as well. IDK since serial prob not

  // ignores reset
  val register = Reg(Vec(width, Bool()))
  for (i <- 1 until width) {
    register(i) := register(i - 1)
  }

  // output serial
  io.out := register.last

  // set input
  register(0) := io.in

  // fold
  io.parallel_out := register.fold(0.U(width.W))((acc, next) =>
    (acc << 1) | next
  )
}

/** Parallel in, serial out (In_true)
  *
  * @param width
  *   >= 1
  */
class PISO_IN_TRUE(width: Int) extends Module {
  val io = IO(new Bundle {
    val in = Input(UInt(width.W))
    val in_true = Input(Bool())
    val out = Output(Bool())
  })

  // the thing is that the way you should do it
  // is have an optional in for width cycles
  // to shift the entire thing out
  // before putting a new set in

  // prob dont need in_true
  // since it should be 0 after you do the parallel thing
  // your supposed to wait width cycles

  // there are some options like RST to reset though
  // idk, maybe expose some interface for it

  // ignores reset
  val register = Reg(Vec(width, Bool()))

  // take in the values
  // register := io.in
  when(io.in_true) {
    for (i <- 0 until width) {
      register(i) := io.in & 1.U(width.W)
    }
  }

  // right shift vec so that vec(0) is going to be replaced
  for (i <- 1 until width) {
    register(i) := register(i - 1)
  }

  // output serial
  io.out := register.last

  // set input, if in_true
  when(io.in_true) {
    register(0) := io.in
  }
}

/** Parallel in, serial out
  *
  * @param width
  *   >= 1
  */
class PISO(width: Int) extends Module {
  val io = IO(new Bundle {
    val in = Input(UInt(width.W))
    val out = Output(Bool())
  })

  // ignores reset
  val register = Reg(Vec(width, Bool()))

  // take in the values
  for (i <- 0 until width) {
    register(i) := io.in & 1.U(width.W)
  }

  // right shift vec so that vec(0) is going to be replaced
  for (i <- 1 until width) {
    register(i) := register(i - 1)
  }

  // output serial
  io.out := register.last

  // set input, if in_true
  register(0) := io.in
}
