package com.nexup.backend.challenge.infrastructure.repository

import com.nexup.backend.challenge.domain.entities.Venta
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface VentaRepository : JpaRepository<Venta, Long> {

    fun findBySupermercadoId(supermercadoId: Long): List<Venta>

    fun findBySupermercadoIdAndProductoId(supermercadoId: Long, productoId: Long): List<Venta>

    @Query("SELECT v.producto.id, SUM(v.cantidadVendida) FROM Venta v GROUP BY v.producto.id ORDER BY SUM(v.cantidadVendida) DESC")
    fun findTop5ProductosMasVendidos(): List<Array<Any>>

    @Query("SELECT v.supermercado.id, SUM(v.cantidadVendida * v.precioUnitario) FROM Venta v GROUP BY v.supermercado.id")
    fun findIngresosPorSupermercado(): List<Array<Any>>

    @Query("SELECT SUM(v.cantidadVendida * v.precioUnitario) FROM Venta v")
    fun calcularIngresosTotales(): Double?
}
