package com.hkh.androiddatasecurity.ui.symmetric

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.hkh.androiddatasecurity.R
import com.hkh.androiddatasecurity.common.Constant.AUTHENTICATORS
import com.hkh.androiddatasecurity.common.Constant.KEY_ALIAS_SYMMETRIC
import com.hkh.androiddatasecurity.common.FingerprintPrompt
import com.hkh.androiddatasecurity.databinding.FragmentSymmetricBinding


class SymmetricFragment : Fragment() {

    private val canAuthenticate by lazy {
        BiometricManager.from(requireContext().applicationContext)
            .canAuthenticate(AUTHENTICATORS) == BiometricManager.BIOMETRIC_SUCCESS
    }

    private var _binding: FragmentSymmetricBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: SymmetricViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[SymmetricViewModel::class.java]
        _binding = FragmentSymmetricBinding.inflate(inflater, container, false)
        initActions()
        return binding.root
    }

    private fun initActions() {
        observeIsKeyExist()
        observeShowErrorMessage()
        observeCheckKeyGeneration()
        observeCheckKeyRemove()
        observeCheckEncryptMessage()
        observeCheckDecryptMessage()

        checkKeyStatus()
        setupViews()
    }

    private fun observeIsKeyExist() = viewModel.isKeyExist.observe(viewLifecycleOwner) {
        showToast(getString(if (it) R.string.key_is_exist else R.string.key_was_not_exist))
    }

    private fun observeShowErrorMessage() = viewModel.showErrorMessage.observe(viewLifecycleOwner) {
        showToast(getString(it))
    }

    private fun observeCheckKeyGeneration() =
        viewModel.checkKeyGeneration.observe(viewLifecycleOwner) {
            if (it) resetAllData(true)
            checkKeyStatus()
        }

    private fun observeCheckKeyRemove() =
        viewModel.checkKeyRemove.observe(viewLifecycleOwner) {
            if (it) resetAllData(true)
            checkKeyStatus()
        }

    private fun observeCheckEncryptMessage() =
        viewModel.checkEncryptMessage.observe(viewLifecycleOwner) { userInputText ->
            openBiometric {
                viewModel.encryptData(userInputText)
                binding.encryptedTextView.text = viewModel.sealedData?.getBase64CipherData()?.trim()
                resetDecryptedText()
            }
        }

    private fun observeCheckDecryptMessage() =
        viewModel.checkDecryptMessage.observe(viewLifecycleOwner) { encryptedText ->
            openBiometric {
                viewModel.decryptData(encryptedText)
                binding.decryptedTextView.text = viewModel.decryptedData
                viewModel.sealedData = null
            }
        }

    private fun setupViews() = with(binding) {
        if (!canAuthenticate) errorView.visibility = View.VISIBLE
        generateKeyButton.setOnClickListener {
            viewModel.checkKeyGeneration()
        }
        removeKeyButton.setOnClickListener {
            viewModel.checkKeyRemove()
        }
        encryptKeyButton.setOnClickListener {
            viewModel.checkEncryption(binding.userInputEditText.text.toString())
        }
        decryptKeyButton.setOnClickListener {
            viewModel.checkDecryption(
                binding.encryptedTextView.text.toString(),
                getString(R.string.unknown_encrypted)
            )
        }
        userInputEditText.addTextChangedListener {
            resetAllData(false)
        }
    }

    private fun resetAllData(canResetInput: Boolean) {
        resetEncryptedText()
        resetDecryptedText()
        viewModel.sealedData = null
        viewModel.decryptedData = null
        if (canResetInput) binding.userInputEditText.setText("")
    }

    private fun resetEncryptedText() =
        binding.encryptedTextView.setText(getString(R.string.unknown_encrypted))

    private fun resetDecryptedText() =
        binding.decryptedTextView.setText(getString(R.string.unknown_decrypted))

    private fun checkKeyStatus() = binding.keyStatusTextView.apply {
        text = if (viewModel.keyStoreManager.isKeyExist(KEY_ALIAS_SYMMETRIC)) {
            setTextColor(GREEN_COLOR)
            getString(R.string.key_is_exist)
        } else {
            setTextColor(RED_COLOR)
            getString(R.string.key_was_not_exist)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun openBiometric(onSuccess: () -> Unit) {
        FingerprintPrompt.show(requireActivity()).observe(viewLifecycleOwner) { result ->
            if (result.isSuccess) {
                onSuccess.invoke()
            } else {
                if (result.isFailedToReadFingerPrint())
                    showToast(getString(R.string.failed_to_read_biometric))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        val GREEN_COLOR = Color.argb(255, 33, 103, 94)
        val RED_COLOR = Color.argb(255, 188, 19, 31)
    }

}