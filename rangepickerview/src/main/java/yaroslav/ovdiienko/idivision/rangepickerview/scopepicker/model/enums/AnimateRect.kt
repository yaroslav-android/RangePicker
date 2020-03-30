package yaroslav.ovdiienko.idivision.rangepickerview.scopepicker.model.enums

sealed class AnimateRect(val property: String) {
  object Left : AnimateRect("left")
  object Right : AnimateRect("right")
}