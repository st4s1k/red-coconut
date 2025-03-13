package com.st4s1k.red_coconut.model.client

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table

@Entity
@Table(name = "client")
data class Client(

    @Id
    @GeneratedValue(
        generator = "client_id_seq",
        strategy = GenerationType.SEQUENCE
    )
    @SequenceGenerator(
        name = "client_id_seq",
        sequenceName = "client_id_seq",
        allocationSize = 1
    )
    var id: Long?,

    @Column(name = "username", nullable = false)
    var username: String?,

    @Column(name = "first_name", nullable = false)
    var firstName: String?,

    @Column(name = "last_name", nullable = false)
    var lastName: String?

)
