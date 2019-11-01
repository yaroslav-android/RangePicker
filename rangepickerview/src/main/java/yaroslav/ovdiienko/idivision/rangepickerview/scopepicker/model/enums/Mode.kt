package yaroslav.ovdiienko.idivision.rangepickerview.scopepicker.model.enums

import yaroslav.ovdiienko.idivision.rangepickerview.scopepicker.strategy.DuoPickStrategy
import yaroslav.ovdiienko.idivision.rangepickerview.scopepicker.strategy.SinglePickStrategy
import yaroslav.ovdiienko.idivision.rangepickerview.scopepicker.strategy.Strategy


sealed class Mode(strategy: Strategy) {
    object Single : Mode(SinglePickStrategy())
    object Duo : Mode(DuoPickStrategy())
}