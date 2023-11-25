package com.unex.musicgo.ui.fragments

import android.app.Dialog
import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.unex.musicgo.R
import com.unex.musicgo.database.MusicGoDatabase
import com.unex.musicgo.databinding.ConfigurationBinding
import com.unex.musicgo.databinding.DialogBinding
import com.unex.musicgo.ui.activities.LoginActivity
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {

    companion object {
        const val TAG = "SettingsFragment"

        fun newInstance() = SettingsFragment()
    }

    private var _binding: ConfigurationBinding? = null
    private val binding get() = _binding!!

    private var db: MusicGoDatabase? = null
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate SettingsFragment")

        firestore = Firebase.firestore

        lifecycleScope.launch {
            db = MusicGoDatabase.getInstance(requireContext())
        }
    }

    override fun onAttach(context: Context) {
        Log.d(TAG, "onAttach SettingsFragment")
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView SongListFragment")
        _binding = ConfigurationBinding.inflate(inflater, container, false)
        with(binding) {
            bind()
        }
        return binding.root
    }

    private fun ConfigurationBinding.bind() {
        val user = Firebase.auth.currentUser
        user?.let {
            lifecycleScope.launch {
                val username = db?.userDao()?.getUserByEmail(user.email!!)!!.username
                profileInfoNameSecondary.text = username
            }
        }
        bindLang()
        logOutInfo.setOnClickListener {
            Firebase.auth.signOut()
            refreshAfterSignOut()
        }
        deleteAccountInfo.setOnClickListener {
            // Delete account
            val dialog = Dialog(requireContext())
            val dialogBinding = DialogBinding.inflate(layoutInflater)
            dialog.setContentView(dialogBinding.root)
            // Get the string from the resources (dialog_delete_account_title)
            val title = resources.getString(R.string.dialog_delete_account_title)
            dialogBinding.dialogMessage.text = title
            dialogBinding.dialogPlaylistName.text = user?.email
            dialogBinding.confirmButton.setOnClickListener {
                deleteAccount()
                dialog.dismiss()
            }
            dialogBinding.cancelButton.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }
    }

    private fun deleteAccount() {
        val user = Firebase.auth.currentUser
        user?.let {
            val email = user.email
            Log.d(TAG, "Email: $email")
            // Delete user from the user collection
            val userCollection = firestore.collection("users")
            userCollection.whereEqualTo("email", email).get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        Log.d(TAG, "${document.id} => ${document.data}")
                        try {
                            // Delete the document
                            userCollection.document(document.id).delete()

                            // Delete user from the authentication
                            it.delete()
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Log.d(TAG, "User account deleted.")
                                        refreshAfterSignOut()
                                    } else {
                                        Log.d(TAG, "User account not deleted.")
                                        Toast.makeText(
                                            requireContext(),
                                            R.string.error_delete_account,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        // Insert again the user in the database
                                        val userMap = hashMapOf(
                                            "username" to document.data["username"],
                                            "userSurname" to document.data["userSurname"],
                                            "email" to document.data["email"],
                                            "password" to document.data["password"],
                                        )
                                        userCollection.add(userMap)
                                            .addOnSuccessListener { documentReference ->
                                                Log.d(
                                                    TAG,
                                                    "DocumentSnapshot added with ID: ${documentReference.id}"
                                                )
                                            }
                                        return@addOnCompleteListener
                                    }
                                }
                        } catch (e: Exception) {
                            Log.d(TAG, "Error deleting user: $e")
                            Toast.makeText(
                                requireContext(),
                                R.string.error_delete_account,
                                Toast.LENGTH_SHORT
                            ).show()
                            return@addOnSuccessListener
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        }
    }

    private fun refreshAfterSignOut() {
        lifecycleScope.launch {
            db?.userDao()?.deleteAll()
            val intent = LoginActivity.newIntent(requireContext())
            startActivity(intent)
            requireActivity().finish()
        }
    }

    private fun ConfigurationBinding.bindLang() {
        val currentLocale = Resources.getSystem().configuration.locales.get(0)
        val languageInfoName = when (currentLocale.language) {
            "en" -> "English"
            "es" -> "Español"
            else -> "English"
        }
        laguageInfoNameSecondary.text = languageInfoName
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // avoid memory leaks
    }

}