package de.hdmstuttgart.thelaendofadventure.ui.helper

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Color
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ProgressBar
import com.google.android.material.snackbar.Snackbar
import de.hdmstuttgart.the_laend_of_adventure.databinding.SnackbarDefaultBinding
import java.util.LinkedList
import java.util.Queue

class SnackbarHelper private constructor() {

    private var snackbar: Snackbar? = null
    private val snackbarQueue: Queue<SnackbarData> = LinkedList()

    data class SnackbarData(val context: Context, val message: String, val iconResource: Int)

    fun enqueueSnackbar(context: Context, message: String, iconResource: Int) {
        val snackbarData = SnackbarData(context, message, iconResource)
        snackbarQueue.offer(snackbarData)
        Log.d(TAG, "Snackbar: $message is added to queue!")
        if (snackbar == null) {
            showNextSnackbar()
        }
    }

    private fun showNextSnackbar() {
        if (snackbarQueue.isNotEmpty() && snackbar == null) {
            val (context, message, iconResource) = snackbarQueue.poll()!!
            showSnackbar(context, message, iconResource)
        }
    }

    private fun showSnackbar(context: Context, message: String, iconResource: Int) {
        val binding = SnackbarDefaultBinding.inflate(LayoutInflater.from(context))
        binding.textMessage.text = message
        binding.snackbarImage.setImageResource(iconResource)

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
        Log.d(TAG, "Snackbar: $message is shown!")
        snackbar?.show()
    }

    private fun setupTimer(progressBar: ProgressBar) {
        val timer = object : CountDownTimer(TIMER_DURATION, TIMER_STEP_SIZE) {
            override fun onTick(millisUntilFinished: Long) {
                val progress = (TIMER_DURATION - millisUntilFinished) / TIMER_DURATION.toFloat()
                val progressValue = (progress * progressBar.max).toInt()
                progressBar.progress = progressValue
            }

            override fun onFinish() {
                dismissSnackbar()
            }
        }
        timer.start()
    }

    private fun setupSnackbarCallbacks() {
        snackbar?.addCallback(object : Snackbar.Callback() {
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                super.onDismissed(transientBottomBar, event)
                snackbar = null
                showNextSnackbar()
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
            rootView = getRootViewFromContext(context.baseContext)
        }

        return rootView
    }

    companion object {
        private const val TIMER_STEP_SIZE: Long = 20
        private const val TIMER_DURATION: Long = 10000
        private const val TAG: String = "SnackbarHelper"

        private val instance: SnackbarHelper by lazy { SnackbarHelper() }

        @JvmStatic
        fun getSnackbarInstance(): SnackbarHelper {
            return instance
        }
    }
}
