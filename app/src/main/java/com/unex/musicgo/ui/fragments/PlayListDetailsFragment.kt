package com.unex.musicgo.ui.fragments

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.unex.musicgo.R
import com.unex.musicgo.database.MusicGoDatabase
import com.unex.musicgo.databinding.DialogBinding
import com.unex.musicgo.databinding.ListDisplayBinding
import com.unex.musicgo.models.PlayList
import com.unex.musicgo.models.PlayListWithSongs
import com.unex.musicgo.models.Song
import kotlinx.coroutines.launch
import java.nio.charset.StandardCharsets

class PlayListDetailsFragment : Fragment(), SongListFragment.OnSongDeleteListener {

    private val TAG = "PlayListDetailsFragment"

    companion object {

        @JvmStatic
        fun newCreateInstance() =
            PlayListDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_MODE, State.CREATE.name)
                }
            }

        @JvmStatic
        fun newInstance(playlist: PlayList) =
            PlayListDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_MODE, State.VIEW.name)
                    putSerializable(ARG_PLAYLIST, playlist)
                }
            }

        private const val ARG_MODE = "mode"
        private const val ARG_PLAYLIST = "playList"
    }

    private enum class State {
        CREATE, VIEW, EDIT
    }

    private var _binding: ListDisplayBinding? = null
    private val binding get() = _binding!!
    private var db: MusicGoDatabase? = null

    private lateinit var state: State // The state of the fragment

    private var playlistWithSongs: PlayListWithSongs? = null
    private var playlist: PlayList? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate PlayListDetailsFragment")
        arguments?.let {
            state = State.valueOf(it.getString(ARG_MODE)!!)
            playlist = it.getSerializable(ARG_PLAYLIST) as PlayList?
            Log.d(TAG, "Initial state: $state")
            Log.d(TAG, "Playlist: $playlist")
        }

        lifecycleScope.launch {
            db = MusicGoDatabase.getInstance(requireContext())
        }
    }

    private fun ListDisplayBinding.bindSongs() {
        lifecycleScope.launch {
            playlist?.let {
                Log.d(TAG, "Getting playlist with songs")
                playlistWithSongs = db?.playListDao()?.getPlayList(it.id)
                Log.d(TAG, "Playlist with songs: $playlistWithSongs")
                playlistWithSongs?.let {
                    val fragment = SongListFragment.newPlayListInstance(it)
                    with(binding) {
                        this@PlayListDetailsFragment
                            .childFragmentManager
                            .beginTransaction()
                            .replace(this.fragmentSongListContainer.id, fragment)
                            .commit()
                    }
                }
            }
        }
    }

    private fun ListDisplayBinding.bindBrushes() {
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        var isTitleBrushIcon = true
        var isDescriptionBrushIcon = true
        this.brushIconPlaylist.setOnClickListener {
            // If the current drawable is the brush icon, show the input to edit the title
            if (isTitleBrushIcon) {
                Log.d(TAG, "Click on brush icon")
                // Change the brush icon to a check icon
                this.brushIconPlaylist.setImageResource(android.R.drawable.ic_menu_save)
                toggleVisibility(gone = this.playlistInfoNames, visible = this.playlistName)
                this.playlistName.requestFocus()
                imm.showSoftInput(this.playlistName, InputMethodManager.SHOW_IMPLICIT)
                isTitleBrushIcon = false
            } else {
                Log.d(TAG, "Click on check icon")
                // Validate the title
                if (validTitle()) {
                    // Change the check icon to a brush icon
                    this.brushIconPlaylist.setImageResource(R.drawable.ic_brush)
                    toggleVisibility(gone = this.playlistName, visible = this.playlistInfoNames)
                    this.playlistName.clearFocus()
                    imm.hideSoftInputFromWindow(this.playlistName.windowToken, 0)
                    // Save the title
                    saveTitle()
                    // Change the state of the fragment
                    isTitleBrushIcon = true
                }
            }
        }
        this.brushIconDescription.setOnClickListener {
            // If the drawable is a check icon, save the description
            if (isDescriptionBrushIcon) {
                Log.d(TAG, "Click on brush icon")
                // Change the brush icon to a check icon
                this.brushIconDescription.setImageResource(android.R.drawable.ic_menu_save)
                toggleVisibility(
                    gone = this.playlistInfoDescription,
                    visible = this.playlistDescription
                )
                this.playlistDescription.requestFocus()
                imm.showSoftInput(this.playlistDescription, InputMethodManager.SHOW_IMPLICIT)
                isDescriptionBrushIcon = false
            } else {
                Log.d(TAG, "Click on check icon")
                // Validate the description
                if (validDescription()) {
                    // Change the check icon to a brush icon
                    this.brushIconDescription.setImageResource(R.drawable.ic_brush)
                    toggleVisibility(
                        gone = this.playlistDescription,
                        visible = this.playlistInfoDescription
                    )
                    this.playlistDescription.clearFocus()
                    imm.hideSoftInputFromWindow(this.playlistDescription.windowToken, 0)
                    // Save the description
                    saveDescription()
                    // Change the state of the fragment
                    isDescriptionBrushIcon = true
                }
            }
        }
    }

    private fun validTitle(): Boolean {
        if (binding.playlistName.text.toString().isNotEmpty()) {
            return true
        }
        Toast.makeText(
            requireContext(),
            "The title of the playlist cannot be empty",
            Toast.LENGTH_SHORT
        ).show()
        return false
    }

    private fun saveTitle() {
        val title = binding.playlistName.text.toString()
        if (playlist == null) {
            playlist = PlayList(title = title, description = "")
            Toast.makeText(
                requireContext(),
                "Add a description to the playlist to save it",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            playlist!!.title = title
            lifecycleScope.launch {
                playlist?.let {
                    if (state == State.CREATE) {
                        db?.playListDao()?.insert(it)
                    } else {
                        db?.playListDao()?.update(it)
                    }
                }
            }
        }
        binding.playlistInfoNames.text = title
    }

    private fun validDescription(): Boolean {
        if (binding.playlistDescription.text.toString().isNotEmpty()) {
            return true
        }
        Toast.makeText(
            requireContext(),
            "The description of the playlist cannot be empty",
            Toast.LENGTH_SHORT
        ).show()
        return false
    }

    private fun saveDescription() {
        val description = binding.playlistDescription.text.toString()
        if (playlist == null) {
            playlist = PlayList(title = "", description = description)
            Toast.makeText(
                requireContext(),
                "Add a title to the playlist to save it",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            playlist!!.description = description
            lifecycleScope.launch {
                playlist?.let {
                    if (state == State.CREATE) {
                        db?.playListDao()?.insert(it)
                    } else {
                        db?.playListDao()?.update(it)
                    }
                }
            }
        }
        binding.playlistInfoDescription.text = description
    }

    private fun ListDisplayBinding.bindData() {
        playlist?.let {

            this.playlistInfoNames.text = it.title
            this.playlistInfoDescription.text = it.description
            this.playlistName.setText(it.title)
            this.playlistDescription.setText(it.description)
        }
    }

    private fun ListDisplayBinding.bindArrows() {
        this.previousIcon.visibility = View.INVISIBLE
        this.nextIcon.visibility = View.INVISIBLE
        // If the state is CREATE, not do anything
        if (state == State.CREATE) {
            return
        }
        lifecycleScope.launch {
            // Get the previous and next playlist (if exists)
            val playlists = db?.playListDao()?.getPlayListsCreatedByUserWithoutSongs()
            val previousPlaylist = playlists?.getOrNull(playlists.indexOf(playlist!!) - 1)
            val nextPlaylist = playlists?.getOrNull(playlists.indexOf(playlist!!) + 1)
            // If the previous playlist exists, show the previous arrow
            if (previousPlaylist != null) {
                previousIcon.visibility = View.VISIBLE
                previousIcon.setOnClickListener {
                    Log.d(TAG, "Click on previous icon")
                    playlist = previousPlaylist
                    bind()
                }
            }
            // If the next playlist exists, show the next arrow
            if (nextPlaylist != null) {
                nextIcon.visibility = View.VISIBLE
                nextIcon.setOnClickListener {
                    Log.d(TAG, "Click on next icon")
                    playlist = nextPlaylist
                    bind()
                }
            }
        }
    }

    private fun ListDisplayBinding.bindTrash() {
        // If the state is CREATE, hide the trash icon
        if (state == State.CREATE) {
            this.trashIcon.visibility = View.GONE
        }

        this.trashIcon.setOnClickListener {
            Log.d(TAG, "Click on trash icon")

            // Show a dialog to confirm the deletion of the playlist
            val dialog = Dialog(requireContext())

            // Dialog binding
            val dialogBinding = DialogBinding.inflate(layoutInflater)
            dialog.setContentView(dialogBinding.root)

            with(dialogBinding) {
                dialogMessage.text = getString(R.string.dialog_delete_playlist_menu)
                dialogPlaylistName.text = playlist?.title

                confirmButton.setOnClickListener {
                    Log.d(TAG, "Deleting playlist")
                    playlist?.let {
                        Log.d(TAG, "Deleting playlist ${it}")
                        lifecycleScope.launch {
                            db?.playListSongCrossRefDao()?.deleteAllByPlayList(it.id)
                            db?.playListDao()?.delete(it.id)
                            Log.d(TAG, "Playlist deleted")
                            requireActivity().onBackPressed()
                        }
                    }
                    dialog.dismiss()
                }

                cancelButton.setOnClickListener {
                    dialog.dismiss()
                }
            }

            // Show the dialog
            dialog.show()
        }
    }

    private fun ListDisplayBinding.bindShare() {
        // If the state is CREATE, hide the share icon
        if (state == State.CREATE) {
            this.shareIcon.visibility = View.GONE
        }

        this.shareIcon.setOnClickListener {
            Log.d(TAG, "Click on share icon")

            // Encode the playlist to json and then to base64
            val gson = Gson()
            val playlistJson = gson.toJson(playlistWithSongs)
            val encodedJson = Base64.encodeToString(
                playlistJson.toByteArray(StandardCharsets.UTF_8),
                Base64.NO_WRAP
            )

            // The uri is: musicgo://playlist?data=encodedJson
            val uri = "musicgo://playlist?data=$encodedJson"
            val redirectUrl =
                "655a04d83b89142e66e542c0--flourishing-cajeta-2f3342.netlify.app/musicgo/redirect?url=$uri"

            // The message to share is: "Check out this playlist: $redirectUrl"
            val message = "Check out this playlist: $redirectUrl"

            // Create the intent
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, message)
                type = "text/plain"
            }

            // Init the intent chooser
            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }
    }

    private fun bind() {
        Log.d(TAG, "Binding")
        binding.bindBrushes()
        binding.bindSongs()
        binding.bindData()
        binding.bindArrows()
        binding.bindTrash()
        binding.bindShare()
    }

    private fun toggleVisibility(gone: View, visible: View) {
        gone.visibility = View.GONE
        visible.visibility = View.VISIBLE
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView PlayListDetailsFragment")
        _binding = ListDisplayBinding.inflate(inflater, container, false)
        bind()
        return binding.root
    }

    override fun onStart() {
        Log.d(TAG, "onStart PlayListDetailsFragment")
        super.onStart()
        // Set the bottom navigation item as selected
        val bottomNavigation =
            activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigation?.let {
            it.menu.getItem(1).isCheckable = true
            it.menu.getItem(1).isChecked = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // avoid memory leaks
    }

    override fun onSongDelete(song: Song) {
        Log.d(TAG, "onDeleteSongFromPlayListClick")

        // Show a dialog to confirm the deletion of the song
        val dialog = Dialog(requireContext())
        val dialogBinding = DialogBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        with(dialogBinding) {
            dialogMessage.text = getString(R.string.dialog_delete_song_from_list)
            dialogPlaylistName.text = song.title

            confirmButton.setOnClickListener {
                lifecycleScope.launch {
                    playlist?.let {
                        db?.playListSongCrossRefDao()?.delete(it.id, song.id)
                        Log.d(TAG, "Song deleted from playlist")
                        this@PlayListDetailsFragment.bind()
                    }
                }
                dialog.dismiss()
            }

            cancelButton.setOnClickListener {
                dialog.dismiss()
            }
        }

        // Show the dialog
        dialog.show()
    }

}