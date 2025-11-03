package com.nexup.backend.challenge.application.service

import com.nexup.backend.challenge.domain.exception.NoHayVentasRegistradasException
import com.nexup.backend.challenge.domain.exception.ProductoNotFoundException
import com.nexup.backend.challenge.domain.exception.SupermercadoNotFoundException
import com.nexup.backend.challenge.infrastructure.repository.ProductoRepository
import com.nexup.backend.challenge.infrastructure.repository.SupermercadoRepository
import com.nexup.backend.challenge.infrastructure.repository.VentaRepository
import org.springframework.stereotype.Service
import java.time.DayOfWeek
import java.time.LocalTime

@Service
class CadenaService(
    private val ventaRepository: VentaRepository,
    private val productoRepository: ProductoRepository,
    private val supermercadoRepository: SupermercadoRepository
) {

    fun obtenerTop5ProductosMasVendidos(): String {
        val resultados = ventaRepository.findTop5ProductosMasVendidos()

        if (resultados.isEmpty()) {
            throw NoHayVentasRegistradasException("No hay ventas registradas en la cadena")
        }

        return resultados
            .take(5)
            .joinToString(" - ") {
                val productoId = it[0] as Long
                val cantidadVendida = it[1] as Long
                val nombre = productoRepository.findById(productoId)
                    .orElseThrow { ProductoNotFoundException("Producto con ID $productoId no encontrado") }
                    .nombre
                "$nombre: $cantidadVendida"
            }
    }

    fun obtenerIngresosTotales(): Double {
        return ventaRepository.calcularIngresosTotales()
            ?: throw NoHayVentasRegistradasException("No se han registrado ventas en la cadena aún")
    }

    fun supermercadoConMasIngresos(): String {
        val ingresos = ventaRepository.findIngresosPorSupermercado()

        if (ingresos.isEmpty()) {
            throw NoHayVentasRegistradasException("No hay ventas en ningún supermercado")
        }

        val maxIngreso = ingresos.maxByOrNull { it[1] as Double }
            ?: throw NoHayVentasRegistradasException("No se pudo determinar el supermercado con mayores ingresos")

        val supermercadoId = maxIngreso[0] as Long
        val total = maxIngreso[1] as Double

        val supermercado = supermercadoRepository.findById(supermercadoId)
            .orElseThrow { SupermercadoNotFoundException("Supermercado con ID $supermercadoId no encontrado") }

        return "${supermercado.nombre} ($supermercadoId). Ingresos totales: %.2f".format(total)
    }

    fun supermercadosAbiertosEn(dia: DayOfWeek, hora: LocalTime): String {
        val supermercados = supermercadoRepository.findAll()

        val abiertos = supermercados.filter {
            dia in it.diasAbiertos &&
                    hora >= it.horarioApertura &&
                    hora <= it.horarioCierre
        }

        if (abiertos.isEmpty()) {
            throw SupermercadoNotFoundException("No hay supermercados abiertos el $dia a las $hora")
        }

        return abiertos.joinToString(", ") {
            "${it.nombre} (${it.id})"
        }
    }
}