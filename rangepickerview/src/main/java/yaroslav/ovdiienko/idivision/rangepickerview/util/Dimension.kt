package yaroslav.ovdiienko.idivision.rangepickerview.util


interface Dimension {
  fun toDp(dp: Float): Float
  fun toSp(sp: Float): Float

  fun getMinWidthValue(): Int
  fun getMaxWidthValue(): Int
  fun getScreenHeight(): Int
  fun getScreenWidth(): Int
}