package com.example.fashionsapp.tab.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.fashionsapp.R
import com.example.fashionsapp.databinding.FragmentAccountBinding
import com.example.fashionsapp.playback.view.PlaybackActivity

import com.vnpttech.ipcamera.VNPTCamera


 class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!


    private lateinit var camera: VNPTCamera

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.editTextUid.setText("VNTTC-000237-DXKPK")
        binding.editTextPass.setText("Phh7a0j3")

        binding.buttonLogin.setOnClickListener {
            val uid = binding.editTextUid.text.toString()
            val pass = binding.editTextPass.text.toString()
            if (uid.isNotEmpty() && pass.isNotEmpty()) {
                val intent = Intent(requireContext(), PlaybackActivity::class.java).apply {
                    putExtra("UID", uid)
                    putExtra("PASS", pass)
                }
                startActivity(intent)
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }




}
