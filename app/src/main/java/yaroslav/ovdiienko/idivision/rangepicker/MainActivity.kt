package yaroslav.ovdiienko.idivision.rangepicker

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import yaroslav.ovdiienko.idivision.rangepickerview.rangepicker.model.Option

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        picker.apply {
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

        btn_get_values.setOnClickListener {
            Toast.makeText(this, "${picker.getSelectedItems()}", Toast.LENGTH_SHORT).show()
            picker.resetToDefaultState()
        }
    }

    private fun getOptions(): List<Option> {
        return listOf(
            Option("Single"),
            Option("2"),
            Option("3"),
            Option("4"),
            Option("5+")
        )
    }
}
