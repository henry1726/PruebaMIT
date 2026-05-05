package com.example.pruebamit.presentation.cards

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pruebamit.R
import com.example.pruebamit.databinding.FragmentCardsBinding
import com.example.pruebamit.domain.util.Resource
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CardsFragment: Fragment(R.layout.fragment_cards) {
    private var _binding: FragmentCardsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CardsViewModel by viewModels()
    private val adapter = CardsAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCardsBinding.bind(view)

        setUpAdapter()
        setObservers()
        addActions()
    }

    private fun setUpAdapter() {
        binding.rvCards.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCards.adapter = adapter
    }

    private fun addActions() {
        binding.fabAddCard.setOnClickListener { showAddCardDialog() }
    }

    private fun setObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.cards.collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.layoutEmpty.visibility = View.GONE
                    }
                    is Resource.Success -> {
                        binding.progressBar.visibility = View.GONE
                        if (resource.data.isEmpty()) {
                            binding.layoutEmpty.visibility = View.VISIBLE
                            binding.rvCards.visibility = View.GONE
                        } else {
                            binding.layoutEmpty.visibility = View.GONE
                            binding.rvCards.visibility = View.VISIBLE
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

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.addCardState.collect { resource ->
                when(resource){
                    is Resource.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.layoutEmpty.visibility = View.GONE
                    }

                    is Resource.Success -> {
                        binding.progressBar.visibility = View.GONE
                        binding.rvCards.visibility = View.VISIBLE
                        viewModel.resetAddCardState()
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

    private fun showAddCardDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_add_card, null)

        val etCardNumber = dialogView.findViewById<TextInputEditText>(R.id.etCardNumber)
        val etExpiration = dialogView.findViewById<TextInputEditText>(R.id.etExpiration)
        val etCardholderName = dialogView.findViewById<TextInputEditText>(R.id.etCardholderName)

        // Formato para Número de Tarjeta: XXXX XXXX XXXX XXXX
        etCardNumber.addTextChangedListener(object : TextWatcher {
            private var isUpdating = false
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (isUpdating) return
                isUpdating = true

                val original = s.toString()
                val clean = original.replace(" ", "")
                val formatted = StringBuilder()

                for (i in clean.indices) {
                    formatted.append(clean[i])
                    if ((i + 1) % 4 == 0 && (i + 1) < clean.length && formatted.length < 19) {
                        formatted.append(" ")
                    }
                }

                if (formatted.toString() != original) {
                    val selection = etCardNumber.selectionStart
                    val spacesBefore = original.substring(0, selection).count { it == ' ' }
                    
                    s?.replace(0, s.length, formatted.toString())
                    
                    val newSelection = selection + (formatted.toString().substring(0, minOf(selection, formatted.length)).count { it == ' ' } - spacesBefore)
                    etCardNumber.setSelection(newSelection.coerceIn(0, s?.length ?: 0))
                }
                isUpdating = false
            }
        })

        // Formato para Fecha de Expiración: MM/YY
        etExpiration.addTextChangedListener(object : TextWatcher {
            private var isUpdating = false
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (isUpdating) return
                isUpdating = true

                val original = s.toString()
                val clean = original.replace("/", "")
                val formatted = StringBuilder()

                for (i in clean.indices) {
                    formatted.append(clean[i])
                    if (i == 1 && (i + 1) < clean.length) {
                        formatted.append("/")
                    }
                }

                if (formatted.toString() != original) {
                    s?.replace(0, s.length, formatted.toString())
                    etExpiration.setSelection(s?.length ?: 0)
                }
                isUpdating = false
            }
        })

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogView)
            .create()

        dialogView.findViewById<MaterialButton>(R.id.btnSaveCard).setOnClickListener {
            val name = etCardholderName.text.toString()
            val number = etCardNumber.text.toString()
            val exp = etExpiration.text.toString()

            // Validación: Nombre no vacío, 16 dígitos y fecha MM/YY (longitud 5)
            if (name.isNotBlank() && number.replace(" ", "").length == 16 && exp.length == 5) {
                viewModel.addCard(name, number, exp)
                dialog.dismiss()
            } else {
                Toast.makeText(requireContext(), "Verifica que los datos sean correctos (16 dígitos y fecha MM/YY)", Toast.LENGTH_LONG).show()
            }
        }

        dialogView.findViewById<MaterialButton>(R.id.btnCancel).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}