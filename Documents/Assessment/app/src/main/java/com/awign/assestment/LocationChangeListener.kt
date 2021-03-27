package com.awign.assestment

import android.location.Location

interface LocationChangeListener {

    fun onLocationChange(location: Location?)

    fun showProgressBar(boolean: Boolean)
}