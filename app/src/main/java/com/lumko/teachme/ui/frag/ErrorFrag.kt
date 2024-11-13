package com.lumko.teachme.ui.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.lumko.teachme.R
import com.lumko.teachme.databinding.EmptyStateBinding
import com.lumko.teachme.manager.App
import com.lumko.teachme.manager.db.AppDb
import com.lumko.teachme.manager.net.RetryListener
import com.lumko.teachme.ui.SplashScreenActivity
import java.util.*


class ErrorFrag : Fragment(), View.OnClickListener {

    private lateinit var mBinding: EmptyStateBinding

    companion object {
        private val mRetryListeners = ArrayDeque<RetryListener>()

        @JvmStatic
        fun getInstance(): ErrorFrag {
            return ErrorFrag()
        }

        var isFragVisible = false
    }

    override fun onStart() {
        super.onStart()
        isFragVisible = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = EmptyStateBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        view?.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.pageBg))
        mBinding.emptyStatetitleTv.text = getString(R.string._no_network)
        mBinding.emptyStateDescTV.text = getString(R.string.no_network_desc)
        mBinding.emptyStateImg.setImageResource(R.drawable.learning)
        mBinding.emptyStateLoginBtn.visibility = View.VISIBLE
        mBinding.emptyStateLoginBtn.text = getString(R.string.retry)
        mBinding.emptyStateLoginBtn.setOnClickListener(this)

        if (arguments != null && requireArguments().getBoolean(App.COURSES)) {
            (activity as SplashScreenActivity).hideToolbar()
            checkToShowMyClassesBtn()
        } else {
            if (activity is SplashScreenActivity) {
                (activity as SplashScreenActivity).setStatusBarColor(R.color.accent)
            }
        }
    }

    private fun checkToShowMyClassesBtn() {
        val db = AppDb(requireContext())
        if (db.checkIfMyCoursesIsSaved()) {
            mBinding.emptyStateMyClassesBtnContainer.visibility = View.VISIBLE
            mBinding.emptyStateMyClassesBtn.setOnClickListener(this)
        }
        db.close()
    }

    fun addOnRetryListener(retryListener: RetryListener?) {
        if (retryListener != null) {
            mRetryListeners.add(retryListener)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.emptyStateLoginBtn -> {
                activity?.supportFragmentManager?.popBackStack()
                while (mRetryListeners.size > 0) {
                    mRetryListeners.poll()?.onRetry()
                }
            }

            R.id.emptyStateMyClassesBtn -> {
                (activity as SplashScreenActivity).transact(MyClassesOfflineFrag())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        isFragVisible = false
    }
}