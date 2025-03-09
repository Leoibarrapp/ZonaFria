package com.example.vidosaZF
import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
data class Defecto(val codigo: String, val nombre: String)

class Defectos {
    companion object {
        private val defectosJSON = """
            [
                {
                    "codigo": "0A1",
                    "nombre": "BURBUJAS <1,60 mm"
                },
                {
                    "codigo": "0A2",
                    "nombre": "COLOR DE VIDRIO F/NORMA"
                },
                {
                    "codigo": "0A3",
                    "nombre": "ENVASES CON POLVO"
                },
                {
                    "codigo": "0A4",
                    "nombre": "ENVASES FRÍOS/LAMPAREADOS"
                },
                {
                    "codigo": "0A5",
                    "nombre": "ENVASES RAYADOS"
                },
                {
                    "codigo": "0A6",
                    "nombre": "ENVASES SUCIO EXTERNO"
                },
                {
                    "codigo": "0A7",
                    "nombre": "EXCESO DE TRATAMIENTO EXT."
                },
                {
                    "codigo": "0A8",
                    "nombre": "MACHO SUCIO"
                },
                {
                    "codigo": "0A9",
                    "nombre": "MARCA DE ANILLO"
                },
                {
                    "codigo": "0A10",
                    "nombre": "MARCA DE ARCHA"
                },
                {
                    "codigo": "0A11",
                    "nombre": "MARCA DE ESPIGA"
                },
                {
                    "codigo": "0A12",
                    "nombre": "MARCA DE GOTA"
                },
                {
                    "codigo": "0A13",
                    "nombre": "MARCA DE HILO"
                },
                {
                    "codigo": "0A14",
                    "nombre": "MARCA DE MACHO FRÍO FONDO"
                },
                {
                    "codigo": "0A15",
                    "nombre": "MARCAS DE TIJERAS"
                },
                {
                    "codigo": "0A16",
                    "nombre": "MOLDE PICADO"
                },
                {
                    "codigo": "0A17",
                    "nombre": "MOLDE RAYADO"
                },
                {
                    "codigo": "0A18",
                    "nombre": "MOLDE SUCIO"
                },
                {
                    "codigo": "0A19",
                    "nombre": "ÓPTICO INTERNO"
                },
                {
                    "codigo": "0A20",
                    "nombre": "ÓPTICO EXTERNO"
                },
                {
                    "codigo": "0A21",
                    "nombre": "PIEDRAS < 2,5mm"
                },
                {
                    "codigo": "0A22",
                    "nombre": "PIEDRAS < 2,5mm"
                },
                {
                    "codigo": "0A23",
                    "nombre": "PIEL DE NARANJA"
                },
                {
                    "codigo": "0B1",
                    "nombre": "ALTURA TOT B/MIN."
                },
                {
                    "codigo": "0B2",
                    "nombre": "ALTURA TOTS/MAX."
                },
                {
                    "codigo": "0B3",
                    "nombre": "BURBÚJAS >1,60 mm"
                },
                {
                    "codigo": "0B4",
                    "nombre": "CAPACIDAD B/MIN"
                },
                {
                    "codigo": "0B5",
                    "nombre": "CAPACIDAD B/MIN"
                },
                {
                    "codigo": "0B6",
                    "nombre": "CAPACIDAD S/MAX"
                },
                {
                    "codigo": "0B7",
                    "nombre": "FALTA DE TRAT. EXT."
                },
                {
                    "codigo": "0B8",
                    "nombre": "FALTA DE TRAT. INT."
                },
                {
                    "codigo": "0B9",
                    "nombre": "LÍNEAS BRILLANTES"
                },
                {
                    "codigo": "0B10",
                    "nombre": "PESO ALTO"
                },
                {
                    "codigo": "0B11",
                    "nombre": "PESO BAJO"
                },
                {
                    "codigo": "0B12",
                    "nombre": "PIEDRAS (9,9-2,5mm)"
                },
                {
                    "codigo": "0B13",
                    "nombre": "ROT. POR CHOQUE TERM."
                },
                {
                    "codigo": "0B14",
                    "nombre": "SEMILLAS"
                },
                {
                    "codigo": "0B15",
                    "nombre": "TEMPLE ALTO"
                },
                {
                    "codigo": "0B16",
                    "nombre": "VIDRIO EXT. PEGADO"
                },
                {
                    "codigo": "0B17",
                    "nombre": "VIDRIO EXT. PEGADO"
                },
                {
                    "codigo": "0C1",
                    "nombre": "AGUJA"
                },
                {
                    "codigo": "0C2",
                    "nombre": "ALETAS"
                },
                {
                    "codigo": "0C3",
                    "nombre": "***BURBÚJAS SUP INT/EXT FRACTURABLE"
                },
                {
                    "codigo": "0C4",
                    "nombre": "CONTAMINACIÓN INT."
                },
                {
                    "codigo": "0C5",
                    "nombre": "ENVASES CON HONGOS"
                },
                {
                    "codigo": "0C6",
                    "nombre": "ENVASES REVENTADOS"
                },
                {
                    "codigo": "0C7",
                    "nombre": "PIEDRAS>10mm"
                },
                {
                    "codigo": "0C8",
                    "nombre": "TELÉFONO"
                },
                {
                    "codigo": "0C9",
                    "nombre": "SANGRE"
                },
                {
                    "codigo": "0C10",
                    "nombre": "SOBREPRENSA"
                },
                {
                    "codigo": "0C11",
                    "nombre": "VIDRIO INTERNO PEGADO"
                },
                {
                    "codigo": "0C12",
                    "nombre": "VIDRIO INTERNO SUELTO"
                },
                {
                    "codigo": "1A1",
                    "nombre": "CORONA CORTA"
                },
                {
                    "codigo": "1A2",
                    "nombre": "CORONA SUCIA"
                },
                {
                    "codigo": "1A3",
                    "nombre": "DEPRESIÓN INT. ACABADO"
                },
                {
                    "codigo": "1B1",
                    "nombre": "*ACABADO DESCENTRADO >3 mm"
                },
                {
                    "codigo": "1B2",
                    "nombre": "*ACABADO FALLO"
                },
                {
                    "codigo": "1B3",
                    "nombre": "*ACABADO HINCHADO"
                },
                {
                    "codigo": "1B4",
                    "nombre": "*ACABADO HUNDIDO"
                },
                {
                    "codigo": "1B5",
                    "nombre": "*ACABADO INCLINADO > 0,5 mm"
                },
                {
                    "codigo": "1B6",
                    "nombre": "*ACABADO OVALADO"
                },
                {
                    "codigo": "1B7",
                    "nombre": "*ACABADO SUCIO EXT."
                },
                {
                    "codigo": "1B8",
                    "nombre": "*ARRUGA INT. ACAB."
                },
                {
                    "codigo": "1B9",
                    "nombre": "*BURBUJAS ACABADO >1,6 mm"
                },
                {
                    "codigo": "1B10",
                    "nombre": "*CORONAS LIGADAS"
                },
                {
                    "codigo": "1B11",
                    "nombre": "*DIAM. \"A\" B/MÍNIMO"
                },
                {
                    "codigo": "1B12",
                    "nombre": "*DIAM. \"A\" S/MÁXIMO"
                },
                {
                    "codigo": "1B13",
                    "nombre": "*DIAM. \"B\" B/MÍNIMO"
                },
                {
                    "codigo": "1B14",
                    "nombre": "*DIAM. \"B\" S/MÁXIMO"
                },
                {
                    "codigo": "1B15",
                    "nombre": "*DIAM. \"C\" B/MÍNIMO"
                },
                {
                    "codigo": "1B16",
                    "nombre": "*DIAM. \"C\" S/MÁXIMO"
                },
                {
                    "codigo": "1B17",
                    "nombre": "*DIAM. \"E\" B/MÍNIMO"
                },
                {
                    "codigo": "1B18",
                    "nombre": "*DIAM. \"E\" S/MÁXIMO"
                },
                {
                    "codigo": "1B19",
                    "nombre": "*DIAM. \"I\" B/MÍNIMO"
                },
                {
                    "codigo": "1B20",
                    "nombre": "*DIAM. \"L\" B/MÍNIMO"
                },
                {
                    "codigo": "1B21",
                    "nombre": "*DIAM. \"L\" S/MÁXIMO"
                },
                {
                    "codigo": "1B22",
                    "nombre": "*DIAM. \"N\" B/MÍNIMO"
                },
                {
                    "codigo": "1B23",
                    "nombre": "*DIAM. \"N\" S/MÁXIMO"
                },
                {
                    "codigo": "1B24",
                    "nombre": "*DIAM. \"T\" B/MÍNIMO"
                },
                {
                    "codigo": "1B25",
                    "nombre": "*DIAM. \"T\" S/MÁXIMO"
                },
                {
                    "codigo": "1B26",
                    "nombre": "*DIST. \"H\" B/MÍNIMO"
                },
                {
                    "codigo": "1B27",
                    "nombre": "*DIST. \"H\" S/MÁXIMO"
                },
                {
                    "codigo": "1B28",
                    "nombre": "*HILO ACAB. FALLO"
                },
                {
                    "codigo": "1B29",
                    "nombre": "*K.O. ACABADO"
                },
                {
                    "codigo": "1B30",
                    "nombre": "*MARCAS PROF. EN EL ACAB"
                },
                {
                    "codigo": "1B31",
                    "nombre": "*ROT. HORIZONTAL ACAB. <3mm"
                },
                {
                    "codigo": "1B32",
                    "nombre": "*ROT. HORIZONTAL ACAB. >3mm"
                },
                {
                    "codigo": "1B33",
                    "nombre": "*ROT. HORIZONTAL TRANSFER<3mm"
                },
                {
                    "codigo": "1B34",
                    "nombre": "*ROT. HORIZONTAL TRANSFER>3mm"
                },
                {
                    "codigo": "1B35",
                    "nombre": "*ROT. VERTICAL ACABADO"
                },
                {
                    "codigo": "1B36",
                    "nombre": "*ROT. VERTICAL TRANSFER"
                },
                {
                    "codigo": "1C1",
                    "nombre": "***ACABADO ASTILLADO O ROTO"
                },
                {
                    "codigo": "1C2",
                    "nombre": "***ACABADO OBSTRUIDO"
                },
                {
                    "codigo": "1C3",
                    "nombre": "***ACABADO SUCIO INTERNO"
                },
                {
                    "codigo": "1C4",
                    "nombre": "***DIENTES ASTILLADOS"
                },
                {
                    "codigo": "1C5",
                    "nombre": "***ENVASE DEGOLLADO"
                },
                {
                    "codigo": "1C6",
                    "nombre": "***REBABAS ASTILLADA ACAB."
                },
                {
                    "codigo": "1C7",
                    "nombre": "***REBABA EXT. FILOSA ACABADO> ø E"
                },
                {
                    "codigo": "1C8",
                    "nombre": "***REBABA INT. ACABADO"
                },
                {
                    "codigo": "1C9",
                    "nombre": "***TRANSFER ASTILLADO"
                },
                {
                    "codigo": "2A1",
                    "nombre": "ARRUGA INT. CUELLO"
                },
                {
                    "codigo": "2A2",
                    "nombre": "BOMBAS EN EL CUELLO"
                },
                {
                    "codigo": "2A3",
                    "nombre": "MARCAS EN EL CUELLO"
                },
                {
                    "codigo": "2A4",
                    "nombre": "VIDRIO GRUESO CUELLO"
                },
                {
                    "codigo": "2B1",
                    "nombre": "*ARRUGA BRILLANTE CUELLO"
                },
                {
                    "codigo": "2B2",
                    "nombre": "*CUELLO CALCINADO"
                },
                {
                    "codigo": "2B3",
                    "nombre": "*CUELLO DEFORME"
                },
                {
                    "codigo": "2B4",
                    "nombre": "*CUELLO INCLINADO"
                },
                {
                    "codigo": "2B5",
                    "nombre": "*CUELLO LISO"
                },
                {
                    "codigo": "2B6",
                    "nombre": "*CUELLO OBSTRUIDO"
                },
                {
                    "codigo": "2B7",
                    "nombre": "*CUELLO OVALADO"
                },
                {
                    "codigo": "2B8",
                    "nombre": "*DIAM. CUELLO B/MIN"
                },
                {
                    "codigo": "2B9",
                    "nombre": "*DIAM. CUELLO S/MAX."
                },
                {
                    "codigo": "2B10",
                    "nombre": "*K.O. CUELLO"
                },
                {
                    "codigo": "2B11",
                    "nombre": "*LÍNEA BRILLANTE CUELLO"
                },
                {
                    "codigo": "2B12",
                    "nombre": "*REBABA EN CUELLO"
                },
                {
                    "codigo": "2B13",
                    "nombre": "*ROTURA DE CUELLO"
                },
                {
                    "codigo": "2C1",
                    "nombre": "***ARRUGA CON VIDRIO FILOSO CUELLO"
                },
                {
                    "codigo": "2C2",
                    "nombre": "***COSTURA AST. CUELLO"
                },
                {
                    "codigo": "2C3",
                    "nombre": "***REBABA AST. CUELLO"
                },
                {
                    "codigo": "3B1",
                    "nombre": "HOMBRO CAÍDO"
                },
                {
                    "codigo": "3B2",
                    "nombre": "*HOMBRO CALCINADO"
                },
                {
                    "codigo": "3B3",
                    "nombre": "*DIAM. HOMBRO B/MIN. "
                },
                {
                    "codigo": "3B4",
                    "nombre": "*DIAM. HOMBRO S/MAX."
                },
                {
                    "codigo": "3B5",
                    "nombre": "*PTO SUP DE CONTAC B/MIN"
                },
                {
                    "codigo": "3B6",
                    "nombre": "*ROTURA DE HOMBRO"
                },
                {
                    "codigo": "4A1",
                    "nombre": "ARRUGA INT. CUERPO"
                },
                {
                    "codigo": "4A2",
                    "nombre": "MARCAS EN EL CUERPO"
                },
                {
                    "codigo": "4B1",
                    "nombre": "*BOMBAS EN EL CPO."
                },
                {
                    "codigo": "4B2",
                    "nombre": "*CUERPO CALCINADO"
                },
                {
                    "codigo": "4B3",
                    "nombre": "*CUERPO DEFORME"
                },
                {
                    "codigo": "4B4",
                    "nombre": "*CUERPO HINCHADO"
                },
                {
                    "codigo": "4B5",
                    "nombre": "*CUERPO HUNDIDO"
                },
                {
                    "codigo": "4B6",
                    "nombre": "*CUERPO OVALADO"
                },
                {
                    "codigo": "4B7",
                    "nombre": "*CUERPO ROTO"
                },
                {
                    "codigo": "4B8",
                    "nombre": "*DIAM. CUERPO B/MIN"
                },
                {
                    "codigo": "4B9",
                    "nombre": "*DIAM. CUERPO S/MAX"
                },
                {
                    "codigo": "4B10",
                    "nombre": "*K.O. CUERPO"
                },
                {
                    "codigo": "4B11",
                    "nombre": "*LÍNEA BRILLANTE CPO."
                },
                {
                    "codigo": "4B12",
                    "nombre": "MARCAS PROFUNDAS CPO"
                },
                {
                    "codigo": "4B13",
                    "nombre": "*MOLDES LIGADOS"
                },
                {
                    "codigo": "4B14",
                    "nombre": "*VIDRIO FINO CUERPO"
                },
                {
                    "codigo": "5A1",
                    "nombre": "AÑO F/NORMA"
                },
                {
                    "codigo": "5A2",
                    "nombre": "RELIEVES BORROSOS"
                },
                {
                    "codigo": "5B1",
                    "nombre": "*DIAM. TALÓN B/MIN."
                },
                {
                    "codigo": "5B2",
                    "nombre": "*DIAM. TALÓN S/MAX."
                },
                {
                    "codigo": "5B3",
                    "nombre": "*GUÍA ROTA"
                },
                {
                    "codigo": "5B4",
                    "nombre": "*GUÍA VACÍA/INCOMPLETA"
                },
                {
                    "codigo": "5B5",
                    "nombre": "*PROF. GUÍA B/MIN"
                },
                {
                    "codigo": "5B6",
                    "nombre": "*PROF. GUÍA S/MAX"
                },
                {
                    "codigo": "5B7",
                    "nombre": "*PTO INF DE CONTAC B/MIN"
                },
                {
                    "codigo": "5B8",
                    "nombre": "*TALÓN CALCINADO"
                },
                {
                    "codigo": "6A1",
                    "nombre": "ARRUGA INT. FONDO"
                },
                {
                    "codigo": "6A2",
                    "nombre": "BAFFLE CORRIDO"
                },
                {
                    "codigo": "6A3",
                    "nombre": "BASTONEADO LISO"
                },
                {
                    "codigo": "6A4",
                    "nombre": "FONDO ABOMBILLADO (NO AFECTA ESTABILIDAD)"
                },
                {
                    "codigo": "6A5",
                    "nombre": "FONDO GRUESO"
                },
                {
                    "codigo": "6A6",
                    "nombre": "FONDO ILEGIBLE"
                },
                {
                    "codigo": "6A7",
                    "nombre": "FONDO IRREGULAR"
                },
                {
                    "codigo": "6A8",
                    "nombre": "FONDOS LIGADOS"
                },
                {
                    "codigo": "6A9",
                    "nombre": "FONDO SUCIO"
                },
                {
                    "codigo": "6A10",
                    "nombre": "FONDOS FRÍOS"
                },
                {
                    "codigo": "6A11",
                    "nombre": "MARCAS BAFFLE PROF"
                },
                {
                    "codigo": "6A12",
                    "nombre": "MARCA DE ARCHA"
                },
                {
                    "codigo": "6A13",
                    "nombre": "REBABA EN EL FONDO"
                },
                {
                    "codigo": "6B1",
                    "nombre": "*BAFFLE ASTILLADO"
                },
                {
                    "codigo": "6B2",
                    "nombre": "*FONDO CALCINADO"
                },
                {
                    "codigo": "6B3",
                    "nombre": "*FONDO CORRIDO"
                },
                {
                    "codigo": "6B4",
                    "nombre": "*FONDO DESIGUAL > 5 mm"
                },
                {
                    "codigo": "6B5",
                    "nombre": "*FONDO HINCHADO"
                },
                {
                    "codigo": "6B6",
                    "nombre": "*FONDO OSCILANTE"
                },
                {
                    "codigo": "6B7",
                    "nombre": "*K.O. FONDO"
                },
                {
                    "codigo": "6B8",
                    "nombre": "*LÍNEA BRILLANT. FONDO"
                },
                {
                    "codigo": "6B9",
                    "nombre": "*ROTURA DE FONDO"
                },
                {
                    "codigo": "6B10",
                    "nombre": "*VIDRIO FINO FONDO"
                },
                {
                    "codigo": "6C1",
                    "nombre": "***BASTONEADO AST. O ROTO"
                },
                {
                    "codigo": "6C2",
                    "nombre": "***FONDO ASTILLADO O ROTO"
                },
                {
                    "codigo": "6C3",
                    "nombre": "***REBABA AST. FONDO"
                },
                {
                    "codigo": "7A1",
                    "nombre": "COSTURA ABIERTA EN ACAB."
                },
                {
                    "codigo": "7A2",
                    "nombre": "COSTURA DOBLE EN EL CPO."
                },
                {
                    "codigo": "7A3",
                    "nombre": "COSTURA PROFUNDA CPO."
                },
                {
                    "codigo": "7A4",
                    "nombre": "MOLDE CORRIDO"
                },
                {
                    "codigo": "7B1",
                    "nombre": "*COSTURA CALCINADA"
                },
                {
                    "codigo": "7B2",
                    "nombre": "*COSTURA GRUESA ACAB. > 9"
                },
                {
                    "codigo": "7B3",
                    "nombre": "*COSTURA GRUESA FILOSA CPO. > 9"
                },
                {
                    "codigo": "7B4",
                    "nombre": "*COSTURA ABIERTA"
                },
                {
                    "codigo": "7B5",
                    "nombre": "*COSTURA HUNDIDA"
                },
                {
                    "codigo": "7B6",
                    "nombre": "*COST. GRUESA CUELLO"
                },
                {
                    "codigo": "7B7",
                    "nombre": "*ROTURA VERT. COST. DEL ACABADO"
                },
                {
                    "codigo": "7C1",
                    "nombre": "***COST. ASTILLADA O ROTA"
                },
                {
                    "codigo": "8A1",
                    "nombre": "ENVASES DESALINEADOS"
                },
                {
                    "codigo": "8A2",
                    "nombre": "ENVASES CAÍDOS ARCHA"
                },
                {
                    "codigo": "8A3",
                    "nombre": "ESPACIO EN EL ARCHA"
                },
                {
                    "codigo": "8A4",
                    "nombre": "FALLA ELÉCTRICA"
                },
                {
                    "codigo": "8A5",
                    "nombre": "TRANSFERENCIA"
                },
                {
                    "codigo": "8A6",
                    "nombre": "SECCIÓN PARADA"
                },
                {
                    "codigo": "9B1",
                    "nombre": "CARTÓN SUCIO O ROTO"
                },
                {
                    "codigo": "9B2",
                    "nombre": "FLEJE ROTO"
                },
                {
                    "codigo": "9B3",
                    "nombre": "PALETA PARTIDA"
                },
                {
                    "codigo": "9B4",
                    "nombre": "PALETA SUCIA"
                },
                {
                    "codigo": "9B5",
                    "nombre": "POLYSTRETCH ROTO"
                },
                {
                    "codigo": "9B6",
                    "nombre": "POLYSTRETCH SUCIO"
                },
                {
                    "codigo": "9B7",
                    "nombre": "BULK DESPLAZADO"
                },
                {
                    "codigo": "9C2",
                    "nombre": "CONTAMINACIÓN INTERNA"
                },
                {
                    "codigo": "9C3",
                    "nombre": "PALETAS/ENV. MOJADOS"
                }
            ]
        """.trimIndent()

        private val lista = Json.decodeFromString<List<Defecto>>(defectosJSON)
        val map = lista.associate { it.codigo to it.nombre }
    }
}
