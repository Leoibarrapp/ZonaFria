package com.example.ZonaFria;

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import org.json.JSONObject

interface WebSocketEventListener {
    fun onConnectionFailed(reason: String)
}

class WebSocketManager(private val eventListener: WebSocketEventListener): WebSocketListener() {

    private lateinit var webSocket: WebSocket
    //agregar esta IP a res/xml/network_security_config.xml
    private val serverURL = "ws://172.16.0.3:8080"

    fun connect() {
        val client = OkHttpClient()

        val request = Request.Builder()
            .url(serverURL) // Cambia a la URL de tu servidor
            .build()

        webSocket = client.newWebSocket(request, this)
    }

    override fun onOpen(webSocket: WebSocket, response: okhttp3.Response) {
        println("Conexión establecida")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        println("Mensaje recibido: $text")
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        println("Mensaje binario recibido: ${bytes.hex()}")
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        println("Cerrando conexión: $reason")
        webSocket.close(1000, null)
        eventListener.onConnectionFailed(reason)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: okhttp3.Response?) {
        println("Error en la conexión: ${t.message}")
        eventListener.onConnectionFailed(t.message?: "Error Desconocido")
    }

    fun sendMessage(message: JSONObject) {
        webSocket.send(message.toString())
    }

    fun closeConnection() {
        webSocket.close(1000, "Conexión cerrada")
    }
}
