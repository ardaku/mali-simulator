package mali

import chisel3._

class ReadWriteSmem extends Module {
  val width: Int = 8
  val io = IO(new Bundle {
    val enable = Input(Bool())
    val write = Input(Bool())
    val addr = Input(UInt(10.W))
    val dataIn = Input(UInt(width.W))
    val dataOut = Output(UInt(width.W))
  })

  val mem = SyncReadMem(1024, UInt(width.W))
  // Create one write port and one read port
  mem.write(io.addr, io.dataIn)
  io.dataOut := mem.read(io.addr, io.enable)
}

// General purpose memory module
class Memory extends Module {
  val io = IO(new Bundle {
    val in_row = Input(UInt(32.W))
    val in_col = Input(UInt(32.W))
    val out_byte = Output(UInt(8.W))
  })

  val memory = new ReadWriteSmem()

  // a row x col addressable arch. TO address, row-col to get that byte
}
