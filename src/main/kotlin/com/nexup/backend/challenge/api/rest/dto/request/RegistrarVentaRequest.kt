package com.nexup.backend.challenge.api.rest.dto.request

data class RegistrarVentaRequest(
    val productoId: Long,
    val cantidad: Int
)
