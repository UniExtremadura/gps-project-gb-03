package com.unex.musicgo.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.unex.musicgo.databinding.PlaylistListBinding
import com.unex.musicgo.models.PlayList

class PlaylistListAdapter(
    private var playlists: List<PlayList>,
    private val onPlayListClick: (playlist: PlayList) -> Unit,
    private val context: Context?
) : RecyclerView.Adapter<PlaylistListAdapter.ShowViewHolder>() {

    class ShowViewHolder(
        private val binding: PlaylistListBinding,
        private val onPlayListClick: (playlist: PlayList) -> Unit,
        private val context: Context?
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(playlist: PlayList, totalItems: Int) {
            with(binding) {
                this.listTitle.text = playlist.title

                songListItem.setOnClickListener {
                    onPlayListClick(playlist)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowViewHolder {
        val binding =
            PlaylistListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ShowViewHolder(binding, onPlayListClick, context)
    }

    override fun getItemCount() = playlists.size

    override fun onBindViewHolder(holder: ShowViewHolder, position: Int) {
        holder.bind(playlists[position], playlists.size)
    }

    fun updateData(playlists: List<PlayList>) {
        this.playlists = playlists
        notifyDataSetChanged()
    }

}