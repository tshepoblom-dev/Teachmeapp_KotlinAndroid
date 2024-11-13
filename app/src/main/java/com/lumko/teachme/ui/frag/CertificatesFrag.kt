package com.lumko.teachme.ui.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.lumko.teachme.R
import com.lumko.teachme.databinding.EmptyStateBinding
import com.lumko.teachme.databinding.RvBinding
import com.lumko.teachme.manager.App
import com.lumko.teachme.manager.adapter.CertificateRvAdapter
import com.lumko.teachme.manager.listener.ItemClickListener
import com.lumko.teachme.manager.listener.OnItemClickListener
import com.lumko.teachme.manager.net.observer.NetworkObserverFragment
import com.lumko.teachme.model.Certificate
import com.lumko.teachme.model.CompletionCert
import com.lumko.teachme.model.Quiz
import com.lumko.teachme.model.QuizResult
import com.lumko.teachme.presenterImpl.CertificatesPresenterImpl
import com.lumko.teachme.ui.MainActivity
import com.lumko.teachme.ui.frag.abs.EmptyState

class CertificatesFrag : NetworkObserverFragment(), OnItemClickListener, EmptyState {

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
        initUI()
        val presenter = CertificatesPresenterImpl(this)

        val type = requireArguments().getSerializable(App.TYPE) as Certificate.Type

        when (type) {
            Certificate.Type.QUIZ -> {
                presenter.getAchievementCertificates()
            }

            Certificate.Type.COMPLTETION -> {
                presenter.getCompletionCertificates()
            }

            Certificate.Type.CLASS -> {
                presenter.getClassCertificates()
            }
        }
    }

    private fun initUI() {
        mBinding.rvContainer.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                android.R.color.transparent
            )
        )
        mBinding.rvEmptyState.root.visibility = View.INVISIBLE
    }

    fun onCertsReceived(data: List<QuizResult>) {
        if (data.isNotEmpty()) {
            setAdapter(CertificateRvAdapter(data))
        } else {
            showEmptyState()
        }
    }

    fun onClassCertsReceived(data: List<Quiz>) {
        if (data.isNotEmpty()) {
            setAdapter(CertificateRvAdapter(data))
        } else {
            showEmptyState()
        }
    }

    private fun setAdapter(adapter: CertificateRvAdapter<*>) {
        mBinding.rvProgressBar.visibility = View.INVISIBLE
        mBinding.rv.layoutManager = LinearLayoutManager(requireContext())
        mBinding.rv.adapter = adapter
        mBinding.rv.addOnItemTouchListener(ItemClickListener(mBinding.rv, this))
    }

    override fun onClick(view: View?, position: Int, id: Int) {
        val certificate = (mBinding.rv.adapter as CertificateRvAdapter<*>).items[position]

        val bundle = Bundle()
        bundle.putParcelable(App.CERTIFICATE, certificate)

        val frag = CertificateDetailsFrag()
        frag.arguments = bundle
        (activity as MainActivity).transact(frag)
    }

    override fun onLongClick(view: View?, position: Int, id: Int) {
    }

    fun showEmptyState() {
        showEmptyState(
            R.drawable.no_certificate,
            R.string.no_certificates,
            R.string.no_certificates_desc
        )
    }

    override fun emptyViewBinding(): EmptyStateBinding {
        return mBinding.rvEmptyState
    }

    override fun getVisibleFrag(): Fragment {
        return this
    }

    fun onCompletionCertsReceived(certs: List<CompletionCert>) {
        if (certs.isNotEmpty()) {
            setAdapter(CertificateRvAdapter(certs))
        } else {
            showEmptyState()
        }
    }
}