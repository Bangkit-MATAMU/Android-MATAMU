package com.akih.matarak.hospital

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.akih.maps.model.NearbyPlaceResponse
import com.akih.matarak.R
import com.akih.matarak.databinding.FragmentHospitalBinding
import com.akih.matarak.viewmodel.MapsFragmentState
import com.akih.matarak.viewmodel.MapsViewModel
import com.akih.matarak.viewmodel.ViewModelProviderFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class HospitalFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private var _binding: FragmentHospitalBinding? = null
    private val binding get() = _binding!!
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mMaps: GoogleMap
    var initial_marker    = "My Location"
    private lateinit var viewModel: MapsViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHospitalBinding.inflate(inflater, container, false)
        val mFragment = childFragmentManager.findFragmentById(R.id.maps) as SupportMapFragment
        mFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        getLocation()
        setupViewModel()
        observe()
        return binding.root
    }

    private fun fetchNearbyPlaces(lat: String, lng : String){
        viewModel.nearby(lat, lng)
    }

    private fun setupViewModel(){
        viewModel = ViewModelProvider(this, ViewModelProviderFactory()).get(MapsViewModel::class.java)
    }

    private fun observe(){
        viewModel.listenToPlaces().observe(viewLifecycleOwner, Observer { handlePlaces(it) })
        viewModel.listenToStare().observe(viewLifecycleOwner, Observer { handleState(it) })
    }

    private fun handlePlaces(places: List<NearbyPlaceResponse.PlaceResponse>){
        places.forEach {
            println(it.name)
            val lat = if(it.geometry == null || it.geometry?.location == null) 0.0 else it.geometry!!.location!!.latitude
            val lng = if(it.geometry == null || it.geometry?.location == null) 0.0 else it.geometry!!.location!!.longitude

            val marker = mMaps.addMarker(MarkerOptions().position(LatLng(lat,lng)).title(it.name))
            marker.tag = it.id
            mMaps.setOnMarkerClickListener(this@HospitalFragment)
        }
    }

    private fun handleState(state: MapsFragmentState){
        when(state){
            is MapsFragmentState.IsLoading -> {}
            is MapsFragmentState.ShowToast -> Toast.makeText(requireActivity(), state.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMaps = googleMap
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        mMaps!!.setMyLocationEnabled(true)
    }

    @SuppressLint("MissingPermission")
    private fun getLocation(){
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let { currentLocation ->
                val sydney = LatLng(currentLocation.latitude, currentLocation.longitude);
//                mMaps.addMarker(MarkerOptions().position(sydney).title(initial_marker));
                mMaps.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15f));

                fetchNearbyPlaces(currentLocation.latitude.toString(), currentLocation.longitude.toString())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMarkerClick(p0: Marker?): Boolean {
        val tag = p0?.tag
        if (tag != null) {
            Toast.makeText(requireActivity(), "id place $tag diklik", Toast.LENGTH_SHORT).show()
        }
        return true

    }
}