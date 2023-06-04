package de.hdmstuttgart.thelaendofadventure.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.hdmstuttgart.the_laend_of_adventure.R
import de.hdmstuttgart.thelaendofadventure.data.dao.datahelper.RiddleDetails

class RiddleAnswerAdapter(
    private val riddleDetailsList: List<RiddleDetails>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<RiddleAnswerAdapter.ViewHolder>() { // ktlint-disable max-line-length

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.riddle_popup_listitem, parent, false)
        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val riddleDetails = riddleDetailsList[position]

        // sets the text to the textview from our itemHolder class
        holder.textViewAnswer.text = riddleDetails.possibleAnswers
        // sets the OnClickListener to the textview from our itemHolder class
        holder.textViewAnswer.setOnClickListener {
            listener.onItemClick(position)
        }
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return riddleDetailsList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewAnswer: TextView = itemView.findViewById(R.id.riddleAnswer)
    }
}
