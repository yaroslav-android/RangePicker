package yaroslav.ovdiienko.idivision.rangepickerview.scopepicker

// TODO: provide documentation
interface PickerAgreement {

  /***/
  fun addOption(option: String)

  /***/
  fun replaceOption(at: Int, newOption: String)

  /***/
  fun addOptions(newOptions: List<String>)

  /***/
  fun getOptions(): List<String>

  /***/
  fun clearOptions()

  /***/
  fun toSingleChoiceStrategy()

  /***/
  fun toDuoChoiceStrategy()

  // Planned:
  // fun getSelectedItems(): Pair<Int, Int>
  // fun setSelectedItems(items: Pair<Int, Int>)
  // fun addOnScopeSelectedListener(...)
  // fun removeOnScopeSelectedListener(...)
  // fun addOnScopeChangeListener(...)
  // fun removeOnScopeChangeListener(...)
  // fun resetToDefaultState(withAnimation: Boolean = true)

  // Nice to have:
  // fun setStripColor(color: Int)
  // fun setSelectedBoxColor(color: Int)
  // fun setSelectedTextColor(color: Int)
  // fun setUnselectedTextColor(color: Int)
  // [?] fun setVibrationPattern(...)
  // [?] fun enableVibration(...)
  // [?] fun disableVibration(...)
  // fun setCornerRadius(radius: Int)
  // fun setFont(font: Int)
  // fun setTextSize(size: Int)
  // fun setStripThickness(thickness: Int)
}