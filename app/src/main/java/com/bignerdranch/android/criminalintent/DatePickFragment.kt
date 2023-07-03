package com.bignerdranch.android.criminalintent

import android.app.DatePickerDialog
import android.app.Dialog
import android.icu.util.Calendar
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.util.Date
import java.util.GregorianCalendar

private const val ARG_DATE = "date"

class DatePickFragment : DialogFragment() {

    interface Callbacks {
        fun onDateSelected(date: Date)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // 获取 Date 对象并初始化 DatePickerDialog
        val date = arguments?.getSerializable(ARG_DATE) as Date
        val calendar = Calendar.getInstance()
        calendar.time = date
        val initialYear = calendar.get(Calendar.YEAR)
        val initialMonth = calendar.get(Calendar.MONTH)
        val initialDay = calendar.get(Calendar.DAY_OF_MONTH)

        // 发回日期
        val dateListener = DatePickerDialog.OnDateSetListener {
                _: DatePicker, year: Int, month: Int, day: Int ->

            val resultDate : Date = GregorianCalendar(year, month, day).time

            targetFragment?.let { fragment ->
                (fragment as Callbacks).onDateSelected(resultDate)
            }
        }

        return DatePickerDialog(
            requireContext(),   // 获取视图相关必须资源的 context 对象
//            null,       // 日期监听器
            dateListener,
            initialYear,        // 供日期选择器初始化使用的 年、月、日初始值
            initialMonth,
            initialDay
        )
    }

    companion object {
        fun newInstance(date: Date): DatePickFragment {
            val args = Bundle().apply {
                putSerializable(ARG_DATE, date)
            }

            return DatePickFragment().apply {
                arguments = args
            }
        }
    }
}