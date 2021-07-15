package com.example.assignmentthree

import android.graphics.Bitmap

data class LocationsPic(val latitude: Double? = null, val longitude: Double? = null, val pic: Bitmap? = null) {
    // Null default values create a no-argument default constructor, which is needed
    // for deserialization from a DataSnapshot.
}