package kr.kyungjoon.maps.models

import android.net.Uri
import com.google.android.gms.maps.model.LatLng

data class PlaceInfo(
        var name: String = "",
        var address: String = "",
        var phoneNumber: String = "",
        var id: String = "",
        var websiteUri: Uri? = null,
        var latlng: LatLng? = null,
        var rating: Float = 0.toFloat(),
        var attributions: String = ""
)