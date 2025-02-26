package com.example.vidosaZF

import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {

    // IDs de los TextViews para mostrar las horas en turno 1 y turno 2
    private lateinit var horas1TVs: List<TextView>
    private lateinit var horas2TVs: List<TextView>
    private lateinit var fechaTV: TextView
    private lateinit var udsTotalesTurnoTV: TextView
    private lateinit var udsTotalesHoraETs: List<EditText>

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

        fechaTV = findViewById(R.id.tv_fecha)
        udsTotalesTurnoTV = findViewById(R.id.tv_uds_totales_turno)

        val list = listOf(R.id.et_uds_1, R.id.et_uds_2, R.id.et_uds_3, R.id.et_uds_4, R.id.et_uds_5, R.id.et_uds_6, R.id.et_uds_7, R.id.et_uds_8)
        udsTotalesHoraETs = list.map { id -> findViewById(id) }


        // Obtiene los TextViews de las horas para actualizarlos posteriormente
        obtenerHorasTextViews()
        calcularUdsTotalesTurno()

        // Inicia la ejecución del Runnable
        handler.post(runnableCode)
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
