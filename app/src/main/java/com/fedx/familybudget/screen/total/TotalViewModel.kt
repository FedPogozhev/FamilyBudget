package com.fedx.familybudget.screen.total

import android.graphics.Color
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fedx.familybudget.adapter.InConAdapter
import com.fedx.familybudget.data.DataInCon
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.jjoe64.graphview.series.LineGraphSeries
import com.whiteelephant.monthpicker.MonthPickerDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.broadcast
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.time.days

class TotalViewModel : ViewModel() {

    private val database = Firebase.database
    private val myRefIncome = database.getReference("income").orderByChild("date")
    private val myRefCon = database.getReference("consumption").orderByChild("date")

    private val _barListInc = MutableLiveData<ArrayList<Float>>()
    val barListInc: LiveData<ArrayList<Float>>
        get() = _barListInc

    private val _barListCons = MutableLiveData<ArrayList<Float>>()
    val barListCons: LiveData<ArrayList<Float>>
        get() = _barListCons

    fun getIncome(date: String) {
        viewModelScope.launch(Dispatchers.Main) {
            myRefIncome.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = ArrayList<Float>()
                    var strDate = ""
                    var strDateDrop = ""
                    for (s in snapshot.children.reversed()) {
                        val dataInCon = s.getValue(DataInCon::class.java)
                        strDate = dataInCon?.date.toString()
                        strDateDrop = strDate.dropLast(3)
                        if (strDateDrop.equals(SimpleDateFormat(date).format(Date()))) {
                            list.add(dataInCon?.sum!!.toFloat())
                        }
                    }
                    _barListInc.value = list

                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("MyLog", "snapshot error ${error.message}")
                }
            })
        }

    }

    fun getCons(date : String) {
        viewModelScope.launch(Dispatchers.Main) {
            myRefCon.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = ArrayList<Float>()
                    var strDate = ""
                    var strDateDrop = ""
                    for (s in snapshot.children.reversed()) {
                        val dataInCon = s.getValue(DataInCon::class.java)
                        strDate = dataInCon?.date.toString()
                        strDateDrop = strDate.dropLast(3)
                        if (strDateDrop.equals(SimpleDateFormat(date).format(Date()))) {
                            list.add(dataInCon?.sum!!.toFloat())
                        }
                    }
                    _barListCons.value = list
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("MyLog", "snapshot error ${error.message}")
                }
            })
        }

    }
}