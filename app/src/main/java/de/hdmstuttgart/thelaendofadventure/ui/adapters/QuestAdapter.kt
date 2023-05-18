package de.hdmstuttgart.thelaendofadventure.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.hdmstuttgart.the_laend_of_adventure.R
import de.hdmstuttgart.thelaendofadventure.data.dao.datahelper.WholeQuest

class QuestAdapter(private val questList: List<WholeQuest>) : RecyclerView.Adapter<QuestAdapter.ViewHolder>() { // ktlint-disable max-line-length

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.questpage_listitem, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val quest = questList[position]

        // sets the text to the textview from our itemHolder class
        holder.textView.text = quest.name
        // sets the max to the progressBar from our itemHolder class
        holder.progressBar.max = quest.targetGoalNumber
        // sets the progress to the progressBar from our itemHolder class
        holder.progressBar.setProgress(quest.currentGoalNumber, true)
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return questList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.quest_name)
        val progressBar: ProgressBar = itemView.findViewById(R.id.quest_progress)
    }
}
