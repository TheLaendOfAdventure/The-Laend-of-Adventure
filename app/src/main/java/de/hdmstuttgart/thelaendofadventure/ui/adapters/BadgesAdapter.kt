package de.hdmstuttgart.thelaendofadventure.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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
import de.hdmstuttgart.thelaendofadventure.data.dao.datahelper.BadgeDetails
import de.hdmstuttgart.thelaendofadventure.data.entity.ActionEntity
import de.hdmstuttgart.thelaendofadventure.data.repository.BadgeRepository
import de.hdmstuttgart.thelaendofadventure.ui.helper.SharedPreferencesHelper
import de.hdmstuttgart.thelaendofadventure.ui.helper.StringHelper

class BadgesAdapter(
    private val badgeList: List<BadgeDetails>,
    val accepted: Boolean,
    private val lifecycleOwner: LifecycleOwner
) :
    RecyclerView.Adapter<BadgesAdapter.ViewHolder>() {

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
    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val badge = badgeList[position]

        // sets the image to the imageview from our itemHolder class
        val imageName = badge.imagePath
        val resourceID = context.resources.getIdentifier(imageName, "drawable", context.packageName)
        holder.imageView.setImageResource(resourceID)
        if (!accepted) {
            holder.imageView.setColorFilter(
                Color.parseColor("#70000000"),
                PorterDuff.Mode.DARKEN
            )
        }
        // sets the text to the textview from our itemHolder class
        holder.badgeName.text = badge.name
        // sets the max to the progressBar from our itemHolder class
        holder.progressBar.max = badge.targetGoalNumber
        // sets the progress to the progressBar from our itemHolder class
        holder.progressBar.setProgress(badge.currentGoalNumber, true)
        // sets the progress to the progress textfield
        holder.progressNumeric.text = context.getString(
            R.string.quest_progress_numeric_text,
            badge.currentGoalNumber,
            badge.targetGoalNumber
        )

        val userID = SharedPreferencesHelper.getUserID(context)

        bindUnacceptedBadges(userID, badge, holder)
        bindAcceptedBadges(userID, badge, holder)
    }

    private fun bindAcceptedBadges(userID: Int, badge: BadgeDetails, holder: ViewHolder) {
        // show already completed BadgesGoals
        val actionCompleted = badgeRepository.getCompletedGoalsForBadgeByUserID(
            userID,
            badge.badgeID
        ).asLiveData()
        val actionObserverCompleted = Observer<List<ActionEntity>> { actions ->
            if (actions.isEmpty()) {
                holder.badgeGoalsCompleted.visibility = View.GONE
            } else {
                holder.badgeGoalsCompleted.visibility = View.VISIBLE
            }
            // Handle the questList
            var textList = ""
            for ((index, action) in actions.withIndex()) {
                // Perform your desired operations with the item
                var line = StringHelper.strikethroughText(action.description)
                // check for next line
                if (index < actions.size - 1) {
                    line += "\n"
                }
                textList += line
            }
            holder.badgeGoalsCompleted.text = textList
        }
        actionCompleted.observe(lifecycleOwner, actionObserverCompleted)
    }

    private fun bindUnacceptedBadges(userID: Int, badge: BadgeDetails, holder: ViewHolder) {
        // show uncompleted BadgesGoals
        val actionUncompleted = badgeRepository.getUncompletedGoalsForBadgeByUserID(
            userID,
            badge.badgeID
        ).asLiveData()
        val actionObserverUncompleted = Observer<List<ActionEntity>> { actions ->
            if (actions.isEmpty()) {
                holder.badgeGoalsUncompleted.visibility = View.GONE
            } else {
                holder.badgeGoalsUncompleted.visibility = View.VISIBLE
            }
            // Handle the questList
            var textList = ""
            for ((index, action) in actions.withIndex()) {
                // Perform your desired operations with the item
                var line = action.description
                // check for next line
                if (index < actions.size - 1) {
                    line += "\n"
                }
                textList += line
            }
            holder.badgeGoalsUncompleted.text = textList
        }
        actionUncompleted.observe(lifecycleOwner, actionObserverUncompleted)
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return badgeList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.badge_image)
        val badgeName: TextView = itemView.findViewById(R.id.badge_name)
        val badgeGoalsUncompleted: TextView = itemView.findViewById(R.id.badge_goals_uncompleted)
        val badgeGoalsCompleted: TextView = itemView.findViewById(R.id.badge_goals_completed)
        val innerInfo: LinearLayout = itemView.findViewById(R.id.inner_info)
        val wrapper: CardView = itemView.findViewById(R.id.wrapper)
        val progressBar: ProgressBar = itemView.findViewById(R.id.badge_progress)
        val progressNumeric: TextView = itemView.findViewById(R.id.badge_progress_numeric)
        val arrow: ImageView = itemView.findViewById(R.id.badge_arrow)
    }
}
