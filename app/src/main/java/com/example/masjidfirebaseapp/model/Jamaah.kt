package com.example.masjidfirebaseapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Jamaah(
    var id: String? = null,
    var nama: String? = null,
    var alamat: String? = null,
    var tglInput: String? = null,
) : Parcelable
