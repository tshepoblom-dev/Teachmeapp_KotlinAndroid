package com.lumko.teachme.manager.listener

interface MapCallback<T, U> {
    fun onMapReceived(map: Map<T, U>)
}