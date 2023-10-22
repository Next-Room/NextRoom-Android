package com.nextroom.nextroom.presentation.ui

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.databinding.ActivityMainBinding
import com.nextroom.nextroom.presentation.extension.repeatOnStarted
import com.nextroom.nextroom.presentation.util.BillingClientLifecycle
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModels()
    private val billingViewModel: BillingViewModel by viewModels()

    private val billingClientLifecycle by lazy {
        BillingClientLifecycle.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycle.addObserver(billingClientLifecycle)
        billingViewModel.setBillingClientLifecycle(billingClientLifecycle)

        binding.fcvNavHost.apply {
            systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            setOnApplyWindowInsetsListener { _, insets -> insets }
        }

        repeatOnStarted {
            viewModel.event.collect(::observe)
        }
        repeatOnStarted {
            viewModel.loginState.collect { loggedIn ->
                if (!loggedIn) viewModel.logout()
            }
        }
        repeatOnStarted {
            billingViewModel.buyEvent.collect {
                billingClientLifecycle.launchBillingFlow(this@MainActivity, it)
            }
        }
    }

    private fun observe(event: MainEvent) {
        val navController =
            supportFragmentManager.findFragmentById(R.id.fcv_nav_host)?.findNavController()

        when (event) {
            is MainEvent.GoToGameScreen -> {
                navController?.navigate(R.id.mainFragment)
            }

            MainEvent.GoToAdminCode -> {
                navController?.navigate(R.id.action_global_adminCodeFragment)
            }
        }
    }
}
