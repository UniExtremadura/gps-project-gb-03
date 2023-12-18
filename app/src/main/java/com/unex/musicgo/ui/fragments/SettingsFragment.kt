package com.unex.musicgo.ui.fragments

import android.app.Dialog
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.unex.musicgo.R
import com.unex.musicgo.databinding.ConfigurationBinding
import com.unex.musicgo.databinding.DialogBinding
import com.unex.musicgo.ui.activities.LoginActivity
import com.unex.musicgo.ui.vms.SettingsFragmentViewModel
import java.util.Locale

class SettingsFragment : Fragment() {

    companion object {
        const val TAG = "SettingsFragment"

        fun newInstance() = SettingsFragment()
    }

    private var _binding: ConfigurationBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingsFragmentViewModel by lazy {
        ViewModelProvider(
            this,
            SettingsFragmentViewModel.Factory
        )[SettingsFragmentViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView SongListFragment")
        _binding = ConfigurationBinding.inflate(inflater, container, false)
        binding.bind()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViewModel()
    }

    private fun setUpViewModel() {
        viewModel.toastLiveData.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }
        viewModel.user.observe(viewLifecycleOwner) {
            it?.let {
                Log.d(TAG, "User: $it")
                binding.profileInfoNameSecondary.text = it.username
            }
        }
    }

    private fun ConfigurationBinding.bind() {
        bindLang()
        logOutInfo.setOnClickListener {
            refreshAfterSignOut()
        }
        deleteAccountInfo.setOnClickListener {
            showDeleteAccountDialog()
        }
    }

    private fun showDeleteAccountDialog() {
        // Delete account
        val dialog = Dialog(requireContext())
        val dialogBinding = DialogBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)
        // Get the string from the resources (dialog_delete_account_title)
        val title = resources.getString(R.string.dialog_delete_account_title)
        dialogBinding.dialogMessage.text = title
        dialogBinding.dialogPlaylistName.text = viewModel.user.value?.email
        dialogBinding.confirmButton.setOnClickListener {
            dialog.dismiss()
            viewModel.deleteAccount({
                refreshAfterSignOut()
            }, { errorMessage ->
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
            })
        }
        dialogBinding.cancelButton.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun refreshAfterSignOut() {
        viewModel.logOut {
            Log.d(TAG, "User logged out")
            val intent = LoginActivity.newIntent(requireContext())
            startActivity(intent)
            requireActivity().finish()
        }
    }

    private fun ConfigurationBinding.bindLang() {
        val currentLocale: Locale = Resources.getSystem().configuration.locales.get(0)
        val languageInfoName = viewModel.getLang(currentLocale)
        laguageInfoNameSecondary.text = languageInfoName
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // avoid memory leaks
    }

}