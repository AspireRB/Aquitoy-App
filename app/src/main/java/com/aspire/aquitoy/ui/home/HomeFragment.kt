package com.aspire.aquitoy.ui.home


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.aspire.aquitoy.R
import com.aspire.aquitoy.data.LocationService
import com.aspire.aquitoy.databinding.FragmentHomeBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch

class HomeFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var map: GoogleMap
    private val locationService: LocationService = LocationService()

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        createMarker()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        createMapFragment()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun createMapFragment() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.Map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun createMarker() {
        lifecycleScope.launch {
            val result = locationService.getUserLocation(requireContext())
            result?.let { location ->
                val coordinates = LatLng(location.latitude, location.longitude)
                map.addMarker(MarkerOptions().position(coordinates).title("Ubicación"))
                map.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(coordinates, 18f),
                    4000,
                    null
                )
            }
        }
    }
}