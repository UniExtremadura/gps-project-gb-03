package com.unex.musicgo.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.unex.musicgo.R
import com.unex.musicgo.database.MusicGoDatabase
import com.unex.musicgo.databinding.ProfileBinding
import kotlinx.coroutines.launch

class ProfileStatisticsFragment : Fragment() {

    companion object {
        const val TAG = "ProfileStatisticsFragment"

        fun newInstance() = ProfileStatisticsFragment()
    }

    interface OnConsultStatisticsListener {
        fun onConsultStatistics()
    }

    private lateinit var listener: OnConsultStatisticsListener
    private var _binding: ProfileBinding? = null
    private val binding get() = _binding!!

    private var db: MusicGoDatabase? = null
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate ProfileStatisticsFragment")

        firestore = Firebase.firestore

        lifecycleScope.launch {
            db = MusicGoDatabase.getInstance(requireContext())
        }
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
        with(binding) {
            bind()
        }
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        // Set the bottom navigation item as selected
        with(binding) {
            val bottomNavigation =
                activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)
            bottomNavigation?.let {
                it.menu.getItem(3).isCheckable = true
                it.menu.getItem(3).isChecked = true
            }
        }
    }

    private fun ProfileBinding.bind() {
        val user = Firebase.auth.currentUser
        user?.let {
            lifecycleScope.launch {
                val email = user.email
                Log.d(TAG, "email: $email")
                val dbUser = db?.userDao()?.getUserByEmail(it.email!!)
                binding.tittleFavs.text = dbUser?.username ?: "Unknown"
            }
        }
        binding.checkStatisticsBtn.setOnClickListener {
            listener.onConsultStatistics()
        }
        // Add the top favorites songs
        val fragment = SongListFavoritesFragment.newInstance()
        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(this.frameLayout.id, fragment)
        transaction.commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // avoid memory leaks
    }

}