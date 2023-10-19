package com.hkh.androiddatasecurity.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.hkh.androiddatasecurity.R
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

        homeViewModel.text.observe(viewLifecycleOwner) {
            binding.homeText.text = it
        }

        setupListener()

        return root
    }

    private fun setupListener() = with(binding) {
        symmetricButton.setOnClickListener {
            findNavController().navigate(R.id.symmetricFragment)
        }
        asymmetricButton.setOnClickListener {
            findNavController().navigate(R.id.asymmetricFragment)
        }
        hashButton.setOnClickListener {
            findNavController().navigate(R.id.hashFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}