package com.example.ZonaFria

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
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
import org.json.JSONException
import android.widget.Filter
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity(), WebSocketEventListener {
    var iniciandoApp = true

    private lateinit var webSocket: WebSocketManager

    //mapa con los codigos y nombres de los defectos
    private val mapaDefectos = Defectos.map

    private var turno: Int = 0
    private var horaActual: Int = 0

    private lateinit var camposObligatorios: List<View>

    //lista de filas que contiene a la lista de celdas
    private lateinit var celdasTabla1: List<List<EditText>>
    private lateinit var celdasDefectos: List<List<AutoCompleteTextView>>
    private lateinit var celdasTabla2: List<List<EditText>>

    private lateinit var horasTVs: List<List<TextView>>
    private lateinit var moldesETs: List<EditText>

    private lateinit var udsTotalesHoraTVs: List<TextView>
    private lateinit var udsTotalesTurnoTV: TextView
    private lateinit var eficienciaHoraTVs: List<TextView>
    private lateinit var eficienciaTotalTurnoTV: TextView

    private lateinit var btnGuardar: Button

    private lateinit var fechaTV: TextView
    private lateinit var lineaET: EditText
    private lateinit var moldeET: EditText
    private lateinit var velocidadET: EditText
    private lateinit var tiempoDeArchaET: EditText
    private lateinit var objDeLineaET: EditText
    private lateinit var udsHoraET: EditText
    private lateinit var udsTurnoET: EditText

    private lateinit var grupoET: EditText
    private lateinit var firmaET: EditText
    private lateinit var observacionesET: EditText

    private var detallesArray: JSONArray = JSONArray()

    private val serverUrl = "http://192.168.68.60:8080"
    private lateinit var queue: com.android.volley.RequestQueue

    // Handler y Runnable para actualizar el turno cada minuto
    private val handler = Handler(Looper.getMainLooper())

    private val runnableInactividad = Runnable {
        mostrarMensaje(
            titulo = "¡Alerta!",
            mensaje = "Has estado inactivo durante 5 minutos.",
            botonAceptar = "OK" to { reiniciarTemporizadorInactividad() }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webSocket = WebSocketManager(this)
        webSocket.connect()

        val rootLayout = findViewById<View>(R.id.root) // ID del diseño raíz
        rootLayout.setOnTouchListener { _, _ ->
            reiniciarTemporizadorInactividad()
            rootLayout.performClick()
            true
        }

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
            listOf(R.id.horaA_tabla1_tv, R.id.horaB_tabla1_tv, R.id.horaC_tabla1_tv, R.id.horaD_tabla1_tv, R.id.horaE_tabla1_tv, R.id.horaF_tabla1_tv, R.id.horaG_tabla1_tv, R.id.horaH_tabla1_tv),
            listOf(R.id.horaA_tabla2_tv, R.id.horaB_tabla2_tv, R.id.horaC_tabla2_tv, R.id.horaD_tabla2_tv, R.id.horaE_tabla2_tv, R.id.horaF_tabla2_tv, R.id.horaG_tabla2_tv, R.id.horaH_tabla2_tv)
        ).map { tabla ->
            tabla.map { id ->
                val celda = findViewById<TextView>(id)
                celda.tag = resources.getResourceEntryName(id)
                    celda
            }
        }

        moldesETs = listOf(R.id.molde1, R.id.molde2, R.id.molde3, R.id.molde4, R.id.molde5, R.id.molde6)
            .map { id ->
                val celda = findViewById<EditText>(id)
                celda.tag = resources.getResourceEntryName(id)
                observarCambiosMoldes(celda)
                    celda
            }

        // Lista de filas, que a su vez tienen la lista de celdas
        celdasTabla1 = listOf(
            listOf(R.id.A1_Hrs,R.id.A2_Hrs,R.id.A3_Hrs,R.id.A4_Hrs,R.id.A5_Hrs,R.id.A6_Hrs,R.id.A7_Hrs,R.id.A8_Hrs),
            listOf(R.id.B1_Hrs,R.id.B2_Hrs,R.id.B3_Hrs,R.id.B4_Hrs,R.id.B5_Hrs,R.id.B6_Hrs,R.id.B7_Hrs,R.id.B8_Hrs),
            listOf(R.id.C1_Hrs,R.id.C2_Hrs,R.id.C3_Hrs,R.id.C4_Hrs,R.id.C5_Hrs,R.id.C6_Hrs,R.id.C7_Hrs,R.id.C8_Hrs),
            listOf(R.id.D1_Hrs,R.id.D2_Hrs,R.id.D3_Hrs,R.id.D4_Hrs,R.id.D5_Hrs,R.id.D6_Hrs,R.id.D7_Hrs,R.id.D8_Hrs),
            listOf(R.id.E1_Hrs,R.id.E2_Hrs,R.id.E3_Hrs,R.id.E4_Hrs,R.id.E5_Hrs,R.id.E6_Hrs,R.id.E7_Hrs,R.id.E8_Hrs),
            listOf(R.id.F1_Hrs,R.id.F2_Hrs,R.id.F3_Hrs,R.id.F4_Hrs,R.id.F5_Hrs,R.id.F6_Hrs,R.id.F7_Hrs,R.id.F8_Hrs),
            listOf(R.id.G1_Hrs,R.id.G2_Hrs,R.id.G3_Hrs,R.id.G4_Hrs,R.id.G5_Hrs,R.id.G6_Hrs,R.id.G7_Hrs,R.id.G8_Hrs),
            listOf(R.id.H1_Hrs,R.id.H2_Hrs,R.id.H3_Hrs,R.id.H4_Hrs,R.id.H5_Hrs,R.id.H6_Hrs,R.id.H7_Hrs,R.id.H8_Hrs)
        ).map { fila ->
            fila.map { id ->
                val celda = findViewById<EditText>(id)
                celda.tag = resources.getResourceEntryName(id)
                observarCambiosCeldas(celda)
                    celda
            }
        }

        celdasDefectos = listOf(
            listOf(R.id.A9_Def1,R.id.A10_Def2,R.id.A11_Def3,R.id.A12_Def4,R.id.A13_Def5,R.id.A14_Def6),
            listOf(R.id.B9_Def1,R.id.B10_Def2,R.id.B11_Def3,R.id.B12_Def4,R.id.B13_Def5,R.id.B14_Def6),
            listOf(R.id.C9_Def1,R.id.C10_Def2,R.id.C11_Def3,R.id.C12_Def4,R.id.C13_Def5,R.id.C14_Def6),
            listOf(R.id.D9_Def1,R.id.D10_Def2,R.id.D11_Def3,R.id.D12_Def4,R.id.D13_Def5,R.id.D14_Def6),
            listOf(R.id.E9_Def1,R.id.E10_Def2,R.id.E11_Def3,R.id.E12_Def4,R.id.E13_Def5,R.id.E14_Def6),
            listOf(R.id.F9_Def1,R.id.F10_Def2,R.id.F11_Def3,R.id.F12_Def4,R.id.F13_Def5,R.id.F14_Def6),
            listOf(R.id.G9_Def1,R.id.G10_Def2,R.id.G11_Def3,R.id.G12_Def4,R.id.G13_Def5,R.id.G14_Def6),
            listOf(R.id.H9_Def1,R.id.H10_Def2,R.id.H11_Def3,R.id.H12_Def4,R.id.H13_Def5,R.id.H14_Def6)
        ).map { fila ->
            fila.map { id ->
                val celda = findViewById<AutoCompleteTextView>(id)
                celda.tag = resources.getResourceEntryName(id)
                observarCambiosDefectos(celda)
                    celda
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

        grupoET = findViewById(R.id.et_grupo)
        lineaET = findViewById(R.id.et_linea)
        moldeET = findViewById(R.id.et_molde)
        velocidadET = findViewById(R.id.et_velocidad)
        tiempoDeArchaET = findViewById(R.id.et_tiempo_de_archa)
        objDeLineaET = findViewById(R.id.et_obj_de_linea)
        udsHoraET = findViewById(R.id.et_uds_hora)
        udsTurnoET = findViewById(R.id.et_uds_turno)
        firmaET = findViewById(R.id.et_firma)
        observacionesET = findViewById(R.id.et_observaciones)

        camposObligatorios = listOf(grupoET, lineaET, moldeET, velocidadET, tiempoDeArchaET, objDeLineaET, udsHoraET, udsTurnoET, firmaET)
//                udsTotalesHoraTVs + udsTotalesTurnoTV + eficienciaHoraTVs + eficienciaTotalTurnoTV

        camposObligatorios.forEach { celda ->
            when (celda) {
                is TextView -> celda.text = null
                is EditText -> celda.text = null
                is AutoCompleteTextView -> celda.text = null
                else -> throw IllegalArgumentException("Tipo de vista no soportado: ${celda.javaClass.simpleName}")
            }
        }

        btnGuardar.setOnClickListener{
            if (verificarCamposObligatorios()) {
//                enviarDatosTablaUsuario()
                guardarEnBD()
            }
            else {
                mostrarMensaje("Por favor, complete todos los campos obligatorios.")
            }
        }

        queue = Volley.newRequestQueue(this) // Initialize the request queue here

        actualizarTurno()
        reiniciarTemporizadorInactividad()
        bloquearFilasDesde(obtenerFilaDeHora(horaActual))

        iniciandoApp = false
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnableInactividad)
        webSocket.closeConnection()
    }




//    fun mostrarMensaje(mensaje: String) {
//        runOnUiThread {
//            val builder = AlertDialog.Builder(this)
//            builder.setMessage(mensaje)
//                .setPositiveButton("OK") { dialog, _ ->
//                    dialog.dismiss()
//                }
//
//            val alertDialog = builder.create()
//            alertDialog.show()
//
//            val color = ContextCompat.getColor(this, R.color.primary)
//            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(color)
//        }
//    }


    fun mostrarMensaje(
        mensaje: String,
        titulo: String? = null,
        botonAceptar: Pair<String, () -> Unit>? = "OK" to { },
        botonCancelar: Pair<String, () -> Unit>? = null
    ) {
        runOnUiThread {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(titulo)
            builder.setMessage(mensaje)

//            if(titulo == "Error") {
//                builder.setTitle("<font color='#FF0000'>$titulo</font>")
//            }

            if (botonAceptar != null) {
                builder.setPositiveButton(botonAceptar.first) { dialog, _ ->
                    botonAceptar.second() // Ejecuta la función asociada
                    dialog.dismiss()
                }
            }

            if (botonCancelar != null) {
                builder.setNegativeButton(botonCancelar.first) { dialog, _ ->
                    botonCancelar.second() // Ejecuta la función asociada
                    dialog.dismiss()
                }
            }

            val alertDialog = builder.create()
            alertDialog.show()
            if (botonCancelar != null) {
                alertDialog.setCancelable(false)
            }

            val colorAceptar = ContextCompat.getColor(this, R.color.primary)
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(colorAceptar)
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(Color.RED)
        }
    }



    // Obtiene la hora actual
    fun obtenerHora(): Int {
        return Calendar.getInstance()[Calendar.HOUR_OF_DAY]
    }

    // Determina el turno basado en la hora actual
    fun decidirTurno(hora: Int): Int {
        return when (hora) {
            in 7..14 -> 1
            in 15..22 -> 2
            23, in 0..6 -> 3
            else -> throw IllegalArgumentException("Hora inválida: $hora")
        }
    }

    // Actualiza los turnos y los TextViews correspondientes al cambiar de hora
    fun actualizarTurno() {
        val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val fechaActual = formatoFecha.format(Calendar.getInstance().time) // Usamos .time para obtener la fecha completa
        fechaTV.text = fechaActual

        val horaDelDia = obtenerHora()
        val nuevoTurno = decidirTurno(horaDelDia)

        //hay cambio de turno
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

            if(!iniciandoApp) {
                guardarEnBD()
                detallesArray = JSONArray()

                restablecerTablas()
                mostrarMensaje("Nuevo Turno")
                bloquearFilasDesde('A')
            }
        } else {    //no hay cambio de turno, solo actualiza las horas
            val fila = obtenerFilaDeHora(horaDelDia)
            desbloquearFila(fila)
        }

        horaActual = horaDelDia
        handler.postDelayed({ actualizarTurno() }, 1000 * 60)
    }

    fun bloquearFilasDesde(fila: Char) {
        val posicion = obtenerPosicionDeFila(fila)
        val aBloquear: MutableList<View> = mutableListOf()

        for(i in posicion+1..7) {
            aBloquear += celdasTabla1[i] + celdasTabla2[i] + celdasDefectos[i]
        }

        aBloquear.forEach { celda ->
            celda.isEnabled = false
            celda.setBackgroundResource(R.drawable.disabled_cell)
        }
    }

    fun desbloquearFila(fila: Char) {
        val posicion = obtenerPosicionDeFila(fila)
        val aDesloquear = celdasTabla1[posicion] + celdasTabla2[posicion] + celdasDefectos[posicion]

        aDesloquear.forEach { celda ->
            celda.isEnabled = true
            celda.setBackgroundResource(R.drawable.cell_shape)
        }
    }

    fun restablecerTablas() {
        moldesETs.forEach { tv ->
            tv.text = null
        }

        camposObligatorios.forEach { view ->
            if (view is EditText) {
                view.text = null
            } else if (view is TextView) {
                view.text = null
            }
        }

        celdasTabla1.forEach { fila ->
            fila.forEach { celda ->
                celda.text = null
            }
        }

        celdasDefectos.forEach { fila ->
            fila.forEach { celda ->
                celda.text = null
            }
        }

        celdasTabla2.forEach { fila ->
            fila.forEach { celda ->
                celda.text = null
            }
        }

        findViewById<EditText>(R.id.et_observaciones).text.clear()
    }







    //Le aplica el listener de texto a las celdas para actualizar los totales de cada hora
    fun observarCambiosCeldas(celda: EditText) {         //si no es defecto realizar calculos
        celda.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val fila = celda.tag.toString().first()
                calcularUdsTotalesHora(fila)
                reiniciarTemporizadorInactividad()
            }
        })
    }

    fun observarCambiosDefectos(actv: AutoCompleteTextView) {
        val opcionesDefectos = mapaDefectos.map { (clave, valor) -> "$clave - $valor" }

        val adapter = object : ArrayAdapter<String>(
            this,
            android.R.layout.simple_dropdown_item_1line,
            opcionesDefectos
        ) {
            override fun getFilter(): Filter {
                return object : Filter() {
                    override fun performFiltering(constraint: CharSequence?): FilterResults {
                        val resultados = FilterResults()
                        if (constraint.isNullOrEmpty()) {
                            // Mostrar todas las opciones si no hay texto ingresado
                            resultados.values = opcionesDefectos
                            resultados.count = opcionesDefectos.size
                        } else {
                            // Filtrar opciones basándose en la entrada del usuario
                            val filtro = constraint.toString().lowercase()
                            val filtrados = mapaDefectos.filter { (clave, valor) ->
                                clave.lowercase().contains(filtro) || valor.lowercase().contains(filtro)
                            }.map { (clave, valor) -> "$clave - $valor" }
                            resultados.values = filtrados
                            resultados.count = filtrados.size
                        }
                        return resultados
                    }

                    override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                        clear()
                        if (results?.values is List<*>) {
                            @Suppress("UNCHECKED_CAST")
                            addAll(results.values as List<String>)
                        }
                        notifyDataSetChanged()
                    }
                }
            }
        }

        actv.setDropDownBackgroundResource(R.drawable.dropdown_background)
        actv.setAdapter(adapter)

        actv.setOnClickListener { actv.showDropDown() }

        // Manejo de selección: solo establecer el código al seleccionar una opción
        actv.setOnItemClickListener { parent, _, position, _ ->
            val seleccionado = parent.getItemAtPosition(position).toString()
            val codigo = seleccionado.substringBefore(" -") // Extraer solo la parte del código
            val fila = actv.tag.toString().first()
            actv.setText(codigo)

            enviarAlWebSocket("defecto", obtenerHoraDeFila(fila).toString(), actv)
            reiniciarTemporizadorInactividad()
        }
    }


    fun observarCambiosMoldes(tv: TextView) {
        tv.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                enviarAlWebSocket("molde", horaActual.toString(), tv)
                reiniciarTemporizadorInactividad()
            }

        })
    }

    fun observarCambiosUdsTotalesHora(tv: TextView) {
        tv.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val fila = tv.tag.toString().last()
                calcularEficienciaHora(fila)
                calcularUdsTotalesTurno()
                reiniciarTemporizadorInactividad()
            }
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
                val fila = tv.tag.toString().last()
                enviarAlWebSocket("eficiencia", obtenerHoraDeFila(fila).toString(), tv)
                reiniciarTemporizadorInactividad()
            }
        })
    }

    fun calcularUdsTotalesHora(fila: Char) {
        var suma = 0
        val posicionFila = obtenerPosicionDeFila(fila)

        celdasTabla1[posicionFila].forEach { celda ->
            if(celda is EditText) {
                suma += celda.text.toString().toIntOrNull() ?: 0
            }
        }

        if(suma != 0) {
            udsTotalesHoraTVs[posicionFila].text = suma.toString()
            calcularEficienciaHora(fila)
        }
        else {
            udsTotalesHoraTVs[posicionFila].text = null
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
            udsTotalesTurnoTV.text = null
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

            var valido = true
            when (eficiencia) {
                in 0.0..50.0 -> eficienciaTV.setBackgroundResource(R.drawable.red_cell_shape)
                in 50.0..80.0 -> eficienciaTV.setBackgroundResource(R.drawable.yellow_cell_shape)
                in 80.0..100.0 -> eficienciaTV.setBackgroundResource(R.drawable.green_cell_shape)
                else -> valido = false
            }

            if(valido) {
                eficienciaTV.text = String.format(Locale.getDefault(), "%.2f%%", eficiencia)
            }
            else {
                mostrarMensaje("Estás excediendo el numero de unidades por hora.")
                eficienciaTV.setBackgroundResource(R.drawable.yellow_cell_shape)
                eficienciaTV.text = null
            }
        }
        else {
            eficienciaTV.text = null
            eficienciaTV.setBackgroundResource(R.drawable.header_cell_shape)
        }
    }

    fun calcularEficienciaTotalTurno() {
        var suma = 0.0
        var contadorValidos = 0

        eficienciaHoraTVs.forEach { tv ->
            val text = tv.text.toString().substringBefore("%")

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
            eficienciaTotalTurnoTV.text = null
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

    fun obtenerHoraDeFila(charFila: Char): Int {
        val fila = obtenerPosicionDeFila(charFila)
        val horaTV = horasTVs[0][fila]

        val horaRedoneada = horaTV.text.toString().substringBefore(':').toInt()
        return horaRedoneada
    }

    fun obtenerFilaDeHora(hora: Int): Char {
        return when (hora) {
            7, 15, 23 -> 'A'
            8, 16, 0 -> 'B'
            9, 17, 1 -> 'C'
            10, 18, 2 -> 'D'
            11, 19, 3 -> 'E'
            12, 20, 4 -> 'F'
            13, 21, 5 -> 'G'
            14, 22, 6 -> 'H'
            else -> throw IllegalArgumentException("Hora inválida: $hora")
        }
    }

    fun verificarCamposObligatorios(): Boolean {
//        camposObligatorios.forEach { view ->
//            if (view is EditText && view.text.toString().isEmpty()) {
//                return false
//            }
//            else if (view is TextView && view.text.toString().isEmpty()) {
//                return false
//            }
//        }
        return true
    }

    fun enviarDatosTablaUsuario() {
        val casillasTabla1 = celdasTabla1.map { fila ->
            fila.map { et ->
                if(et is EditText){
                    val text = et.text.toString()
                    if (text.isNotEmpty() && text.matches(Regex("\\d+"))) {
                        text.toInt()
                    } else {
                        0 // O un valor por defecto que prefieras
                    }
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

    fun enviarAlWebSocket(tipo: String, horaFila: String, view: View) {
        val mensaje = JSONObject().apply {
            put("type", tipo)
            put("hour", horaFila)
        }

        if (view is TextView && !view.text.isNullOrEmpty()) {
            mensaje.apply {
                put("tag", view.tag.toString())
                put("data", view.text.toString())
            }
            webSocket.sendMessage(mensaje)

        } else if (view is AutoCompleteTextView && !view.text.isNullOrEmpty()) {
            mensaje.apply {
                put("tag", view.tag.toString())
                put("data", view.text.toString())
            }
            webSocket.sendMessage(mensaje)
        }
    }

    fun guardarEnBD() {

        val fila = obtenerFilaDeHora(horaActual)
        val posicion = obtenerPosicionDeFila(fila)

        val detalleTurno = JSONObject().apply {
            put("hora", horaActual.toString()+":00")
            put("paleta_por_hora", "0")                                                                                      //TODO: agregarPaleta
            celdasTabla1[posicion].forEachIndexed { i, celda -> put("produccion_por_hora_${i+1}", celda.text.toString()) }
            celdasDefectos[posicion].forEachIndexed { i, defecto -> put("defecto_por_hora_${i+1}", defecto.text.toString()) }
            put("temple", "POR DEFINIR")                                                                                               //TODO: agregarTemple
            put("total_uds", udsTotalesHoraTVs[posicion].text.toString())
            put("eficiencia", eficienciaHoraTVs[posicion].text.toString().substringBefore("%"))
            put("id_turno", turno.toString())
        }

        detallesArray = JSONArray().apply { put(detalleTurno) }

        val turno = JSONObject().apply {
            put("observaciones", observacionesET.text.toString())
            put("operador", firmaET.text.toString())
            put("unidades_totales", udsTotalesTurnoTV.text.toString())
            put("eficiencia_total", eficienciaTotalTurnoTV.text.toString().substringBefore("%"))
            moldesETs.forEachIndexed { i, molde -> put("moldeSec${i+1}", molde.text.toString()) }

            put("detalle_por_hora", detallesArray)
        }

        val mensaje = JSONObject().apply {
            put("type", "bd")

            put("fecha", fechaTV.text.toString())
            put("linea", lineaET.text.toString())
            put("molde", moldeET.text.toString())
            put("velocidad", velocidadET.text.toString())
            put("tiempo_de_archa", tiempoDeArchaET.text.toString())
            put("obj_de_linea", objDeLineaET.text.toString())
            put("uds_por_hora", udsHoraET.text.toString())
            put("uds_por_turno", udsTurnoET.text.toString())
            put("turno", turno)
        }

        try {
            webSocket.sendMessage(mensaje)
        } catch (e: JSONException) {
            mostrarMensaje("Error al crear JSON: ${e.message}")
        }
    }

    fun enviarDatosTabla(){
        var mensaje: String = ""

        moldesETs.forEach { celda ->
            enviarAlWebSocket("molde", horaActual.toString(), celda)
            mensaje += celda.text.toString()
        }

        celdasDefectos.forEach { fila ->
            fila.forEach { celda ->
                enviarAlWebSocket("defecto", obtenerHoraDeFila(celda.tag.toString().first()).toString(), celda)
                mensaje += celda.text.toString()
            }
        }

        eficienciaHoraTVs.forEach { tv ->
            enviarAlWebSocket("eficiencia", obtenerHoraDeFila(tv.tag.toString().last()).toString(), tv)
            mensaje += tv.text.toString()
        }
    }

    override fun onConnectionFailed(reason: String) {
        mostrarMensaje(
            titulo = "Error",
            mensaje = "Hubo un error conectandose con el servidor:\n$reason",
            botonAceptar = "Reintentar" to { webSocket.connect() },
            botonCancelar = "Cerrar app" to { finishAffinity() }
        )
    }

    override fun onConnectedSuccess() {
//        enviarDatosTabla()
    }

    override fun onError(message: String) {
        mostrarMensaje(
            titulo = "Error",
            mensaje = "Hubo un error enviando la información a la Base de Datos:\n$message"
        )
    }

    private fun reiniciarTemporizadorInactividad() {
        handler.removeCallbacks(runnableInactividad)
        handler.postDelayed(runnableInactividad, 1000 * 60 * 5) //5 minutos
    }
}