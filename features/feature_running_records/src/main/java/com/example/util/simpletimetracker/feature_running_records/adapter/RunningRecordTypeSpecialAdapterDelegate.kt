package com.example.util.simpletimetracker.feature_running_records.adapter

import com.example.util.simpletimetracker.feature_base_adapter.createRecyclerBindingAdapterDelegate
import com.example.util.simpletimetracker.feature_views.extension.dpToPx
import com.example.util.simpletimetracker.feature_views.extension.setOnClickWith
import com.example.util.simpletimetracker.feature_running_records.databinding.ItemRunningRecordTypeLayoutBinding as Binding
import com.example.util.simpletimetracker.feature_running_records.viewData.RunningRecordTypeSpecialViewData as ViewData

fun createRunningRecordTypeSpecialAdapterDelegate(
    onItemClick: ((ViewData) -> Unit)
) = createRecyclerBindingAdapterDelegate<ViewData, Binding>(
    Binding::inflate
) { binding, item, _ ->

    with(binding.viewRecordTypeItem) {
        item as ViewData

        layoutParams = layoutParams.also { params ->
            params.width = item.width.dpToPx()
            params.height = item.height.dpToPx()
        }

        itemIsRow = item.asRow
        itemColor = item.color
        itemIcon = item.iconId
        itemName = item.name
        setOnClickWith(item, onItemClick)
    }
}