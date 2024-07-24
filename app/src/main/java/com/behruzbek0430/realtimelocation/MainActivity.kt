package com.behruzbek0430.realtimelocation

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.behruzbek0430.realtimelocation.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var mMap: GoogleMap
    lateinit var locationRequest: LocationRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.create()
        locationRequest.setInterval(10000)
        locationRequest.setFastestInterval(5000)
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

        checkSettingsAndStartUpdates()
    }
    val locationCallback = object : LocationCallback(){
        override fun onLocationResult(p0: LocationResult) {
            if (p0 == null){
                return
            }
            for (location : Location in p0.locations) {
                Toast.makeText(this@MainActivity, "${location.latitude}, ${location.longitude}", Toast.LENGTH_SHORT).show()
                mMap.addMarker(MarkerOptions().position(LatLng(location.latitude, location.longitude)))
                val camerPosition = CameraPosition.Builder()
                    .zoom(18f)
                    .tilt(45f)
                    .bearing(90f)
                    .target(LatLng(location.latitude, location.longitude))

                mMap.mapType = GoogleMap.MAP_TYPE_HYBRID

                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camerPosition.build()))
            }
        }
    }
    override fun onMapReady(p0: GoogleMap) {
        mMap = p0




    }




    fun checkSettingsAndStartUpdates() {
        val request = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .build()
        val client = LocationServices.getSettingsClient(this)
        val locationSettingsResponseTask: Task<LocationSettingsResponse> =
            client.checkLocationSettings(request)
        locationSettingsResponseTask.addOnSuccessListener {
            //Settings of device are satisfied and we can start location updates
            startLocationUpdates()
        }
        locationSettingsResponseTask.addOnFailureListener {
            Log.d(TAG, "checkSettingsAndStartUpdates: Error")
            Toast.makeText(this, "Xatolik \ncheckSettingsAndStartUpdates", Toast.LENGTH_SHORT)
                .show()
        }
    }

    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }


}