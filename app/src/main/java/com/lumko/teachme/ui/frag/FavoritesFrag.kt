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
import com.lumko.teachme.manager.ToastMaker
import com.lumko.teachme.manager.adapter.FavoritesRvAdapter
import com.lumko.teachme.manager.net.observer.NetworkObserverFragment
import com.lumko.teachme.model.BaseResponse
import com.lumko.teachme.model.Course
import com.lumko.teachme.model.Favorite
import com.lumko.teachme.model.ToolbarOptions
import com.lumko.teachme.presenter.Presenter
import com.lumko.teachme.presenterImpl.FavoritesPresenterImpl
import com.lumko.teachme.ui.MainActivity
import com.lumko.teachme.ui.frag.abs.EmptyState
import com.lumko.teachme.ui.frag.course.BaseCourseAddToFav
import com.lumko.teachme.ui.frag.course.CourseAddToFavFactory

class FavoritesFrag : NetworkObserverFragment(), EmptyState {

    private lateinit var mBinding: RvBinding
    private lateinit var mPresenter: Presenter.FavoritesPresenter
    private var mAddToFavaObj: BaseCourseAddToFav? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = RvBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        mBinding.rvContainer.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                android.R.color.transparent
            )
        )
        mBinding.rvEmptyState.root.visibility = View.INVISIBLE

        val toolbarOptions = ToolbarOptions()
        toolbarOptions.startIcon = ToolbarOptions.Icon.NAV

        (activity as MainActivity).showToolbar(toolbarOptions, R.string.favorites)

        mPresenter = FavoritesPresenterImpl(this)
        mPresenter.getFavorites()
    }

    fun onFavoritesReceived(items: List<Favorite>) {
        mBinding.rvProgressBar.visibility = View.INVISIBLE
        if (items.isNotEmpty()) {
            mBinding.rv.layoutManager = LinearLayoutManager(requireContext())
            mBinding.rv.adapter = FavoritesRvAdapter(items.toMutableList(), this)
        } else {
            showEmptyState()
        }
    }

    fun removeItem(course: Course, adapterPosition: Int) {
        if (mAddToFavaObj == null) {
            mAddToFavaObj = CourseAddToFavFactory.getAddToFavObj(course)
        }

        mPresenter.removeFromFavorite(mAddToFavaObj!!.getAddToFavItem(), adapterPosition)
    }

    fun onItemRemoved(response: BaseResponse, adapterPosition: Int) {
        if (context == null) return

        if (response.isSuccessful) {
            val adapter = mBinding.rv.adapter as FavoritesRvAdapter
            adapter.items.removeAt(adapterPosition)
            adapter.notifyItemRemoved(adapterPosition)

            if (adapter.itemCount == 0) {
                showEmptyState()
            }

        } else {
            ToastMaker.show(
                requireContext(),
                getString(R.string.error),
                response.message,
                ToastMaker.Type.ERROR
            )
        }
    }

    fun showEmptyState() {
        showEmptyState(R.drawable.no_favorites, R.string.no_favorites, R.string.no_favorites_desc)
    }

    override fun emptyViewBinding(): EmptyStateBinding {
        return mBinding.rvEmptyState
    }

    override fun getVisibleFrag(): Fragment {
        return this
    }
}