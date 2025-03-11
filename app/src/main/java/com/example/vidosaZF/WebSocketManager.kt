package com.example.vidosaZF;

import okhttp3.*
import okio.ByteString
import org.json.JSONObject

class WebSocketManager {

    var isConnected: Boolean = false
    private val client = OkHttpClient()
    private var webSocket: WebSocket? = null

    fun connect(url: String) {
        val request = Request.Builder()
            .url(url)
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                println("Conexión abierta")
                isConnected = true
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                println("Mensaje recibido: $text")
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                println("Mensaje recibido (binario): ${bytes.hex()}")
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                println("Conexión cerrando: $code, razón: $reason")
                webSocket.close(1000, null)
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                println("Conexión cerrada: $code, razón: $reason")
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                println("Error en la conexión: ${t.message}")
                isConnected = false
            }
        })
    }

    fun sendMessage(message: JSONObject) {
        webSocket?.send(message.toString())
    }

    fun closeConnection() {
        webSocket?.close(1000, "Conexión cerrada")
    }
}
