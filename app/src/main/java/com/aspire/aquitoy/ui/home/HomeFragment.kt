package com.aspire.aquitoy.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.aspire.aquitoy.R
import com.aspire.aquitoy.common.common
import com.aspire.aquitoy.data.ApiService
import com.aspire.aquitoy.databinding.FragmentHomeBinding
import com.aspire.aquitoy.ui.home.callback.FirebaseFailedListener
import com.aspire.aquitoy.ui.home.callback.FirebaseNurseInfoListener
import com.aspire.aquitoy.ui.home.model.GeoQueryModel
import com.aspire.aquitoy.ui.home.model.NurseGeoModel
import com.aspire.aquitoy.ui.home.model.NurseInfoModel
import com.aspire.aquitoy.ui.home.model.RouteResponse
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.firebase.geofire.GeoQueryEventListener
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

@AndroidEntryPoint
@Suppress("DEPRECATION")
class HomeFragment : Fragment(), OnMapReadyCallback, FirebaseNurseInfoListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var map: GoogleMap
    private lateinit var mapFragment: SupportMapFragment

    //Location
    lateinit var locationRequest: LocationRequest
    lateinit var locationCallback: LocationCallback
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    //Routes
    private var coordinates: LatLng = LatLng(0.0, 0.0)
    private var start: String = ""
    private var end: String = ""

    var poly: Polyline? = null

    //Load Nurse
    var distance = 1.0
    val LIMIT_RANGE = 10.0
    var previousLocation: Location? = null
    var currentLocation: Location? = null

    var firsTime = true

    //Listener
    lateinit var iFirebaseNurseInfoListener: FirebaseNurseInfoListener
    lateinit var iFirebaseFailedListener: FirebaseFailedListener

    //Loading
    private var _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private var serviceId: String = ""

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()
        )
    }

    private fun stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    override fun onPause() {
        stopLocationUpdates()
        super.onPause()
    }

    override fun onDestroy() {
        stopLocationUpdates()
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        if (::fusedLocationProviderClient.isInitialized && ::locationCallback.isInitialized) {
            startLocationUpdates()
            loadAvailableNurse()
        }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap!!
        Dexter.withContext(requireContext())
            .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(object: PermissionListener{
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    if (ActivityCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        return
                    }
                    map.isMyLocationEnabled = true
                    map.uiSettings.isMyLocationButtonEnabled = true
                    map.setOnMyLocationButtonClickListener {
                        fusedLocationProviderClient.lastLocation
                            .addOnFailureListener { e ->
                                Snackbar.make(requireView(),e.message!!,
                                    Snackbar.LENGTH_LONG).show()
                            }
                            .addOnSuccessListener { location ->
                                val userLatLng = LatLng(location.latitude,location.longitude)
                                map.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng,18f))
                            }
                        true
                    }
                    val locationButton = (mapFragment.requireView()!!.findViewById<View>("1".toInt())!!
                        .parent!! as View)
                        .findViewById<View>("2".toInt())
                    val params = locationButton.layoutParams as RelativeLayout.LayoutParams
                    params.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
                    params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE)
                    params.bottomMargin = 250
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    Snackbar.make(requireView(),p0!!.permissionName+" Permiso necesario",
                        Snackbar.LENGTH_LONG).show()
                }

                override fun onPermissionRationaleShouldBeShown(p0: PermissionRequest?, p1: PermissionToken?) {

                }

            })
            .check()
        initListeners()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        createMapFragment()
        init()
        initUIState()

        return root
    }

    private fun initUIState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                isLoading.collect {
                    _binding!!.pbLoading.isVisible = it
                }
            }
        }
    }

    private fun init() {
        iFirebaseNurseInfoListener = this

        locationRequest = LocationRequest()
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        locationRequest.setFastestInterval(3000)
        locationRequest.setSmallestDisplacement(10f)
        locationRequest.interval = 5000

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                p0?.let { super.onLocationResult(it) }
                p0?.lastLocation?.let { location ->
                    val newPos = LatLng(location.latitude, location.longitude)
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(newPos, 18f))
                    coordinates = newPos

                    if (firsTime) {
                        previousLocation =  p0.lastLocation
                        currentLocation = p0.lastLocation

                        firsTime = false
                    } else {
                        previousLocation =  currentLocation
                        currentLocation = p0.lastLocation
                    }

                    if(previousLocation!!.distanceTo(currentLocation!!)/1000 <= LIMIT_RANGE)
                        loadAvailableNurse();
                }
            }
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper())

        loadAvailableNurse()
    }

    private fun loadAvailableNurse() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationProviderClient.lastLocation
            .addOnFailureListener { e ->
                Snackbar.make(requireView(),e.message!!,Snackbar.LENGTH_SHORT).show()
            }
            .addOnSuccessListener { location ->
                if (location != null) { // Add null check for location
                    //Load all nurses
                    try {
                        //Query
                        val nurse_location_ref = FirebaseDatabase.getInstance().getReference(common.NURSE_LOCATION_REFERENCE)
                        val geoFire = GeoFire(nurse_location_ref)
                        val geoQuery = geoFire.queryAtLocation(
                            GeoLocation(location.latitude,location
                            .longitude),distance)
                        geoQuery.removeAllListeners()

                        nurse_location_ref.addChildEventListener(object : ChildEventListener {
                            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                                val geoQueryModel = snapshot.getValue(GeoQueryModel::class.java)
                                val geoLocation = GeoLocation(geoQueryModel!!.l!![1], geoQueryModel!!.l!![0])
                                val nurseGeoModel = NurseGeoModel(snapshot.key, geoLocation)

                                val newDistance = calculateDistance(location.latitude, location.longitude, geoLocation.latitude, geoLocation.longitude)
                                if (newDistance <= LIMIT_RANGE) {
                                    findNurseByKey(nurseGeoModel)
                                }
                            }

                            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

                            }

                            override fun onChildRemoved(snapshot: DataSnapshot) {

                            }

                            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

                            }

                            override fun onCancelled(error: DatabaseError) {
                                Snackbar.make(requireView(),error.message,Snackbar
                                    .LENGTH_SHORT)
                                    .show()
                            }
                        })

                        Log.d("GeoQuery", "Starting geoQuery at location: (${location.latitude}, ${location.longitude}) with distance: $distance")

                        geoQuery.addGeoQueryEventListener(object : GeoQueryEventListener {
                            override fun onKeyEntered(key: String?, location: GeoLocation?) {
                                if (key != null && location != null) {
                                    common.nurseFound.add(NurseGeoModel(key, location))
                                    Log.d("nurseFound", "Tiene: ${common.nurseFound.size}")
                                } else {
                                    Log.e("GeoQuery", "Key or location is null. Key: $key, Location: $location")
                                }
                            }

                            override fun onKeyExited(key: String?) {
                                Log.d("GeoQuery", "onKeyExited called with Key: $key")
                            }

                            override fun onKeyMoved(key: String?, location: GeoLocation?) {
                                Log.d("GeoQuery", "onKeyMoved called with Key: $key, Location: $location")
                            }

                            override fun onGeoQueryReady() {
                                Log.d("GeoQuery", "onGeoQueryReady called")
                                if (distance <= LIMIT_RANGE) {
                                    distance++
                                    loadAvailableNurse()
                                } else {
                                    distance = 0.0
                                    if (_binding != null) {
                                        addNurseMarker()
                                    } else {
                                        Log.d("HomeFragment", "La vista no está disponible aún")
                                    }
                                }
                            }

                            override fun onGeoQueryError(error: DatabaseError?) {
                                Log.e("GeoQuery", "Error: ${error!!.message}")
                                Snackbar.make(requireView(),error!!.message,Snackbar
                                    .LENGTH_SHORT)
                                    .show()
                            }
                        })
                    } catch (e: IOException) {
                        Snackbar.make(requireView(),getString(R.string.permission_require),Snackbar
                            .LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Log.d("LoadNurse", "Nurse null")
                }
            }
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371.0 // Radius of the Earth in kilometers
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return R * c
    }

    private fun addNurseMarker() {
        if (common.nurseFound.size > 0) {
            Observable.fromIterable(common.nurseFound)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { nurseGeoModel : NurseGeoModel? ->
                        findNurseByKey(nurseGeoModel)
                    }, {
                            t: Throwable? -> Snackbar.make(requireView(), t!!.message!!, Snackbar
                        .LENGTH_SHORT).show()
                    }
                )
        }
    }

    private fun findNurseByKey(nurseGeoModel: NurseGeoModel?) {
        if (nurseGeoModel == null || nurseGeoModel.key == null) {
            Log.e("findNurseByKey", "NurseGeoModel or key is null")
            return
        }

        Log.d("findNurseByKey", "Searching for nurse with key: ${nurseGeoModel.key}")

        FirebaseDatabase.getInstance()
            .getReference(common.NURSE_INFO_REFERENCES) // Asegúrate de que esta referencia sea
            // correcta
            .child(nurseGeoModel.key!!)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val nurseInfo = snapshot.getValue(NurseInfoModel::class.java)
                        if (nurseInfo != null) {
                            Log.d("findNurseByKey", "Nurse data found: $nurseInfo")
                            nurseGeoModel.nurseInfo = nurseInfo
                            iFirebaseNurseInfoListener.onNurseInfoLoadSuccess(nurseGeoModel)
                        } else {
                            Log.e("findNurseByKey", "Nurse data is null for key: ${nurseGeoModel.key}")
                        }
                    } else {
                        Log.e("findNurseByKey", "No data exists for key: ${nurseGeoModel.key}")
                        iFirebaseFailedListener.onFirebaseFailed(getString(R.string.key_not_found) + nurseGeoModel.key)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("findNurseByKey", "Firebase error: ${error.message}")
                    iFirebaseFailedListener.onFirebaseFailed(getString(R.string.key_not_found) + nurseGeoModel.key)
                }
            })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initListeners() {
        if (::map.isInitialized)  {
            getService()
            // Configurar el listener del marcador solo si map está inicializado
            map.setOnMarkerClickListener { marker ->
                val nurseID = marker.title
                var checkState = homeViewModel.checkState(nurseID!!)
                if (nurseID != null) {
                    checkState = homeViewModel.checkState(nurseID!!)
                    if (checkState == true) {
                        _binding!!.btnService.visibility = View.VISIBLE
                        lifecycleScope.launch {
                            _binding!!.btnService.setOnClickListener {
                                homeViewModel.initialService(nurseID, coordinates)
                                _isLoading.value = true
                                getService()
                                Toast.makeText(context, "Esperando respuesta", Toast.LENGTH_SHORT).show()
                                _binding!!.btnService.visibility = View.INVISIBLE
                            }
                        }
                    } else {
                        Toast.makeText(context, "No disponible", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.d("nurseID", "Falla")
                }
                true // Devuelve true para indicar que has manejado el evento
            }
        } else {
            Log.d("Map", "El mapa no está inicializado aún")
        }
    }

    fun getService() {
        homeViewModel.getService()
        homeViewModel.serviceInfoListLiveData.observe(viewLifecycleOwner) { serviceInfoList ->
            serviceInfoList.forEach { serviceInfoModel ->
                Log.d("SERVICE","${serviceInfoModel.copy()}")
                homeViewModel.getLocationNurse(serviceInfoModel.nurseID!!) { nurseLocationService ->
                    if (serviceInfoModel.state == "accept") {
                        _isLoading.value = false
                        homeViewModel.updateState(false)
                        serviceId = serviceInfoModel.serviceID!!
                        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
                        start = "${coordinates.longitude}, ${coordinates.latitude}"
                        end = "${nurseLocationService!!.longitude}, ${nurseLocationService!!
                            .latitude}"
                        map.clear()
                        poly?.remove()
                        if (poly != null) {
                            poly = null
                        }
                        if (::map.isInitialized) {
                            map.addMarker(MarkerOptions()
                                .position(LatLng(nurseLocationService.latitude, nurseLocationService
                                    .longitude))
                                .flat(true)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_nurse)))
                            createRoute()
                        }
                    }
                }

                if (serviceInfoModel.state == "decline") {
                    _isLoading.value = false
                    homeViewModel.deleteService(serviceInfoModel.serviceID)
                }

                if (serviceId == serviceInfoModel.serviceID) {
                    if (serviceInfoModel.state == "finalized") {
                        val state = homeViewModel.getState()
                        if (state != true) {
                            homeViewModel.updateState(true)
                            poly?.remove()
                            map.clear()
                            Toast.makeText(context, "Servicio finalizado", Toast.LENGTH_SHORT).show()
                            init()
                            loadAvailableNurse()
                        }
                    }
                }
            }
        }
    }

    private fun createMapFragment() {
        mapFragment = childFragmentManager.findFragmentById(R.id.Map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.openrouteservice.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun createRoute() {
        CoroutineScope(Dispatchers.IO).launch {
            val call = getRetrofit().create(ApiService::class.java).getRoute("5b3ce3597851110001cf62483630a995a4b0492894d42ad8669f8933", start, end,)
            if (call.isSuccessful) {
                drawRoute(call.body())
                Log.i("Aspire", "OK")
            } else {
                Log.i("Aspire", "NO OK")
            }
        }
    }

    private fun drawRoute(routeResponse: RouteResponse?) {
        val polyLineOptions = PolylineOptions()
        routeResponse?.features?.first()?.geometry?.coordinates?.forEach {
            polyLineOptions.add(LatLng(it[1], it[0]))
        }
        activity?.runOnUiThread {
            poly = map.addPolyline(polyLineOptions)
        }
    }

    override fun onNurseInfoLoadSuccess(nurseGeoModel: NurseGeoModel?) {
        if (!common.markerList.containsKey(nurseGeoModel!!.key)) {
            val marker = map.addMarker(MarkerOptions()
                .position(LatLng(nurseGeoModel!!.geoLocation!!.latitude, nurseGeoModel!!
                    .geoLocation!!.longitude))
                .flat(true)
                .title(nurseGeoModel.key)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_nurse)))
            if (marker != null) {  // Check if marker is not null
                common.markerList.put(nurseGeoModel!!.key!!, marker)
            }
        }

        val nurseLocation = FirebaseDatabase.getInstance()
            .getReference(common.NURSE_LOCATION_REFERENCE)
            .child(nurseGeoModel!!.key!!)
        nurseLocation.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(!snapshot.exists()) {
                    if(common.markerList.get(nurseGeoModel!!.key!!) != null){
                        val marker = common.markerList.get(nurseGeoModel!!.key!!)
                        marker!!.remove()
                        common.markerList.remove(nurseGeoModel!!.key!!)
                        nurseLocation.removeEventListener(this)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Snackbar.make(requireView(),error.message,Snackbar.LENGTH_SHORT).show()
            }

        })
    }
}