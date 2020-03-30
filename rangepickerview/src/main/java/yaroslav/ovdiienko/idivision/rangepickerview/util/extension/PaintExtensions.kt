package yaroslav.ovdiienko.idivision.rangepickerview.util.extension

import android.graphics.Color
import android.graphics.Paint


fun Paint.applySelectedTextSettings() {
  this.apply {
    isAntiAlias = true
    style = Paint.Style.FILL
    color = Color.parseColor("#ff000000")
  }
}

fun Paint.applyTextSettings() {
  this.apply {
    isAntiAlias = true
    style = Paint.Style.FILL
    color = Color.parseColor("#ff000000")
  }
}

fun Paint.applyStripSettings() {
  this.apply {
    isAntiAlias = true
    style = Paint.Style.FILL
  }
}

fun Paint.applySelectedBoxSettings() {
  this.apply {
    isAntiAlias = true
    style = Paint.Style.FILL
    color = Color.BLUE
  }
}