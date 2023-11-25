package com.unex.musicgo.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.unex.musicgo.R
import com.unex.musicgo.databinding.GenreListBinding
import com.unex.musicgo.models.Genre

class GenreListAdapter(
    private var genres: List<Genre>,
    private val onClick: (genre: Genre) -> Unit,
    private val context: Context?
) : RecyclerView.Adapter<GenreListAdapter.ShowViewHolder>() {

    class ShowViewHolder(
        private val binding: GenreListBinding,
        private val onClick: (genre: Genre) -> Unit,
        private val context: Context?
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(genre: Genre, totalItems: Int) {
            with(binding) {
                this.btn.text = genre.title
                this.btn.setOnClickListener {
                    onClick(genre)
                    // Get the current background color of the button
                    val color = btn.backgroundTintList?.defaultColor
                    // If the button is not selected, set the background color to the primary color
                    if (color != context?.getColor(R.color.blue)) {
                        btn.backgroundTintList = context?.getColorStateList(R.color.blue)
                    } else {
                        // If the button is selected, set the background color to the secondary color
                        btn.backgroundTintList =
                            context?.getColorStateList(R.color.custom_green)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowViewHolder {
        val binding = GenreListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ShowViewHolder(binding, onClick, context)
    }

    override fun getItemCount() = genres.size

    override fun onBindViewHolder(holder: ShowViewHolder, position: Int) {
        holder.bind(genres[position], genres.size)
    }

    fun updateData(genres: List<Genre>) {
        this.genres = genres
        notifyDataSetChanged()
    }

}