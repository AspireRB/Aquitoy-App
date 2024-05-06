package com.aspire.aquitoy.ui.requests

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.aspire.aquitoy.databinding.FragmentRequestsBinding
import com.aspire.aquitoy.ui.requests.model.HyperRequestAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RequestsFragment : Fragment() {

    private var _binding: FragmentRequestsBinding? = null
    private val binding get() = _binding!!

    private lateinit var requestsViewModel: RequestsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        requestsViewModel = ViewModelProvider(this).get(RequestsViewModel::class.java)

        _binding = FragmentRequestsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        initRecyclerView()

        return root
    }

    private fun initRecyclerView() {
        binding.recyclerViewServices.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = HyperRequestAdapter(requestsViewModel.getServiceList)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}