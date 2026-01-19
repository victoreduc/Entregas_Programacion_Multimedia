package u1comienzo.segundapractica

/*
* EJERCICIOS OPCIONALES
* Ejercicio Opcional 1: Juego de adivinanza de números (Opcional1.kt)
* Objetivo: Implementar un juego interactivo donde el usuario adivina un número aleatorio con pistas.
*/

import java.util.Scanner

fun main() {
    val scanner = Scanner(System.`in`)

    // Variable de estado para el récord.
    var recordPersonal = Int.MAX_VALUE
    var seguirJugando = true

    println("¡Bienvenido al Juego de Adivinanza!")

    while (seguirJugando) {
        val numeroSecreto = (1..100).random()
        var intentosPartida = 0
        var adivinado = false

        println("\nHe pensado un número del 1 al 100.")
        if (recordPersonal != Int.MAX_VALUE) {
            println("Récord actual a batir: $recordPersonal intentos.")
        }

        // Bucle del juego actual
        while (!adivinado) {
            print("Intento #${intentosPartida + 1} -> Introduce tu número: ")

            val entrada = scanner.next()
            val numeroUsuario = entrada.toIntOrNull()

            if (numeroUsuario == null) {
                println("Eso no es un número válido. Intenta otra vez.")
                continue // Vuelve al inicio del while
            }

            // Aumentamos contador
            intentosPartida++

            // Comprobación
            when {
                numeroUsuario < numeroSecreto -> println("Es MAYOR que $numeroUsuario.")
                numeroUsuario > numeroSecreto -> println("Es MENOR que $numeroUsuario.")
                else -> {
                    adivinado = true
                    println("\n¡CORRECTO! El número era $numeroSecreto.")
                    println("Te ha tomado $intentosPartida intentos.")
                }
            }
        }

        // Gestión del récord al finalizar la partida
        if (intentosPartida < recordPersonal) {
            recordPersonal = intentosPartida
            println("¡NUEVO RÉCORD! Eres increíble.")
        } else {
            println("Tu mejor marca sigue siendo: $recordPersonal intentos.")
        }

        // Preguntar si quiere jugar otra vez
        print("\n¿Quieres jugar otra vez? (S/N): ")
        val respuesta = scanner.next()

        // equalsIgnoreCase en Java -> equals(..., ignoreCase = true) en Kotlin
        if (!respuesta.equals("S", ignoreCase = true)) {
            seguirJugando = false
            println("Gracias por jugar. ¡Hasta la próxima!")
        }
    }
}
