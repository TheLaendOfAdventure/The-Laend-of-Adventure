package de.hdmstuttgart.thelaendofadventure.ui.adapters

import android.view.View
import android.widget.TextView

class QuestCardClickListener(val textView: TextView) : View.OnClickListener {
    override fun onClick(view: View) {
        if (textView.visibility == View.VISIBLE) {
            textView.visibility = View.GONE
        } else {
            textView.visibility = View.VISIBLE
        }
    }
}
