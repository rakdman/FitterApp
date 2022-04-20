package com.example.fittr.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment

import com.example.fittr.databinding.FragmentPhotoBinding

class ImageFragment : DialogFragment() {

    private var _binding: FragmentPhotoBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPhotoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.image.setImageDrawable(ProfileFragment.getInstance()?.getCurrentImage())

        binding.backButton.setOnClickListener {
            dismiss()
        }
    }
}