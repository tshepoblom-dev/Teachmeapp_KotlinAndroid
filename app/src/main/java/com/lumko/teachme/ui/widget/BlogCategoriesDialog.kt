package com.lumko.teachme.ui.widget

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lumko.teachme.R
import com.lumko.teachme.databinding.DialogBlogCategoriesBinding
import com.lumko.teachme.manager.adapter.BlogCategoriesRvAdapter
import com.lumko.teachme.manager.listener.ItemCallback
import com.lumko.teachme.manager.listener.ItemClickListener
import com.lumko.teachme.manager.listener.OnItemClickListener
import com.lumko.teachme.model.Category
import com.lumko.teachme.presenterImpl.BlogCategoriesPresenterImpl

class BlogCategoriesDialog : BottomSheetDialogFragment(), View.OnClickListener,
    OnItemClickListener {

    private lateinit var mBinding: DialogBlogCategoriesBinding
    private var mCallback: ItemCallback<Category>? = null

    override fun onStart() {
        super.onStart()
        WidgetHelper.removeBottomSheetDialogHalfExpand(dialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DialogBlogCategoriesBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    fun setCallback(callback: ItemCallback<Category>) {
        mCallback = callback
    }

    private fun init() {
        mBinding.blogsCategoriesCancelBtn.setOnClickListener(this)
        val presenter = BlogCategoriesPresenterImpl(this)
        presenter.getBlogCategories()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.blogs_categories_cancel_btn -> {
                dismiss()
            }
        }
    }

    fun onBlogCatsReceived(cats: List<Category>) {
        mBinding.blogsCategoriesRvProgressBar.visibility = View.GONE
        mBinding.blogsCategoriesRv.adapter = BlogCategoriesRvAdapter(cats)
        mBinding.blogsCategoriesRv.addOnItemTouchListener(
            ItemClickListener(
                mBinding.blogsCategoriesRv,
                this
            )
        )
    }

    override fun onClick(view: View?, position: Int, id: Int) {
        val category =
            (mBinding.blogsCategoriesRv.adapter as BlogCategoriesRvAdapter).items[position]
        mCallback?.onItem(category)
        dismiss()
    }

    override fun onLongClick(view: View?, position: Int, id: Int) {
    }
}