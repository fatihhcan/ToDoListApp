package com.example.todolistapp.model

class Model {
    companion object  Factory{
        fun createList(): Model = Model()
    }

    var UID: String? = null
    var itemsDataText: String? = null
    var doneOkay: Boolean? = false
}