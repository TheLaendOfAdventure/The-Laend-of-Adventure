package de.hdmstuttgart.thelaendofadventure.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.RecyclerView
import de.hdmstuttgart.the_laend_of_adventure.R
import de.hdmstuttgart.the_laend_of_adventure.databinding.QuestpageListitemBinding
import de.hdmstuttgart.thelaendofadventure.data.AppDataContainer
import de.hdmstuttgart.thelaendofadventure.data.dao.datahelper.QuestDetails
import de.hdmstuttgart.thelaendofadventure.data.repository.QuestRepository
import de.hdmstuttgart.thelaendofadventure.ui.helper.StringHelper

class QuestAdapter(
    private val questList: List<QuestDetails>,
    private val lifecycleOwner: LifecycleOwner
) : RecyclerView.Adapter<QuestAdapter.ViewHolder>() {

    private lateinit var context: Context
    private lateinit var questRepository: QuestRepository
    private lateinit var view: View
    private lateinit var binding: QuestpageListitemBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        questRepository = AppDataContainer(context).questRepository
        binding = QuestpageListitemBinding.inflate(LayoutInflater.from(context), parent, false)
        val viewHolder = ViewHolder(binding.root)
        view = binding.root
        viewHolder.binding = binding

        viewHolder.binding.questCardView.setOnClickListener(
            ListItemClickListener(viewHolder.binding.innerInfo, viewHolder.binding.questArrow)
        )

        return viewHolder
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val quest = questList[position]

        holder.binding.questName.text = quest.name
        holder.binding.questProgress.max = quest.targetGoalNumber - 1
        holder.binding.questProgress.setProgress(quest.currentGoalNumber - 1, true)
        holder.binding.questProgressNumeric.text = context.getString(
            R.string.quest_progress_numeric_text,
            quest.currentGoalNumber - 1,
            quest.targetGoalNumber - 1
        )
        holder.binding.questDescription.text = (quest.description)

        val actionDescription = questRepository.getAllActionDescriptionsByQuestID(quest.questID).asLiveData() // ktlint-disable max-line-length
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
                if (index < descriptions.size - 1) {
                    line += "\n"
                }
                textList += line
            }
            holder.binding.questGoals.text = textList
        }
        actionDescription.observe(lifecycleOwner, actionObserver)
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return questList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var binding: QuestpageListitemBinding
    }
}
