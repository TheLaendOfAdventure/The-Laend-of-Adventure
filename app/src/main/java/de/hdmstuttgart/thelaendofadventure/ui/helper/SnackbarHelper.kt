package de.hdmstuttgart.thelaendofadventure.ui.helper

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Color
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.widget.ProgressBar
import com.google.android.material.snackbar.Snackbar
import de.hdmstuttgart.the_laend_of_adventure.databinding.SnackbarDefaultBinding

class SnackbarHelper(private val context: Context) {

    private var snackbar: Snackbar? = null
    private var timer: CountDownTimer? = null

    fun showTimerSnackbar(message: String, iconResource: Int) {
        val binding = SnackbarDefaultBinding.inflate(LayoutInflater.from(context), null, false)
        binding.textMessage.text = message
        binding.imageIcon.setImageResource(iconResource)

        val progressBar = binding.progressBar
        progressBar.max = TIMER_DURATION.toInt()

        val rootView = getRootViewFromContext(context)
        snackbar = Snackbar.make(rootView!!, "", Snackbar.LENGTH_INDEFINITE)
        val snackbarLayout = snackbar!!.view as Snackbar.SnackbarLayout
        snackbarLayout.setPadding(0, 0, 0, 0)
        snackbarLayout.setBackgroundColor(Color.TRANSPARENT)
        snackbarLayout.addView(binding.root, 0)

        setupTimer(progressBar)
        setupSnackbarCallbacks()

        snackbar?.show()
        timer?.start()
    }

    private fun setupTimer(progressBar: ProgressBar) {
        timer = object : CountDownTimer(TIMER_DURATION, TIMER_STEP_SIZE) {
            override fun onTick(millisUntilFinished: Long) {
                val progress = (TIMER_DURATION - millisUntilFinished) / TIMER_DURATION.toFloat()
                val progressValue = (progress * progressBar.max).toInt()
                progressBar.progress = progressValue
            }

            override fun onFinish() {
                dismissSnackbar()
            }
        }
    }

    private fun setupSnackbarCallbacks() {
        snackbar?.addCallback(object : Snackbar.Callback() {
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                super.onDismissed(transientBottomBar, event)
                timer?.cancel()
            }
        })
    }

    fun dismissSnackbar() {
        snackbar?.dismiss()
    }

    private fun getRootViewFromContext(context: Context): View? {
        var rootView: View? = null

        if (context is Activity) {
            rootView = context.window.decorView.rootView
        } else if (context is ContextWrapper) {
            val baseContext = context.baseContext
            rootView = getRootViewFromContext(baseContext)
        }

        return rootView
    }

    companion object {
        private const val TIMER_STEP_SIZE: Long = 20
        private const val TIMER_DURATION: Long = 5000
    }
}
