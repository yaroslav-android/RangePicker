package yaroslav.ovdiienko.idivision.rangepickerview.scopepicker.model.exceptions


class MaxWordLengthException : RuntimeException {
    constructor(message: String) : super(message)
    constructor(error: Throwable) : super(error)
}