package com.nexup.backend.challenge.application.service

import com.nexup.backend.challenge.domain.entities.Venta
import com.nexup.backend.challenge.domain.exception.ProductoNoDisponibleException
import com.nexup.backend.challenge.domain.exception.ProductoNotFoundException
import com.nexup.backend.challenge.domain.exception.StockInsuficienteException
import com.nexup.backend.challenge.domain.exception.SupermercadoNotFoundException
import com.nexup.backend.challenge.infrastructure.repository.ProductoRepository
import com.nexup.backend.challenge.infrastructure.repository.StockRepository
import com.nexup.backend.challenge.infrastructure.repository.SupermercadoRepository
import com.nexup.backend.challenge.infrastructure.repository.VentaRepository
import org.springframework.stereotype.Service

@Service
class SupermercadoService(
    private val ventaRepository: VentaRepository,
    private val supermercadoRepository: SupermercadoRepository,
    private val productoRepository: ProductoRepository,
    private val stockRepository: StockRepository
) {

    fun registrarVenta(supermercadoId: Long, productoId: Long, cantidad: Int): Double {
        val supermercado = supermercadoRepository.findById(supermercadoId)
            .orElseThrow{SupermercadoNotFoundException("Supermercado no encontrado")}

        val producto =
            productoRepository.findById(productoId).orElseThrow{ ProductoNotFoundException("Producto no encontrado") }

        val stock = stockRepository.findBySupermercadoAndProducto(supermercado, producto)
            ?: throw ProductoNoDisponibleException("Producto no disponble en el stock de este supermercado")

        if (stock.cantidad < cantidad) throw StockInsuficienteException("Stock insuficiente")

        val venta = Venta(
            supermercado = supermercado,
            producto = producto,
            cantidadVendida = cantidad,
            precioUnitario = producto.precio
        )
        stock.cantidad -= cantidad

        stockRepository.save(stock)
        ventaRepository.save(venta)
        return venta.precioTotal()
    }

    fun obtenerCantidadVendida(supermercadoId: Long, productoId: Long): Int {
        validarSupermercadoYProducto(supermercadoId, productoId)
        return ventaRepository.findBySupermercadoIdAndProductoId(supermercadoId, productoId)
            .sumOf { it.cantidadVendida }
    }

    fun obtenerIngresosPorProducto(supermercadoId: Long, productoId: Long): Double {
        validarSupermercadoYProducto(supermercadoId, productoId)
        return ventaRepository.findBySupermercadoIdAndProductoId(supermercadoId, productoId)
            .sumOf { it.precioTotal() }
    }

    fun obtenerIngresosTotales(supermercadoId: Long): Double {
        validarSupermercado(supermercadoId)
        return ventaRepository.findBySupermercadoId(supermercadoId)
            .sumOf { it.precioTotal() }
    }

    private fun validarSupermercado(supermercadoId: Long) {
        if (!supermercadoRepository.existsById(supermercadoId)) {
            throw SupermercadoNotFoundException("Supermercado no encontrado")
        }
    }

    private fun validarSupermercadoYProducto(supermercadoId: Long, productoId: Long) {
        val supermercado = supermercadoRepository.findById(supermercadoId)
            .orElseThrow { SupermercadoNotFoundException("Supermercado no encontrado") }

        productoRepository.findById(productoId)
            .orElseThrow { ProductoNotFoundException("Producto no encontrado") }

        val stock =
            stockRepository.findBySupermercadoAndProducto(supermercado, productoRepository.getReferenceById(productoId))
        if (stock == null) {
            throw ProductoNoDisponibleException("Producto no disponible en este supermercado")
        }
    }
}
