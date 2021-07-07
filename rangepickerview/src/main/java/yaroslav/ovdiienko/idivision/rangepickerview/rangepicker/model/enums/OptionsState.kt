package yaroslav.ovdiienko.idivision.rangepickerview.rangepicker.model.enums

@Deprecated("RangePickerView is no more valid. Please replace it with ScopePikerView.")
enum class OptionsState(val tapId: Int) {
    SINGLE(0),
    MULTIPLE(1),
    NONE(-1);

    companion object {
        fun findByTapId(tapId: Int): OptionsState {
            return OptionsState.values().firstOrNull { it.tapId == tapId } ?: NONE
        }
    }
}