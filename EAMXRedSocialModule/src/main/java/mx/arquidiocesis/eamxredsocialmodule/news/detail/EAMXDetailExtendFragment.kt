package mx.arquidiocesis.eamxredsocialmodule.news.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.gson.Gson
import mx.arquidiocesis.eamxcommonutils.application.AppMyConstants
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.util.*
import mx.arquidiocesis.eamxredsocialmodule.R
import mx.arquidiocesis.eamxredsocialmodule.news.detail.adapter.EAMXCarouselAdapter
import mx.arquidiocesis.eamxredsocialmodule.news.detail.bottomship.EamxBottomSheetDialogFragment
import mx.arquidiocesis.eamxredsocialmodule.model.like.EAMXLikeRequest

fun EAMXDetailFragment.initElements() {
    mBinding.apply {
//        callBack.showToolbar(true, AppMyConstants.noticias)
        //txtTitleFragmentWhiteRed.text = AppMyConstants.noticias
        //btnBackWhiteRed.setOnClickListener { activity?.onBackPressed() }
        txtName.text = modelPublication.author.name
        //Glide.with(this@initElements).load(modelPublication.author.image).centerCrop().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(imgUser)
        txtNameChurch.buildTextSuccess(modelPublication.content, root.context)
        txtNameChurch.setOnClickListener {
            urlPrueba = "".buildTextSuccessUrl(modelPublication.content)
            if (urlPrueba.urlValidator()) {
                val uri = Uri.parse(urlPrueba)
                val i = Intent(Intent.ACTION_VIEW, uri)
                startActivity(i)
            }
        }
        //txtCount.text = modelPublication.totalReactions.toString()
        txtCount.visibility = View.GONE
        val dateResponse = modelPublication.createdAt.toLong()
        val objectFormat = EAMXFormatDate(root.context)
        val miFecha = objectFormat.diferencia(dateResponse)
        txtDate.text = miFecha

        val adapter = EAMXCarouselAdapter(modelPublication.multimedia)
        viewPagerCarousel.adapter = adapter
        dotsIndicator.setViewPager2(viewPagerCarousel)

        adapter.onItemClickListener = { media, index ->

            if (!(media.format.contains("image") || media.format.contains("jpeg") || media.format.contains(
                    "jpg"
                ))
            ) {
                val i = Intent(activity, EAMXVideoPlayer::class.java)
                i.putExtra(EAMXVideoPlayer.VIDEO_URL, media.url)
                requireActivity().startActivity(i)
            } else {
                val bundle = Bundle()
                val items = Gson().toJson(modelPublication.multimedia).toString()
                bundle.putString(EAMXEnumUser.PUBLICATIONS.name, items)
                bundle.putInt(INDEX_FOCUS, index)
                if (modelPublication.multimedia.isNotEmpty()) {

                    eamxBackHandler.changeFragment(
                        EAMXExpandedViewFragment.newInstance(bundle),
                        R.id.contentFragmentRedSocial,
                        EAMXExpandedViewFragment::class.java.simpleName
                    )

                } else {
                    toast("No hay imagenes para mostrar")
                }
            }
        }
        /*txtOracion.setOnClickListener {
            val requestLike = EAMXLikeRequest(modelPublication.id)

            viewModel.requestLike(requestLike)
        }*/
        txtOracion.visibility = View.GONE
        layoutPlayer.setOnClickListener {
            if (SystemClock.elapsedRealtime() - lastClickTime < 2000) {
                return@setOnClickListener
            }
            lastClickTime = SystemClock.elapsedRealtime()
            val idReaction = modelPublication.id
            EamxBottomSheetDialogFragment(
                idReaction,
                eamxBackHandler
            ).show(
                (activity as FragmentActivity).supportFragmentManager,
                EamxBottomSheetDialogFragment.TAG
            )
        }
    }

}
