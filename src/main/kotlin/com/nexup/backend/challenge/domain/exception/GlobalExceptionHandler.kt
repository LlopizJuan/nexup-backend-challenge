package com.nexup.backend.challenge.domain.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler {

    // 404 Not_Found
    @ExceptionHandler(
        SupermercadoNotFoundException::class,
        ProductoNotFoundException::class,
        ProductoNoDisponibleException::class,
        NoHayVentasRegistradasException::class
    )
    fun handleNotFound(ex: RuntimeException): ResponseEntity<String> =
        ResponseEntity(ex.message ?: "Recurso no encontrado", HttpStatus.NOT_FOUND)

    // 400: stock insuficiente
    @ExceptionHandler(StockInsuficienteException::class)
    fun handleStockInsuficiente(ex: StockInsuficienteException): ResponseEntity<String> =
        ResponseEntity(ex.message ?: "Stock insuficiente", HttpStatus.BAD_REQUEST)

    // 400: body faltante/mal formado
    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleBodyMissing(ex: HttpMessageNotReadableException): ResponseEntity<String> =
        ResponseEntity("Cuerpo de la solicitud inválido o ausente", HttpStatus.BAD_REQUEST)

    // fallback 500
    @ExceptionHandler(Exception::class)
    fun handleGeneral(ex: Exception): ResponseEntity<String> =
        ResponseEntity(ex.message ?: "Error interno del servidor", HttpStatus.INTERNAL_SERVER_ERROR)
}
