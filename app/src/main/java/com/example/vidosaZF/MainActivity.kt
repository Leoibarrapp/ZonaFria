package com.example.vidosaZF

import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import com.android.volley.toolbox.Volley
import java.util.Calendar
import java.util.Locale

import com.android.volley.Request
import com.android.volley.Response
import org.json.JSONException


class MainActivity : AppCompatActivity() {

    // IDs de los TextViews para mostrar las horas en turno 1 y turno 2
    private lateinit var horas1TVs: List<TextView>
    private lateinit var horas2TVs: List<TextView>
    private lateinit var fechaTV: TextView
    private lateinit var titulo: TextView
    private lateinit var udsTotalesTurnoTV: TextView
    private lateinit var udsTotalesHoraETs: List<EditText>
    private lateinit var celdasTabla1: List<EditText>
    private lateinit var btnGuardar: Button
    private val serverUrl = "http://192.168.68.64:8080"
    private lateinit var queue: com.android.volley.RequestQueue



    private var turno: Int = 0

    // Handler y Runnable para actualizar el turno cada minuto
    private val handler = Handler()
    private val runnableCode = object : Runnable {
        override fun run() {
            actualizarTurno()
            // Programa la siguiente ejecución del Runnable en 1 minuto (60 * 1000 milisegundos -> 60 segundos)
            handler.postDelayed(
                this,
                60 * 1000
            )
        }
    }



    // Metodo onCreate que se ejecuta al crear la actividad
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnGuardar= findViewById(R.id.btn_submit)
        titulo = findViewById(R.id.tv_title)
        fechaTV = findViewById(R.id.tv_fecha)
        udsTotalesTurnoTV = findViewById(R.id.tv_uds_totales_turno)
        val list2= listOf(R.id.A1_Hrs, R.id.A2_Hrs, R.id.A3_Hrs, R.id.A4_Hrs, R.id.A5_Hrs, R.id.A6_Hrs, R.id.A7_Hrs, R.id.A8_Hrs,R.id.A9_Def1,R.id.A10_Def2,R.id.A11_Def3,R.id.A12_Def4,R.id.A13_Def5,R.id.A14_Def6,R.id.B1_Hrs,R.id.B2_Hrs,R.id.B3_Hrs,R.id.B4_Hrs,R.id.B5_Hrs,R.id.B6_Hrs,R.id.B7_Hrs,R.id.B8_Hrs,R.id.B9_Def1,R.id.B10_Def2,R.id.B11_Def3,R.id.B12_Def4,R.id.B13_Def5,R.id.B14_Def6,R.id.C1_Hrs,R.id.C2_Hrs,R.id.C3_Hrs,R.id.C4_Hrs,R.id.C5_Hrs,R.id.C6_Hrs,R.id.C7_Hrs,R.id.C8_Hrs,R.id.C9_Def1,R.id.C10_Def2,R.id.C11_Def3,R.id.C12_Def4,R.id.C13_Def5,R.id.C14_Def6,R.id.D1_Hrs,R.id.D2_Hrs,R.id.D3_Hrs,R.id.D4_Hrs,R.id.D5_Hrs,R.id.D6_Hrs,R.id.D7_Hrs,R.id.D8_Hrs,R.id.D9_Def1,R.id.D10_Def2,R.id.D11_Def3,R.id.D12_Def4,R.id.D13_Def5,R.id.D14_Def6,R.id.E1_Hrs,R.id.E2_Hrs,R.id.E3_Hrs,R.id.E4_Hrs,R.id.E5_Hrs,R.id.E6_Hrs,R.id.E7_Hrs,R.id.E8_Hrs,R.id.E9_Def1,R.id.E10_Def2,R.id.E11_Def3,R.id.E12_Def4,R.id.E13_Def5,R.id.E14_Def6,R.id.F1_Hrs,R.id.F2_Hrs,R.id.F3_Hrs,R.id.F4_Hrs,R.id.F5_Hrs,R.id.F6_Hrs,R.id.F7_Hrs,R.id.F8_Hrs,R.id.F9_Def1,R.id.F10_Def2,R.id.F11_Def3,R.id.F12_Def4,R.id.F13_Def5,R.id.F14_Def6,R.id.H1_Hrs,R.id.H2_Hrs,R.id.H3_Hrs,R.id.H4_Hrs,R.id.H5_Hrs,R.id.H6_Hrs,R.id.H7_Hrs,R.id.H8_Hrs,R.id.H9_Def1,R.id.H10_Def2,R.id.H11_Def3,R.id.H12_Def4,R.id.H13_Def5,R.id.H14_Def6,R.id.I1_Hrs,R.id.I2_Hrs,R.id.I3_Hrs,R.id.I4_Hrs,R.id.I5_Hrs,R.id.I6_Hrs,R.id.I7_Hrs,R.id.I8_Hrs,R.id.I9_Def1,R.id.I10_Def2,R.id.I11_Def3,R.id.I12_Def4,R.id.I13_Def5,R.id.I14_Def6)
        val list = listOf(R.id.et_uds_1, R.id.et_uds_2, R.id.et_uds_3, R.id.et_uds_4, R.id.et_uds_5, R.id.et_uds_6, R.id.et_uds_7, R.id.et_uds_8)
        celdasTabla1 = list2.map { id -> findViewById(id) }
        udsTotalesHoraETs = list.map { id -> findViewById(id) }
        queue = Volley.newRequestQueue(this) // Initialize the request queue here


        // Obtiene los TextViews de las horas para actualizarlos posteriormente

        obtenerHorasTextViews()
        calcularUdsTotalesTurno()

        // Inicia la ejecución del Runnable
        handler.post(runnableCode)
        btnGuardar.setOnClickListener{
            enviarDatosTablaUsuario()
        }
    }




        fun enviarDatosTablaUsuario() {
                val casillasTabla1=celdasTabla1.map { et ->
                    val text = et.text.toString()
                    if (text.isNotEmpty() && text.matches(Regex("\\d+"))) {
                        text.toInt()
                    } else {
                        0 // O un valor por defecto que prefieras
                }}
                val udsPorHora = udsTotalesHoraETs.map { et ->
                    val text = et.text.toString()
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
                    Response.Listener { response ->
                        // Manejar la respuesta del servidor
                        titulo.text = "Datos enviados correctamente"
                    },
                    Response.ErrorListener { error ->
                        // Manejar el error
                        titulo.text = "Error al enviar los datos: ${error.message}"
                    })

                queue.add(jsonObjectRequest)

            } catch (e: JSONException) {
                titulo.text = "Error al crear JSON: ${e.message}"
                e.printStackTrace()
            }
            // 3. Enviar los datos al servidor





        }


    fun guardarDatos(){

    }

    // Obtiene todos los textViews de las horas
    fun obtenerHorasTextViews() {
        val horasIds1 = listOf(
            R.id.tv_hora1_tabla1, R.id.tv_hora2_tabla1, R.id.tv_hora3_tabla1, R.id.tv_hora4_tabla1,
            R.id.tv_hora5_tabla1, R.id.tv_hora6_tabla1, R.id.tv_hora7_tabla1, R.id.tv_hora8_tabla1
        )
        val horasIds2 = listOf(
            R.id.tv_hora1_tabla2, R.id.tv_hora2_tabla2, R.id.tv_hora3_tabla2, R.id.tv_hora4_tabla2,
            R.id.tv_hora5_tabla2, R.id.tv_hora6_tabla2, R.id.tv_hora7_tabla2, R.id.tv_hora8_tabla2
        )

        // Inicializa los TextViews para mostrar las horas en turno 1 y turno 2
        horas1TVs = horasIds1.map { id -> findViewById(id) }
        horas2TVs = horasIds2.map { id -> findViewById(id) }
    }

    // Obtiene la hora actual
    fun obtenerHora(): Int {
        val hora = Calendar.getInstance()[Calendar.HOUR_OF_DAY]

        // Muestra la hora actual en el campo de texto et_fecha
        return hora
    }

    // Determina el turno basado en la hora actual
    fun decidirTurno(): Int {
        val hora = obtenerHora()

        return when {
            hora in 7..14 -> 1
            hora in 15..22 -> 2
            hora == 23 || hora in 0..6 -> 3
            else -> throw IllegalArgumentException("Hora inválida: $hora")
        }
    }

    // Actualiza los turnos y los TextViews correspondientes
    fun actualizarTurno() {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val currentDate = dateFormat.format(Calendar.getInstance().time) // Usamos .time para obtener la fecha completa
        fechaTV.setText(currentDate)

            turno = decidirTurno()

        val horasTurno = when (turno) {
            1 -> listOf("7:00 am", "8:00 am", "9:00 am", "10:00 am", "11:00 am", "12:00 pm", "1:00 pm", "2:00 pm")
            2 -> listOf("3:00 pm", "4:00 pm", "5:00 pm", "6:00 pm", "7:00 pm", "8:00 pm", "9:00 pm", "10:00 pm")
            3 -> listOf("11:00 pm", "12:00 am", "1:00 am", "2:00 am", "3:00 am", "4:00 am", "5:00 am", "6:00 am")
            else -> throw IllegalArgumentException("Turno inválido: $turno")
        }

        // Actualiza los TextViews con las horas correspondientes al turno
        for(i in 0..7) {
            horas1TVs[i].text = horasTurno[i]
            horas2TVs[i].text = horasTurno[i]
        }
    }

    fun calcularUdsTotalesTurno() {
        udsTotalesHoraETs.forEach { et ->
            et.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    sumarUds()
                }
                override fun afterTextChanged(s: Editable?) {}
            })
        }
    }

    fun sumarUds() {
        var sum = 0
        udsTotalesHoraETs.forEach{ et ->
            val text = et.text.toString()

            if (text.isNotEmpty()) {
                sum += text.toInt()
            }
        }

        udsTotalesTurnoTV.text = sum.toString()
    }
}
