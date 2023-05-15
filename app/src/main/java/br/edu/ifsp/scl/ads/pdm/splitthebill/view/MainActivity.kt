package br.edu.ifsp.scl.ads.pdm.splitthebill.view

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import br.edu.ifsp.scl.ads.pdm.splitthebill.modal.Person
import br.edu.ifsp.scl.ads.pdm.splitthebill.R
import br.edu.ifsp.scl.ads.pdm.splitthebill.adapter.PersonAdapter
import br.edu.ifsp.scl.ads.pdm.splitthebill.controller.PersonController
import br.edu.ifsp.scl.ads.pdm.splitthebill.databinding.ActivityMainBinding

class MainActivity : BaseActivity() {
    private val amb: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    //DataSource
    private val personList : MutableList<Person> = mutableListOf()
    //Adapter
    private val personAdpter: PersonAdapter by lazy {
        PersonAdapter(this, personList)
    }
    private lateinit var parl: ActivityResultLauncher<Intent>

    private val personController: PersonController by lazy {
        PersonController(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(amb.root)
        supportActionBar?.subtitle = "Lista de Pessoas"
        personController.getPeople()

        //fillContactList()
        amb.personsLv.adapter = personAdpter

        parl = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result->
            if(result.resultCode == RESULT_OK) {
                val person = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    result.data?.getParcelableExtra("Person",Person::class.java)
                }
                else{
                    result.data?.getParcelableExtra<Person>(EXTRA_PERSON)
                }
                person?.let{_person ->
                    val position = personList.indexOfFirst{it.id == _person.id}
                    if(position != -1){
                        personList[position] = _person
                        personController.editPerson(_person)
                        Toast.makeText(this, "Pessoa atualizada!", Toast.LENGTH_SHORT).show()
                    }
                    else{
                        personController.insert(_person)
                        personController.getPeople()
                        Toast.makeText(this, "Pessoa adicionada!", Toast.LENGTH_SHORT).show()
                    }
                    personAdpter.notifyDataSetChanged()
                }
            }
        }
        registerForContextMenu(amb.personsLv)

        amb.personsLv.setOnItemClickListener(object: AdapterView.OnItemClickListener{
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long){
                val person = personList[position]
                val personIntent = Intent(this@MainActivity, PersonActivity::class.java)
                personIntent.putExtra(EXTRA_PERSON, person)
                personIntent.putExtra(EXTRA_VIEW_PERSON, true)
                parl.launch(personIntent)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.addPersonMi -> {
                parl.launch(Intent(this, PersonActivity::class.java))
                true
            }
            R.id.splitBillMi -> {
                val splitIntent = Intent(this, SplitBillActivity::class.java)
                splitIntent.putParcelableArrayListExtra(EXTRA_SPLIT, ArrayList<Person>(personList))
                parl.launch(splitIntent)
                true
            }
            else ->false
        }
    }
    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        menuInflater.inflate(R.menu.context_menu_main, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val position =(item.menuInfo as AdapterView.AdapterContextMenuInfo).position
        val person = personList[position]
        return when(item.itemId){
            R.id.editPersonMi -> {
                val person = personList[position]
                val personIntent = Intent(this, PersonActivity::class.java)
                personIntent.putExtra(EXTRA_PERSON, person)
                parl.launch(personIntent)
                true
            }
            R.id.removePersonMi -> {

                personList.removeAt(position)
                personController.deletePerson(person)
                personAdpter.notifyDataSetChanged()
                Toast.makeText(this, "Pessoa removida!", Toast.LENGTH_SHORT).show()

                true
            }
            else -> false
        }
    }

    /*private fun fillContactList() {
        for (index in 1..6) {
            personList.add(
                Person(
                    index, "Name $index", 12F, "description $index"
                )
            )
        }
    }*/

    fun updatePersonList(_personList: MutableList<Person>){
        personList.clear()
        personList.addAll(_personList)
        personAdpter.notifyDataSetChanged()
    }
}