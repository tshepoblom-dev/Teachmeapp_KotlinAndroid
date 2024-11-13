package com.lumko.teachme.ui.widget

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lumko.teachme.R
import com.lumko.teachme.databinding.DialogBlogCategoriesBinding
import com.lumko.teachme.manager.adapter.BlogCategoriesRvAdapter
import com.lumko.teachme.manager.listener.ItemCallback
import com.lumko.teachme.manager.listener.ItemClickListener
import com.lumko.teachme.manager.listener.OnItemClickListener
import com.lumko.teachme.manager.net.observer.NetworkObserverBottomSheetDialog
import com.lumko.teachme.model.Category
import com.lumko.teachme.model.KeyValuePair
import com.lumko.teachme.presenterImpl.CategoriesDialogPresenterImpl

class CategoriesDialog : NetworkObserverBottomSheetDialog(), View.OnClickListener,
    OnItemClickListener {

    private lateinit var mBinding: DialogBlogCategoriesBinding
    private var mCallback: ItemCallback<List<KeyValuePair>>? = null

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

    fun setCallback(callback: ItemCallback<List<KeyValuePair>>) {
        mCallback = callback
    }

    private fun init() {
        mBinding.blogsCategoriesTv.text = getString(R.string.categories)
        mBinding.blogsCategoriesCancelBtn.setOnClickListener(this)
        val presenter = CategoriesDialogPresenterImpl(this)
        presenter.getCategories()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.blogs_categories_cancel_btn -> {
                dismiss()
            }
        }
    }

    override fun onClick(view: View?, position: Int, id: Int) {
        val category =
            (mBinding.blogsCategoriesRv.adapter as BlogCategoriesRvAdapter).items[position]

        val keyVals = ArrayList<KeyValuePair>()
        keyVals.add(KeyValuePair("cat", category.id.toString()))

        mCallback?.onItem(keyVals)
        dismiss()
    }

    override fun onLongClick(view: View?, position: Int, id: Int) {
    }

    fun onCategoriesRecevied(items: List<Category>) {
        val cats = ArrayList<Category>()
        for (item in items) {
            if (item.subCategories.isNullOrEmpty()) {
                cats.add(item)
            } else {
                cats.addAll(item.subCategories)
            }
        }

        mBinding.blogsCategoriesRvProgressBar.visibility = View.GONE
        mBinding.blogsCategoriesRv.adapter = BlogCategoriesRvAdapter(cats)
        mBinding.blogsCategoriesRv.addOnItemTouchListener(
            ItemClickListener(
                mBinding.blogsCategoriesRv,
                this
            )
        )
    }
}