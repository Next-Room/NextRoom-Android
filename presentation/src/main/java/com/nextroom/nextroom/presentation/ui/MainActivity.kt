package com.nextroom.nextroom.presentation.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.common.NRDialog
import com.nextroom.nextroom.presentation.databinding.ActivityMainBinding
import com.nextroom.nextroom.presentation.extension.repeatOn
import com.nextroom.nextroom.presentation.extension.repeatOnStarted
import com.nextroom.nextroom.presentation.ui.billing.BillingViewModel
import com.nextroom.nextroom.presentation.util.BillingClientLifecycle
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModels()
    private val billingViewModel: BillingViewModel by viewModels()

    @Inject
    lateinit var billingClientLifecycle: BillingClientLifecycle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycle.addObserver(billingClientLifecycle)

        binding.fcvNavHost.apply {
            systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            setOnApplyWindowInsetsListener { _, insets -> insets }
        }

        repeatOnStarted {
            viewModel.event.collect(::observe)
        }
        repeatOn(Lifecycle.State.CREATED) {
            viewModel.loginState.collect { loggedIn ->
                if (!loggedIn) viewModel.logout()
            }
        }
        repeatOnStarted {
            viewModel.minVersionFlow().collect {
                viewModel.compareVersion(
                    appVersion = getAppVersion(),
                    minVersion = it,
                )
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

            MainEvent.GoToLoginScreen -> {
                navController?.navigate(R.id.action_global_loginFragment)
            }

            MainEvent.ShowForceUpdateDialog -> showForceUpdateDialog()
        }
    }

    private fun getAppVersion(): String {
        return packageManager
            .getPackageInfo(packageName, 0)
            .versionName
    }

    private fun showForceUpdateDialog() {
        NRDialog.Builder(this)
            .setTitle(R.string.dialog_noti)
            .setMessage(R.string.dialog_update_message)
            .setCancelable(false)
            .setPositiveButton(R.string.dialog_update) { _, _ ->
                Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")).also {
                    startActivity(it)
                }
            }
            .setNegativeButton(R.string.dialog_app_finish) { _, _ ->
                finish()
            }.show(supportFragmentManager, "MainActivity")
    }
}
