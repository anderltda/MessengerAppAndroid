package br.com.anderltda.messengerApp.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import br.com.anderltda.messengerApp.R

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class LocationActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    private lateinit var root: ViewGroup

    val lat = ""
    val long = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        val toolbar = findViewById<Toolbar>(R.id.toolbar)

        val title = toolbar.findViewById(R.id.tv_title) as TextView

        title.text = resources.getString(R.string.title_location)

        val back = toolbar.findViewById(R.id.tv_back) as TextView
        back.visibility = View.VISIBLE
        back.setOnClickListener{
            finish()
        }

        mapFragment.getMapAsync(this)

        val it = intent
        val lat = it?.getDoubleExtra("lat", 0.0)
        val long = it?.getDoubleExtra("long", 0.0)
        val name = it.getStringExtra("name")

        val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener {
            mapFragment.getMapAsync { googleMap ->
                googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL

                val latLngOrigin = LatLng(-23.5565804, -46.662113)
                val latLngDestination = LatLng(lat!!, long!!)

                googleMap.addMarker(
                    MarkerOptions()
                        .position(latLngOrigin)
                        .title(name)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
                )

               googleMap.addMarker(
                    MarkerOptions()
                        .position(latLngDestination)
                        .title("Destination")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                )

                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngOrigin, 12f))
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val it = intent
        val lat = it?.getDoubleExtra("lat", 0.0)
        val long = it?.getDoubleExtra("long", 0.0)
        val name = it.getStringExtra("name")

        googleMap.addMarker(
            MarkerOptions()
                .position(LatLng(lat!!, long!!))
                .title(name)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
        )

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat, long), 10f))

    }
}
