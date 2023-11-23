package com.unex.musicgo.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.unex.musicgo.R
import com.unex.musicgo.databinding.PlaylistListFragmentBinding
import com.unex.musicgo.ui.interfaces.OnCreatePlayListButtonClick

class PlayListFragment : Fragment() {

    private lateinit var onCreatePlayListClickListener: OnCreatePlayListButtonClick
    private var binding: PlaylistListFragmentBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate PlayListFragment")
    }

    override fun onAttach(context: Context) {
        Log.d(TAG, "onAttach PlayListFragment")
        super.onAttach(context)
        if (context is OnCreatePlayListButtonClick) {
            onCreatePlayListClickListener = context
        } else {
            throw RuntimeException("$context must implement OnCreatePlayListButtonClick")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView PlayListFragment")
        binding = PlaylistListFragmentBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            binding?.let {
                it.createPlaylistBtn.setOnClickListener {
                    onCreatePlayListClickListener.onCreatePlayListButtonClick()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        with(binding) {
            val bottomNavigation =
                activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)
            bottomNavigation?.let {
                it.menu.getItem(1).isCheckable = true
                it.menu.getItem(1).isChecked = true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null // avoid memory leaks
    }

    companion object {

        const val TAG = "PlayListFragment"

        @JvmStatic
        fun newInstance() = PlayListFragment()
    }
}