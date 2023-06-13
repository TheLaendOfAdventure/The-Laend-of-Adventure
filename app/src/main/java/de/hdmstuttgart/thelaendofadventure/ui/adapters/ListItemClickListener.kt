package de.hdmstuttgart.thelaendofadventure.ui.adapters

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import de.hdmstuttgart.the_laend_of_adventure.R

class ListItemClickListener(private val innerInfo: LinearLayout, private val arrow: ImageView) : View.OnClickListener { // ktlint-disable max-line-length
    override fun onClick(view: View) {
        if (innerInfo.visibility == View.VISIBLE) {
            innerInfo.visibility = View.GONE
            arrow.setImageResource(R.drawable.down_arrow)
        } else {
            innerInfo.visibility = View.VISIBLE
            arrow.setImageResource(R.drawable.up_arrow)
        }
    }
}
