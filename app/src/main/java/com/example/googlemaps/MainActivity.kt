package com.example.googlemaps

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private var mGoogleMap: GoogleMap? = null
    private lateinit var mapOptionButton: ImageView
    private lateinit var autocompleteFragment: AutocompleteSupportFragment
    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    private lateinit var geofencingManager: GeofencingManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Places.initialize(applicationContext, getString(R.string.google_map_api_key))
        autocompleteFragment =
            supportFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment
        autocompleteFragment.setPlaceFields(
            listOf(
                Place.Field.ID,
                Place.Field.ADDRESS,
                Place.Field.LAT_LNG
            )
        )

        geofencingManager = GeofencingManager(this)


        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onError(status:Status) {
                Toast.makeText(
                    this@MainActivity,
                    "Error searching for place: ${status.statusMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onPlaceSelected(place: Place) {
                val latLng = place.latLng!!
                addMarkerToMap(latLng, place.name!!)
                zoomOnMap(latLng)

                geofencingManager.addGeofence(latLng)

            }
        })

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        mapOptionButton = findViewById(R.id.mapOptionsMenu)
        val popupMenu = PopupMenu(this, mapOptionButton)
        popupMenu.menuInflater.inflate(R.menu.map_options, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            changeMap(menuItem.itemId)
            true
        }
        mapOptionButton.setOnClickListener {
            popupMenu.show()
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        val geofence_radius = 100
        mGoogleMap = googleMap
        enableMyLocation()
        mGoogleMap?.setOnMapClickListener { latLng ->
            mGoogleMap?.clear()
            val markerTitle = "Lat: ${latLng.latitude}, Lng: ${latLng.longitude}"
            mGoogleMap?.addMarker(MarkerOptions().position(latLng).title(markerTitle))
            geofencingManager.addGeofence(latLng)


            googleMap.addCircle(
                CircleOptions()
                    .center(LatLng(11.355896741411405,77.82673638314009))
                    .radius(geofence_radius.toDouble())
                    .strokeColor(ContextCompat.getColor(this, R.color.red))
                    .fillColor(ContextCompat.getColor(this, R.color.white))
            )

        }
    }

    private fun zoomOnMap(latLng: LatLng) {
        mGoogleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
    }

    private fun changeMap(itemId: Int) {
        when (itemId) {
            R.id.normal_map -> mGoogleMap?.mapType = GoogleMap.MAP_TYPE_NORMAL
            R.id.hybrid_map -> mGoogleMap?.mapType = GoogleMap.MAP_TYPE_HYBRID
            R.id.satellite_map -> mGoogleMap?.mapType = GoogleMap.MAP_TYPE_SATELLITE
            R.id.terrain_map -> mGoogleMap?.mapType = GoogleMap.MAP_TYPE_TERRAIN
        }
    }

    private fun addMarkerToMap(latLng: LatLng, title: String) {
        mGoogleMap?.addMarker(MarkerOptions().position(latLng).title(title))
    }

    private fun enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mGoogleMap?.isMyLocationEnabled = true
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation()
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
