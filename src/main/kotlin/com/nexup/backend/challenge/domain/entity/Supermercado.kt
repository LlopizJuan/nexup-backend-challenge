package com.nexup.backend.challenge.domain.entity

import jakarta.persistence.*
import java.time.DayOfWeek
import java.time.LocalTime

@Entity
@Table(name = "supermercado")
class Supermercado(

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val nombre: String,

    @OneToMany(
        mappedBy = "supermercado",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    val stock: MutableList<Stock> = mutableListOf(),

    @Column(nullable = false)
    val horarioApertura: LocalTime,

    @Column(nullable = false)
    val horarioCierre: LocalTime,

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "supermercado_dia_abierto", joinColumns = [JoinColumn(name = "supermercado_id")])
    @Column(name = "dia", nullable = false)
    @Enumerated(EnumType.STRING)
    val diasAbiertos: MutableSet<DayOfWeek> = mutableSetOf()

)