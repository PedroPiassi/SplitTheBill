package br.edu.ifsp.scl.ads.pdm.splitthebill.modal

import androidx.room.*

@Dao
interface PersonDao {
    @Insert
    fun create(person: Person)
    @Query("SELECT * FROM Person WHERE id = :id")
    fun findOne(id: Int): Person?
    @Query("SELECT * FROM Person")
    fun findAll(): MutableList<Person>
    @Update
    fun update(person: Person) :Int
    @Delete
    fun delete(person: Person):Int
}