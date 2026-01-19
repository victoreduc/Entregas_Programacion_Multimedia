package u1comienzo.segundapractica

/*
Ejercicio 4: Analizador de textos con estadísticas (Ejercicio3.kt)
Objetivo: Analizar un texto y generar estadísticas detalladas sobre su contenido.
*/

import java.util.Scanner

data class Contacto(
    val nombre: String,
    val telefono: String,
    val email: String,
    var esFavorito: Boolean = false
)

class GestorContactos {
    // Usamos MutableList porque es como un ArrayList de Java
    private val agenda: MutableList<Contacto> = mutableListOf()

    // Regex para validaciones (Estándar habitual en Java/Kotlin)
    private val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
    private val telefonoRegex = "^[0-9]{9,15}$".toRegex() // 9 a 15 dígitos

    /**
     * Crea y valida un contacto.
     * Retorna un Result indicando éxito o la razón del fallo.
     */
    fun agregarContacto(nombre: String, telefono: String, email: String): Result<Contacto> {
        // Validaciones previas
        if (nombre.isBlank()) {
            return Result.failure(IllegalArgumentException("El nombre no puede estar vacío."))
        }
        if (!telefono.matches(telefonoRegex)) {
            return Result.failure(IllegalArgumentException("El teléfono no es válido (solo números, 9-15 dígitos)."))
        }
        if (!email.matches(emailRegex)) {
            return Result.failure(IllegalArgumentException("El formato del email es incorrecto."))
        }

        // Comprobar duplicados (simulando un Set por teléfono)
        val existe = agenda.any { it.telefono == telefono }
        if (existe) {
            return Result.failure(Exception("Ya existe un contacto con ese teléfono."))
        }

        // Crear y guardar
        val nuevoContacto = Contacto(nombre, telefono, email)
        agenda.add(nuevoContacto)
        return Result.success(nuevoContacto)
    }

    /**
     * Busca contactos que contengan el texto (Case insensitive)
     */
    fun buscarPorNombre(busqueda: String): List<Contacto> {
        // En Java usaría un stream().filter()... en Kotlin es más directo
        return agenda.filter { it.nombre.contains(busqueda, ignoreCase = true) }
    }

    /**
     * Retorna todos los contactos ordenados alfabéticamente
     */
    fun obtenerTodosOrdenados(): List<Contacto> {
        return agenda.sortedBy { it.nombre }
    }

    /**
     * Filtra solo los favoritos
     */
    fun obtenerFavoritos(): List<Contacto> {
        return agenda.filter { it.esFavorito }
    }

    /**
     * Elimina un contacto por teléfono (CRUD: Delete)
     */
    fun eliminarContacto(telefono: String): Result<Boolean> {
        val eliminado = agenda.removeIf { it.telefono == telefono }
        return if (eliminado) Result.success(true) else Result.failure(Exception("No se encontró el contacto."))
    }

    /**
     * Cambiar estado de favorito (CRUD: Update parcial)
     */
    fun toggleFavorito(telefono: String): Result<Boolean> {
        val contacto = agenda.find { it.telefono == telefono }
            ?: return Result.failure(Exception("Contacto no encontrado."))

        contacto.esFavorito = !contacto.esFavorito
        return Result.success(contacto.esFavorito)
    }
}

fun main() {
    val scanner = Scanner(System.`in`) // Costumbre de Java, aunque readln() también sirve
    val gestor = GestorContactos()
    var salir = false

    println("=== GESTOR DE CONTACTOS (Kotlin Ed.) ===")

    while (!salir) {
        mostrarMenu()

        try {
            print("\nSeleccione una opción: ")
            // Usamos toInt() que puede lanzar NumberFormatException, lo capturamos abajo
            val opcion = scanner.nextLine().toInt()

            when (opcion) {
                1 -> vistaAgregarContacto(scanner, gestor)
                2 -> vistaBuscarContacto(scanner, gestor)
                3 -> vistaListarTodos(gestor)
                4 -> vistaListarFavoritos(gestor)
                5 -> vistaEliminarContacto(scanner, gestor)
                6 -> vistaToggleFavorito(scanner, gestor)
                0 -> {
                    println("Saliendo del sistema...")
                    salir = true
                }
                else -> println("Opción no válida. Intente de nuevo.")
            }
        } catch (e: NumberFormatException) {
            println("Error: Debe introducir un número entero.")
        } catch (e: Exception) {
            println("Error inesperado: ${e.message}")
        }

        if (!salir) {
            println("----------------------------------------")
        }
    }
}

fun mostrarMenu() {
    println("\n1. Nuevo Contacto")
    println("2. Buscar por Nombre")
    println("3. Ver todos (Alfabético)")
    println("4. Ver Favoritos")
    println("5. Eliminar Contacto")
    println("6. Marcar/Desmarcar Favorito")
    println("0. Salir")
}

fun vistaAgregarContacto(scanner: Scanner, gestor: GestorContactos) {
    println("\n--- Nuevo Contacto ---")
    print("Nombre: ")
    val nombre = scanner.nextLine()
    print("Teléfono: ")
    val telefono = scanner.nextLine()
    print("Email: ")
    val email = scanner.nextLine()

    // Manejo de Result<T> con fold (estilo funcional que me está gustando)
    val resultado = gestor.agregarContacto(nombre, telefono, email)

    resultado.onSuccess { c ->
        println("Contacto guardado: ${c.nombre}")
    }.onFailure { error ->
        println("Error al guardar: ${error.message}")
    }
}

fun vistaBuscarContacto(scanner: Scanner, gestor: GestorContactos) {
    print("Introduzca nombre a buscar: ")
    val busqueda = scanner.nextLine()
    val resultados = gestor.buscarPorNombre(busqueda)

    if (resultados.isEmpty()) {
        println("No se encontraron coincidencias.")
    } else {
        println("Resultados encontrados (${resultados.size}):")
        imprimirLista(resultados)
    }
}

fun vistaListarTodos(gestor: GestorContactos) {
    println("\n--- Agenda Completa ---")
    val lista = gestor.obtenerTodosOrdenados()
    if (lista.isEmpty()) println("La agenda está vacía.") else imprimirLista(lista)
}

fun vistaListarFavoritos(gestor: GestorContactos) {
    println("\n--- Favoritos ---")
    val lista = gestor.obtenerFavoritos()
    if (lista.isEmpty()) println("No tienes favoritos.") else imprimirLista(lista)
}

fun vistaEliminarContacto(scanner: Scanner, gestor: GestorContactos) {
    print("Ingrese el teléfono del contacto a eliminar: ")
    val telefono = scanner.nextLine()

    gestor.eliminarContacto(telefono)
        .onSuccess { println("Contacto eliminado correctamente.") }
        .onFailure { println("No se pudo eliminar: ${it.message}") }
}

fun vistaToggleFavorito(scanner: Scanner, gestor: GestorContactos) {
    print("Ingrese el teléfono del contacto: ")
    val telefono = scanner.nextLine()

    gestor.toggleFavorito(telefono)
        .onSuccess { esFav ->
            val estado = if (esFav) "Favorito" else "Normal"
            println("Estado actualizado a: $estado")
        }
        .onFailure { println("Error: ${it.message}") }
}

fun imprimirLista(contactos: List<Contacto>) {
    contactos.forEach { c ->
        val estrella = if (c.esFavorito) " " else ""
        println("$estrella [${c.nombre}] - Tlf: ${c.telefono} - Email: ${c.email}")
    }
}
