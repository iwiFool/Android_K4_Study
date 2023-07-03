package com.bignerdranch.android.criminalintent

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import java.util.Date
import java.util.UUID

private const val TAG = "CrimeFragment"
private const val ARG_CRIME_ID = "crime_id"
private const val DIALOG_DATE = "DialogDate"
private const val REQUEST_DATE = 0
class CrimeFragment : Fragment(), DatePickFragment.Callbacks {

    private lateinit var crime: Crime
    private lateinit var titleField: EditText
    private lateinit var dateButton: Button
    private lateinit var solvedCheckBox: CheckBox
    // 关联 CrimeFragment 和 CrimeDetailViewModel
    private val crimeDetailViewModel: CrimeDetailViewModel by lazy {
        ViewModelProviders.of(this).get(CrimeDetailViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crime = Crime()
        // 从 argument 中获取 crime ID
        val crimeId: UUID = arguments?.getSerializable(ARG_CRIME_ID) as UUID
//        Log.d(TAG, "args bundle crime ID: $crimeId")
//         Eventually, load crime from database
        crimeDetailViewModel.loadCrime(crimeId)
    }

    // 配置 生成视图并托管给 activity的容器视图
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crime, container, false)
        // 生成并使用 EdiText 部件
        titleField = view.findViewById(R.id.crime_title) as EditText
        // 生成 Button 部件
        dateButton = view.findViewById(R.id.crime_date) as Button

//        dateButton.apply {
//            text = crime.date.toString()
//            isEnabled = false
//        }

        // 生成 CheckBox 部件
        solvedCheckBox = view.findViewById(R.id.crime_solved) as CheckBox

        return view
    }

    // 观察数据变化
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        crimeDetailViewModel.crimeLiveData.observe(
            viewLifecycleOwner,
            Observer { crime ->
                crime?.let {
                    this.crime = crime
                    updateUI()
                }
            }
        )
    }

    private fun updateUI() {
        titleField.setText(crime.title)
        dateButton.text = crime.date.toString()
        // 跳过 checkbox 动画
//        solvedCheckBox.isChecked = crime.isSolved
        solvedCheckBox.apply {
            isChecked = crime.isSolved
            jumpDrawablesToCurrentState()
        }
    }

    override fun onStart() {
        super.onStart()

        val titleWatcher = object : TextWatcher {

            override fun beforeTextChanged(
                sequence: CharSequence?,
                start: Int,
                befor: Int,
                count: Int
            ) {
                // left blank
            }

            override fun onTextChanged(
                p0: CharSequence?,
                p1: Int,
                p2: Int,
                p3: Int
            ) {
                crime.title = p0.toString()
            }

            override fun afterTextChanged(p0: Editable?) {
//                TODO("Not yet implemented")
            }
        }
        // 给 EditText 部件添加监听器
        titleField.addTextChangedListener(titleWatcher)

        // 监听 CheckBox 的变化
        solvedCheckBox.apply {
            setOnCheckedChangeListener { _, isChecked ->
                crime.isSolved = isChecked
            }
        }

        // 显示 DialogFragment
        dateButton.setOnClickListener {
//            DatePickFragment().apply {
            DatePickFragment.newInstance(crime.date).apply {
                // 设置目标 fragment
                setTargetFragment(this@CrimeFragment, REQUEST_DATE)
                // 需要 this@CrimeFragment 来调用 requireFragmentManager()
                // 注意，在 apply 函数块中，this 引用的是 外部的 DatePickerFragment，因此 这里需要加 this 关键字
                show(this@CrimeFragment.requireFragmentManager(), DIALOG_DATE)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        crimeDetailViewModel.saveCrime(crime)
    }

    // 编写 newInstance(UUID) 函数
    companion object {

        fun newInstance(crimeId: UUID): CrimeFragment {
            val args = Bundle().apply {
                putSerializable(ARG_CRIME_ID, crimeId)
            }
            return CrimeFragment().apply {
                arguments = args
            }
        }
    }

    override fun onDateSelected(date: Date) {
        crime.date = date
        updateUI()
    }
}