package gpu

import chisel3._
import chisel3.util._

// Takes 90K cycles to render an entire frame (NES) 256x240 pixels
// each pixel takes 1.3 cycles to render (extra prob fetch or something)
// a scanline (256 pixels) takes 341 cycles
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

  // mux fine_x
  // only the upper 8 bits of shift reg
  val BG_pixel_0 = Mux(io.latch_in_0, shift_reg_0, sipo_0)
  val BG_pixel_1 = Mux(io.latch_in_1, shift_reg_0, sipo_1)

}

class VRAM extends Module {}
