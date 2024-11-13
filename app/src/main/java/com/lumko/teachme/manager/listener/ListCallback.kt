package com.lumko.teachme.manager.listener

interface ListCallback<T> {
    fun onMapReceived(items: List<T>)
}