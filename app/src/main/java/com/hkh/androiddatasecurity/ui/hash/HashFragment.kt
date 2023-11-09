package com.hkh.androiddatasecurity.ui.hash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.hkh.androiddatasecurity.R
import com.hkh.androiddatasecurity.common.Utils.showToast
import com.hkh.androiddatasecurity.databinding.FragmentHashBinding

class HashFragment: Fragment() {

    private var _binding: FragmentHashBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: HashViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[HashViewModel::class.java]
        _binding = FragmentHashBinding.inflate(inflater, container, false)
        initActions()
        return binding.root
    }

    private fun initActions() {
        observeHashedMessage()
        observeVerifiedMessage()
        setupViews()
    }

    private fun observeHashedMessage() =
        viewModel.hashedMessage.observe(viewLifecycleOwner) { hashedMessage ->
            hashedMessage?.let {
                binding.hashedTextView.text = hashedMessage.trim()
            } ?: resetHashedTextView()
            resetVerifiedTextView()
        }

    private fun observeVerifiedMessage() =
        viewModel.verifiedMessage.observe(viewLifecycleOwner) { verifiedMessage ->
            verifiedMessage?.let {
                binding.verifiedTextView.text = it.toString()
            } ?: resetVerifiedTextView()
        }

    private fun setupViews() = with(binding) {
        hashKeyButton.setOnClickListener {
            val input = getUserInputText()
            if (input.isNullOrEmpty()) {
                showToast(getString(R.string.user_input_is_empty))
            } else {
                viewModel.generateHashedData(input)
            }
        }
        verifyKeyButton.setOnClickListener {
            val input = getUserInputText()
            val hashed = getHashedText()
            if (input.isNullOrEmpty()) {
                showToast(getString(R.string.user_input_is_empty))
            } else if (hashed == getString(R.string.unknown_hashed)) {
                showToast(getString(R.string.hashed_text_is_empty))
            } else {
                viewModel.verifyHashedData(input, hashed)
            }
        }
        resetKeyButton.setOnClickListener {
            resetAllData(true)
        }
        userInputEditText.addTextChangedListener {
            if (it?.toString().isNullOrEmpty()) resetAllData(false)
        }
    }

    private fun resetAllData(resetInput: Boolean) {
        with(viewModel) {
            resetHashedMessage()
            resetVerifiedMessage()
        }
        if (resetInput) binding.userInputEditText.setText("")
    }

    private fun resetHashedTextView() {
        binding.hashedTextView.text = getString(R.string.unknown_hashed)
    }

    private fun resetVerifiedTextView() {
        binding.verifiedTextView.text = getString(R.string.unknown_verified)
    }

    private fun getUserInputText() = binding.userInputEditText.text?.toString()

    private fun getHashedText() = binding.hashedTextView.text?.toString() ?: ""

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}