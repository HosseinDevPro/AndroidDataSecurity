package com.hkh.androiddatasecurity.ui.symmetric

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.hkh.androiddatasecurity.databinding.FragmentSymmetricBinding

class SymmetricFragment: Fragment() {

    private var _binding: FragmentSymmetricBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this)[SymmetricViewModel::class.java]

        _binding = FragmentSymmetricBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.symmetricText
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}