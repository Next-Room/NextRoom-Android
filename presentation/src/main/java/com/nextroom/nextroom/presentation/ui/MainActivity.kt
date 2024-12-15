package com.nextroom.nextroom.presentation.ui

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
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
import com.nextroom.nextroom.presentation.util.WindowInsetsManager
import com.nextroom.nextroom.presentation.util.WindowInsetsManagerImpl
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity :
    AppCompatActivity(),
    WindowInsetsManager by WindowInsetsManagerImpl() {
    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModels()
    private val billingViewModel: BillingViewModel by viewModels()

    @Inject
    lateinit var billingClientLifecycle: BillingClientLifecycle

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(getColor(R.color.Dark01)),
        )
        setActivity(this)

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycle.addObserver(billingClientLifecycle)

        repeatOnStarted {
            viewModel.event.collect(::observe)
        }
        repeatOn(Lifecycle.State.CREATED) {
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

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleDeepLink(intent)
    }

    private fun handleDeepLink(intent: Intent?) {
        intent?.data?.let { deepLinkUri ->
            getFindNavController()?.graph?.forEach { navDestination ->
                if (navDestination.hasDeepLink(deepLinkUri)) {
                    getFindNavController()?.navigate(navDestination.id)
                    return@let
                }
            }
        }
    }

    private fun observe(event: MainEvent) {
        when (event) {
            is MainEvent.GoToGameScreen -> {
                getFindNavController()?.navigate(R.id.gameFragment)
            }

            MainEvent.GoToLoginScreen -> {
                getFindNavController()?.navigate(R.id.action_global_loginFragment)
            }

            MainEvent.ShowForceUpdateDialog -> showForceUpdateDialog()
        }
    }

    private fun getFindNavController() = supportFragmentManager.findFragmentById(R.id.fcv_nav_host)?.findNavController()

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
