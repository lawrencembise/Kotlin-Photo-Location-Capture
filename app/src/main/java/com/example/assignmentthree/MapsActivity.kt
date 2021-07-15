package com.example.assignmentthree

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.io.ByteArrayOutputStream


private lateinit var fusedLocationClient: FusedLocationProviderClient
internal var mCurrLocationMarker: Marker? = null



class MapsActivity : FragmentActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var mMap: GoogleMap

    //last location current location
    private lateinit var lastLocation: Location

    //Firebase
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference


    companion object {
        private const val MY_PERMISSION_FINE_LOCATION = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        //fused location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        //Firebase reference
        database = FirebaseDatabase.getInstance()
        reference = database.getReference("locations")

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.setOnMarkerClickListener(this)

        //check permissions
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            //condition for Marshmello and above
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSION_FINE_LOCATION
                )
            }
        }

        // 1 isMyLocationEnabled = true enables the my-location layer which draws a light blue dot on the user’s location. It also adds a button to the map that, when tapped, centers the map on the user’s location.
        mMap.isMyLocationEnabled = true
        // 2 fusedLocationClient.getLastLocation() gives you the most recent location currently available.
        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            // Got last known location. In some rare situations this can be null.
            // 3 If you were able to retrieve the the most recent location, then move the camera to the user’s current location.
            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))

                //kuonesha marker ya green
                val latLng = LatLng(location.latitude, location.longitude)
                val markerOptions = MarkerOptions()
                markerOptions.position(latLng)
                markerOptions.title("Current Position")
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                mCurrLocationMarker = mMap!!.addMarker(markerOptions)
            }
            sendData()
        }

        mMap.setOnMarkerClickListener(this)
    }

    override fun onMarkerClick(p0: Marker?) = false

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSION_FINE_LOCATION -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {//permission to access location grant
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    mMap.isMyLocationEnabled = true
                }
            }
            //permission to access location denied
            else {
                Toast.makeText( //message displayed on the bottom of the screen
                    applicationContext,
                    "This app requires location permissions to be granted",
                    Toast.LENGTH_LONG
                ).show()
                finish()
            }
        }
    }

    public fun sendData() {
        val bitmap = intent.getParcelableExtra("BitmapImage") as Bitmap?


        val location = LocationsPic(lastLocation.latitude, lastLocation.longitude, bitmap)
        val id = reference.push().key
        //sending the data to firebase
        reference.child(id!!).setValue(location)
    }

}