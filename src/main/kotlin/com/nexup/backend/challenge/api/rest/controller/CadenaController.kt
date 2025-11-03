package com.nexup.backend.challenge.api.rest.controller

import com.nexup.backend.challenge.application.service.CadenaService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.DayOfWeek
import java.time.LocalTime

@RestController
@RequestMapping("/api/cadena")
class CadenaController(
    private val cadenaService: CadenaService
) {

    @GetMapping("/top5-productos")
    fun top5MasVendidos(): ResponseEntity<String> {
        val resultado = cadenaService.obtenerTop5ProductosMasVendidos()
        return ResponseEntity.ok(resultado)
    }

    @GetMapping("/ingresos")
    fun ingresosTotales(): ResponseEntity<Double> {
        val ingresos = cadenaService.obtenerIngresosTotales()
        return ResponseEntity.ok(ingresos)
    }

    @GetMapping("/mejor-supermercado")
    fun supermercadoConMasIngresos(): ResponseEntity<String> {
        val resultado = cadenaService.supermercadoConMasIngresos()
        return ResponseEntity.ok(resultado)
    }

    @GetMapping("/abiertos")
    fun supermercadosAbiertos(
        @RequestParam dia: DayOfWeek,
        @RequestParam hora: String // en formato "HH:mm"
    ): ResponseEntity<String> {
        val localTime = LocalTime.parse(hora)
        val abiertos = cadenaService.supermercadosAbiertosEn(dia, localTime)
        return ResponseEntity.ok(abiertos)
    }
}
