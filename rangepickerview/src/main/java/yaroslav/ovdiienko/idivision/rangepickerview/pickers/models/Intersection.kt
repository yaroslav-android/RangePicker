package yaroslav.ovdiienko.idivision.rangepickerview.pickers.models


sealed class Intersection {
  object Left : Intersection()
  object Right : Intersection()
  object Undefined : Intersection()
}