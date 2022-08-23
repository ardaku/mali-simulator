package mali

import chisel3._

// Just storage
class RegisterFile(n: Int, size: Int) extends Bundle {
  // 32X or N registers of size S
  val registers = VecInit(Seq.fill(n)(0.U(size.W)))
}

class ALU extends Module {
  val io = IO(new Bundle {
    val opcode = Input(Bool())
    val section1 = Input(UInt(10.W))
    val section2 = Input(UInt(20.W))
  })

  // figure out the specific instruction

  // figure out the answer of the instruction
  // e.g. memory read, write
  // fma, add, sub, bitwise, tensor op, special op
}
