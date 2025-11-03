package com.nexup.backend.challenge.infrastructure.repository

import com.nexup.backend.challenge.domain.entities.Producto
import org.springframework.data.jpa.repository.JpaRepository

interface ProductoRepository : JpaRepository<Producto, Long> {
}