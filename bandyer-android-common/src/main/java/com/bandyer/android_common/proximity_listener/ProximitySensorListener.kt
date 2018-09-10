package com.bandyer.android_common.proximity_listener

/**
 * Class which represents proximity sensor receiver listener
 */
interface ProximitySensorListener {

    /**
     * Called when proximity sensor receiver has been triggered with a new proximity change.
     *
     * @param isNear if proximity sensor has been triggered with near state
     */
    fun onProximitySensorChanged(isNear: Boolean)

}