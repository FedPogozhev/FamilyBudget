package com.fedx.familybudget.screen.income

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fedx.familybudget.MainActivity
import com.fedx.familybudget.R
import com.fedx.familybudget.adapter.InConAdapter
import com.fedx.familybudget.data.DataInCon
import com.fedx.familybudget.databinding.IncomeFragmentBinding
import com.google.firebase.ktx.Firebase
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import android.widget.ArrayAdapter
import androidx.core.view.size

class IncomeFragment : Fragment(), InConAdapter.Listener {
    private lateinit var binding: IncomeFragmentBinding
    private lateinit var viewModel: IncomeViewModel

    private val adapter = InConAdapter(this)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = IncomeFragmentBinding.inflate(layoutInflater)
        (activity as MainActivity).supportActionBar?.title = resources.getString(R.string.income)
        viewModel = ViewModelProvider(this).get(IncomeViewModel::class.java)

        binding.rvIncome.layoutManager = LinearLayoutManager(context)

        binding.rvIncome.adapter = adapter

        viewModel.getData(adapter)
        binding.btnAddIncome.setOnClickListener {
            alertDialog()
        }
        return binding.root
    }

    @SuppressLint("SimpleDateFormat")
    fun alertDialog() {
        var cal = Calendar.getInstance()

        val view = LayoutInflater.from(context).inflate(R.layout.item_add, null)

        val etComment = view.findViewById<AutoCompleteTextView>(R.id.etCommentInner)
        val etSum = view.findViewById<EditText>(R.id.etSumInner)
        val tvDate = view.findViewById<TextView>(R.id.tvDate)

        val adapterStr: ArrayAdapter<String>? = context?.let {
            ArrayAdapter<String>(
                it.applicationContext,
                android.R.layout.select_dialog_item,
                resources.getStringArray(R.array.income))
        }
        etComment.threshold = 1
        etComment.setAdapter(adapterStr)


        val builder = AlertDialog.Builder(context)
        builder.setTitle("Добавить запись доходов")
            .setView(view)

        fun getDate(){
            val date = SimpleDateFormat("yyyy-MM-dd")
            val currentDate = date.format(cal.time)
            tvDate.text = currentDate
        }

        tvDate.text = SimpleDateFormat("yyyy-MM-dd").format(Date())
        var date = SimpleDateFormat("yyyy-MM-dd").format(Date())

        val datePickerDialog =
            DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, month)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                getDate()
            }

        tvDate.setOnClickListener {
            context?.let { it1 ->
                DatePickerDialog(
                    it1,
                    datePickerDialog,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show()
            }
        }

        val alert = builder.show()

        view.findViewById<Button>(R.id.btnRecord).setOnClickListener {
            viewModel.sendIncome(etComment.text.toString(), tvDate.text.toString(), etSum.text.toString())
            alert.dismiss()
            binding.apply {
                adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver(){
                    override fun onItemRangeInserted(
                        positionStart: Int,
                        itemCount: Int
                    ) {
                        rvIncome.scrollToPosition(0)
                    }
                })
            }

        }
    }

    override fun change(dataInCon: DataInCon) {
        var cal = Calendar.getInstance()
        val view = LayoutInflater.from(context).inflate(R.layout.item_add, null)

        val etComment = view.findViewById<EditText>(R.id.etCommentInner)
        val etSum = view.findViewById<EditText>(R.id.etSumInner)
        val tvDate = view.findViewById<TextView>(R.id.tvDate)

        etComment.setText(dataInCon.comment)
        etSum.setText(dataInCon.sum)
        tvDate.text = dataInCon.date

        val builder = AlertDialog.Builder(context)
        builder.setTitle("Изменить запись дохода")
            .setView(view)

        fun getDate(){
            val date = SimpleDateFormat("yyyy-MM-dd")
            val currentDate = date.format(cal.time)
            tvDate.text = currentDate
        }

        val datePickerDialog =
            DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, month)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                getDate()
            }

        tvDate.setOnClickListener {
            context?.let { it1 ->
                DatePickerDialog(
                    it1,
                    datePickerDialog,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show()
            }
        }

        val alert = builder.show()

        view.findViewById<Button>(R.id.btnRecord).setOnClickListener {
            viewModel.changeData(etComment.text.toString(), tvDate.text.toString(), etSum.text.toString(), dataInCon.keyId.toString())


            alert.dismiss()
        }
    }

    override fun deletePosition(dataInCon: DataInCon) {
        val builder = AlertDialog.Builder(context)
        builder.setMessage("Удалить запись ${dataInCon.comment}?")
        builder.setPositiveButton("Ok"){dialog, which ->
            viewModel.deleteData(dataInCon.keyId.toString())
        }
        builder.setNegativeButton("Отмена"){dialog, which ->
        }
        builder.show()
    }
}

