package com.example.util.simpletimetracker.feature_widget.universal.activity.view

import android.os.Bundle
import com.example.util.simpletimetracker.core.base.BaseActivity
import com.example.util.simpletimetracker.core.manager.ThemeManager
import com.example.util.simpletimetracker.core.provider.ContextProvider
import com.example.util.simpletimetracker.feature_widget.R
import com.example.util.simpletimetracker.navigation.Router
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class WidgetUniversalActivity : BaseActivity() {

    @Inject
    lateinit var router: Router

    @Inject
    lateinit var themeManager: ThemeManager

    @Inject
    lateinit var contextProvider: ContextProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        contextProvider.attach(this)

        themeManager.setTheme(this)
        setContentView(R.layout.widget_universal_activity)
        router.bind(this)
    }
}
