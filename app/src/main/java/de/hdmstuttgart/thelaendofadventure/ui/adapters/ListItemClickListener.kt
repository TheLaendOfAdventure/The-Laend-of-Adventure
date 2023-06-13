package de.hdmstuttgart.thelaendofadventure.ui.adapters

import android.transition.ChangeBounds
import android.transition.Fade
import android.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.cardview.widget.CardView

class ListItemClickListener(
    private val cardView: CardView,
    private val innerInfo: LinearLayout,
    private val arrow: ImageView
) : View.OnClickListener {
    override fun onClick(view: View) {
        if (innerInfo.visibility == View.VISIBLE) {
            TransitionManager.beginDelayedTransition(
                cardView.parent.parent as ViewGroup?,
                ChangeBounds()
            )
            TransitionManager.beginDelayedTransition(innerInfo, Fade())
            arrow.animate().rotation(0f)
            innerInfo.visibility = View.GONE
        } else {
            TransitionManager.beginDelayedTransition(
                cardView.parent.parent as ViewGroup?,
                ChangeBounds()
            )
            TransitionManager.beginDelayedTransition(innerInfo, Fade())
            innerInfo.visibility = View.VISIBLE
            arrow.animate().rotation(rotation)
        }
    }

    companion object {
        private const val rotation = -180f
    }
}
