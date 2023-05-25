package de.hdmstuttgart.thelaendofadventure.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.RecyclerView
import de.hdmstuttgart.the_laend_of_adventure.R
import de.hdmstuttgart.thelaendofadventure.data.AppDataContainer
import de.hdmstuttgart.thelaendofadventure.data.dao.datahelper.QuestDetails
import de.hdmstuttgart.thelaendofadventure.data.repository.QuestRepository
import de.hdmstuttgart.thelaendofadventure.ui.helper.StringHelper

class QuestAdapter(
    private val questList: List<QuestDetails>,
    private val lifecycleOwner: LifecycleOwner
) : RecyclerView.Adapter<QuestAdapter.ViewHolder>() { // ktlint-disable max-line-length

    lateinit var context: Context
    lateinit var questRepository: QuestRepository

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        context = parent.context
        questRepository = AppDataContainer(context).questRepository
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.questpage_listitem, parent, false)
        val viewHolder = ViewHolder(view)

        viewHolder.cardView.setOnClickListener(QuestCardClickListener(viewHolder.infoInner))

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

        var actionDescription = questRepository.getAllActionDescriptionsByQuestID(quest.questID).asLiveData()
        val actionObserver = Observer<List<String>> { descriptions ->
            // Handle the questList
            var textList = ""
            for ((index, description) in descriptions.withIndex()) {
                // Perform your desired operations with the item
                var line = "${index + 1}: $description"
                // check if goal is reached and strike through if so
                if (quest.currentGoalNumber > index + 1) {
                    line = StringHelper.strikethroughText(line)
                }
                if (index < descriptions.size) {
                    line += "\n"
                }
                textList += line
            }
            holder.questGoals.text = textList
        }
        actionDescription.observe(lifecycleOwner, actionObserver)
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
        val infoInner: LinearLayout = itemView.findViewById(R.id.inner_info)
        val questGoals: TextView = itemView.findViewById(R.id.quest_goals)
        val cardView: CardView = itemView.findViewById(R.id.quest_card_view)
    }
}
