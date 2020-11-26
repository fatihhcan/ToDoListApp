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
import com.google.firebase.database.*

class MainActivity : AppCompatActivity(), UpdateAndDelete {
    lateinit var database: DatabaseReference
    var toDoList: MutableList<Model>? = null
    lateinit var adapter: Adapter
    private var listViewItem: ListView?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val addButton = findViewById<View>(R.id.addButton) as FloatingActionButton
        listViewItem = findViewById<ListView>(R.id.itemsListView)
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
        toDoList = mutableListOf<Model>()
        adapter = Adapter(this,toDoList!!)
        listViewItem!!.adapter=adapter
        database.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                toDoList!!.clear()
                addItemToList(snapshot)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext,"Eklenemedi!", Toast.LENGTH_LONG).show()
            }

        })
    }

    private fun addItemToList(snapshot: DataSnapshot) {
        val items=snapshot.children.iterator()

        if (items.hasNext()){
            val toDoIndexValue = items.next()
            val itemsIterator = toDoIndexValue.children.iterator()

            while (itemsIterator.hasNext()){
                val currentItem = itemsIterator.next()
                val toDoItemData = Model.createList()
                val map = currentItem.getValue() as HashMap<String, Any>

                toDoItemData.UID = currentItem.key
                toDoItemData.doneOkay=map.get("doneOkay") as Boolean?
                toDoItemData.itemsDataText=map.get("itemsDataText") as String
                toDoList!!.add(toDoItemData)
            }
        }
        adapter.notifyDataSetChanged()
    }

    override fun modifyItem(itemUID: String, isDone: Boolean) {
        val itemReference = database.child("todo").child(itemUID)
        itemReference.child("doneOkay").setValue(isDone)
    }

    override fun onItemDelete(itemUID: String) {
        val itemReference = database.child("todo").child(itemUID)
        itemReference.removeValue()
        adapter.notifyDataSetChanged()

    }
}