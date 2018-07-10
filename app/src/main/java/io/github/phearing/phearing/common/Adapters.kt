package io.github.phearing.phearing.common

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import io.github.phearing.phearing.R
import io.github.phearing.phearing.room.headphone.Headphone
import kotlinx.android.synthetic.main.item_speech_choice.view.*
import java.text.SimpleDateFormat
import java.util.*

class WidgetCardHintRVAdapter(context: Context, private val mContainerList: List<View>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val mInflater = LayoutInflater.from(context)

    class ViewHolder(root: View) : RecyclerView.ViewHolder(root) {
        val container: FrameLayout = root.findViewById(R.id.widget_card_hint_rv_container)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(mInflater.inflate(R.layout.widget_card_hint_item, parent, false))
    }

    override fun getItemCount(): Int {
        return mContainerList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as ViewHolder
        mContainerList[position].parent?.let {
            (it as ViewGroup).removeView(mContainerList[position])
        }
        viewHolder.container.removeAllViews()
        viewHolder.container.addView(mContainerList[position])
    }
}

class WidgetListDialogRVAdapter(context: Context, private val mHeadphoneList: List<Headphone>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val mInflater = LayoutInflater.from(context)
    private var mClickCallback: ((position: Int) -> Unit)? = null
    private var mLongClickCallback: ((position: Int) -> Unit)? = null

    inner class ViewHolder(root: View) : RecyclerView.ViewHolder(root) {
        val name: TextView = root.findViewById(R.id.widget_list_dialog_item_tv)

        init {
            root.setOnClickListener {
                mClickCallback?.invoke(adapterPosition)
            }
            root.setOnLongClickListener {
                mLongClickCallback?.invoke(adapterPosition)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(mInflater.inflate(R.layout.widget_list_dialog_item, parent, false))
    }

    override fun getItemCount(): Int {
        return mHeadphoneList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as ViewHolder
        viewHolder.name.text = mHeadphoneList[position].name
    }

    fun setOnClickCallback(callback: (position: Int) -> Unit) {
        mClickCallback = callback
    }

    fun setOnLongClickCallback(callback: (position: Int) -> Unit) {
        mLongClickCallback = callback
    }
}

class HistoryRVAdapter(context: Context, private val mHistoryTimeList: List<Long>, private val mHistoryDataList: List<String>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val mInflater = LayoutInflater.from(context)
    private var mClickCallback: ((position: Int) -> Unit)? = null
    private var mLongClickCallback: ((position: Int) -> Unit)? = null

    inner class ViewHolder(root: View) : RecyclerView.ViewHolder(root) {
        val time: TextView = root.findViewById(R.id.item_history_time_tv)
        val data: TextView = root.findViewById(R.id.item_history_level_tv)

        init {
            root.setOnClickListener {
                mClickCallback?.invoke(adapterPosition)
            }
            root.setOnLongClickListener {
                mLongClickCallback?.invoke(adapterPosition)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(mInflater.inflate(R.layout.item_history, parent, false))
    }

    override fun getItemCount(): Int {
        return mHistoryTimeList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as ViewHolder
        viewHolder.time.text = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(mHistoryTimeList[position])
        viewHolder.data.text = mHistoryDataList[position]
    }

    fun setOnClickCallback(callback: (position: Int) -> Unit) {
        mClickCallback = callback
    }

    fun setOnLongClickCallback(callback: (position: Int) -> Unit) {
        mLongClickCallback = callback
    }
}

class NewsNavigationRVAdapter(context: Context, private val mImageList: List<Drawable>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val mInflater = LayoutInflater.from(context)
    private var mClickCallback: ((position: Int) -> Unit)? = null

    inner class ViewHolder(root: View) : RecyclerView.ViewHolder(root) {
        val imageView: ImageView = root.findViewById(R.id.item_news_navigation_iv)

        init {
            root.setOnClickListener {
                mClickCallback?.invoke(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(mInflater.inflate(R.layout.item_news_navigation, parent, false))
    }

    override fun getItemCount(): Int {
        return mImageList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as ViewHolder
        viewHolder.imageView.setImageDrawable(mImageList[position])
    }

    fun setOnClickCallback(callback: (position: Int) -> Unit) {
        mClickCallback = callback
    }
}

class NewsDataRVAdapter(context: Context, private val mDataList: List<NewsData>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val mInflater = LayoutInflater.from(context)
    private var mClickCallback: ((position: Int) -> Unit)? = null

    inner class ViewHolder(root: View) : RecyclerView.ViewHolder(root) {
        val titleTv: TextView = root.findViewById(R.id.item_news_data_title_tv)
        val contentTv: TextView = root.findViewById(R.id.item_news_data_content_tv)
        val imageView: ImageView = root.findViewById(R.id.item_news_data_iv)

        init {
            root.setOnClickListener {
                mClickCallback?.invoke(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(mInflater.inflate(R.layout.item_news_data, parent, false))
    }

    override fun getItemCount(): Int {
        return mDataList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as ViewHolder
        viewHolder.titleTv.text = mDataList[position].title
        viewHolder.contentTv.text = mDataList[position].content
        if (mDataList[position].image != null) {
            viewHolder.imageView.setImageDrawable(mDataList[position].image)
        } else {
            viewHolder.imageView.visibility = View.GONE
        }
    }

    fun setOnClickCallback(callback: (position: Int) -> Unit) {
        mClickCallback = callback
    }
}

class SpeechChoiceRVAdapter(context: Context, private val mChoiceList: List<String>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val mInflater = LayoutInflater.from(context)
    private var mClickCallback: ((position: Int) -> Unit)? = null

    inner class ViewHolder(root: View) : RecyclerView.ViewHolder(root) {
        val text: TextView = root.item_speech_choice_tv

        init {
            root.setOnClickListener {
                mClickCallback?.invoke(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(mInflater.inflate(R.layout.item_speech_choice, parent, false))
    }

    override fun getItemCount(): Int {
        return mChoiceList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as ViewHolder

        viewHolder.text.text = mChoiceList[position]
    }

    fun setOnClickCallback(callback: (position: Int) -> Unit) {
        mClickCallback = callback
    }
}

class ViewPagerFragmentAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    private val mFragmentList = mutableListOf<Fragment>()
    private val mTitleList = mutableListOf<String>()

    override fun getPageTitle(position: Int): CharSequence? {
        return mTitleList[position]
    }

    override fun getCount(): Int {
        return mFragmentList.size
    }

    override fun getItem(position: Int): Fragment {
        return mFragmentList[position]
    }

    fun addFragment(fragment: Fragment, title: String) {
        mFragmentList.add(fragment)
        mTitleList.add(title)
    }
}

class ViewPagerViewAdapter : PagerAdapter() {
    private val mViewList = mutableListOf<View>()
    private val mTitleList = mutableListOf<String>()

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return mViewList.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return mTitleList[position]
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        container.addView(mViewList[position])
        return mViewList[position]
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(mViewList[position])
    }

    fun addView(view: View, title: String) {
        mViewList.add(view)
        mTitleList.add(title)
    }
}