package gpu

import chisel3._
import chiseltest._
import org.scalatest.freespec.AnyFreeSpec
import chisel3.experimental.BundleLiterals._

class PPUTest extends AnyFreeSpec with ChiselScalatestTester {
  "SIPO should work for all input combinations" in {
    test(new SIPO(4)) { sipo =>
      sipo.io.in.poke(true.B)
      sipo.clock.step(1)
      sipo.io.out.expect(false.B)
      sipo.io.parallel_out.expect(8.U(4.W))

      sipo.io.in.poke(false.B)
      sipo.clock.step(3)
      sipo.io.out.expect(true.B)
      sipo.io.parallel_out.expect(1.U(4.W))
    }
  }

}
