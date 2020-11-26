package com.example.todolistapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.todolistapp.model.Model
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {
    lateinit var database: DatabaseReference
    var toDoList: MutableList<Model>? = null
    lateinit var adapter: Adapter
    private var listViewItem: ListView?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val addButton = findViewById<View>(R.id.addButton) as FloatingActionButton

        database = FirebaseDatabase.getInstance().reference

        addButton.setOnClickListener {  view ->
            val alertDialog = AlertDialog.Builder(this)
            val editText = EditText(this)
            alertDialog.setMessage("Yapılacaklar Listesine Ekle")
            alertDialog.setTitle("Başlık")
            alertDialog.setView(editText)
            alertDialog.setPositiveButton("Ekle"){dialog, i ->
                val todoItemData = Model.createList()
                todoItemData.itemsDataText = editText.text.toString()
                todoItemData.doneOkay = false

                val newItemData = database.child("todo").push()
                todoItemData.UID=newItemData.key

                newItemData.setValue(todoItemData)

                dialog.dismiss()
                Toast.makeText(this,"oluşturuldu", Toast.LENGTH_LONG).show()
            }
            alertDialog.show()
        }
    }
}