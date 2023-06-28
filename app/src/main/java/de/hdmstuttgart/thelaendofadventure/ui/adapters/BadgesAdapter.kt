package de.hdmstuttgart.thelaendofadventure.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import de.hdmstuttgart.the_laend_of_adventure.R
import de.hdmstuttgart.the_laend_of_adventure.databinding.BadgespageListitemBinding
import de.hdmstuttgart.thelaendofadventure.data.AppDataContainer
import de.hdmstuttgart.thelaendofadventure.data.dao.datahelper.BadgeDetails
import de.hdmstuttgart.thelaendofadventure.data.repository.BadgeRepository
import de.hdmstuttgart.thelaendofadventure.ui.helper.StringHelper

class BadgesAdapter(
    private val badgeList: List<Pair<BadgeDetails, List<Pair<List<String>, Boolean>>>>
) : RecyclerView.Adapter<BadgesAdapter.ViewHolder>() {

    private lateinit var badgeRepository: BadgeRepository
    private lateinit var context: Context
    private lateinit var view: View
    private lateinit var binding: BadgespageListitemBinding

    companion object {
        private const val paddingBottom = 200
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        badgeRepository = AppDataContainer(context).badgeRepository

        binding = BadgespageListitemBinding.inflate(LayoutInflater.from(context), parent, false)
        val viewHolder = ViewHolder(binding.root)
        view = binding.root
        viewHolder.binding = binding

        viewHolder.binding.wrapper.setOnClickListener(
            ListItemClickListener(
                viewHolder.binding.wrapper,
                viewHolder.binding.innerInfo,
                viewHolder.binding.badgeArrow
            )
        )

        return viewHolder
    }

    // binds the list items to a view
    @SuppressLint("DiscouragedApi")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (badge, actionDetailsList) = badgeList[position]

        // Fill the information into the holder
        val imageName = badge.imagePath
        val resourceID = context.resources.getIdentifier(imageName, "drawable", context.packageName)
        holder.binding.badgeImage.setImageResource(resourceID)
        holder.binding.badgeName.text = badge.name
        holder.binding.badgeProgress.max = badge.targetGoalNumber
        holder.binding.badgeProgress.setProgress(badge.currentGoalNumber, true)
        holder.binding.badgeProgressNumeric.text = context.getString(
            R.string.quest_progress_numeric_text,
            badge.currentGoalNumber,
            badge.targetGoalNumber
        )

        // add padding bottom if last item
        if (position == badgeList.size - 1) {
            holder.binding.badgeOuter.setPadding(0, 0, 0, paddingBottom)
        } else {
            holder.binding.badgeOuter.setPadding(0, 0, 0, 0)
        }

        val stringBuilder = StringBuilder()
        for (actionPair in actionDetailsList) {
            val actions = actionPair.first
            val completed = actionPair.second

            if (completed) {
                stringBuilder.append(StringHelper.strikethroughText(actions.joinToString("\n")))
            } else {
                stringBuilder.append(actions.joinToString("\n"))
            }
        }

        val allActions = stringBuilder.toString().trim()
        holder.binding.badgeGoals.text = allActions
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return badgeList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var binding: BadgespageListitemBinding
    }
}
