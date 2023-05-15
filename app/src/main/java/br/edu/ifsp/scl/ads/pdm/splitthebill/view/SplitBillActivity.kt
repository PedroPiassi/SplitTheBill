package br.edu.ifsp.scl.ads.pdm.splitthebill.view

import android.os.Build
import android.os.Bundle
import br.edu.ifsp.scl.ads.pdm.splitthebill.adapter.PersonAdapter
import br.edu.ifsp.scl.ads.pdm.splitthebill.databinding.ActivitySplitBillBinding
import br.edu.ifsp.scl.ads.pdm.splitthebill.modal.Person

class SplitBillActivity : BaseActivity() {
    private val asbb: ActivitySplitBillBinding by lazy {
        ActivitySplitBillBinding.inflate(layoutInflater)
    }

    private val personList: MutableList<Person> = mutableListOf()

    private val personAdpter: PersonAdapter by lazy {
        PersonAdapter(this, personList)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(asbb.root)
        supportActionBar?.subtitle = "Racha de conta"

        val receivedPerson = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayListExtra(EXTRA_SPLIT, Person::class.java)
        } else {
            intent.getParcelableArrayListExtra(EXTRA_SPLIT)
        }

        receivedPerson?.let {
            updatePersonList(it, calculateSplitBill(it))
            asbb.personsLv.adapter = personAdpter
        }
    }

    fun updatePersonList(_personList: ArrayList<Person>, value: Float) {
        personList.clear()
        for (person in _personList) {
            person.value = person.value - value
            if (person.value < 0) {
                person.name = person.name.plus(" - a pagar ")
            } else if (person.value > 0) {
                person.name = person.name.plus(" - a receber ")
            } else {
                person.name = person.name.plus(" - tá certo ")
            }
            person.value = Math.abs(person.value)
        }

        personList.addAll(_personList)
        personAdpter.notifyDataSetChanged()
    }

    private fun calculateSplitBill(personList: ArrayList<Person>): Float {
        var total = 0F
        val people = personList
        val qtdd = people.size

        for (person in people) {
            total += person.value
        }
        total = total / qtdd

        return total
    }
}