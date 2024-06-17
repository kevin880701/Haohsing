package com.clockworkorange.haohsing.ui.dialog

import com.clockworkorange.domain.entity.AreaInfo
import com.clockworkorange.domain.entity.PlaceInfo

sealed class PlaceDialogType {
    object Add : PlaceDialogType()
    data class Select(val place: List<PlaceInfo>) : PlaceDialogType()
}

sealed class AreaDialogType {
    object Add : AreaDialogType()
    data class Select(val area: List<AreaInfo>) : AreaDialogType()
}