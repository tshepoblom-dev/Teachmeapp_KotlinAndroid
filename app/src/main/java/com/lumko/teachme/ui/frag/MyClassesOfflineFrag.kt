package com.lumko.teachme.ui.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.lumko.teachme.R
import com.lumko.teachme.databinding.RvBinding
import com.lumko.teachme.manager.App
import com.lumko.teachme.manager.adapter.MyClassesRvAdapter
import com.lumko.teachme.manager.db.AppDb
import com.lumko.teachme.manager.listener.ItemClickListener
import com.lumko.teachme.manager.listener.OnItemClickListener
import com.lumko.teachme.model.AppConfig
import com.lumko.teachme.model.Course
import com.lumko.teachme.model.UserProfile
import com.lumko.teachme.ui.SplashScreenActivity

class MyClassesOfflineFrag : Fragment(), OnItemClickListener {

    private lateinit var mBinding: RvBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = RvBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        (activity as SplashScreenActivity).showToolbar(R.string.my_classes)

        val db = AppDb(requireContext())
        val items = db.getListData(AppDb.DataType.COURSE)
        val config = db.getData(AppDb.DataType.APP_CONFIG)
        val user = db.getData(AppDb.DataType.USER)
        db.close()

        App.appConfig = Gson().fromJson(config, AppConfig::class.java)
        App.loggedInUser = Gson().fromJson(user, UserProfile::class.java)

        if (items != null) {
            //            val courses =
//                Gson().fromJson<List<Course>>(data, object : TypeToken<List<Course>>() {}.type)

            val gson = GsonBuilder().setLenient().serializeNulls().create()
            val courses = ArrayList<Course>()
            for (item in items) {
                courses.add(gson.fromJson(item, Course::class.java))
            }
            mBinding.rvProgressBar.visibility = View.GONE
            mBinding.rv.layoutManager = LinearLayoutManager(requireContext())
            mBinding.rv.adapter = MyClassesRvAdapter(courses)
            mBinding.rv.addOnItemTouchListener(ItemClickListener(mBinding.rv, this))
        }
    }

    override fun onClick(view: View?, position: Int, id: Int) {
        val course = (mBinding.rv.adapter as MyClassesRvAdapter).items[position]
        course.isOfflineMode = true

        val bundle = Bundle()
        bundle.putParcelable(App.COURSE, course)
        bundle.putBoolean(App.OFFLINE, true)

        val frag = CourseLearningTabsFrag()

        frag.arguments = bundle
        (activity as SplashScreenActivity).transact(frag)
    }

    override fun onLongClick(view: View?, position: Int, id: Int) {
    }
}