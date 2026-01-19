package u1comienzo.segundapractica

/*
* Ejercicio Opcional 2: Aplicación de notas rápidas (Opcional2.kt)
* Objetivo: Crear un sistema de notas que permite crear, buscar, modificar y eliminar notas.
*/

import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Scanner

// ==========================================
// Modelo
// ==========================================

data class Nota(
    val titulo: String,
    val contenido: String,
    var esImportante: Boolean = false,
    val fecha: LocalDateTime = LocalDateTime.now() // Timestamp automático
) {
    fun fechaFormateada(): String {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
        return fecha.format(formatter)
    }
}

// Gestor de Notas
class GestorNotas {
    private val notas = mutableListOf<Nota>()

    fun crearNota(titulo: String, contenido: String) {
        notas.add(Nota(titulo, contenido))
    }

    /**
     * Retorna las notas ordenadas por fecha (más recientes primero)
     */
    fun listarPorFecha(): List<Nota> {
        return notas.sortedByDescending { it.fecha }
    }

    /**
     * Busca por título o contenido.
     * Requisito: Uso de filter y map.
     * Aquí filtro las notas y luego las 'mapeo' a un String resumen.
     */
    fun buscar(texto: String): List<String> {
        return notas
            .filter { nota ->
                nota.titulo.contains(texto, ignoreCase = true) ||
                        nota.contenido.contains(texto, ignoreCase = true)
            }
            .map { nota ->
                // Transformo el objeto Nota directamente a un String formateado para mostrar
                val imp = if (nota.esImportante) "[!]" else ""
                "$imp ${nota.titulo} (${nota.fechaFormateada()})"
            }
    }

    /**
     * Marca una nota como importante dado su índice en la lista actual
     */
    fun marcarImportante(indice: Int): Boolean {
        if (indice in 0 until notas.size) {
            notas[indice].esImportante = !notas[indice].esImportante // Toggle
            return true
        }
        return false
    }

    /**
     * Exporta las notas a un archivo de texto.
     * Requisito: StringBuilder y writeText.
     */
    fun exportarNotas(): Result<String> {
        if (notas.isEmpty()) return Result.failure(Exception("No hay notas para exportar."))

        val sb = StringBuilder()
        sb.append("=== MIS NOTAS RAPIDAS ===\n\n")

        // Iteramos para construir el texto
        notas.forEach {
            val estado = if (it.esImportante) "★ IMPORTANTE" else ""
            sb.append("Título: ${it.titulo} $estado\n")
            sb.append("Fecha: ${it.fechaFormateada()}\n")
            sb.append("Contenido: ${it.contenido}\n")
            sb.append("---------------------------\n")
        }

        return try {
            val nombreArchivo = "notas_exportadas.txt"
            val archivo = File(nombreArchivo)

            archivo.writeText(sb.toString())

            Result.success(archivo.absolutePath)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Método auxiliar para obtener la lista cruda (para mostrarlas antes de editar)
    fun obtenerTodas(): List<Nota> = notas
}

fun main() {
    val scanner = Scanner(System.`in`)
    val gestor = GestorNotas()
    var salir = false

    println("Gestor de Notas Rápidas (Kotlin IO)")

    while (!salir) {
        println("\n1. Nueva Nota")
        println("2. Ver Notas (Cronológico)")
        println("3. Buscar Notas")
        println("4. Marcar/Desmarcar Importante")
        println("5. Exportar a TXT")
        println("6. Salir")
        print("Opción: ")

        // try-catch simple para evitar crash por meter letras en vez de números
        try {
            when (scanner.nextLine().toInt()) {
                1 -> {
                    print("Título: ")
                    val titulo = scanner.nextLine()
                    print("Contenido: ")
                    val contenido = scanner.nextLine()
                    gestor.crearNota(titulo, contenido)
                    println("Nota creada.")
                }
                2 -> {
                    println("\n--- Historial ---")
                    val lista = gestor.listarPorFecha()
                    lista.forEachIndexed { index, nota ->
                        val imp = if (nota.esImportante) "★" else " "
                        println("$index. $imp [${nota.fechaFormateada()}] ${nota.titulo}")
                    }
                }
                3 -> {
                    print("Texto a buscar: ")
                    val q = scanner.nextLine()
                    val resultados = gestor.buscar(q)
                    if (resultados.isEmpty()) println("No hay coincidencias.")
                    else resultados.forEach { println(it) }
                }
                4 -> {
                    println("Indique el número (índice) de la nota a modificar (ver opción 2):")
                    val idx = scanner.nextLine().toInt()
                    if (gestor.marcarImportante(idx)) println("Estado de importancia cambiado.")
                    else println("Índice inválido.")
                }
                5 -> {
                    println("Exportando...")
                    gestor.exportarNotas()
                        .onSuccess { path -> println("Archivo guardado en: $path") }
                        .onFailure { err -> println("Error al guardar: ${err.message}") }
                }
                6 -> {
                    salir = true
                    println("Adiós.")
                }
                else -> println("Opción no válida.")
            }
        } catch (e: NumberFormatException) {
            println("Por favor, introduce un número.")
        }
    }
}
