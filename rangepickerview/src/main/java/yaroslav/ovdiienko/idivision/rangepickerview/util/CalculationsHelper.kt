package yaroslav.ovdiienko.idivision.rangepickerview.util


import kotlin.math.abs


class CalculationsHelper {
  var desiredCenterOfRect: Float = 0f
  var startingCenterOfRect: Float = 0f

  var widthOfDesiredRect: Float = 0f
  var widthOfStartingRect: Float = 0f

  var deltaWidth: Float = 0f
  var deltaCenter: Float = 0f

  fun calculateOnDirectionChange(): CalculationsHelper {
    return this.apply {
      deltaWidth = widthOfDesiredRect - widthOfStartingRect
      deltaCenter = abs(desiredCenterOfRect - startingCenterOfRect)
    }
  }

  fun calculateWidthChanges(lastCenterAfterMove: Float): Float {
    return with(deltaCenter - lastCenterAfterMove) {
      if (this != 0f) (this * deltaWidth) / deltaCenter else deltaCenter
    }
  }
}
