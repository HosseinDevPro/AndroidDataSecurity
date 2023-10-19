package com.hkh.androiddatasecurity.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.hkh.androiddatasecurity.databinding.FragmentHomeBinding

class HomeFragment: Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this)[HomeViewModel::class.java]

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.homeText
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        setupListener()

        return root
    }

    private fun setupListener() = with(binding) {
        symmetricButton.setOnClickListener {
            Toast.makeText(requireContext(), "symmetric", Toast.LENGTH_SHORT).show()
        }
        asymmetricButton.setOnClickListener {
            Toast.makeText(requireContext(), "asymmetric", Toast.LENGTH_SHORT).show()
        }
        hashButton.setOnClickListener {
            Toast.makeText(requireContext(), "hash", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}