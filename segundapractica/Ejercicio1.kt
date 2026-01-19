package u1comienzo.segundapractica

/*
Ejercicio 1: Sistema de gestión de biblioteca (Ejercicio1.kt)
Objetivo: Crear un sistema para gestionar libros en una biblioteca con búsqueda y
filtrado avanzado.
*/

// Data class para Libro
data class Libro(
    val titulo: String,
    val autor: String,
    val año: Int,
    val disponible: Boolean
)

// Lista inicial de libros
val libros = mutableListOf(
    Libro("Cien años de soledad", "Gabriel García Márquez", 1967, true),
    Libro("El Principito", "Antoine de Saint-Exupéry", 1943, false),
    Libro("Rayuela", "Julio Cortázar", 1963, true),
    Libro("Don Quijote", "Miguel de Cervantes", 1605, true),
    Libro("La sombra del viento", "Carlos Ruiz Zafón", 2001, false)
)

// Buscar libros por autor (insensible a mayúsculas)
fun buscarPorAutor(autor: String): List<Libro> =
    libros.filter { it.autor.lowercase().contains(autor.lowercase()) }

// Buscar libros por rango de años (ambos inclusive)
fun buscarPorAño(desde: Int, hasta: Int): List<Libro> =
    libros.filter { it.año in desde..hasta }

// Obtener solo los libros disponibles
fun librosDisponibles(): List<Libro> =
    libros.filter { it.disponible }

// Búsqueda por título (insensible a mayúsculas)
fun buscarPorTitulo(titulo: String): List<Libro> =
    libros.filter { it.titulo.lowercase().contains(titulo.lowercase()) }

// Mostrar libros ordenados por año
fun librosOrdenadosPorAño(): List<Libro> =
    libros.sortedBy { it.año }

// Agregar nuevo libro
fun agregarLibro(nuevo: Libro) {
    libros.add(nuevo)
}

// Estadísticas de la biblioteca
fun calcularEstadisticas() {
    val total = libros.size
    val disponibles = librosDisponibles().size
    val porAutor = libros.groupBy { it.autor }
    val autorConMasLibros = porAutor.maxByOrNull { it.value.size }?.key // Manejo seguro de nulls
    println("Total de libros: $total")
    println("Libros disponibles: $disponibles")
    println("Libros por autor:")
    porAutor.forEach { (autor, lista) -> println(" - $autor: ${lista.size}") }
    println("Autor con más libros: ${autorConMasLibros ?: "Ninguno"}")
}

// Pruebas de las funciones
fun main() {
    println("Buscar por autor 'Julio': ${buscarPorAutor("Julio")}")
    println("Buscar por año 1940-1970: ${buscarPorAño(1940, 1970)}")
    println("Libros disponibles: ${librosDisponibles()}")
    println("Buscar por título 'Principito': ${buscarPorTitulo("Principito")}")
    println("Libros ordenados por año: ${librosOrdenadosPorAño()}")
    agregarLibro(Libro("Pedro Páramo", "Juan Rulfo", 1955, true))
    calcularEstadisticas()
}
