package de.hdmstuttgart.thelaendofadventure.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.hdmstuttgart.the_laend_of_adventure.R
import de.hdmstuttgart.thelaendofadventure.data.entity.QuestEntity

class QuestAdapter(private val mList: List<QuestEntity>) : RecyclerView.Adapter<QuestAdapter.ViewHolder>() { // ktlint-disable max-line-length

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.questpage_listitem, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val quests = mList[position]

        // sets the text to the textview from our itemHolder class
        holder.textView.text = quests.name

        holder.progressBar.max = quests.targetGoalNumber
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.quest_name)
        val progressBar: ProgressBar = itemView.findViewById(R.id.quest_progress)
    }
}
