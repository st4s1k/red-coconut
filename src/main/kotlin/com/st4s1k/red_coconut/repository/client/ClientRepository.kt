package com.st4s1k.red_coconut.repository.client

import com.st4s1k.red_coconut.model.client.Client
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.query.Param
import org.springframework.data.rest.core.annotation.RepositoryRestResource

@Suppress("unused")
@RepositoryRestResource(collectionResourceRel = "clients", path = "clients")
interface ClientRepository : JpaRepository<Client, Long> {

    fun findByUsername(@Param("username") username: String): Client?

    fun existsByUsername(@Param("username") username: String): Boolean

}
