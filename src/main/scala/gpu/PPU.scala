package gpu

import chisel3._
import chisel3.util._

// Takes 90K cycles to render an entire frame (NES) 256x240 pixels
// each pixel takes 1.3 cycles to render (extra prob fetch or something)
// a scanline (256 pixels) takes 341 cycles
// why 262 scanlines if only 240 pixels in height? Maybe some gets cutoff?
class PPU extends Module {
  // Picture Processing Unit
  // 8 cycles to do stuff, maybe cause serial beforehand
  val io = IO(new Bundle {
    val vram_addr = Input(UInt(8.W))
    val palette_addr = Input(UInt(8.W))
    val fine_x = Input(UInt(8.W))
    val latch_in_0 = Input(Bool())
    val latch_in_1 = Input(Bool())
  })

  // create vram module
  val vram = Module(new VRAM)

  // 16 bit shift registers PISO, loaded every 8 cycles
  // upper 8 bits = next tile's data (8x8), prob just an 8bit addr into VRAM
  // lower 8 bits = pixel to render (x, y)
  // contains pattern table data
  val shift_reg_0 = ShiftRegister(io.vram_addr, 16)
  val shift_reg_1 = ShiftRegister(shift_reg_0, 16)

  // 8-bit SIPO, loaded every 8 cycles
  // fetch the next palette for the tile above
  // latch serial in, outputs into the register
  val sipo_0 = ShiftRegister(io.palette_addr, 8)
  val sipo_1 = ShiftRegister(sipo_0, 8)

  // we only need 64 bits (8 bytes) to store each pixel's color
  // also 3 color emphasis bits
  // the background palette uses 13 of the 64 colors
  // with only one common backdrop color
  // a sprite can use up to 12 different colors (4 of 3 subpalettes)
  // a set of 8x8 pixels can have 3 different colors to choose from
  // so as a 8x16 set of pixels

  // mux fine_x
  // only the upper 8 bits of shift reg
  val BG_pixel_0 = Mux(io.latch_in_0, shift_reg_0, sipo_0)
  val BG_pixel_1 = Mux(io.latch_in_1, shift_reg_0, sipo_1)

}

class VRAM extends Module {}

// object SIPO extends Module {
//   val reg = RegInit(0.U(SIPO.width))
// }

/** Serial in, parallel out
  *
  * @param width
  *   >= 1
  */
class SIPO(width: Int) extends Module {
  val io = IO(new Bundle {
    val in = Input(Bool())
    // it makes sense to make a vec or wire of Bool, len width
    val parallel_out = Output(UInt(width.W))
    val out = Output(Bool())
  })

  // static reg or maybe companion object
  // does it keep its value?
  // but a register should get remebered
  // not a wire or a local var

  // ignores reset
  val register = Reg(UInt(width.W))
  register >> 1

  // output serial
  io.out := 1.U(width.W) & register

  // output parallel
  io.parallel_out := register
}
