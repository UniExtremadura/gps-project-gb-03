package com.unex.musicgo.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.unex.musicgo.R
import com.unex.musicgo.databinding.ProfileBinding
import com.unex.musicgo.ui.vms.ProfileStatisticsFragmentViewModel

class ProfileStatisticsFragment : Fragment() {

    companion object {
        const val TAG = "ProfileStatisticsFragment"

        fun newInstance() = ProfileStatisticsFragment()
    }

    interface OnConsultStatisticsListener {
        fun onConsultStatistics()
    }

    private var _binding: ProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var listener: OnConsultStatisticsListener

    private val viewModel: ProfileStatisticsFragmentViewModel by lazy {
        ViewModelProvider(
            this,
            ProfileStatisticsFragmentViewModel.Factory
        )[ProfileStatisticsFragmentViewModel::class.java]
    }

    override fun onAttach(context: Context) {
        Log.d(TAG, "onAttach ProfileStatisticsFragment")
        super.onAttach(context)
        if (context is OnConsultStatisticsListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnConsultStatisticsListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView ProfileStatisticsFragment")
        _binding = ProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViewModel()
        setUpViews()
    }

    private fun setUpViewModel() {
        viewModel.toastLiveData.observe(viewLifecycleOwner) {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
        viewModel.user.observe(viewLifecycleOwner) {
            binding.tittleFavs.text = it?.username
        }
    }

    private fun setUpViews() {
        binding.tittleFavs.text = "Unknown"

        /** Set the listener for the check statistics button */
        binding.checkStatisticsBtn.setOnClickListener {
            listener.onConsultStatistics()
        }

        /** Add the top favorites songs */
        val fragment = SongListFavoritesFragment.newInstance()
        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(binding.frameLayout.id, fragment)
        transaction.commit()
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart ProfileStatisticsFragment")
        // Set the bottom navigation item as selected
        val bottomNavigation =
            requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigation?.let {
            it.menu.getItem(3).isCheckable = true
            it.menu.getItem(3).isChecked = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // avoid memory leaks
    }

}