package com.unex.musicgo.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.unex.musicgo.databinding.CommentListBinding
import com.unex.musicgo.models.Comment

class CommentListAdapter(
    private var comments: List<Comment>,
    private val context: Context?
) : RecyclerView.Adapter<CommentListAdapter.ShowViewHolder>() {

    class ShowViewHolder(
        private val binding: CommentListBinding,
        private val context: Context?
    ) : RecyclerView.ViewHolder(binding.root) {

        /**
         * Get a readable timestamp from a timestamp in milliseconds.
         *
         * The readable timestamp is in the format: 1 minute ago, 2 hours ago, 3 days ago, etc.
         *
         * @param timestamp The timestamp in milliseconds.
         * @return A readable timestamp.
         */
        private fun CommentListBinding.bindReadableTimestamp(timestamp: Long) {
            val currentTime = System.currentTimeMillis()
            val difference = currentTime - timestamp

            val seconds = difference / 1000
            val minutes = seconds / 60
            val hours = minutes / 60
            val days = hours / 24
            val weeks = days / 7
            val months = weeks / 4
            val years = months / 12

            date.text = when {
                years > 0 -> {
                    if (years == 1L) {
                        "1 year ago"
                    } else {
                        "$years years ago"
                    }
                }

                months > 0 -> {
                    if (months == 1L) {
                        "1 month ago"
                    } else {
                        "$months months ago"
                    }
                }

                weeks > 0 -> {
                    if (weeks == 1L) {
                        "1 week ago"
                    } else {
                        "$weeks weeks ago"
                    }
                }

                days > 0 -> {
                    if (days == 1L) {
                        "1 day ago"
                    } else {
                        "$days days ago"
                    }
                }

                hours > 0 -> {
                    if (hours == 1L) {
                        "1 hour ago"
                    } else {
                        "$hours hours ago"
                    }
                }

                minutes > 0 -> {
                    if (minutes == 1L) {
                        "1 minute ago"
                    } else {
                        "$minutes minutes ago"
                    }
                }

                else -> {
                    if (seconds == 1L) {
                        "1 second ago"
                    } else {
                        "$seconds seconds ago"
                    }
                }
            }
        }

        fun bind(comment: Comment, totalItems: Int) {
            with(binding) {
                this.username.text = comment.username
                this.comment.text = comment.description
                bindReadableTimestamp(comment.timestamp)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowViewHolder {
        val binding = CommentListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ShowViewHolder(binding, context)
    }

    override fun getItemCount() = comments.size

    override fun onBindViewHolder(holder: ShowViewHolder, position: Int) {
        holder.bind(comments[position], comments.size)
    }

    fun updateData(comments: List<Comment>) {
        this.comments = comments
        notifyDataSetChanged()
    }

}