package com.example.pruebamit.presentation.movements

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pruebamit.R
import com.example.pruebamit.databinding.FragmentMovementsBinding
import com.example.pruebamit.domain.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MovementsFragment: Fragment(R.layout.fragment_movements) {
    private var _binding: FragmentMovementsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MovementsViewModel by viewModels()
    private val adapter = MovementsAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMovementsBinding.bind(view)

        setUpAdapter()
        setObservers()
    }

    private fun setObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.movements.collect { resource ->
                when (resource) {
                    is Resource.Loading -> binding.progressBar.visibility = View.VISIBLE
                    is Resource.Success -> {
                        binding.progressBar.visibility = View.GONE
                        if (resource.data.isEmpty()) {
                            binding.layoutEmpty.visibility = View.VISIBLE
                            binding.rvMovements.visibility = View.GONE
                        } else {
                            binding.layoutEmpty.visibility = View.GONE
                            binding.rvMovements.visibility = View.VISIBLE
                            adapter.submitList(resource.data)
                        }
                    }
                    is Resource.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun setUpAdapter() {
        binding.rvMovements.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMovements.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}