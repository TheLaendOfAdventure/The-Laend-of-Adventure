package de.hdmstuttgart.thelaendofadventure.ui.helper

import android.content.Context
import de.hdmstuttgart.the_laend_of_adventure.R

object SharedPreferencesHelper {
    fun getUserID(context: Context): Int {
        val sharedPreferences = context.getSharedPreferences(
            context.getString(R.string.sharedPreferences),
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getInt(context.getString(R.string.userID), -1)
    }
}
