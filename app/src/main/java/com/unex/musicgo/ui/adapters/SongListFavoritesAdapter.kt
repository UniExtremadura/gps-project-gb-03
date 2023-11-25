package com.unex.musicgo.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.unex.musicgo.databinding.TopSongsItemBinding
import com.unex.musicgo.models.Song

class SongListFavoritesAdapter(
    private var songs: List<Song>,
    private val onClick: (song: Song) -> Unit,
    private val onOptionsClick: (song: Song, view: View) -> Unit,
    private val context: Context?
) : RecyclerView.Adapter<SongListFavoritesAdapter.ShowViewHolder>() {

    class ShowViewHolder(
        private val binding: TopSongsItemBinding,
        private val onClick: (song: Song) -> Unit,
        private val onOptionsClick: (song: Song, view: View) -> Unit,
        private val context: Context?
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(song: Song, totalItems: Int) {
            with(binding) {
                this.songTitle.text = song.title
                this.songArtist.text = song.artist
                this.numberFav.text = (adapterPosition + 1).toString() + "."

                context?.let {
                    Glide.with(context)
                        .load(song.coverPath)
                        .into(this.songImage)
                }

                songListItem.setOnClickListener {
                    onClick(song)
                }

                songOptions.setOnClickListener {
                    onOptionsClick(song, it)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowViewHolder {
        val binding =
            TopSongsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ShowViewHolder(binding, onClick, onOptionsClick, context)
    }

    override fun getItemCount() = songs.size

    override fun onBindViewHolder(holder: ShowViewHolder, position: Int) {
        holder.bind(songs[position], songs.size)
    }

    fun updateData(songs: List<Song>) {
        this.songs = songs
        notifyDataSetChanged()
    }

}