package yaroslav.ovdiienko.idivision.rangepickerview.pickers.core


interface OptionsAgreement {
  fun addOption(option: String)
  fun addOptions(newOptions: List<String>)
  fun replaceOption(at: Int, newOption: String)
  fun getOptions(): List<String>
  fun clearOptions()
}