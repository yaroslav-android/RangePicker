package yaroslav.ovdiienko.idivision.rangepickerview.util.exceptions


class MaxOptionsSizeException : RuntimeException {
  constructor(message: String) : super(message)
  constructor(error: Throwable) : super(error)
}