package nz.co.warehouseandroidtest.ui.common

import kotlin.math.abs
import kotlin.math.roundToInt

internal fun formatPrice(price: Double): String {
    val cents = (price * 100).roundToInt()
    val dollars = cents / 100
    val remainder = abs(cents % 100)
    return "$$dollars.${remainder.toString().padStart(2, '0')}"
}
