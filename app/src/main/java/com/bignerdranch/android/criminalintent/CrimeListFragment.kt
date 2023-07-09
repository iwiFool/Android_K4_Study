package com.bignerdranch.android.criminalintent

import android.content.Context
import android.media.Image
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.UUID

private const val  TAG = "CrimeListFragment"

class CrimeListFragment : Fragment() {

    /**
     * 托管 activity 所需实现的 接口
     */
    interface Callbacks {
        fun onCrimeSelected(crimeId: UUID)
    }

    private var callbacks: Callbacks? = null

    private lateinit var crimeRecyclerView: RecyclerView
    // 配置 adapter
//    private var adapter: CrimeAdapter? = null
    // 关联 RecyclerView
    private var adapter: CrimeAdapter? = CrimeAdapter(emptyList())

    private val crimeListViewModel: CrimeListViewModel by lazy {
        ViewModelProviders.of(this).get(CrimeListViewModel::class.java)
    }

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        // 记录 CrimeListViewModel 中存放的 crime 对象数
//        Log.d(TAG, "Total crimes: ${crimeListViewModel.crimes.size}")
//    }

    // 当 fragment 附加到 activity 时， 会调用 Fragment.onAttach(Context) 生命周期函数。
    override fun onAttach(context: Context) {
        super.onAttach(context)
        // 这里，我们把传给 onAttach(...) 的 Context 值参保存到 callbacks 属性里。
        callbacks = context as Callbacks?
    }

    // 接收选项菜单函数回调
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    companion object {
        // 为了让 activity 调用获取 fragment 实例
        fun newInstance(): CrimeListFragment {
            return CrimeListFragment()
        }
    }

    // 为 CrimeListFragment 配置视图
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crime_list, container, false)

        crimeRecyclerView =
            view.findViewById(R.id.crime_recycler_view) as RecyclerView
        crimeRecyclerView.layoutManager = LinearLayoutManager(context)

//        updateUI()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        crimeListViewModel.crimeListLiveData.observe(
            viewLifecycleOwner,
            Observer {crimes ->
                crimes?.let {
                    Log.i(TAG, "Got crimes ${crimes.size}")
                    updateUI(crimes)
                }
            }
        )
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    // 实例化选项菜单
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_crime_list, menu)
    }

    // 响应菜单栏选择事件
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.new_crime -> {
                val crime = Crime()
                crimeListViewModel.addCrime(crime)
                callbacks?.onCrimeSelected(crime.id)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    // RecyclerView 的任务仅限于回收和摆放屏幕上的 View
    // 列表项 View 能够显示数据还离不开 ViewHolder 子类和 Adapter 子类
    private inner class CrimeHolder(view: View)
        : RecyclerView.ViewHolder(view), View.OnClickListener {

        // 在 CrimeHolder 中实现 bind(Crime) 函数
        private lateinit var crime: Crime

        // 在构造函数里生成视图
        private val titleTextView: TextView = itemView.findViewById(R.id.crime_title)
        private val dateTextView: TextView = itemView.findViewById(R.id.crime_date)
        private val solvedImageView: ImageView = itemView.findViewById(R.id.crime_solved)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(crime: Crime) {
            this.crime = crime
            titleTextView.text = this.crime.title
            dateTextView.text = DateFormat.format("yyyy MM dd,EEEE, h:mmaa", crime.date) // 格式化日期

            // 控制手铐图片显示
            solvedImageView.visibility = if (crime.isSolved) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }

        // 检测用户点击事件
        override fun onClick(p0: View?) {
//            Toast.makeText(context, "${crime.title} pressed!", Toast.LENGTH_SHORT)
//                .show()
            // 响应用户点击 crime 列表项事件
            callbacks?.onCrimeSelected(crime.id)
        }
    }

    // 创建 CrimeAdapter
    // Adapter 是一个控制器对象，从模型层获取数据，然后提供给 RecyclerView 显示
    //  负责：1. 创建必要的 ViewHolder, 2. 绑定 ViewHolder 至模型层数据
    private inner class CrimeAdapter(var crimes: List<Crime>)
        : RecyclerView.Adapter<CrimeHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            : CrimeHolder {
            val view = layoutInflater.inflate(R.layout.list_item_crime, parent, false)
            return CrimeHolder(view)
        }

        override fun getItemCount() = crimes.size

        override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
            val crime = crimes[position]
            /*holder.apply {
                titleTextView.text = crime.title
                dateTextView.text = crime.date.toString()
            }*/
            // 调用 bind(Crime) 函数
            holder.bind(crime)
        }
    }

//    private fun updateUI() {
    private fun updateUI(crimes: List<Crime>) {
//        val crimes = crimeListViewModel.crimes
        adapter = CrimeAdapter(crimes)
        crimeRecyclerView.adapter = adapter
    }
}