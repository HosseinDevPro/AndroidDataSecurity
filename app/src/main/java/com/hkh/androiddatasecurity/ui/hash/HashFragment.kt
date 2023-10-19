package com.hkh.androiddatasecurity.ui.hash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.hkh.androiddatasecurity.databinding.FragmentHashBinding

class HashFragment: Fragment() {

    private var _binding: FragmentHashBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val hashViewModel =
            ViewModelProvider(this)[HashViewModel::class.java]

        _binding = FragmentHashBinding.inflate(inflater, container, false)
        val root: View = binding.root

        hashViewModel.text.observe(viewLifecycleOwner) {
            binding.hashText.text = it
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}