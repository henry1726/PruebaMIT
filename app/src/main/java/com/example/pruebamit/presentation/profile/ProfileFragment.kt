package com.example.pruebamit.presentation.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.pruebamit.R
import com.example.pruebamit.databinding.FragmentProfileBinding
import com.example.pruebamit.domain.util.Resource
import com.example.pruebamit.presentation.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment: Fragment(R.layout.fragment_profile) {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProfileBinding.bind(view)

        setObservers()
        addActions()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadProfileData()
    }

    private fun addActions() {
        binding.btnLogout.setOnClickListener {
            viewModel.logout()
            requireActivity().finish()
            startActivity(
                Intent(requireContext(), LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            )
        }
    }

    private fun setObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.profileData.collect { resource ->
                if (resource is Resource.Success) {
                    binding.tvUsername.text = resource.data.username
                    binding.tvTotalCards.text = "Tarjetas registradas: ${resource.data.totalCards}"
                    binding.tvTotalMovements.text = "Movimientos realizados: ${resource.data.totalMovements}"
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}