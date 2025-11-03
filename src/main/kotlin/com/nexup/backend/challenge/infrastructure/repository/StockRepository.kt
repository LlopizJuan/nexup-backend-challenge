package com.nexup.backend.challenge.infrastructure.repository

import com.nexup.backend.challenge.domain.entities.Producto
import com.nexup.backend.challenge.domain.entities.Stock
import com.nexup.backend.challenge.domain.entities.Supermercado
import org.springframework.data.jpa.repository.JpaRepository

interface StockRepository : JpaRepository<Stock, Long> {
    fun findBySupermercadoAndProducto(supermercado: Supermercado, producto: Producto): Stock?
}