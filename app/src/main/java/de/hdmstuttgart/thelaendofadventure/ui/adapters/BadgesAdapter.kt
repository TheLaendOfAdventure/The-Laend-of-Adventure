package de.hdmstuttgart.thelaendofadventure.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import de.hdmstuttgart.the_laend_of_adventure.R
import de.hdmstuttgart.thelaendofadventure.data.AppDataContainer
import de.hdmstuttgart.thelaendofadventure.data.dao.datahelper.BadgeDetails
import de.hdmstuttgart.thelaendofadventure.data.repository.BadgeRepository
import de.hdmstuttgart.thelaendofadventure.ui.helper.StringHelper

class BadgesAdapter(
    private val badgeList: List<Pair<BadgeDetails, List<Pair<List<String>, Boolean>>>>
) : RecyclerView.Adapter<BadgesAdapter.ViewHolder>() {

    private lateinit var badgeRepository: BadgeRepository
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.badgespage_listitem, parent, false)
        context = parent.context
        badgeRepository = AppDataContainer(context).badgeRepository
        val viewHolder = ViewHolder(view)
        viewHolder.wrapper.setOnClickListener(
            ListItemClickListener(viewHolder.wrapper, viewHolder.innerInfo, viewHolder.arrow)
        )
        return ViewHolder(view)
    }

    // binds the list items to a view
    @SuppressLint("DiscouragedApi")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (badge, actionDetailsList) = badgeList[position]
        val (actions, completed) = actionDetailsList

        // Fill the information into the holder
        val imageName = badge.imagePath
        val resourceID = context.resources.getIdentifier(imageName, "drawable", context.packageName)
        holder.imageView.setImageResource(resourceID)
        holder.badgeName.text = badge.name
        holder.progressBar.max = badge.targetGoalNumber
        holder.progressBar.setProgress(badge.currentGoalNumber, true)
        holder.progressNumeric.text = context.getString(
            R.string.quest_progress_numeric_text,
            badge.currentGoalNumber,
            badge.targetGoalNumber
        )

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
        holder.badgeGoals.text = allActions
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return badgeList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.badge_image)
        val badgeName: TextView = itemView.findViewById(R.id.badge_name)
        val badgeGoals: TextView = itemView.findViewById(R.id.badge_goals)
        val innerInfo: LinearLayout = itemView.findViewById(R.id.inner_info)
        val wrapper: CardView = itemView.findViewById(R.id.wrapper)
        val progressBar: ProgressBar = itemView.findViewById(R.id.badge_progress)
        val progressNumeric: TextView = itemView.findViewById(R.id.badge_progress_numeric)
        val arrow: ImageView = itemView.findViewById(R.id.badge_arrow)
    }
}
