package de.hdmstuttgart.thelaendofadventure.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import de.hdmstuttgart.the_laend_of_adventure.R
import de.hdmstuttgart.thelaendofadventure.data.dao.datahelper.QuestDetails

class QuestAdapter(private val questList: List<QuestDetails>) : RecyclerView.Adapter<QuestAdapter.ViewHolder>() { // ktlint-disable max-line-length

    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        context = parent.context
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.questpage_listitem, parent, false)
        val viewHolder = ViewHolder(view)

        viewHolder.cardView.setOnClickListener(QuestCardClickListener(viewHolder.descriptionField))

        return viewHolder
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
        // sets the progress to the progress textfield
        holder.progressNumeric.text = context.getString(
            R.string.quest_progress_numeric_text,
            quest.currentGoalNumber,
            quest.targetGoalNumber
        )
        // sets the description to the description textfield
        holder.descriptionField.text = (quest.description)
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return questList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.quest_name)
        val progressBar: ProgressBar = itemView.findViewById(R.id.quest_progress)
        val progressNumeric: TextView = itemView.findViewById(R.id.quest_progress_numeric)
        val descriptionField: TextView = itemView.findViewById(R.id.quest_description)
        val cardView: CardView = itemView.findViewById(R.id.quest_card_view)
    }
}
