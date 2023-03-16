package mx.arquidiocesis.eamxcommonutils.multimedia

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import mx.arquidiocesis.eamxcommonutils.util.log
import java.util.*


class Maps(
    var map: MutableLiveData<GoogleMap>,
    var maker: MutableLiveData<Marker>,
    var isDirrecion: Boolean = false,
) :
    Fragment(), OnMapReadyCallback,
    GoogleMap.OnMarkerClickListener, GoogleApiClient.ConnectionCallbacks,
    GoogleMap.OnMyLocationButtonClickListener,
    GoogleApiClient.OnConnectionFailedListener {

    val addressMap = MutableLiveData<String>()
    private lateinit var client: MutableLiveData<GoogleApiClient>
    private lateinit var contextLocal: Context


    override fun onMapReady(googleMap: GoogleMap) {
        map.value = googleMap!!

        /*if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }*/

        map?.value?.isMyLocationEnabled = true
        map?.value?.setOnMyLocationButtonClickListener(this)
    }

    override fun onMarkerClick(p0: Marker): Boolean {
        maker.value = p0!!
        return true
    }

    override fun onConnected(p0: Bundle?) {
    }

    override fun onConnectionSuspended(p0: Int) {
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
    }

    @Synchronized
    fun buildGoogleApiClient(clientApi: MutableLiveData<GoogleApiClient>, context: Context) {
        //public fun buildGoogleApiClient(clientApi: MutableLiveData<GoogleApiClient>, context: Context) {
        this.contextLocal = context;
        client = clientApi
        client.value = GoogleApiClient.Builder(this.contextLocal)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API).build()
    }

    @Synchronized
    private fun moveMap(lat: Double, lng: Double, mover: Boolean = true) {
        if (isDirrecion) {
            location(lat, lng)
        }
        if (mover) {
            val zoom = 14f
            val centerMap = LatLng(lat, lng)
            map.value?.animateCamera(CameraUpdateFactory.newLatLngZoom(centerMap, zoom))
        }
    }

    private fun location(lat: Double, lng: Double) {
        val geocoder = Geocoder(contextLocal, Locale.getDefault())

        val addresses = geocoder.getFromLocation(
            lat,
            lng,
            1
        )

        val result = if (addresses?.size == 1) {
            addresses[0].getAddressLine(0).log()
            addresses[0].getAddressLine(0)
        } else {
            ""
        }
        ("Mi adrrees" + result).log()
        addressMap.value = result
    }

    fun addMaker(lat: Double, lng: Double, mover: Boolean, icon: BitmapDescriptor? = null) {
        map.value?.clear()
        val centerMark =
            LatLng(
                lat,
                lng
            )
        val markerOptions = MarkerOptions()
        icon?.let {
            markerOptions.icon(it)
        }
        moveMap(lat, lng, mover)

        markerOptions.position(centerMark).draggable(true)

        maker.value = map.value?.addMarker(markerOptions)
    }

    fun moveMarker(lat: Double, lng: Double) {
        moveMap(lat, lng)
        val centerMark =
            LatLng(
                lat,
                lng
            )
        maker.value?.position = centerMark
    }

    override fun onMyLocationButtonClick(): Boolean {
        // enableMyLocation()
        "Mapa: Entra btn".log()
        return false
    }
}