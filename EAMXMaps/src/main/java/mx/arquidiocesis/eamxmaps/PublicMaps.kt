package mx.arquidiocesis.eamxmaps

import android.content.Context
import android.location.Address
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.LocationServices
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import mx.arquidiocesis.eamxcommonutils.util.log
import android.location.Geocoder
import java.util.*


class PublicMaps(
    var map: MutableLiveData<GoogleMap>,
    var maker: MutableLiveData<Marker>,
    var isDirrecion: Boolean = false
) :
    Fragment(), OnMapReadyCallback,
    GoogleMap.OnMarkerClickListener, GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener {
    private lateinit var contextLocal: Context
    val addressMap = MutableLiveData<String>()
    private lateinit var client: MutableLiveData<GoogleApiClient>

    override fun onMapReady(googleMap: GoogleMap?) {
        map.value = googleMap!!
    }

    override fun onMarkerClick(p0: Marker?): Boolean {
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
    public fun buildGoogleApiClient(clientApi: MutableLiveData<GoogleApiClient>, context: Context) {
        this.contextLocal = context;
        client = clientApi
        client.value = GoogleApiClient.Builder(this.contextLocal)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API).build()
    }

    @Synchronized
    private fun moveMap(lat: Double, lng: Double) {
        if (isDirrecion) {
            location(lat, lng)
        }
        val zoom = 14f
        val centerMap = LatLng(lat, lng)
        map.value?.animateCamera(CameraUpdateFactory.newLatLngZoom(centerMap, zoom))

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
        addressMap.value = result
    }

    fun addMaker(lat: Double, lng: Double, icon: BitmapDescriptor? = null) {
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
        moveMap(lat, lng)
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
}