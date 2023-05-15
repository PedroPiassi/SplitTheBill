package br.edu.ifsp.scl.ads.pdm.splitthebill.controller

import androidx.room.Room
import br.edu.ifsp.scl.ads.pdm.splitthebill.modal.Person
import br.edu.ifsp.scl.ads.pdm.splitthebill.modal.PersonDao
import br.edu.ifsp.scl.ads.pdm.splitthebill.modal.PersonDaoRoom
import br.edu.ifsp.scl.ads.pdm.splitthebill.view.MainActivity

class PersonController(private val mainActivity: MainActivity) {
    private val personDaoImpl: PersonDao = Room.databaseBuilder(
        mainActivity,
        PersonDaoRoom::class.java,
        PersonDaoRoom.PERSON_DATABASE_FILE
    ).build().getPersonDao()

    fun insert(person: Person) {
        Thread {
            personDaoImpl.create(person)
            getPeople()
        }.start()
    }

    fun getPerson(id: Int) = personDaoImpl.findOne(id)
    fun getPeople() {
        Thread {
            val person = personDaoImpl.findAll()
            mainActivity.runOnUiThread {
                mainActivity.updatePersonList(person)
            }
        }.start()
    }

    fun editPerson(person: Person) {
        Thread {
            personDaoImpl.update(person)
            getPeople()
        }.start()
    }

    fun deletePerson(person: Person) {
        Thread {
            personDaoImpl.delete(person)
            getPeople()
        }.start()
    }
}