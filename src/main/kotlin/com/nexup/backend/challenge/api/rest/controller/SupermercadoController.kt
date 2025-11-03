package com.nexup.backend.challenge.api.rest.controller

import com.nexup.backend.challenge.api.rest.dto.request.RegistrarVentaRequest
import com.nexup.backend.challenge.api.rest.dto.responses.IngresosResponse
import com.nexup.backend.challenge.application.service.SupermercadoService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/supermercados")
class SupermercadoController(
    private val supermercadoService: SupermercadoService
) {

    @PostMapping("/{id}/ventas")
    fun registrarVenta(
        @PathVariable id: Long,
        @RequestBody body: RegistrarVentaRequest
    ): ResponseEntity<IngresosResponse> {
        val total = supermercadoService.registrarVenta(id, body.productoId, body.cantidad)
        return ResponseEntity.ok(IngresosResponse(total))
    }

    @GetMapping("/{id}/productos/{productoId}/cantidad-vendida")
    fun cantidadVendida(
        @PathVariable id: Long,
        @PathVariable productoId: Long
    ): ResponseEntity<Int> {
        val cantidad = supermercadoService.obtenerCantidadVendida(id, productoId)
        return ResponseEntity.ok(cantidad)
    }

    @GetMapping("/{id}/productos/{productoId}/ingresos")
    fun ingresosPorProducto(
        @PathVariable id: Long,
        @PathVariable productoId: Long
    ): ResponseEntity<Double> {
        val ingresos = supermercadoService.obtenerIngresosPorProducto(id, productoId)
        return ResponseEntity.ok(ingresos)
    }

    @GetMapping("/{id}/ingresos")
    fun ingresosTotales(
        @PathVariable id: Long
    ): ResponseEntity<Double> {
        val ingresos = supermercadoService.obtenerIngresosTotales(id)
        return ResponseEntity.ok(ingresos)
    }
}
