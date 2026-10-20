package com.example.appcimuc.repository

import com.example.appcimuc.model.Capacidad
import com.example.appcimuc.model.Categoria
import com.example.appcimuc.model.Integrante
import com.example.appcimuc.model.Proyecto

// Muestra completamente sintética; no representa proyectos ni personas reales de CIM UC.
internal object CatalogoEjemplo {
    val categorias = listOf(
        Categoria("cat-materiales", "Materiales"),
        Categoria("cat-procesos", "Procesos")
    )
    val integrantes = listOf(
        Integrante("int-ana", "Ana Ejemplo", "Integrante ficticia dedicada al estudio de materiales."),
        Integrante("int-luis", "Luis Ejemplo", "Integrante ficticio dedicado al análisis de procesos.")
    )
    val capacidades = listOf(
        Capacidad("cap-ensayos", "Ensayos de materiales", "Capacidad de ejemplo para caracterizar propiedades de materiales."),
        Capacidad("cap-modelado", "Modelado de procesos", "Capacidad de ejemplo para comparar escenarios de un proceso.")
    )
    val proyectos = listOf(
        Proyecto("proy-reciclaje", "Materiales reciclados", "Estudio sintético de mezclas recicladas y sus propiedades.",
            "cat-materiales", 2025, listOf("int-ana", "int-luis"), listOf("cap-ensayos"), estado = "Finalizado",
            cuposDisponibles = 0, aceptaPostulaciones = false),
        Proyecto("proy-energia", "Eficiencia de procesos", "Simulación sintética de alternativas para reducir el uso de energía.",
            "cat-procesos", 2026, listOf("int-luis"), listOf("cap-modelado"), estado = "En desarrollo",
            cuposDisponibles = 2, aceptaPostulaciones = true),
        Proyecto("proy-mezclas", "Diseño de mezclas", "Comparación sintética de formulaciones mediante ensayos y modelado.",
            "cat-materiales", 2026, listOf("int-ana"), listOf("cap-ensayos", "cap-modelado"), estado = "En desarrollo",
            cuposDisponibles = 1, aceptaPostulaciones = true)
    )
}
