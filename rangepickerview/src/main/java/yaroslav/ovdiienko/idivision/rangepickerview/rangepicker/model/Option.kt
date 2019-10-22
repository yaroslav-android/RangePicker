package yaroslav.ovdiienko.idivision.rangepickerview.rangepicker.model

@Deprecated("RangePickerView is no more valid. Please replace it with ScopePikerView.")
data class Option(private val option: String) {
    fun getOption(): String {
        return option
    }
}