package yaroslav.ovdiienko.idivision.rangepickerview.pickers.models

import yaroslav.ovdiienko.idivision.rangepickerview.util.exceptions.MaxOptionCharactersSizeException
import yaroslav.ovdiienko.idivision.rangepickerview.util.exceptions.MaxOptionsSizeException


data class Options(private val list: MutableList<String> = mutableListOf()) {

  fun addOption(option: String) {
    check(option)
    list.add(option)
  }

  fun replaceOption(at: Int, newOption: String) {
    check(newOption)
    list[at] = newOption
  }

  fun addOptions(options: List<String>) {
    list.addAll(options)
  }

  fun getOptions() = list.toList()

  internal fun getOption(at: Int) = list[at]

  fun clear() {
    list.clear()
  }

  private fun check(candidate: String) {
    if (MAX_ELEMENTS_ALLOWED < list.size) {
      throw MaxOptionsSizeException(
        "Maximum available size is $MAX_ELEMENTS_ALLOWED elements, " +
            "but size is ${list.size} elements."
      )
    }

    if (MAX_LETTERS_IN_WORD_ALLOWED < candidate.length) {
      throw MaxOptionCharactersSizeException(
        "Maximum word length is $MAX_LETTERS_IN_WORD_ALLOWED characters, " +
            "but length of word \"$candidate\" is ${candidate.length} characters."
      )
    }
  }

  companion object {
    const val MAX_ELEMENTS_ALLOWED = 5
    const val MAX_LETTERS_IN_WORD_ALLOWED = 6
  }
}