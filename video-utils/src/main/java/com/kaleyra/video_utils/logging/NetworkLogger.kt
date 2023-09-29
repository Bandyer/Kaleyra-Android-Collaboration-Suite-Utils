/*
 *  Copyright (C) 2022 Kaleyra S.p.a. All Rights Reserved.
 *  See LICENSE.txt for licensing information
 */
package com.kaleyra.video_utils.logging


/**
 * Log network activity
 *
 * @author kristiyan
 */
interface NetworkLogger {

    /**
     * Method called when the connection has been established
     * @param url String connection endpoint
     */
    fun onConnected(tag: String, url: String)

    /**
     * Method called when a message has been received from the remote
     * @param response String the message received
     */
    fun onMessageReceived(tag: String, response: String)

    /**
     * Method called when a message has been sent to the server
     * @param request String the message sent
     */
    fun onMessageSent(tag: String, request: String)

    /**
     * Method called when an error has occurred
     * @param reason String reason for the error
     */
    fun onError(tag: String, reason: String)

    /**
     * Method called when the client has been disconnected from the server
     */
    fun onDisconnected(tag: String)
}