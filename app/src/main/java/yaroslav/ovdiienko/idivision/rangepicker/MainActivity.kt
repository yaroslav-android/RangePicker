package yaroslav.ovdiienko.idivision.rangepicker

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import yaroslav.ovdiienko.idivision.rangepickerview.rangepicker.model.Option

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        oldView()
        newView()
    }

    private fun oldView() {
        picker.apply {
            visibility = View.VISIBLE
            setOptions(getOptions())
            setDefaultSelectedPositions(0 to 4)
            setOnRangeSelectedListener { view, leftPoint, rightPoint ->
                Toast.makeText(
                    this@MainActivity,
                    view.getSelectedIndexes().toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        btn_get_values.visibility = View.VISIBLE
        btn_get_values.setOnClickListener {
            Toast.makeText(this, "${picker.getSelectedItems()}", Toast.LENGTH_SHORT).show()
            picker.resetToDefaultState()
        }
    }

    private fun newView() {
        spv.apply {
            setOptions(getOptionsStrings())
        }
    }

    private fun getOptions(): List<Option> {
        return getOptionsStrings().map { Option(it) }
    }

    private fun getOptionsStrings() = listOf("Single", "2", "3", "4", "5+")
}