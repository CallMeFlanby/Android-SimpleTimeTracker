package com.example.util.simpletimetracker.feature_change_record_type

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class ChangeRecordTypeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(
            R.layout.change_record_type_fragment,
            container,
            false
        )
    }

    companion object {
        fun newInstance() = ChangeRecordTypeFragment()
    }
}