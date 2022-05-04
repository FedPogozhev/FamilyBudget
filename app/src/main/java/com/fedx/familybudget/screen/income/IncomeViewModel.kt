package com.fedx.familybudget.screen.income

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.fedx.familybudget.adapter.InConAdapter
import com.fedx.familybudget.data.DataInCon
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import java.util.concurrent.CountDownLatch
import kotlin.collections.ArrayList

class IncomeViewModel : ViewModel() {
    private val database = Firebase.database
    private val myRef = database.getReference("income")
    private val myRefSort = database.getReference("income").orderByChild("date")

    private val _keyId = MutableLiveData<String>()

    private var _list = MutableLiveData<ArrayList<DataInCon>>()
    val list: LiveData<ArrayList<DataInCon>>
        get() = _list


    fun getData(adapter: InConAdapter) {
        myRefSort.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = ArrayList<DataInCon>()
                for (s in snapshot.children) {
                    val dataInCon = s.getValue(DataInCon::class.java)
                    dataInCon?.keyId = s.key
                    if (dataInCon != null){
                        list.add(dataInCon)
                    }
                    _keyId.value = dataInCon?.keyId.toString()
                }
                adapter.submitList(list.reversed())
                _list.value = list
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("MyLog", "snapshot error ${error.message}")
            }
        })
    }

    fun sendIncome(comment: String, date: String, sum: String) {
        myRef.child(myRef.push().key ?: "0")
            .setValue(DataInCon(comment, date, sum))
    }

    fun changeData(comment: String, date: String, sum: String, key: String){
        val data = mapOf<String, String>(
            "comment" to comment,
            "date" to date,
            "sum" to sum
        )
        myRef.child(key).updateChildren(data)
    }

    fun deleteData(key: String){
        myRef.child(key).removeValue()
    }
}