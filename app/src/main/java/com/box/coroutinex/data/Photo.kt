package com.box.coroutinex.data

import com.box.coroutinex.data.Hits

data class Photo (
    val total : Float = 0f ,val totalHits : Float = 0f,val hits :List<Hits>
)