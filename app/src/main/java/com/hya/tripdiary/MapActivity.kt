package com.hya.tripdiary

import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.hya.tripdiary.database.AppDatabase
import com.hya.tripdiary.database.Map
import kotlinx.android.synthetic.main.activity_map.*
import org.jetbrains.anko.toast
import java.lang.Exception
import java.util.*

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    lateinit var gMap: GoogleMap
    lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var mapDb: AppDatabase

    private var appDatabase: AppDatabase? = null
    private var selectLocation: LatLng? = null
    private lateinit var placesAutoCompleteFragment: AutocompleteSupportFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        initializePlacesAPI()
        initializingPlacesAutoCompleteFragment()

        (map_fragment as SupportMapFragment).getMapAsync(this)

        appDatabase = AppDatabase.getInstance(this)

        val addRunnable = Runnable {
            val newMap = Map(
                id = null,
                latitude = selectLocation!!.latitude,
                longitude = selectLocation!!.longitude
            )
            appDatabase?.mapDao()?.insert(newMap)
        }

        saveBtn.setOnClickListener {
            if (selectLocation == null) {
                Toast.makeText(this, "Please selected location", Toast.LENGTH_LONG).show()
            } else {
                val addThread = Thread(addRunnable)
                addThread.start()
                Log.e("latitude", "${selectLocation!!.latitude}")
                Log.e("longitude", "${selectLocation!!.longitude}")
                Toast.makeText(this, "Saved location", Toast.LENGTH_LONG).show()

                gMap.clear()
                val middle = LatLng(31.1, 35.0)
                gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(middle, 0f))

                mapDb = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "tripdiary-db").allowMainThreadQueries().build()
                val maplist = mapDb.mapDao().getAll()
                for (i in 0..maplist.size - 1) {
                    gMap.addMarker(MarkerOptions().position(LatLng(maplist[i].latitude!!, maplist[i].longitude)).icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(resources, R.mipmap.ic_user_location))))
                }
            }
        }

        viewAllBtn.setOnClickListener {
            gMap.clear()
            val middle = LatLng(31.1, 35.0)
            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(middle, 0f))

            mapDb = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "tripdiary-db").allowMainThreadQueries().build()
            val maplist = mapDb.mapDao().getAll()
            for (i in 0..maplist.size - 1) {
                gMap.addMarker(MarkerOptions().position(LatLng(maplist[i].latitude!!, maplist[i].longitude)).icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(resources, R.mipmap.ic_user_location))))
            }
        }

        deleteRecently.setOnClickListener {
            gMap.clear()
            val middle = LatLng(31.1, 35.0)
            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(middle, 0f))

            mapDb = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "tripdiary-db").allowMainThreadQueries().build()
            val id = mapDb.mapDao().getMaxId()
            mapDb.mapDao().deleteRecently(id)
            val maplist = mapDb.mapDao().getAll()
            for (i in 0..maplist.size - 1) {
                gMap.addMarker(MarkerOptions().position(LatLng(maplist[i].latitude!!, maplist[i].longitude)).icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(resources, R.mipmap.ic_user_location))))
            }
            Toast.makeText(this, "Delete Recently Location", Toast.LENGTH_LONG).show()
        }
    }

    override fun onMapReady(p0: GoogleMap?) {
        gMap = p0?: return
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            gMap.isMyLocationEnabled = true
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val middle = LatLng(31.1, 35.0)
                    var latLong = LatLng(location.latitude, location.longitude)
                    with(gMap) {
                        animateCamera(CameraUpdateFactory.newLatLngZoom(middle, 0f))
                    }
                }
            }
        } else {
            var permission = mutableListOf<String>()
            permission.add(android.Manifest.permission.ACCESS_FINE_LOCATION)
            permission.add(android.Manifest.permission.ACCESS_COARSE_LOCATION)

            ActivityCompat.requestPermissions(this, permission.toTypedArray(), 1)
        }

        mapDb = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "tripdiary-db").allowMainThreadQueries().build()
        try {
            val maplist = mapDb.mapDao().getAll()
            for (i in 0..maplist.size - 1) {
                Log.e("Qwer", maplist[i].longitude.toString())
                Log.e("Qwer", maplist[i].latitude.toString())
                gMap.addMarker(MarkerOptions().position(LatLng(maplist[i].latitude!!, maplist[i].longitude)).icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(resources, R.mipmap.ic_user_location))))
            }
        } catch (e : Exception) { }

        gMap.setOnMapClickListener { location: LatLng ->
            with(gMap) {
                clear()
                animateCamera(CameraUpdateFactory.newLatLngZoom(location, 10f))

                val geocoder = Geocoder(applicationContext, Locale.getDefault())
                var title = ""
                var city = ""

                try {
                    val addressList = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    city = addressList.get(0).locality
                    title = addressList.get(0).getAddressLine(0)
                } catch (e: Exception) {

                }

                val marker = addMarker(MarkerOptions().position(location).snippet(title).title(city))
                marker.showInfoWindow()

                selectLocation = location
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode == 1){
            if(grantResults.isNotEmpty() && (grantResults[0] == PackageManager.PERMISSION_DENIED||
                        grantResults[1] == PackageManager.PERMISSION_DENIED
                        )
            ) {
                toast("need permissions")
            }
        }
    }

    private fun initializePlacesAPI() {
        if ( !Places.isInitialized())
            Places.initialize(applicationContext, getString(R.string.google_maps_key))
        val placesClient: PlacesClient = Places.createClient(this)
    }

    private fun initializingPlacesAutoCompleteFragment() {
        placesAutoCompleteFragment = supportFragmentManager
            .findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment

        placesAutoCompleteFragment.setPlaceFields(
            arrayListOf(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.LAT_LNG
            )
        )

        placesAutoCompleteFragment.setOnPlaceSelectedListener(createPlacesSelectionListener())
    }

    private fun createPlacesSelectionListener(): PlaceSelectionListener {
        return object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                handlePlacesSelection(place)
            }

            override fun onError(status: Status) {
                handleErrorOnPlacesSelection(status)
            }
        }
    }

    private fun handlePlacesSelection(place: Place) {
        val latLng = place.latLng
        try {
            animateCamera(latLng!!)
            addMarkerToMapAndClear(latLng, place)
        } catch (exception: KotlinNullPointerException) {
            Log.d("Map", "latlng was null")
        }
    }

    private fun handleErrorOnPlacesSelection(status: Status) {
        Log.d("Map", "${status.statusMessage}")
    }

    private fun addMarkerToMapAndClear(latLng: LatLng, place: Place) {
        gMap.clear()
        gMap.addMarker(MarkerOptions().position(latLng).title(place.name))
        selectLocation = latLng
    }

    private fun animateCamera(latLng: LatLng) {
        gMap.animateCamera(createCameraPosition(latLng))
    }

    private fun createCameraPosition(latLng: LatLng): CameraUpdate {
        return CameraUpdateFactory.newCameraPosition(
                CameraPosition.Builder().target(latLng).zoom(
                    10.0f
                ).build()
        )
    }

    override fun onDestroy() {
        AppDatabase.destroyInstance()
        super.onDestroy()
    }
}