package de.hdmstuttgart.thelaendofadventure.ui.adapters

import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import de.hdmstuttgart.the_laend_of_adventure.R

class ListItemClickListener(
    private val cardView: CardView,
    private val innerInfo: LinearLayout,
    private val arrow: ImageView
) : View.OnClickListener { // ktlint-disable max-line-length
    override fun onClick(view: View) {
        if (innerInfo.visibility == View.VISIBLE) {
            TransitionManager.beginDelayedTransition(cardView, AutoTransition())
            arrow.setImageResource(R.drawable.down_arrow)
            innerInfo.visibility = View.GONE
        } else {
            TransitionManager.beginDelayedTransition(cardView, AutoTransition())
            innerInfo.visibility = View.VISIBLE
            arrow.setImageResource(R.drawable.up_arrow)
        }
    }
}
