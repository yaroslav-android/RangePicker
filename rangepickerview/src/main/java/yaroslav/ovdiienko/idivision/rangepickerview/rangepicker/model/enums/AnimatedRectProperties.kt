package yaroslav.ovdiienko.idivision.rangepickerview.rangepicker.model.enums


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