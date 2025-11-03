package com.nexup.backend.challenge.api.rest.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.nexup.backend.challenge.api.rest.dto.request.RegistrarVentaRequest
import com.nexup.backend.challenge.domain.entities.Producto
import com.nexup.backend.challenge.domain.entities.Stock
import com.nexup.backend.challenge.domain.entities.Supermercado
import com.nexup.backend.challenge.infrastructure.repository.ProductoRepository
import com.nexup.backend.challenge.infrastructure.repository.StockRepository
import com.nexup.backend.challenge.infrastructure.repository.SupermercadoRepository
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
class SupermercadoControllerTests(
    @Autowired val mockMvc: MockMvc,
    @Autowired val supermercadoRepository: SupermercadoRepository,
    @Autowired val productoRepository: ProductoRepository,
    @Autowired val stockRepository: StockRepository
) {

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun setUp() {

        stockRepository.deleteAll()
        productoRepository.deleteAll()
        supermercadoRepository.deleteAll()

        // Semilla mínima para que los tests sean independientes
        val p1 = productoRepository.save(Producto(nombre = "Pan", precio = 10.0)) // id=1
        val p2 = productoRepository.save(Producto(nombre = "Leche", precio = 20.0)) // id=2
        productoRepository.save(Producto(nombre = "Cereal", precio = 30.0))         // id=3

        val sup1 = supermercadoRepository.save(
            Supermercado(
                nombre = "Supermercado Central",             // id=1
                horarioApertura = LocalTime.of(8, 0),
                horarioCierre = LocalTime.of(20, 0),
                diasAbiertos = mutableSetOf(
                    DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                    DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY
                )
            )
        )

        // Stock: en sup 1 hay p1 y p2; NO hay p7 para probar "no disponible"
        stockRepository.saveAll(
            listOf(
                Stock(supermercado = sup1, producto = p1, cantidad = 500),
                Stock(
                    supermercado = sup1,
                    producto = p2,
                    cantidad = 500
                ) // alcanza para 3000? no; para ese test esperamos 400? ajustá a gusto
            )
        )
    }

    @Test
    fun `registrar venta y devolver precio total`() {
        val req = RegistrarVentaRequest(productoId = 2, cantidad = 7) // 7 * 20 = 140
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/supermercados/1/ventas")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(req))
        )
            .andExpect(status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.total").value(140.0))
    }

    @Test
    fun `venta con supermercado inexistente`() {
        val req = RegistrarVentaRequest(productoId = 2, cantidad = 7)
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/supermercados/999/ventas")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(req))
        )
            .andExpect(status().isNotFound)
            .andExpect(MockMvcResultMatchers.content().string("Supermercado no encontrado"))
    }

    @Test
    fun `venta con producto inexistente`() {
        val req = RegistrarVentaRequest(productoId = 999, cantidad = 7)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/supermercados/1/ventas")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(req))
        )
            .andExpect(status().isNotFound)
            .andExpect(MockMvcResultMatchers.content().string("Producto no encontrado"))
    }

    @Test
    fun `venta con stock insuficiente`() {
        val req = RegistrarVentaRequest(productoId = 2, cantidad = 3000)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/supermercados/1/ventas")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(req))
        )
            .andExpect(status().isBadRequest)
            .andExpect(MockMvcResultMatchers.content().string("Stock insuficiente"))
    }

    @Test
    fun `devuelve cantidad vendida`() {
        val req = RegistrarVentaRequest(productoId = 2, cantidad = 7)
        val json = objectMapper.writeValueAsString(req)
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/supermercados/1/ventas")
                .contentType("application/json")
                .content(json)
        ).andExpect(status().isOk)

        mockMvc.perform(MockMvcRequestBuilders.get("/api/supermercados/1/productos/2/cantidad-vendida"))
            .andExpect(status().isOk)
            .andExpect(MockMvcResultMatchers.content().string("7"))
    }

    @Test
    fun `producto no disponible en supermercado`() {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/supermercados/1/productos/7/cantidad-vendida"))
            .andExpect(status().isNotFound)
            .andExpect(MockMvcResultMatchers.content().string("Producto no encontrado"))
    }

    @Test
    fun `ingresos por producto`() {
        val req = RegistrarVentaRequest(productoId = 2, cantidad = 7)
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/supermercados/1/ventas")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(req))
        ).andExpect(status().isOk)

        mockMvc.perform(MockMvcRequestBuilders.get("/api/supermercados/1/productos/2/ingresos"))
            .andExpect(status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$").value(140.0))
    }

    @Test
    fun `ingresos totales supermercado`() {
        val req = RegistrarVentaRequest(productoId = 2, cantidad = 7)
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/supermercados/1/ventas")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(req))
        ).andExpect(status().isOk)

        mockMvc.perform(MockMvcRequestBuilders.get("/api/supermercados/1/ingresos"))
            .andExpect(status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$").value(140.0))
    }
}
