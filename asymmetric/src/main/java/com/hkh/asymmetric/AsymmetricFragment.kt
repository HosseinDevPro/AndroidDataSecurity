package com.hkh.asymmetric

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
import com.hkh.asymmetric.databinding.FragmentAsymmetricBinding
import com.hkh.common.FingerprintPrompt
import com.hkh.common.SecurityConstant

class AsymmetricFragment: Fragment() {

    private val fingerprintPrompt by lazy {
        FingerprintPrompt(requireActivity())
    }

    private var _binding: FragmentAsymmetricBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: AsymmetricViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[AsymmetricViewModel::class.java]
        _binding = FragmentAsymmetricBinding.inflate(inflater, container, false)
        initActions()
        return binding.root
    }

    private fun initActions() {
        observeIsKeyExist()
        observeShowErrorMessage()
        observeCheckKeyGeneration()
        observeCheckKeyRemove()
        observeCheckSigningMessage()
        observeSignedData()
        observeCheckVerifyingMessage()
        observeVerifiedData()

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

    private fun observeCheckSigningMessage() =
        viewModel.checkSigningMessage.observe(viewLifecycleOwner) { userInputText ->
            openBiometric {
                viewModel.signData(userInputText)
            }
        }

    private fun observeSignedData() =
        viewModel.signedData.observe(viewLifecycleOwner) { signedData ->
            signedData?.let {
                binding.signedTextView.text = it.trim()
            } ?: resetSignedText()
            resetVerifiedText()
        }

    private fun observeCheckVerifyingMessage() =
        viewModel.checkVerifiedMessage.observe(viewLifecycleOwner) { signedMessage ->
            openBiometric {
                viewModel.verifyData(
                    plainText = getUserTextInput(),
                    signedText = signedMessage
                )
            }
        }

    private fun observeVerifiedData() =
        viewModel.isVerifiedData.observe(viewLifecycleOwner) { isVerified ->
            isVerified?.let {
                binding.verifiedTextView.text = it.toString()
            } ?: resetVerifiedText()
        }

    private fun setupViews() = with(binding) {
        if (!fingerprintPrompt.canAuthenticate()) errorView.visibility = View.VISIBLE
        generateKeyButton.setOnClickListener {
            viewModel.checkKeyGeneration()
        }
        removeKeyButton.setOnClickListener {
            viewModel.checkKeyRemove()
        }
        signKeyButton.setOnClickListener {
            viewModel.checkSigning(getUserTextInput())
        }
        verifyKeyButton.setOnClickListener {
            viewModel.checkVerifying(
                binding.signedTextView.text.toString(),
                getString(com.hkh.common.R.string.unknown_signed)
            )
        }
        userInputEditText.addTextChangedListener {
            if (it?.toString().isNullOrEmpty()) resetAllData(false)
        }
    }

    private fun resetAllData(canResetInput: Boolean) {
        with(viewModel) {
            resetSignedData()
            resetVerifiedData()
        }
        if (canResetInput) binding.userInputEditText.setText("")
    }

    private fun resetSignedText() =
        binding.signedTextView.setText(getString(com.hkh.common.R.string.unknown_signed))

    private fun resetVerifiedText() =
        binding.verifiedTextView.setText(getString(com.hkh.common.R.string.unknown_verified))

    private fun checkKeyStatus() = binding.keyStatusTextView.apply {
        text = if (viewModel.keyStoreManager.isKeyExist(SecurityConstant.KEY_ALIAS_ASYMMETRIC)) {
            setTextColor(GREEN_COLOR)
            getString(com.hkh.common.R.string.key_is_exist)
        } else {
            setTextColor(RED_COLOR)
            getString(com.hkh.common.R.string.key_was_not_exist)
        }
    }

    private fun getUserTextInput() = binding.userInputEditText.text.toString()

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}