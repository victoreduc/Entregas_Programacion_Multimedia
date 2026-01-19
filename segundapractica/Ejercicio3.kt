package u1comienzo.segundapractica

/*
Ejercicio 3: Analizador de textos con estadísticas (Ejercicio3.kt)
Objetivo: Analizar un texto y generar estadísticas detalladas sobre su contenido.
*/

import java.text.Normalizer
import java.util.Scanner

// DATA CLASS: Para almacenar los resultados de forma estructurada
data class EstadisticasTexto(
    val textoOriginal: String,
    val totalPalabras: Int,
    val totalCaracteres: Int,
    val totalCaracteresSinEspacios: Int,
    val frecuenciaPalabras: Map<String, Int>,
    val palabraMasRepetida: Pair<String, Int>?
)

class AnalizadorTexto {

    /**
     * Analiza el texto aplicando normalización y cálculos estadísticos.
     * Retorna un Result<T> para manejar errores de forma robusta.
     */
    fun analizar(texto: String): Result<EstadisticasTexto> {
        return runCatching {
            if (texto.isBlank()) {
                throw IllegalArgumentException("El texto no puede estar vacío.")
            }

            // Normalización: Minúsculas y limpieza básica
            // Regex: Mantiene letras (incluyendo tildes/ñ) y números. Elimina puntuación.
            val textoNormalizado = texto.trim().lowercase()
            val regexPuntuacion = Regex("[^a-z0-9áéíóúñü\\s]")
            val textoLimpio = regexPuntuacion.replace(textoNormalizado, "")

            // Tokenización: Dividir por espacios en blanco (uno o más)
            val palabras = textoLimpio.split(Regex("\\s+")).filter { it.isNotEmpty() }

            // Cálculos
            val totalCarac = texto.length
            val totalCaracSinEsp = texto.replace(" ", "").length

            // Agrupar y contar frecuencias
            val frecuencias = palabras.groupingBy { it }.eachCount()

            // Buscar la más repetida
            val masRepetida = frecuencias.maxByOrNull { it.value }?.toPair()

            // Retornamos el objeto con los datos
            EstadisticasTexto(
                textoOriginal = texto,
                totalPalabras = palabras.size,
                totalCaracteres = totalCarac,
                totalCaracteresSinEspacios = totalCaracSinEsp,
                frecuenciaPalabras = frecuencias,
                palabraMasRepetida = masRepetida
            )
        }
    }

    /**
     * Busca cuántas veces aparece un patrón (palabra o frase) específico.
     */
    fun buscarPatron(texto: String, patron: String): Int {
        if (patron.isBlank()) return 0
        // Usamos RegexOption.IGNORE_CASE para que no importen las mayúsculas
        return Regex(Regex.escape(patron), RegexOption.IGNORE_CASE)
            .findAll(texto)
            .count()
    }
}

// --- MENÚ INTERACTIVO ---

fun main() {
    val scanner = Scanner(System.`in`)
    val analizador = AnalizadorTexto()
    var continuar = true

    println("=== EJERCICIO 3: ANALIZADOR DE TEXTOS ===")

    while (continuar) {
        println("\nSelecciona una opción:")
        println("1. Introducir texto para analizar")
        println("2. Buscar patrón en un texto")
        println("3. Salir")
        print("> ")

        when (scanner.nextLine().trim()) {
            "1" -> {
                println("Introduce el texto a analizar (presiona Enter al terminar):")
                val entrada = scanner.nextLine()

                // Uso de Result<T> para manejar el éxito o el fallo
                val resultado = analizador.analizar(entrada)

                resultado.onSuccess { stats ->
                    mostrarEstadisticas(stats)
                }.onFailure { error ->
                    println("Error: ${error.message}")
                }
            }
            "2" -> {
                println("Introduce el texto base:")
                val textoBase = scanner.nextLine()
                println("Introduce la palabra/patrón a buscar:")
                val patron = scanner.nextLine()

                val cantidad = analizador.buscarPatron(textoBase, patron)
                println("El patrón '$patron' aparece $cantidad veces.")
            }
            "3" -> {
                println("Saliendo...")
                continuar = false
            }
            else -> println("Opción no válida. Intenta de nuevo.")
        }
    }
}

// Función auxiliar para imprimir bonito (Clean Code: separar lógica de UI)
fun mostrarEstadisticas(stats: EstadisticasTexto) {
    println("\n--- REPORTE DE ESTADÍSTICAS ---")
    println("Total Palabras: ${stats.totalPalabras}")
    println("Caracteres (con espacios): ${stats.totalCaracteres}")
    println("Caracteres (sin espacios): ${stats.totalCaracteresSinEspacios}")

    if (stats.palabraMasRepetida != null) {
        println("Palabra más frecuente: '${stats.palabraMasRepetida.first}' (${stats.palabraMasRepetida.second} veces)")
    }

    println("\nTop 5 Palabras más usadas:")
    stats.frecuenciaPalabras.entries
        .sortedByDescending { it.value }
        .take(5)
        .forEach { (palabra, cantidad) ->
            println("   - $palabra: $cantidad")
        }
    println("----------------------------------")
}
