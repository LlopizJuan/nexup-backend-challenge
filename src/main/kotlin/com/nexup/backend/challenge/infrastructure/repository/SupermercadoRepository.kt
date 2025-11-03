package com.nexup.backend.challenge.infrastructure.repository

import com.nexup.backend.challenge.domain.entities.Supermercado
import org.springframework.data.jpa.repository.JpaRepository

interface SupermercadoRepository : JpaRepository<Supermercado, Long> {
}