package com.talos.mexicanplacesrf.ui.adapters.fragments

import android.Manifest
import android.R.id.message
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.talos.mexicanplacesrf.R
import com.talos.mexicanplacesrf.utils.message

private const val ARG_LAT = "param1"
private const val ARG_LNG = "param2"
private const val ARG_LOCATION = "location"

class MapsFragment : Fragment(), OnMapReadyCallback {

    private var lat: String? = null
    private var lng: String? = null
    private var location: String? = null

    private var fineLocationPermissionGranted = false
    private lateinit var googleMap: GoogleMap

    private val permissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){ isGranted ->
        if(isGranted){
            actionPermissionGranted()
        }else{
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                AlertDialog.Builder(requireContext())
                    .setTitle("Permiso requerido")
                    .setMessage("Se necesita el permiso para poder ubicar la posición del usuario en el mapa")
                    .setPositiveButton("Entendido"){ _, _ ->
                        updateOrRequestPermissions()
                    }
                    .setNegativeButton("Salir"){ dialog, _ ->
                        dialog.dismiss()
                        requireActivity().finish()
                    }
                    .create()
                    .show()
            } else {
                Toast.makeText(
                    requireContext(),
                    "El permiso de ubicación se ha negado permanentemente",
                    Toast.LENGTH_SHORT
                ).show()
                requireActivity().finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       arguments?.let {
            lat = it.getString(ARG_LAT)
            lng = it.getString(ARG_LNG)
           location = it.getString(ARG_LOCATION)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = SupportMapFragment.newInstance()
        childFragmentManager.beginTransaction()
            .replace(R.id.map_container, mapFragment)
            .commit()
        mapFragment.getMapAsync(this)
        updateOrRequestPermissions()
    }

    companion object {
        @JvmStatic fun newInstance(param1: String, param2: String, locat: String ) =
                MapsFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_LAT, param1)
                        putString(ARG_LNG, param2)
                        putString(ARG_LOCATION, locat)
                    }
                }
    }

    private fun actionPermissionGranted(){

        if (::googleMap.isInitialized) {
            try {
                googleMap.isMyLocationEnabled = true
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }

    }
    private fun updateOrRequestPermissions() {
        fineLocationPermissionGranted = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!fineLocationPermissionGranted) {
            permissionsLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }else{
            actionPermissionGranted()
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        //requireActivity().message("Mapa listo con $lat y $lng")
        MapsInitializer.initialize(requireContext(), MapsInitializer.Renderer.LATEST) {}

        val latLng = LatLng(lat?.toDouble() ?:20.215041321398104, lng?.toDouble() ?: -98.73127823457513)
        map.addMarker(MarkerOptions().position(latLng).title(location).icon(BitmapDescriptorFactory.fromResource(R.drawable.trompo)))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
    }
}