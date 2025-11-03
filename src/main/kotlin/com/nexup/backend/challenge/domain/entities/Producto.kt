package com.nexup.backend.challenge.domain.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "producto")

class Producto(

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(name = "nombre", nullable = false)
    var nombre: String = "",

    @Column(name = "precio", nullable = false)
    var precio: Double = 0.0,

)