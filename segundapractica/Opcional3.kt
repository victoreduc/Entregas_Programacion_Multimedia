package u1comienzo.segundapractica

/*
* Ejercicio Opcional 3: Calculadora de IMC con historial (Opcional3.kt)
* Objetivo: Crear una calculadora de Índice de Masa Corporal que mantiene historial de mediciones.
*/

import java.util.Scanner

data class RegistroIMC(
    val peso: Double,
    val altura: Double,
    val imc: Double,
    val categoria: String
)

class GestorIMC {
    // Lista para almacenar el historial de registros
    private val historial = mutableListOf<RegistroIMC>()

    /**
     * Calcula el IMC, determina la categoría y guarda el registro.
     * Retorna el objeto RegistroIMC creado.
     */
    fun registrarMedicion(peso: Double, altura: Double): RegistroIMC {
        // Fórmula: peso / altura^2
        val imc = peso / (altura * altura)
        val categoria = clasificarIMC(imc)

        val nuevoRegistro = RegistroIMC(peso, altura, imc, categoria)
        historial.add(nuevoRegistro)
        return nuevoRegistro
    }

    /**
     * Clasifica el IMC usando 'when' con rangos según la OMS.
     */
    private fun clasificarIMC(imc: Double): String {
        return when (imc) {
            in 0.0..18.49 -> "Bajo peso"
            in 18.5..24.99 -> "Peso normal"
            in 25.0..29.99 -> "Sobrepeso"
            else -> "Obesidad"
        }
    }

    /**
     * Obtiene el historial completo.
     */
    fun obtenerHistorial(): List<RegistroIMC> {
        return historial
    }

    /**
     * Calcula la diferencia de peso respecto al registro anterior.
     * Retorna null si no hay suficientes registros.
     */
    fun obtenerTendenciaPeso(): Double? {
        if (historial.size < 2) return null

        val actual = historial.last()
        // Accedemos al penúltimo elemento
        val anterior = historial[historial.size - 2]

        return actual.peso - anterior.peso
    }
}

fun main() {
    val scanner = Scanner(System.`in`)
    val gestor = GestorIMC()
    var salir = false

    println("=== Calculadora de IMC con Historial ===")

    while (!salir) {
        println("\n1. Calcular Nuevo IMC")
        println("2. Ver Historial y Tendencias")
        println("3. Salir")
        print("Seleccione una opción: ")

        try {
            val opcion = scanner.next().toInt()

            when (opcion) {
                1 -> vistaCalcular(scanner, gestor)
                2 -> vistaHistorial(gestor)
                3 -> salir = true
                else -> println("Opción no válida.")
            }
        } catch (e: Exception) {
            println("Error: Entrada inválida.")
            scanner.nextLine() // Limpiar buffer
        }
    }
}

fun vistaCalcular(scanner: Scanner, gestor: GestorIMC) {
    println("\n--- Nuevo Cálculo ---")

    // Validación de entrada para Peso
    var peso = -1.0
    while (peso <= 0) {
        print("Ingrese peso (kg): ")
        val input = scanner.next()
        val p = input.toDoubleOrNull()
        if (p != null && p > 0) peso = p else println("El peso debe ser un número positivo.")
    }

    // Validación de entrada para Altura
    var altura = -1.0
    while (altura <= 0) {
        print("Ingrese altura (m) (ej. 1.75): ")
        val input = scanner.next()
        val a = input.toDoubleOrNull()
        if (a != null && a > 0) altura = a else println("La altura debe ser un número positivo.")
    }

    val registro = gestor.registrarMedicion(peso, altura)

    println("\nResultados:")
    println("IMC: %.2f".format(registro.imc))
    println("Categoría: ${registro.categoria}")

    // Mostrar tendencia inmediatamente si existe
    val tendencia = gestor.obtenerTendenciaPeso()
    if (tendencia != null) {
        val signo = if (tendencia > 0) "+" else ""
        println("Diferencia respecto al último peso: $signo%.2f kg".format(tendencia))
    }
}

fun vistaHistorial(gestor: GestorIMC) {
    println("\n--- Historial de Mediciones ---")
    val lista = gestor.obtenerHistorial()

    if (lista.isEmpty()) {
        println("No hay registros aún.")
    } else {
        lista.forEachIndexed { index, reg ->
            print("${index + 1}. Peso: ${reg.peso}kg | Altura: ${reg.altura}m | ")
            print("IMC: %.2f (%s)".format(reg.imc, reg.categoria))

            // Si no es el primero, calculamos la diferencia con el anterior en la iteración
            if (index > 0) {
                val anterior = lista[index - 1]
                val diff = reg.peso - anterior.peso
                val signo = if (diff >= 0) "+" else ""
                print(" | Cambio: $signo%.2f kg".format(diff))
            }
            println()
        }
    }
}
