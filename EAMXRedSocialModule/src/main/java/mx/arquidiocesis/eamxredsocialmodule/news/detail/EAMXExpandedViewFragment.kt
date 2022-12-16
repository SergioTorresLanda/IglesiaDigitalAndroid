package mx.arquidiocesis.eamxredsocialmodule.news.detail

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.google.gson.Gson
import com.google.gson.JsonArray
import mx.arquidiocesis.eamxcommonutils.application.AppMyConstants
import mx.arquidiocesis.eamxcommonutils.common.EAMXBaseFragment
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXHome
import mx.arquidiocesis.eamxredsocialmodule.R
import mx.arquidiocesis.eamxredsocialmodule.databinding.EamxExpandedViewFragmentBinding
import mx.arquidiocesis.eamxredsocialmodule.news.detail.adapter.EAMXCarouselAdapter
import mx.arquidiocesis.eamxredsocialmodule.model.Media
import mx.arquidiocesis.eamxredsocialmodule.model.MultimediaModel

const val INDEX_FOCUS = "INDEX_FOCUS"

class EAMXExpandedViewFragment constructor(private val bundle: Bundle? = null) :
    EAMXBaseFragment() {

    lateinit var mBinding: EamxExpandedViewFragmentBinding
    var list = ArrayList<MultimediaModel>()
    private var indexToFocus = 0

    override fun setViewModel() {}

    override fun getLayout() = R.layout.eamx_expanded_view_fragment

    override fun initBinding(view: View) {
        mBinding = EamxExpandedViewFragmentBinding.bind(view)
    }

    override fun initDependency(savedInstanceState: Bundle?) {}

    override fun initObservers() {}

    override fun initView(view: View) {

        mBinding.btnBackExpanded.setOnClickListener {
            requireActivity().onBackPressed()
        }

        bundle?.let {
            val gson = Gson()
            indexToFocus = it.getInt(INDEX_FOCUS, 0)
            val listString = it.getString(EAMXEnumUser.PUBLICATIONS.name)
            val lis = gson.fromJson(listString, JsonArray::class.java)
            for (jsonElement in lis.asJsonArray) {
                list.add(gson.fromJson(jsonElement, MultimediaModel::class.java))
            }

            val adapter = EAMXCarouselAdapter(list)
            adapter.onItemClickListener = { media, _ ->
                if (!media.format.contains("image")) {
                    val i = Intent(activity, EAMXVideoPlayer::class.java)
                    i.putExtra(EAMXVideoPlayer.VIDEO_URL, media.url)
                    requireActivity().startActivity(i)
                }
            }
            mBinding.viewPagerCarousel.adapter = adapter
            mBinding.dotsIndicator.setViewPager2(mBinding.viewPagerCarousel)
            mBinding.viewPagerCarousel.setCurrentItem(indexToFocus, true)
        }

    }

    companion object {

        @JvmStatic
        fun newInstance(bundle: Bundle) : EAMXExpandedViewFragment {
            val expandedFragment = EAMXExpandedViewFragment(bundle)
            return expandedFragment
        }
    }
}