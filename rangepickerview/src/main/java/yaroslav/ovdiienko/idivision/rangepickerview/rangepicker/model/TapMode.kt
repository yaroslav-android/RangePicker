package yaroslav.ovdiienko.idivision.rangepickerview.rangepicker.model


enum class TapMode(val tapId: Int) {
    SINGLE(0),
    MULTIPLE(1),
    NONE(-1);

    companion object {
        fun findByTapId(tapId: Int): TapMode {
            return TapMode.values().firstOrNull { it.tapId == tapId } ?: NONE
        }
    }
}