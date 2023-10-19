package com.hkh.androiddatasecurity.ui.asymmetric

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.hkh.androiddatasecurity.databinding.FragmentAsymmetricBinding

class AsymmetricFragment: Fragment() {

    private var _binding: FragmentAsymmetricBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val asymmetricViewModel =
            ViewModelProvider(this)[AsymmetricViewModel::class.java]

        _binding = FragmentAsymmetricBinding.inflate(inflater, container, false)
        val root: View = binding.root

        asymmetricViewModel.text.observe(viewLifecycleOwner) {
            binding.asymmetricText.text = it
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}