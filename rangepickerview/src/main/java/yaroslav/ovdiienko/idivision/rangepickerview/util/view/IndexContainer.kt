package yaroslav.ovdiienko.idivision.rangepickerview.util.view

import yaroslav.ovdiienko.idivision.rangepickerview.rangepicker.model.enums.OptionsState


class IndexContainer {
    var firstPreviousIndex: Int = -1
    var secondPreviousIndex: Int = -1

    var firstNewIndex: Int = -1
    var secondNewIndex: Int = -1

    var firstDefaultIndex: Int = -1
    var secondDefaultIndex: Int = -1
    var defaultOptionsState: OptionsState = OptionsState.NONE
}