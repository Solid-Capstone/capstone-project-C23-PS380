package com.solidcapstone.semar.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.solidcapstone.semar.adapter.HomeVideoListAdapter
import com.solidcapstone.semar.adapter.HomeWayangListAdapter
import com.solidcapstone.semar.data.Result
import com.solidcapstone.semar.databinding.FragmentHomeBinding
import com.solidcapstone.semar.ui.profile.ProfileActivity
import com.solidcapstone.semar.ui.video.ListVideoActivity
import com.solidcapstone.semar.utils.WayangViewModelFactory

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var auth: FirebaseAuth

    private val viewModel: HomeViewModel by viewModels {
        WayangViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        auth = Firebase.auth
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        showListWayang()
        showListVideo()

        val currentUser = auth.currentUser
        binding.tvUserName.text = currentUser?.displayName

        binding.btnUserImage.setOnClickListener {
            val intent = Intent(requireContext(), ProfileActivity::class.java)
            startActivity(intent)
        }
        binding.btnMoreWayang.setOnClickListener {
            val intent = Intent(requireContext(), ListWayangActivity::class.java)
            startActivity(intent)
        }
        binding.btnMoreVideos.setOnClickListener {
            val intent = Intent(requireContext(), ListVideoActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showListWayang() {
        binding.rvWayang.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )

        viewModel.getListWayang(2).observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> binding.pbWayang.visibility = View.VISIBLE

                is Result.Success -> {
                    val wayangListAdapter = HomeWayangListAdapter(result.data)
                    binding.rvWayang.adapter = wayangListAdapter
                    binding.pbWayang.visibility = View.GONE
                }

                is Result.Error -> {
                    binding.pbWayang.visibility = View.GONE
                    Log.d("HomeFragmentWayang", result.toString())
                }
            }
        }
    }

    private fun showListVideo() {
        binding.rvVideos.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            )
            viewModel.getListVideo(2).observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Result.Loading -> binding.pbVideo.visibility = View.VISIBLE

                    is Result.Success -> {
                        val videoListAdapter = HomeVideoListAdapter(result.data)
                        binding.rvVideos.adapter = videoListAdapter
                        binding.pbVideo.visibility = View.GONE
                    }

                    is Result.Error -> {
                        binding.pbVideo.visibility = View.GONE
                        Log.d("HomeFragmentWayang", result.toString())
                    }
                }
            }
        }
    }

}