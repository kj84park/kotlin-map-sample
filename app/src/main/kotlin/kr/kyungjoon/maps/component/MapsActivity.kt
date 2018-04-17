package kr.kyungjoon.maps.component

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.places.Places
import com.google.android.gms.location.places.ui.PlacePicker
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_maps.*
import kr.kyungjoon.maps.R
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnNeverAskAgain
import permissions.dispatcher.OnPermissionDenied
import permissions.dispatcher.RuntimePermissions

@RuntimePermissions
class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {

    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        private val TAG = this::class.java.name
        private val PLACE_PICKER_REQUEST = 1
    }

    private lateinit var googleApiClient: GoogleApiClient
    private lateinit var locationManager: LocationManager
    private lateinit var googleMapLocal: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_maps)
        setupToolbar()

        Log.d(TAG, "### setupMapWithPermissionCheck()")
        locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    override fun onResume() {
        super.onResume()
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            setupMapWithPermissionCheck()
        } else {
            AlertDialog.Builder(this)
                    .setMessage("GPS is disabled in your device. Enable it?")
                    .setCancelable(false)
                    .setPositiveButton("Enable GPS", { _: DialogInterface, _: Int -> startActivity(Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)) })
                    .setNegativeButton("Cancel", { dialogInterface: DialogInterface, _: Int -> dialogInterface.cancel() })
                    .create()
                    .show()
        }
    }

    public override fun onPause() {
        super.onPause()
        googleApiClient.stopAutoManage(this)
        googleApiClient.disconnect()
    }

    @OnPermissionDenied(Manifest.permission.ACCESS_FINE_LOCATION)
    fun showDeniedForLocaion() {
        Toast.makeText(this, "denied.", Toast.LENGTH_SHORT).show()
    }

    @OnNeverAskAgain(Manifest.permission.ACCESS_FINE_LOCATION)
    fun showNeverAskForLocaion() {
        Toast.makeText(this, "never ask again.", Toast.LENGTH_SHORT).show()
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {

        googleMapLocal = googleMap
        googleMapLocal.apply {
            fab_style_one.setOnClickListener {
                isMyLocationEnabled = !isMyLocationEnabled
            }

            fab_style_two.setOnClickListener {
                startActivityForResult(PlacePicker.IntentBuilder().build(this@MapsActivity), PLACE_PICKER_REQUEST)
            }

            fab_style_three.setOnClickListener {

            }

            fab_style_four.setOnClickListener {
            }
        }
    }

    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    fun setupMap() {
        googleApiClient = GoogleApiClient.Builder(this).addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build()

        val mapFragment = map as SupportMapFragment
        mapFragment.apply {
            getMapAsync(this@MapsActivity)
        }
    }

    private fun setupToolbar() {
        supportActionBar?.apply {
            title = " - ...."
            subtitle = "...."
        }
    }

    @SuppressLint("NeedOnRequestPermissionsResult")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                data?.let {
                    val place = PlacePicker.getPlace(this, it)
                    googleMapLocal.addMarker(MarkerOptions().position(place.latLng).title(place.name.toString()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)))
                    googleMapLocal.moveCamera(CameraUpdateFactory.newLatLngZoom(place.latLng, 11F))
                    //https://stackoverflow.com/questions/43102425/how-to-show-placepicker-location-on-a-google-map
                    //Toast.makeText(this, "Place : " + place.name, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
