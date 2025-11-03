package com.nexup.backend.challenge.domain.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "venta")
class Venta(

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supermercado_id", nullable = false)
    var supermercado: Supermercado,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    var producto: Producto,

    @Column(nullable = false)
    var cantidadVendida: Int,

    @Column(nullable = false)
    var precioUnitario: Double,

    @Column(nullable = false)
    var fecha: LocalDateTime = LocalDateTime.now()
) {
    fun precioTotal(): Double = cantidadVendida * precioUnitario
}