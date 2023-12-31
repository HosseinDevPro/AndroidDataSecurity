package com.hkh.symmetric

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.hkh.common.base.Constants.GREEN_COLOR
import com.hkh.common.base.Constants.RED_COLOR
import com.hkh.common.base.Utils.showToast
import com.hkh.common.SecurityConstant.KEY_ALIAS_SYMMETRIC
import com.hkh.symmetric.databinding.FragmentSymmetricBinding
import com.hkh.common.FingerprintPrompt

class SymmetricFragment : Fragment() {

    private val fingerprintPrompt by lazy {
        FingerprintPrompt(requireActivity())
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
        viewModel.supportStrongBox = hasStrongBox()
        observeIsKeyExist()
        observeShowErrorMessage()
        observeCheckKeyGeneration()
        observeCheckKeyRemove()
        observeCheckEncryptionMessage()
        observeEncryptedData()
        observeCheckDecryptionMessage()
        observeDecryptedData()

        checkKeyStatus()
        setupViews()
    }

    private fun observeIsKeyExist() = viewModel.isKeyExist.observe(viewLifecycleOwner) {
        showToast(getString(if (it) com.hkh.common.R.string.key_is_exist else com.hkh.common.R.string.key_was_not_exist))
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

    private fun observeCheckEncryptionMessage() =
        viewModel.checkEncryptionMessage.observe(viewLifecycleOwner) { userInputText ->
            openBiometric {
                viewModel.encryptData(userInputText)
            }
        }

    private fun observeEncryptedData() =
        viewModel.encryptedData.observe(viewLifecycleOwner) { data ->
            data?.let {
                binding.encryptedTextView.text = it.getBase64CipherData().trim()
            } ?: resetEncryptedText()
            resetDecryptedText()
        }

    private fun observeCheckDecryptionMessage() =
        viewModel.checkDecryptionMessage.observe(viewLifecycleOwner) { encryptedText ->
            openBiometric {
                viewModel.decryptData(encryptedText)
            }
        }

    private fun observeDecryptedData() =
        viewModel.decryptedData.observe(viewLifecycleOwner) { decryptedText ->
            decryptedText?.let {
                binding.decryptedTextView.text = it
            } ?: resetDecryptedText()
        }

    private fun setupViews() = with(binding) {
        if (!fingerprintPrompt.canAuthenticate()) errorView.visibility = View.VISIBLE
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
                getString(com.hkh.common.R.string.unknown_encrypted)
            )
        }
        userInputEditText.addTextChangedListener {
            resetAllData(false)
        }
    }

    private fun resetAllData(canResetInput: Boolean) {
        with(viewModel) {
            resetEncryptedData()
            resetDecryptedData()
        }
        if (canResetInput) binding.userInputEditText.setText("")
    }

    private fun resetEncryptedText() =
        binding.encryptedTextView.setText(getString(com.hkh.common.R.string.unknown_encrypted))

    private fun resetDecryptedText() =
        binding.decryptedTextView.setText(getString(com.hkh.common.R.string.unknown_decrypted))

    private fun checkKeyStatus() = binding.keyStatusTextView.apply {
        text = if (viewModel.keyStoreManager.isKeyExist(KEY_ALIAS_SYMMETRIC)) {
            setTextColor(GREEN_COLOR)
            getString(com.hkh.common.R.string.key_is_exist)
        } else {
            setTextColor(RED_COLOR)
            getString(com.hkh.common.R.string.key_was_not_exist)
        }
    }

    private fun openBiometric(onSuccess: () -> Unit) {
        fingerprintPrompt.show(
            title = getString(com.hkh.common.R.string.need_finger_print_for_operation),
            description = getString(com.hkh.common.R.string.cancel)
        ).observe(viewLifecycleOwner) { result ->
            if (result.isSuccess) {
                onSuccess.invoke()
            } else {
                if (result.isFailedToReadFingerPrint())
                    showToast(getString(com.hkh.common.R.string.failed_to_read_biometric))
            }
        }
    }

    private fun hasStrongBox() = requireContext().packageManager.hasSystemFeature(PackageManager.FEATURE_STRONGBOX_KEYSTORE)

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}