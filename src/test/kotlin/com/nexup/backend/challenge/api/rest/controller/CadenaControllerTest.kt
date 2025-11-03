package com.nexup.backend.challenge.api.rest.controller

import com.nexup.backend.challenge.domain.entities.Producto
import com.nexup.backend.challenge.domain.entities.Stock
import com.nexup.backend.challenge.domain.entities.Supermercado
import com.nexup.backend.challenge.domain.entities.Venta
import com.nexup.backend.challenge.infrastructure.repository.ProductoRepository
import com.nexup.backend.challenge.infrastructure.repository.StockRepository
import com.nexup.backend.challenge.infrastructure.repository.SupermercadoRepository
import com.nexup.backend.challenge.infrastructure.repository.VentaRepository
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.DayOfWeek
import java.time.LocalTime
import kotlin.test.Test

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CadenaControllerTests(
    @Autowired val mockMvc: MockMvc,
    @Autowired val supermercadoRepository: SupermercadoRepository,
    @Autowired val productoRepository: ProductoRepository,
    @Autowired val stockRepository: StockRepository,
    @Autowired val ventaRepository: VentaRepository
) {

    @BeforeEach
    fun setUp() {
        // LIMPIEZA
        ventaRepository.deleteAll()
        stockRepository.deleteAll()
        productoRepository.deleteAll()
        supermercadoRepository.deleteAll()

        val productos = productoRepository.saveAll(
            listOf(
                Producto(nombre = "Pan", precio = 2.0),
                Producto(nombre = "Leche", precio = 2.0),
                Producto(nombre = "Cereal", precio = 2.0),
                Producto(nombre = "Huevos", precio = 2.0),
                Producto(nombre = "Jugo de Naranja", precio = 2.0),
            )
        )

        val supermercado1 = supermercadoRepository.save(
            Supermercado(
                nombre = "Supermercado Central",
                horarioApertura = LocalTime.of(8, 0),
                horarioCierre = LocalTime.of(20, 0),
                diasAbiertos = mutableSetOf(
                    DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                    DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY
                )
            )
        )

        val supermercado2 = supermercadoRepository.save(
            Supermercado(
                nombre = "Supermercado Norte",
                horarioApertura = LocalTime.of(9, 0),
                horarioCierre = LocalTime.of(22, 0),
                diasAbiertos = mutableSetOf(
                    DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                    DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SUNDAY
                )
            )
        )

        // STOCK (por si tu servicio lo consulta)
        stockRepository.saveAll(
            listOf(
                // Central
                Stock(supermercado = supermercado1, producto = productos[0], cantidad = 100),
                Stock(supermercado = supermercado1, producto = productos[1], cantidad = 100),
                Stock(supermercado = supermercado1, producto = productos[2], cantidad = 100),
                Stock(supermercado = supermercado1, producto = productos[3], cantidad = 100),
                Stock(supermercado = supermercado1, producto = productos[4], cantidad = 100),

                // Norte
                Stock(supermercado = supermercado2, producto = productos[0], cantidad = 100),
                Stock(supermercado = supermercado2, producto = productos[1], cantidad = 100),
                Stock(supermercado = supermercado2, producto = productos[2], cantidad = 100),
                Stock(supermercado = supermercado2, producto = productos[3], cantidad = 100),
                Stock(supermercado = supermercado2, producto = productos[4], cantidad = 100),
            )
        )


        //
        // Cantidades objetivo (Top 5 global):
        // Pan 55, Leche 40, Cereal 30, Huevos 27, Jugo de Naranja 25
        //
        // Distribución por super (igual que tus ejemplos originales):
        // Central: Pan 30, Leche 15, Cereal 10, Huevos 12, Jugo 10  -> 77 u.
        // Norte:   Pan 25, Leche 25, Cereal 20, Huevos 15, Jugo 15  -> 100 u.
        //
        // Objetivo de ingresos exactos:
        // - Total cadena = 406.25
        // - Central = 206.25
        // - Norte = 200.00
        //
        // Construcción de precios por venta (múltiplos de 0.25 para evitar FP):
        // Base 2.00 en todas las ventas + ajustes en CENTRAL:
        //  - Pan (30 u): 2.75  -> 30 * 2.75 = 82.50
        //  - Leche (15 u): 2.75 -> 15 * 2.75 = 41.25
        //  - Cereal (10 u): 3.00 -> 10 * 3.00 = 30.00
        //  - Huevos (12 u): 2.50 -> 12 * 2.50 = 30.00
        //  - Jugo (10 u): 2.25 -> 10 * 2.25 = 22.50
        // Central = 206.25 exacto
        //
        // NORTE: todas a 2.00 exacto -> 100 * 2.00 = 200.00
        //
        // TOTAL = 206.25 + 200.00 = 406.25 exacto

        ventaRepository.saveAll(
            listOf(
                // CENTRAL
                Venta(null, supermercado1, productos[0], 30, 2.75), // Pan 30
                Venta(null, supermercado1, productos[1], 15, 2.75), // Leche 15
                Venta(null, supermercado1, productos[2], 10, 3.00), // Cereal 10
                Venta(null, supermercado1, productos[3], 12, 2.50), // Huevos 12
                Venta(null, supermercado1, productos[4], 10, 2.25), // Jugo 10

                // NORTE
                Venta(null, supermercado2, productos[0], 25, 2.00), // Pan 25
                Venta(null, supermercado2, productos[1], 25, 2.00), // Leche 25
                Venta(null, supermercado2, productos[2], 20, 2.00), // Cereal 20
                Venta(null, supermercado2, productos[3], 15, 2.00), // Huevos 15
                Venta(null, supermercado2, productos[4], 15, 2.00), // Jugo 15
            )
        )
    }

    @Test
    fun `devuelve top 5 de productos`() {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/cadena/top5-productos"))
            .andExpect(status().isOk)
            .andExpect(
                MockMvcResultMatchers.content().string(
                    "Pan: 55 - Leche: 40 - Cereal: 30 - Huevos: 27 - Jugo de Naranja: 25"
                )
            )
    }

    @Test
    fun `devuelve ingresos totales de la cadena`() {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/cadena/ingresos"))
            .andExpect(status().isOk)
            .andExpect(MockMvcResultMatchers.content().string("406.25"))
    }

    @Test
    fun `devuelve supermercado con mas ingresos`() {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/cadena/mejor-supermercado"))
            .andExpect(status().isOk)
            .andExpect(
                MockMvcResultMatchers.content().string(
                    "Supermercado Central (1). Ingresos totales: 206,25"
                )
            )
    }

    @Test
    fun `devuelve supermercados abiertos`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/cadena/abiertos")
                .param("dia", "MONDAY")
                .param("hora", "10:00")
        )
            .andExpect(status().isOk)
            .andExpect(
                MockMvcResultMatchers.content().string(
                    "Supermercado Central (1), Supermercado Norte (2)"
                )
            )
    }
}
