package br.edu.ifsp.scl.ads.pdm.splitthebill.view

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import br.edu.ifsp.scl.ads.pdm.splitthebill.databinding.ActivityPersonBinding
import br.edu.ifsp.scl.ads.pdm.splitthebill.modal.Person

class PersonActivity : BaseActivity() {
    private val apb: ActivityPersonBinding by lazy {
        ActivityPersonBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(apb.root)
        supportActionBar?.subtitle = ("Informações de Pessoa")

        val receivedPerson = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            intent.getParcelableExtra("Person", Person::class.java)
        }
        else{
            intent.getParcelableExtra(EXTRA_PERSON)
        }
        receivedPerson?.let{_receivedPerson->
            with(apb){
                with(_receivedPerson){
                    nameEt.setText(name)
                    valueEt.setText(value.toString())
                    descriptionEt.setText(description)
                }
            }
        }
        val viewPerson = intent.getBooleanExtra(EXTRA_VIEW_PERSON, false)
        with(apb){
            nameEt.isEnabled = !viewPerson
            valueEt.isEnabled = !viewPerson
            descriptionEt.isEnabled = !viewPerson
            saveBt.visibility = if(viewPerson) View.GONE else View.VISIBLE
        }

        apb.saveBt.setOnClickListener(){
            val person: Person = Person(receivedPerson?.id, apb.nameEt.text.toString(),
                apb.valueEt.text.toString().toFloat(), apb.descriptionEt.text.toString())

            val resultIntent = Intent()
            resultIntent.putExtra(EXTRA_PERSON, person)
            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }
}