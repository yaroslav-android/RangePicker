package yaroslav.ovdiienko.idivision.rangepicker.util


enum class AnimatedRectProperties(val property: String) {
    LEFT("left"),
    RIGHT("right"),
    DEFAULT("");

    companion object {
        fun find(property: String): AnimatedRectProperties {
            return AnimatedRectProperties.values().firstOrNull { it.property == property }
                ?: DEFAULT
        }
    }
}