package com.dev.ai.app.chronos.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

object NetworkUtils {
    fun observeNetworkStatus(context: Context): Flow<Boolean> = callbackFlow {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                val capabilities = connectivityManager.getNetworkCapabilities(network)
                val isConnected = capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true &&
                        capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                trySend(isConnected)
            }

            override fun onLost(network: Network) {
                trySend(false)
            }

            override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
                val isConnected = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                        networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                trySend(isConnected)
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(request, callback)


        val initialCapabilities = connectivityManager.activeNetwork?.let { connectivityManager.getNetworkCapabilities(it) }
        val initialIsConnected = initialCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true &&
                initialCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        trySend(initialIsConnected)

        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }
}