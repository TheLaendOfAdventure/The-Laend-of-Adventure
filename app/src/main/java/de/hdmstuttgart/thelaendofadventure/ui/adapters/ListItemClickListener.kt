package de.hdmstuttgart.thelaendofadventure.ui.adapters

import android.view.View
import android.widget.LinearLayout

class ListItemClickListener(val innerInfo: LinearLayout) : View.OnClickListener {
    override fun onClick(view: View) {
        if (innerInfo.visibility == View.VISIBLE) {
            innerInfo.visibility = View.GONE
        } else {
            innerInfo.visibility = View.VISIBLE
        }
    }
}
