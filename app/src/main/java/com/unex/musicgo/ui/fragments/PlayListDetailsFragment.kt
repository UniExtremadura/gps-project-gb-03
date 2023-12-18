package com.unex.musicgo.ui.fragments

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.unex.musicgo.R
import com.unex.musicgo.databinding.DialogBinding
import com.unex.musicgo.databinding.ListDisplayBinding
import com.unex.musicgo.models.PlayList
import com.unex.musicgo.models.Song
import com.unex.musicgo.ui.enums.PlayListDetailsFragmentOption
import com.unex.musicgo.ui.vms.PlayListDetailsFragmentViewModel

class PlayListDetailsFragment : Fragment(), SongListFragment.OnSongDeleteListener {

    companion object {

        const val TAG = "PlayListDetailsFragment"

        @JvmStatic
        fun newCreateInstance() =
            PlayListDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_MODE, PlayListDetailsFragmentOption.CREATE.name)
                }
            }

        @JvmStatic
        fun newInstance(playlist: PlayList) =
            PlayListDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_MODE, PlayListDetailsFragmentOption.VIEW.name)
                    putSerializable(ARG_PLAYLIST, playlist)
                }
            }

        private const val ARG_MODE = "mode"
        private const val ARG_PLAYLIST = "playList"
    }

    private var _binding: ListDisplayBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlayListDetailsFragmentViewModel by lazy {
        ViewModelProvider(
            this,
            PlayListDetailsFragmentViewModel.Factory
        )[PlayListDetailsFragmentViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            viewModel.setStartMode(
                PlayListDetailsFragmentOption.valueOf(it.getString(ARG_MODE)!!),
                it.getSerializable(ARG_PLAYLIST) as? PlayList
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView PlayListDetailsFragment")
        _binding = ListDisplayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViewModel()
        setUpViews()
    }

    private fun setUpViewModel() {
        viewModel.toastLiveData.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }
        viewModel.playList.observe(viewLifecycleOwner) {
            it?.let {
                // When the playlist is loaded, we can show the list data
                binding.playlistInfoNames.text = it.playlist.title
                binding.playlistInfoDescription.text = it.playlist.description
                binding.playlistName.setText(it.playlist.title)
                binding.playlistDescription.setText(it.playlist.description)
                // Launch the fragment with the songs
                val fragment = SongListFragment.newPlayListInstance(it)
                this@PlayListDetailsFragment
                    .childFragmentManager
                    .beginTransaction()
                    .replace(binding.fragmentSongListContainer.id, fragment)
                    .commit()
            }
        }
        viewModel.nextArrowVisibility.observe(viewLifecycleOwner) {
            binding.nextIcon.visibility = if (it) View.VISIBLE else View.INVISIBLE
        }
        viewModel.previousArrowVisibility.observe(viewLifecycleOwner) {
            binding.previousIcon.visibility = if (it) View.VISIBLE else View.INVISIBLE
        }
    }

    private fun setUpViews() {
        binding.nextIcon.visibility = View.INVISIBLE
        binding.previousIcon.visibility = View.INVISIBLE
        if (viewModel.hideTrashIcon()) binding.trashIcon.visibility = View.INVISIBLE
        if (viewModel.hideShareIcon()) binding.shareIcon.visibility = View.INVISIBLE
        binding.nextIcon.setOnClickListener {
            viewModel.nextPlayList()
        }
        binding.previousIcon.setOnClickListener {
            viewModel.previousPlayList()
        }
        binding.brushIconPlaylist.setOnClickListener {
            onTitleBrushClick()
        }
        binding.brushIconDescription.setOnClickListener {
            onDescriptionBrushClick()
        }
        binding.shareIcon.setOnClickListener {
            onShareClick()
        }
        binding.trashIcon.setOnClickListener {
            onTrashClick()
        }
    }

    private fun onTitleBrushClick() {
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val title = binding.playlistName.text.toString()
        viewModel.onTitleBrush(
            onVisible = {
                if(viewModel.validTitle(title)) {
                    binding.brushIconPlaylist.setImageResource(R.drawable.ic_brush)
                    toggleVisibility(gone = binding.playlistName, visible = binding.playlistInfoNames)
                    binding.playlistInfoNames.clearFocus()
                    imm.hideSoftInputFromWindow(binding.playlistInfoNames.windowToken, 0)
                    viewModel.saveTitle(title) {
                        binding.playlistInfoNames.text = title
                    }
                    viewModel.toggleTitleBrushActive()
                }
            },
            onInvisible = {
                binding.brushIconPlaylist.setImageResource(android.R.drawable.ic_menu_save)
                toggleVisibility(gone = binding.playlistInfoNames, visible = binding.playlistName)
                binding.playlistName.requestFocus()
                imm.showSoftInput(binding.playlistName, InputMethodManager.SHOW_IMPLICIT)
                viewModel.toggleTitleBrushActive()
            }
        )
    }

    private fun onDescriptionBrushClick() {
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val description = binding.playlistDescription.text.toString()
        viewModel.onDescriptionBrush(
            onVisible = {
                if(viewModel.validDescription(description)) {
                    binding.brushIconDescription.setImageResource(R.drawable.ic_brush)
                    toggleVisibility(
                        gone = binding.playlistDescription,
                        visible = binding.playlistInfoDescription
                    )
                    binding.playlistInfoDescription.clearFocus()
                    imm.hideSoftInputFromWindow(binding.playlistInfoDescription.windowToken, 0)
                    viewModel.saveDescription(description) {
                        binding.playlistInfoDescription.text = description
                    }
                    viewModel.toggleDescriptionBrushActive()
                }
            },
            onInvisible = {
                binding.brushIconDescription.setImageResource(android.R.drawable.ic_menu_save)
                toggleVisibility(
                    gone = binding.playlistInfoDescription,
                    visible = binding.playlistDescription
                )
                binding.playlistDescription.requestFocus()
                imm.showSoftInput(binding.playlistDescription, InputMethodManager.SHOW_IMPLICIT)
                viewModel.toggleDescriptionBrushActive()
            }
        )
    }

    private fun toggleVisibility(gone: View, visible: View) {
        gone.visibility = View.GONE
        visible.visibility = View.VISIBLE
    }

    private fun onShareClick() {
        Log.d(TAG, "Click on share icon")
        // Create the intent
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, viewModel.getRedirectUrl())
            type = "text/plain"
        }
        // Init the intent chooser
        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    private fun onTrashClick() {
        // Show a dialog to confirm the deletion of the playlist
        val dialog = Dialog(requireContext())
        // Dialog binding
        val dialogBinding = DialogBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)
        with(dialogBinding) {
            dialogMessage.text = getString(R.string.dialog_delete_playlist_menu)
            dialogPlaylistName.text = viewModel.playList.value?.playlist?.title ?: ""

            confirmButton.setOnClickListener {
                Log.d(TAG, "Deleting playlist")
                viewModel.deletePlayList() {
                    Log.d(TAG, "Playlist deleted")
                    Toast.makeText(
                        requireContext(),
                        "Playlist deleted",
                        Toast.LENGTH_SHORT
                    ).show()
                    dialog.dismiss()
                    // Go back to the previous fragment
                    requireActivity().onBackPressed()
                }
            }

            cancelButton.setOnClickListener {
                dialog.dismiss()
            }
        }
        // Show the dialog
        dialog.show()
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
                viewModel.deleteSongFromPlayList(song)
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