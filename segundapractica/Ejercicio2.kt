package u1comienzo.segundapractica

/*
Ejercicio 2: Conversor de temperaturas con validación (Ejercicio2.kt)
Objetivo: Crear un conversor que traduce entre escalas de temperatura con
validación robusta de entrada.
*/

// Definición de excepciones personalizadas
class TemperaturaFisicamenteImposibleException(msg: String): Exception(msg)

// Data class para guardar historial
data class Conversion(val entrada: Double, val origen: String, val destino: String, val resultado: Double)

// Validar si la temperatura es posible en Celsius y Kelvin
fun validarTemperatura(valor: Double, escala: String): Result<Double> {
    return when(escala.lowercase()) {
        "celsius" -> if(valor >= -273.15) Result.success(valor)
        else Result.failure(TemperaturaFisicamenteImposibleException("No existen temperaturas menores a -273.15°C"))
        "kelvin"  -> if(valor >= 0.0) Result.success(valor)
        else Result.failure(TemperaturaFisicamenteImposibleException("No existen temperaturas menores a 0K"))
        else -> Result.failure(IllegalArgumentException("Escala desconocida. Use 'Celsius' o 'Kelvin'."))
    }
}

// Celsius a Fahrenheit
fun celsiusAFahrenheit(celsius: Double): Double = celsius * 9.0 / 5.0 + 32.0

// Kelvin a Celsius
fun kelvinACelsius(kelvin: Double): Double = kelvin - 273.15

// Fahrenheit a Celsius
fun fahrenheitACelsius(f: Double): Double = (f - 32.0) * 5.0 / 9.0

// Celsius a Kelvin
fun celsiusAKelvin(c: Double): Double = c + 273.15

// Conversor genérico con validación y manejo seguro de errores
fun convertir(valor: Double, origen: String, destino: String): Result<Double> {
    // Validación de temperatura física posible según la escala de entrada
    return validarTemperatura(valor, origen).mapCatching {
        when {
            origen.equals("Celsius", true) && destino.equals("Fahrenheit", true) ->
                celsiusAFahrenheit(it)
            origen.equals("Kelvin", true)  && destino.equals("Celsius", true)     ->
                kelvinACelsius(it)
            // Conversiones extra
            origen.equals("Fahrenheit", true) && destino.equals("Celsius", true) ->
                fahrenheitACelsius(it)
            origen.equals("Celsius", true) && destino.equals("Kelvin", true)     ->
                celsiusAKelvin(it)
            else -> throw IllegalArgumentException("Conversión no soportada.")
        }
    }
}

// Alerta de temperatura extrema
fun alertaTemperatura(valor: Double, escala: String): String? {
    return when (escala.lowercase()) {
        "celsius"  -> if (valor < -100 || valor > 100) "¡Temperatura extrema detectada!" else null
        "kelvin"   -> if (valor < 100 || valor > 500) "¡Temperatura atípica detectada!" else null
        "fahrenheit" -> if (valor < -148 || valor > 212) "¡Temperatura extrema detectada!" else null
        else -> null
    }
}

// Menú interactivo con bucle y manejo seguro de errores
fun menuInteractivo() {
    val historial = mutableListOf<Conversion>()
    println("=== Conversor de Temperaturas ===")
    while (true) {
        println("\nOpciones:")
        println("1. Celsius a Fahrenheit")
        println("2. Kelvin a Celsius")
        println("3. Fahrenheit a Celsius (extra)")
        println("4. Celsius a Kelvin (extra)")
        println("5. Ver historial")
        println("0. Salir")
        print("Elige una opción: ")
        val opcion = readLine()
        if (opcion == "0") break

        if (opcion == "5") {
            println("--- Historial ---")
            historial.forEach { println("${it.entrada} ${it.origen} → ${it.resultado} ${it.destino}") }
            continue
        }

        // Solicitar valor y escalas según opción
        print("Introduce el valor de temperatura: ")
        val valorInput = readLine()
        val valor = valorInput?.toDoubleOrNull()
        if (valor == null) {
            println("Error: valor numérico inválido.")
            continue
        }

        // Determinar escalas según opción
        val origen: String
        val destino: String
        when (opcion) {
            "1" -> { origen = "Celsius"; destino = "Fahrenheit" }
            "2" -> { origen = "Kelvin"; destino = "Celsius" }
            "3" -> { origen = "Fahrenheit"; destino = "Celsius" }
            "4" -> { origen = "Celsius"; destino = "Kelvin" }
            else -> { println("Opción inválida."); continue }
        }

        // Intentar conversión
        val resultado = convertir(valor, origen, destino)
        resultado
            .onSuccess {
                println("Resultado: $it $destino")
                alertaTemperatura(it, destino)?.let { alerta -> println(alerta) }
                historial.add(Conversion(valor, origen, destino, it))
            }
            .onFailure {
                println("Error: ${it.message}")
            }
    }
    println("¡Saliendo del conversor!")
}

// Prueba principal
fun main() {
    menuInteractivo()
}
