package de.hdmstuttgart.thelaendofadventure.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import de.hdmstuttgart.the_laend_of_adventure.R
import de.hdmstuttgart.thelaendofadventure.data.entity.BadgeEntity

class BadgesAdapter(private val badgeList: List<BadgeEntity>) :
    RecyclerView.Adapter<BadgesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.badgespage_listitem, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val badge = badgeList[position]

        // sets the image to the imageview from our itemHolder class
        val imagePath = "file:///android_asset/badgeimages/" + badge.imagePath
        Glide.with(holder.imageView.context)
            .load(imagePath)
            .into(holder.imageView)

        // sets the text to the textview from our itemHolder class
        holder.textView.text = badge.name
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return badgeList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.badge_image)
        val textView: TextView = itemView.findViewById(R.id.badge_name)
    }
}
