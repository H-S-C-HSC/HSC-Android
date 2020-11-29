package com.example.hsc_android.network.Data
data class voiceResponse(
    val voices : ArrayList<voice>
)
data class voice (
    var id: Int,
    var title: String
)


