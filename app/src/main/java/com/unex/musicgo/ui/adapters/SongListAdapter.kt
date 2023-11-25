package com.unex.musicgo.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.unex.musicgo.databinding.SongListBinding
import com.unex.musicgo.models.Song

class SongListAdapter(
    private var songs: List<Song>,
    private val onClick: (song: Song) -> Unit,
    private val onOptionsClick: (song: Song, view: View) -> Unit,
    private val onDeleteClick: (song: Song) -> Unit,
    private val showTrash: Boolean,
    private val context: Context?
) : RecyclerView.Adapter<SongListAdapter.ShowViewHolder>() {

    class ShowViewHolder(
        private val binding: SongListBinding,
        private val onClick: (song: Song) -> Unit,
        private val onOptionsClick: (song: Song, view: View) -> Unit,
        private val onDeleteClick: (song: Song) -> Unit,
        private val showTrash: Boolean,
        private val context: Context?
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(song: Song, totalItems: Int) {
            with(binding) {
                this.songTitle.text = song.title
                this.songArtist.text = song.artist

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

                if (showTrash) {
                    songTrash.visibility = View.VISIBLE
                    songTrash.setOnClickListener {
                        onDeleteClick(song)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowViewHolder {
        val binding = SongListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ShowViewHolder(binding, onClick, onOptionsClick, onDeleteClick, showTrash, context)
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