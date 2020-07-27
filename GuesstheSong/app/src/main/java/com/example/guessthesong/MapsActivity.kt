package com.example.guessthesong

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.View
import android.widget.ImageButton
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    override fun onMarkerClick(p0: Marker?) = false

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location
    // private lateinit var locationCallback: LocationCallback
    private var maxlatitude = 51.619000
    private var minlatitude = 51.617000
    private var maxlongtitude = -3.875000
    private var minlongtitude = -3.882500
    private var lyricsAndSong = arrayListOf<String>()
    private var markers = arrayListOf<Marker>()
    private val earthRadius = 6366000
    private var switchMod = false
    override fun onNewIntent(intent: Intent) {

        val extras = intent.extras
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }
        var locationProviders = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (locationProviders == null || locationProviders == "") {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(getString(R.string.gpstitle))
            builder.setMessage(getString(R.string.gpsmessage))
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        setContentView(R.layout.activity_maps)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val imageButton = findViewById<ImageButton>(R.id.someimage)
        imageButton.setBackgroundResource(0)
        imageButton.setImageResource(R.drawable.avatar)
        imageButton.bringToFront()
        val switchbutton = findViewById<Switch>(R.id.switch1)
        switchbutton.bringToFront()
        val textCurrent = findViewById<TextView>(R.id.textView)
        textCurrent.bringToFront()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        map.getUiSettings().setZoomControlsEnabled(true)
        // Customise the styling of the base map using a JSON object defined
        // in a raw resource file.
        googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        this, R.raw.map_json
                )
        )
        map!!.setOnMarkerClickListener(object : GoogleMap.OnMarkerClickListener {
            override fun onMarkerClick(marker: Marker): Boolean {
                val lat = lastLocation.latitude
                val long = lastLocation.longitude
                //requestNewLocationData()

                if (calculateDistanceBetweenPoints(LatLng(lat, long), marker.position) <= 20) {
                    for (i in 0 until markers.size-1) {
                        if (markers.get(i).position == marker.position) {
                            lyricsAndSong.add(markers.get(i).tag.toString())
                            markers.removeAt(i)
                        }
                    }
                    try {
                        marker.remove()
                    } catch (e: java.lang.Exception) {
                    }

                    val myToast = Toast.makeText(applicationContext, getString(R.string.lyrcol), Toast.LENGTH_SHORT)
                    myToast.show()
                } else {
                    val myToast = Toast.makeText(applicationContext, getString(R.string.walkcloser), Toast.LENGTH_SHORT)
                    myToast.show()
                }

                return false
            }
        })
        map.getUiSettings().setMyLocationButtonEnabled(false)
        map.getUiSettings().setZoomControlsEnabled(true)
        map.setOnMapClickListener {
            var locationProviders = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            if (locationProviders == null || locationProviders.equals("")) {
                val builder = AlertDialog.Builder(this)
                builder.setTitle(getString(R.string.gpstitle))
                val message = builder.setMessage(getString(R.string.gpsmessage))
                val dialog: AlertDialog = builder.create()
                dialog.show()
            }
        }
        setUpMap()
        placeMarkersRandomly()
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    private fun setUpMap() {

        if (ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)

        }
        // 1
        map.isMyLocationEnabled = true

        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            // Got last known location. In some rare situations this can be null.
            // 3

            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 18f))
            }
            requestNewLocationData()
        }
        val openProfile = findViewById<View>(R.id.someimage) as ImageButton
        openProfile.setOnClickListener {
            val intentProfile = Intent(this, ProfileNewActivity::class.java)
            if (intent != null) {
                intentProfile.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            }
            intentProfile.putExtra(getString(R.string.lyrandsongs), lyricsAndSong)
            //yricsAndSong.clear()
            startActivity(intentProfile)
        }
        val switchBetweenModes = findViewById<Switch>(R.id.switch1)
        switchBetweenModes.setOnClickListener {
            switchMod = !switchMod
            map.clear()
            markers.clear()
            placeMarkersRandomly()
        }

    }

    private fun placeMarkersRandomly() {
        val markerNumber = 20
        for (i in 0 until markerNumber) {
            val lat = minlatitude + Math.random() * (maxlatitude - minlatitude)
            val long = minlongtitude + Math.random() * (maxlongtitude - minlongtitude)
            val point = LatLng(lat, long)
            val file = readFile()

            val markerOpt = MarkerOptions()
                    .position(point)

            val singleMarker = map.addMarker(markerOpt)
            val tag = file[1] + " / " + file[0]
            singleMarker.tag = tag
            markers.add(singleMarker)
        }
        val lat = minlatitude + Math.random() * (maxlatitude - minlatitude)
        val long = minlongtitude + Math.random() * (maxlongtitude - minlongtitude)
        val point = LatLng(lat, long)
        val file = readFile()

        val markerOpt = MarkerOptions()
                .position(point)

        val singleMarker = map.addMarker(markerOpt)
        val tag = file[1] + " / " + file[2]
        singleMarker.tag = tag
        singleMarker.setIcon((BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)))
        markers.add(singleMarker)

    }

    private fun readFile(): ArrayList<String> {
        val mode: String
        if (switchMod) {
            mode = getString(R.string.current)
        } else {
            mode = getString(R.string.classic)
        }
        val lyrics = assets.list(mode)
        val someVar = (lyrics.indices).random()
        val reader =
                BufferedReader(InputStreamReader(assets.open(mode + "/" + lyrics[someVar])))

        val arrayToFillWithLines = arrayListOf<String>()
        for (line in reader.lines()) {
            arrayToFillWithLines.add(line)
        }
// TODO CHANGDE VAR NAMES
        val randomInitializer = Random()
        val randomInInitializer = randomInitializer.nextInt(arrayToFillWithLines.size)
        val randomLine = arrayToFillWithLines[randomInInitializer]
        var randomLine2 = ""
        for (rand in randomInInitializer until arrayToFillWithLines.size) {
            randomLine2 = randomLine2 + "\n" + arrayToFillWithLines[rand]
        }
        val filename = lyrics[someVar]
        val results = arrayListOf<String>()
        results.add(randomLine)
        results.add(filename)
        results.add(randomLine2)
        return results

    }

    private fun requestNewLocationData() {
        var myLocationRequest = LocationRequest()
        myLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        myLocationRequest.interval = 2000
        myLocationRequest.fastestInterval = 1000
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.requestLocationUpdates(
                myLocationRequest, mLocationCallback,
                Looper.myLooper()
        )

    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location = locationResult.lastLocation
            lastLocation = locationResult.lastLocation
            val lat = mLastLocation.latitude
            val long = mLastLocation.longitude
            val lastLoc = LatLng(lat, long)

            // check if there are any lyrics to collect within 20 meters radius. if so, then change the colour of a marker
            var markersWithoutGreen = markers
            for (marker in markersWithoutGreen) {
                if (calculateDistanceBetweenPoints(lastLoc, marker.position) <= 20) {
                    try {
                        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                    } catch (e: Exception) {
                    }
                } else if (markersWithoutGreen[markersWithoutGreen.size - 1].tag != marker.tag) {
                    try {
                        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                    } catch (e: Exception) {
                    }
                }
            }
        }
    }

    private fun calculateDistanceBetweenPoints(StartPoint: LatLng, EndPoint: LatLng): Double {
        val startlat = StartPoint.latitude
        val endlat = EndPoint.latitude
        val startlon = StartPoint.longitude
        val endlon = EndPoint.longitude
        val dLat = Math.toRadians(endlat - startlat)
        val dLon = Math.toRadians(endlon - startlon)
        val a = (sin(dLat / 2) * sin(dLat / 2) + (cos(Math.toRadians(startlat))
                * cos(Math.toRadians(endlat))
                * sin(dLon / 2) * sin(dLon / 2)))
        val c = 2 * asin(sqrt(a))
        return earthRadius * c
    }

    override fun onResume() {
        super.onResume()
        var locationProviders = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (locationProviders == null || locationProviders.equals("")) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(getString(R.string.gpstitle))
            val message = builder.setMessage(getString(R.string.gpsmessage))
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }
    }


}


