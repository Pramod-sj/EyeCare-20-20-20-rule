package com.pramod.eyecare.framework.ui.utils

import java.util.concurrent.ConcurrentLinkedDeque

abstract class MyObserver<T> {

    val listeners = ConcurrentLinkedDeque<T>()

    fun registerObserver(listener: T) {
        listeners.add(listener)
    }

    fun unregisterObserver(listener: T) {
        listeners.remove(listener)
    }

    fun clear() {
        listeners.clear()
    }

}