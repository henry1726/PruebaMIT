package com.example.pruebamit.presentation.payment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.pruebamit.R
import com.example.pruebamit.databinding.FragmentPaymentBinding
import com.example.pruebamit.domain.util.Resource
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PaymentFragment: Fragment(R.layout.fragment_payment) {
    private var _binding: FragmentPaymentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PaymentViewModel by viewModels()
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPaymentBinding.bind(view)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        setObservers()
        addActions()
        textWatchers()
    }

    private fun textWatchers() {
        // Formato para Tarjeta Destinatario: XXXX XXXX XXXX XXXX
        binding.etDestinationCard.addTextChangedListener(object : TextWatcher {
            private var isUpdating = false
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
                    s?.replace(0, s.length, formatted.toString())
                    binding.etDestinationCard.setSelection(s?.length ?: 0)
                }
                isUpdating = false
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Formato para Monto: $ XX.XX
        binding.etAmount.addTextChangedListener(object : TextWatcher {
            private var isUpdating = false
            override fun afterTextChanged(s: Editable?) {
                if (isUpdating) return
                isUpdating = true

                var current = s.toString()
                if (current.isNotEmpty() && !current.startsWith("$")) {
                    current = "$" + current
                    s?.replace(0, s.length, current)
                } else if (current == "$") {
                    s?.clear()
                }

                binding.etAmount.setSelection(s?.length ?: 0)
                isUpdating = false
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun addActions() {
        binding.btnPay.setOnClickListener {
            val destination = binding.etDestinationCard.text.toString().replace(" ", "")
            val recipient = binding.etRecipientName.text.toString()
            val reason = binding.etReason.text.toString()
            val amountStr = binding.etAmount.text.toString().replace("$", "")
            val amount = amountStr.toDoubleOrNull()
            val sourceCard = binding.spinnerSourceCard.text.toString()

            if (destination.length != 16 || recipient.isBlank() || reason.isBlank() || amount == null) {
                Toast.makeText(requireContext(), "Completa todos los campos correctamente (16 dígitos en tarjeta)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Obtener ubicación antes de procesar el pago
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    val lat = location?.latitude ?: 0.0
                    val lon = location?.longitude ?: 0.0
                    viewModel.makePayment(sourceCard, destination, recipient, reason, amount, lat, lon)
                }.addOnFailureListener {
                    viewModel.makePayment(sourceCard, destination, recipient, reason, amount, 0.0, 0.0)
                }
            } else {
                Toast.makeText(requireContext(), "No se tienen permisos de ubicación", Toast.LENGTH_SHORT).show()
                viewModel.makePayment(sourceCard, destination, recipient, reason, amount, 0.0, 0.0)
            }
        }
    }

    private fun setObservers() {
        // Cargar tarjetas en el Spinner
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.cards.collect { resource ->
                if (resource is Resource.Success) {
                    val cardLabels = resource.data.map {
                        "**** ${it.cardNumber.takeLast(4)}"
                    }
                    val adapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        cardLabels
                    )
                    binding.spinnerSourceCard.setAdapter(adapter)
                }
            }
        }

        // Observar estado del pago
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.paymentState.collect { resource ->
                when (resource) {
                    is Resource.Loading -> binding.progressBar.visibility = View.VISIBLE
                    is Resource.Success -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), "¡Pago realizado con éxito!", Toast.LENGTH_SHORT).show()
                        clearForm()
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

    private fun clearForm() {
        viewModel.resetPaymentState()
        binding.etDestinationCard.text?.clear()
        binding.etRecipientName.text?.clear()
        binding.etReason.text?.clear()
        binding.etAmount.text?.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}