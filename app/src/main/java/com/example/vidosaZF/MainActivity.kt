package com.example.vidosaZF

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import com.android.volley.toolbox.Volley
import java.util.Calendar
import java.util.Locale

import com.android.volley.Request
import okhttp3.OkHttpClient
import org.json.JSONException

class MainActivity : AppCompatActivity() {

    private lateinit var webSocket: WebSocketManager
    private val socketURL = "ws://172.16.0.213:8080"

    //mapa con los codigos y nombres de los defectos
//    private val mapaDefectos = Defectos.map

    private var turno: Int = 0
    private var horaActual: Int = 0

    private lateinit var camposObligatorios: List<View>

    //lista de filas que contiene a la lista de celdas
    private lateinit var celdasTabla1: List<List<EditText>>
    private lateinit var celdasTabla2: List<List<EditText>>

    private lateinit var horasTVs: List<List<TextView>>
//    private lateinit var horas1TVs: List<TextView>
//    private lateinit var horas2TVs: List<TextView>

    private lateinit var udsHoraET: EditText
    private lateinit var fechaTV: TextView

    private lateinit var udsTotalesHoraTVs: List<TextView>
    private lateinit var udsTotalesTurnoTV: TextView
    private lateinit var eficienciaHoraTVs: List<TextView>
    private lateinit var eficienciaTotalTurnoTV: TextView

    private lateinit var btnGuardar: Button

    private val serverUrl = "http://192.168.68.64:8080"
    private lateinit var queue: com.android.volley.RequestQueue

    // Handler y Runnable para actualizar el turno cada minuto
    private val handler = Handler(Looper.getMainLooper())
    private val runnableActualizarTurno = object : Runnable {
        override fun run() {
            actualizarTurno()
            // Programa la siguiente ejecución del Runnable en 1 minuto (60 * 1000 milisegundos -> 60 segundos)
            handler.postDelayed(
                this,
                60 * 1000
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webSocket = WebSocketManager()
        webSocket.connect(socketURL)

//        if(!webSocket.isConnected) {
//            mostrarMensaje(
//                mensaje = "No se pudo conectar al servidor. Por favor, revise la conexión e intente nuevamente. ",
//                botonCancelar = "Cerrar aplicación" to { finishAffinity() }
//            )
//        }

        fechaTV = findViewById(R.id.tv_fecha)

        udsHoraET = findViewById(R.id.et_uds_hora)
        udsHoraET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val filas = listOf('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H')
                filas.forEach { fila -> calcularEficienciaHora(fila) }
            }
        })

        udsTotalesTurnoTV = findViewById(R.id.tv_uds_totales_turno)
        eficienciaTotalTurnoTV = findViewById(R.id.tv_eficiencia_total_turno)

        btnGuardar = findViewById(R.id.btn_submit)

        // TextViews de las horas
        horasTVs = listOf(
            listOf(R.id.tv_hora1_tabla1, R.id.tv_hora2_tabla1, R.id.tv_hora3_tabla1, R.id.tv_hora4_tabla1, R.id.tv_hora5_tabla1, R.id.tv_hora6_tabla1, R.id.tv_hora7_tabla1, R.id.tv_hora8_tabla1),
            listOf(R.id.tv_hora1_tabla2, R.id.tv_hora2_tabla2, R.id.tv_hora3_tabla2, R.id.tv_hora4_tabla2, R.id.tv_hora5_tabla2, R.id.tv_hora6_tabla2, R.id.tv_hora7_tabla2, R.id.tv_hora8_tabla2)
        ).map { tabla -> tabla.map { id -> findViewById(id) } }

        // Lista de filas, que a su vez tienen la lista de celdas
        celdasTabla1 = listOf(
            listOf(R.id.A1_Hrs,R.id.A2_Hrs,R.id.A3_Hrs,R.id.A4_Hrs,R.id.A5_Hrs,R.id.A6_Hrs,R.id.A7_Hrs,R.id.A8_Hrs,R.id.A9_Def1,R.id.A10_Def2,R.id.A11_Def3,R.id.A12_Def4,R.id.A13_Def5,R.id.A14_Def6),
            listOf(R.id.B1_Hrs,R.id.B2_Hrs,R.id.B3_Hrs,R.id.B4_Hrs,R.id.B5_Hrs,R.id.B6_Hrs,R.id.B7_Hrs,R.id.B8_Hrs,R.id.B9_Def1,R.id.B10_Def2,R.id.B11_Def3,R.id.B12_Def4,R.id.B13_Def5,R.id.B14_Def6),
            listOf(R.id.C1_Hrs,R.id.C2_Hrs,R.id.C3_Hrs,R.id.C4_Hrs,R.id.C5_Hrs,R.id.C6_Hrs,R.id.C7_Hrs,R.id.C8_Hrs,R.id.C9_Def1,R.id.C10_Def2,R.id.C11_Def3,R.id.C12_Def4,R.id.C13_Def5,R.id.C14_Def6),
            listOf(R.id.D1_Hrs,R.id.D2_Hrs,R.id.D3_Hrs,R.id.D4_Hrs,R.id.D5_Hrs,R.id.D6_Hrs,R.id.D7_Hrs,R.id.D8_Hrs,R.id.D9_Def1,R.id.D10_Def2,R.id.D11_Def3,R.id.D12_Def4,R.id.D13_Def5,R.id.D14_Def6),
            listOf(R.id.E1_Hrs,R.id.E2_Hrs,R.id.E3_Hrs,R.id.E4_Hrs,R.id.E5_Hrs,R.id.E6_Hrs,R.id.E7_Hrs,R.id.E8_Hrs,R.id.E9_Def1,R.id.E10_Def2,R.id.E11_Def3,R.id.E12_Def4,R.id.E13_Def5,R.id.E14_Def6),
            listOf(R.id.F1_Hrs,R.id.F2_Hrs,R.id.F3_Hrs,R.id.F4_Hrs,R.id.F5_Hrs,R.id.F6_Hrs,R.id.F7_Hrs,R.id.F8_Hrs,R.id.F9_Def1,R.id.F10_Def2,R.id.F11_Def3,R.id.F12_Def4,R.id.F13_Def5,R.id.F14_Def6),
            listOf(R.id.G1_Hrs,R.id.G2_Hrs,R.id.G3_Hrs,R.id.G4_Hrs,R.id.G5_Hrs,R.id.G6_Hrs,R.id.G7_Hrs,R.id.G8_Hrs,R.id.G9_Def1,R.id.G10_Def2,R.id.G11_Def3,R.id.G12_Def4,R.id.G13_Def5,R.id.G14_Def6),
            listOf(R.id.H1_Hrs,R.id.H2_Hrs,R.id.H3_Hrs,R.id.H4_Hrs,R.id.H5_Hrs,R.id.H6_Hrs,R.id.H7_Hrs,R.id.H8_Hrs,R.id.H9_Def1,R.id.H10_Def2,R.id.H11_Def3,R.id.H12_Def4,R.id.H13_Def5,R.id.H14_Def6)
        ).map { fila ->
            fila.map { id ->
                val celda = findViewById<EditText>(id)
                celda.tag = resources.getResourceEntryName(id)
                observarCambiosCeldas(celda)
                    celda // Return the EditText instance
            }
        }

        celdasTabla2 = listOf(
            listOf(R.id.A1_ICD, R.id.A2_ICD, R.id.A3_ICD), listOf(R.id.B1_ICD, R.id.B2_ICD, R.id.B3_ICD), listOf(R.id.C1_ICD, R.id.C2_ICD, R.id.C3_ICD), listOf(R.id.D1_ICD, R.id.D2_ICD, R.id.D3_ICD),
            listOf(R.id.E1_ICD, R.id.E2_ICD, R.id.E3_ICD), listOf(R.id.F1_ICD, R.id.F2_ICD, R.id.F3_ICD), listOf(R.id.G1_ICD, R.id.G2_ICD, R.id.G3_ICD), listOf(R.id.H1_ICD, R.id.H2_ICD, R.id.H3_ICD)
        ).map { fila ->
            fila.map { id -> findViewById(id) }
        }

        // Lista de las unidades totales de cada hora
        udsTotalesHoraTVs = listOf(R.id.tv_uds_A, R.id.tv_uds_B, R.id.tv_uds_C, R.id.tv_uds_D, R.id.tv_uds_E, R.id.tv_uds_F, R.id.tv_uds_G, R.id.tv_uds_H)
            .map { id ->
                val udsTotalesHora = findViewById<TextView>(id)
                udsTotalesHora.tag = resources.getResourceEntryName(id)
                observarCambiosUdsTotalesHora(udsTotalesHora)
                    udsTotalesHora
            }

        // Lista de las eficiencias de cada hora
        eficienciaHoraTVs = listOf(R.id.tv_eficiencia_A, R.id.tv_eficiencia_B, R.id.tv_eficiencia_C, R.id.tv_eficiencia_D, R.id.tv_eficiencia_E, R.id.tv_eficiencia_F, R.id.tv_eficiencia_G, R.id.tv_eficiencia_H)
            .map { id ->
                val eficienciaHora = findViewById<TextView>(id)
                eficienciaHora.tag = resources.getResourceEntryName(id)
                observarCambiosEficienciaHora(eficienciaHora)
                    eficienciaHora
            }

        camposObligatorios = listOf(R.id.et_grupo, R.id.et_linea, R.id.et_molde, R.id.et_velocidad, R.id.et_tiempo_de_archa, R.id.et_obj_de_linea, R.id.et_uds_hora, R.id.et_uds_turno, R.id.et_firma)
            .map { id -> findViewById<View>(id) } + udsTotalesHoraTVs + udsTotalesTurnoTV + eficienciaHoraTVs + eficienciaTotalTurnoTV

        btnGuardar.setOnClickListener{
            if (verificarCamposObligatorios()) {
                enviarDatosTablaUsuario()
            }
            else {
                mostrarMensaje("Por favor, complete todos los campos obligatorios.")
            }
        }

        queue = Volley.newRequestQueue(this) // Initialize the request queue here
        handler.post(runnableActualizarTurno)
    }

    override fun onDestroy() {
        super.onDestroy()
        webSocket.closeConnection()
    }




    fun mostrarMensaje(mensaje: String) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(mensaje)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }

        val alertDialog = builder.create()
        alertDialog.show()

        val color = ContextCompat.getColor(this, R.color.primary)
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(color)
    }


    fun mostrarMensaje(
        mensaje: String,
        botonAceptar: Pair<String, () -> Unit>? = null,
        botonCancelar: Pair<String, () -> Unit>? = null
    ) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(mensaje)

        if(botonAceptar != null) {
            builder.setPositiveButton(botonAceptar.first) { dialog, _ ->
                botonAceptar.second() // Ejecuta la función asociada
                dialog.dismiss()
            }
        }

        if(botonCancelar != null) {
            builder.setNegativeButton(botonCancelar.first) { dialog, _ ->
                botonCancelar.second() // Ejecuta la función asociada
                dialog.dismiss()
            }
        }

        val alertDialog = builder.create()
        alertDialog.show()
        if(botonCancelar != null){
            alertDialog.setCancelable(false)
        }

        val colorAceptar = ContextCompat.getColor(this, R.color.primary)
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(colorAceptar)
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(Color.RED)
    }



    // Obtiene la hora actual
    fun obtenerHora(): Int {
        val hora = Calendar.getInstance()[Calendar.HOUR_OF_DAY]
        horaActual = hora
        // Muestra la hora actual en el campo de texto et_fecha
        return hora
    }

    // Determina el turno basado en la hora actual
    fun decidirTurno(): Int {
        val hora = obtenerHora()

        return when (hora) {
            in 7..14 -> 1
            in 15..22 -> 2
            23, in 0..6 -> 3
            else -> throw IllegalArgumentException("Hora inválida: $hora")
        }
    }

    // Actualiza los turnos y los TextViews correspondientes al cambiar de hora
    fun actualizarTurno() {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val currentDate = dateFormat.format(Calendar.getInstance().time) // Usamos .time para obtener la fecha completa
        fechaTV.text = currentDate

        val nuevoTurno = decidirTurno()

        //verifica si el turno debe cambiar o no
        if(turno != nuevoTurno){
            turno = nuevoTurno

            val horasTurno = when (turno) {
                1 -> listOf("7:00 am", "8:00 am", "9:00 am", "10:00 am", "11:00 am", "12:00 pm", "1:00 pm", "2:00 pm")
                2 -> listOf("3:00 pm", "4:00 pm", "5:00 pm", "6:00 pm", "7:00 pm", "8:00 pm", "9:00 pm", "10:00 pm")
                3 -> listOf("11:00 pm", "12:00 am", "1:00 am", "2:00 am", "3:00 am", "4:00 am", "5:00 am", "6:00 am")
                else -> throw IllegalArgumentException("Turno inválido: $turno")
            }

            // Actualiza los TextViews con las horas correspondientes al turno
            for(i in 0..7) {
                horasTVs[0][i].text = horasTurno[i]
                horasTVs[1][i].text = horasTurno[i]
            }

//            restablecerTablas()

//            mostrarMensaje("Nuevo Turno")
        }
    }

    fun restablecerTablas() {
        celdasTabla1.forEach { fila ->
            fila.forEach { celda ->
                celda.text.clear()
            }
        }

        celdasTabla2.forEach { fila ->
            fila.forEach { celda ->
                celda.text.clear()
            }
        }

        camposObligatorios.forEach { view ->
            if (view is EditText) {
                view.text.clear()
            }
            else if (view is TextView) {
                view.text = ""
            }
        }

        findViewById<EditText>(R.id.et_observaciones).text.clear()
    }







    //Le aplica el listener de texto a las celdas para actualizar los totales de cada hora
    fun observarCambiosCeldas(celda: EditText) {
        //si es defecto enviar mensajes al servidor
        if(celda.tag.toString().contains("Def")) {
            celda.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    webSocket.sendMessage(JSONObject().apply {
                        put("type", "defect")
                        put("hour", horaActual)
                        put("tag", celda.tag.toString())
                        put("data", celda.text.toString())
                    })
                }
            })

        }
        //si no es defecto realizar calculos
        else {
            celda.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val fila: Char = celda.tag.toString().first()
                    calcularUdsTotalesHora(fila)
                }
                override fun afterTextChanged(s: Editable?) {}
            })
        }
    }

    fun observarCambiosUdsTotalesHora(tv: TextView) {
        tv.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val fila = tv.tag.toString().last()
                calcularEficienciaHora(fila)
                calcularUdsTotalesTurno()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    fun observarCambiosEficienciaHora(tv: TextView) {
        tv.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                calcularEficienciaTotalTurno()
            }

            //enviar mensaje al servidor
            override fun afterTextChanged(s: Editable?) {
                webSocket.sendMessage(JSONObject().apply {
                    put("type", "efficiency")
                    put("hour", horaActual)
                    put("tag", tv.tag.toString())
                    put("data", tv.text.toString())
                })
            }
        })
    }

    fun calcularUdsTotalesHora(fila: Char) {
        var suma = 0
        val posicionFila = obtenerPosicionDeFila(fila)

        celdasTabla1[posicionFila].forEach { celdaET ->
            suma += celdaET.text.toString().toIntOrNull() ?: 0
        }

        if(suma != 0) {
            udsTotalesHoraTVs[posicionFila].text = suma.toString()
            calcularEficienciaHora(fila)
        }
        else {
            udsTotalesHoraTVs[posicionFila].text = ""
        }
    }

    fun calcularUdsTotalesTurno() {
        var suma = 0

        udsTotalesHoraTVs.forEach { tv ->
            val text = tv.text.toString()
            if (text.isNotEmpty()) {
                suma += text.toInt()
            }
        }

        if (suma != 0) {
            udsTotalesTurnoTV.text = suma.toString()
        } else {
            udsTotalesTurnoTV.text = ""
        }
    }

    //Calcula la eficiencia de cada hora en base a las Uds/Hora y el total de Unidades de esa hora específica
    fun calcularEficienciaHora(fila: Char) {
        val eficiencia: Double
        val posicionFila = obtenerPosicionDeFila(fila)

        val udsHoraGeneral = udsHoraET.text.toString().toDoubleOrNull()
        val udsTotalesHora = udsTotalesHoraTVs[posicionFila].text.toString().toDoubleOrNull()

        val eficienciaTV = eficienciaHoraTVs[posicionFila]

        if (udsHoraGeneral != null && udsTotalesHora != null) {
            eficiencia = (udsTotalesHora.times(100)) / udsHoraGeneral

            when (eficiencia) {
                in 0.0..50.0 -> eficienciaTV.setBackgroundResource(R.drawable.red_cell_shape)
                in 50.0..80.0 -> eficienciaTV.setBackgroundResource(R.drawable.yellow_cell_shape)
                in 80.0..100.0 -> eficienciaTV.setBackgroundResource(R.drawable.green_cell_shape)
                else -> eficienciaTV.setBackgroundColor(Color.GRAY)
            }

            eficienciaTV.text = String.format(Locale.getDefault(),"%.2f", eficiencia)
        }
        else {
            eficienciaTV.text = ""
            eficienciaTV.setBackgroundResource(R.drawable.header_cell_shape)
        }
    }

    fun calcularEficienciaTotalTurno() {
        var suma = 0.0
        var contadorValidos = 0

        eficienciaHoraTVs.forEach { tv ->
            val text = tv.text.toString()

            if(text.isNotEmpty()) {
                contadorValidos++
                suma += text.toDouble()
            }
        }

        if (contadorValidos > 0) {
            val promedio = suma / contadorValidos
            eficienciaTotalTurnoTV.text = String.format(Locale.getDefault(),"%.2f%%", promedio)
        }
        else {
            eficienciaTotalTurnoTV.text = ""
        }
    }

    fun obtenerPosicionDeFila(charFila: Char): Int {
        return when(charFila) {
            'A' -> 0
            'B' -> 1
            'C' -> 2
            'D' -> 3
            'E' -> 4
            'F' -> 5
            'G' -> 6
            'H' -> 7
            else -> throw IllegalArgumentException("Fila inválida: $charFila")
        }
    }

    fun verificarCamposObligatorios(): Boolean {
        camposObligatorios.forEach { view ->
            if (view is EditText && view.text.toString().isEmpty()) {
                return false
            }
            else if (view is TextView && view.text.toString().isEmpty()) {
                return false
            }
        }
        return true
    }

    fun enviarDatosTablaUsuario() {
        val casillasTabla1 = celdasTabla1.map { fila ->
            fila.map { et ->
                val text = et.text.toString()
                if (text.isNotEmpty() && text.matches(Regex("\\d+"))) {
                    text.toInt()
                } else {
                    0 // O un valor por defecto que prefieras
                }
            }}
        val udsPorHora = udsTotalesHoraTVs.map { tv ->
            val text = tv.text.toString()
            if (text.isNotEmpty() && text.matches(Regex("\\d+"))) {
                text.toInt()
            } else {
                0 // O un valor por defecto que prefieras
            }
        }

        try {
            val jsonObject = JSONObject()
            jsonObject.put("celdas", JSONArray(casillasTabla1))
            jsonObject.put("udsPorHora", JSONArray(udsPorHora))
            val url = "${serverUrl}/enviar_datos_usuario" // URL para la solicitud POST
            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.POST, url, jsonObject,
                { response ->
                    // Manejar la respuesta del servidor
                    mostrarMensaje("Datos enviados correctamente")
                },
                { error ->
                    // Manejar el error
                    mostrarMensaje( "Error al enviar los datos: ${error.message}")
                })

//            queue.add(jsonObjectRequest)

        } catch (e: JSONException) {
            mostrarMensaje("Error al crear JSON: ${e.message}")
            e.printStackTrace()
        }
        // 3. Enviar los datos al servidor

    }
}
