package com.fedx.familybudget.screen.total

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.fedx.familybudget.databinding.TotalFragmentBinding
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.whiteelephant.monthpicker.MonthPickerDialog
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class TotalFragment : Fragment() {
    lateinit var binding: TotalFragmentBinding
    private lateinit var viewModel: TotalViewModel
    lateinit var inputDateOfBirth: String

    @SuppressLint("SimpleDateFormat")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = TotalFragmentBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this).get(TotalViewModel::class.java)

        val today = Calendar.getInstance()
        val  sdf = SimpleDateFormat("yyyy-MM")
        val builder = MonthPickerDialog.Builder(
            activity, { month, year ->
                today.set(Calendar.MONTH, month)
                binding.tvItemMonthYear.text = Html.fromHtml("<u>${getMonth(today.get(Calendar.MONTH))}</u>")
                val dateForGraph = sdf.format(today.time)
                viewModel.getIncome(dateForGraph)
                viewModel.getCons(dateForGraph)
            },
            today.get(Calendar.YEAR),
            today.get(Calendar.MONTH)
        )

        builder.setActivatedMonth(today.get(Calendar.MONTH))
            .setMinYear(1980)
            .setActivatedYear(today.get(Calendar.YEAR))
            .setMaxYear(2030)
            .showMonthOnly()

        buildGraph()
        binding.tvItemMonthYear.text = Html.fromHtml("<u>${getMonth(today.get(Calendar.MONTH))}</u>")
        binding.tvItemMonthYear.setOnClickListener {
            builder.build().show()
        }
        return binding.root
    }

    private fun getMonth(c: Int): String {
        return when (c) {
            0 -> "Январь"
            1 -> "Февраль"
            2 -> "Март"
            3 -> "Апрель"
            4 -> "Май"
            5 -> "Июнь"
            6 -> "Июль"
            7 -> "Август"
            8 -> "Сентябрь"
            9 -> "Октябрь"
            10 -> "Ноябрь"
            11 -> "Декабрь"
            else -> ""
        }
    }

    @SuppressLint("SetTextI18n")
    private fun buildGraph() {
        val c = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy-MM")
        viewModel.getIncome(sdf.format(c.time))
        viewModel.getCons(sdf.format(c.time))
        viewModel.barListCons.observe(viewLifecycleOwner, androidx.lifecycle.Observer {

            val barEntryInc = ArrayList<BarEntry>()
            val listInc = viewModel.barListInc.value
            val barEntryCon = ArrayList<BarEntry>()
            val listCons = it

            var totalIn = 0
            for (i in 0 until listInc!!.size) {
                barEntryInc.add(BarEntry(i.toFloat(), listInc[i]))
                totalIn += listInc[i].toInt()
                binding.tvTotalIn.text = "Всего доходов: $totalIn р."
            }

            var totalCon = 0
            var item = listInc.size
            for (i in 0 until listCons.size) {
                item++
                barEntryCon.add(BarEntry(item.toFloat(), listCons[i]))
                totalCon += listCons[i].toInt()
                binding.tvTotalCon.text = "Всего расходов :$totalCon р."
            }

            val barDataSetIn = BarDataSet(barEntryInc, "Доходы")
            val barDataSetCon = BarDataSet(barEntryCon, "Расходы")

            barDataSetIn.valueTextSize = 14f
            barDataSetCon.valueTextSize = 14f
            barDataSetIn.color = Color.GREEN
            barDataSetCon.color = Color.BLUE

            val finalData = ArrayList<BarDataSet>()
            finalData.add(barDataSetIn)
            finalData.add(barDataSetCon)

            val data = BarData(finalData as List<IBarDataSet>?)
            binding.idBarChart.apply {
                xAxis.isEnabled = false
                axisRight.isEnabled = false
                axisLeft.isEnabled = false
                description.isEnabled = false

                animateXY(1000, 100)
                this.data = data
                invalidate()
            }
        })
    }
}