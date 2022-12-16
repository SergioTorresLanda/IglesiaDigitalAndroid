package mx.arquidiocesis.eamxredsocialmodule.ui

import android.content.Intent
import android.net.Uri
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import mx.arquidiocesis.eamxcommonutils.api.core.response.EAMXGenericResponse
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxcommonutils.util.buildTextSuccessUrl
import mx.arquidiocesis.eamxcommonutils.util.urlValidator
import mx.arquidiocesis.eamxredsocialmodule.R
import mx.arquidiocesis.eamxredsocialmodule.model.EAMXPublicationsAllRequest
import mx.arquidiocesis.eamxredsocialmodule.model.EAMXPublicationsAllResponse
import mx.arquidiocesis.eamxredsocialmodule.model.like.EAMXLikeRequest


fun EAMXRedSocialFragment.setupRecyclerView() {

    binding.apply {
        rvPubliction.adapter = adapter
        rvPubliction.layoutManager = LinearLayoutManager(context)
        rvPubliction.itemAnimator = DefaultItemAnimator()
    }

    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = binding.rvPubliction.layoutManager!! as LinearLayoutManager

            val visibleItemCount: Int = layoutManager.childCount
            val totalItemCount: Int = layoutManager.itemCount
            val firstVisibleItemPosition: Int = layoutManager.findFirstVisibleItemPosition()
            val ultmiVisto: Int = layoutManager.findLastVisibleItemPosition()
            if(totalItemCount-1==ultmiVisto&&cargado){
                rechargePost()
            }
           //
            /*    binding.loadMorePublications(
                    isLoadingMore,
                    visibleItemCount,
                    totalItemCount,
                    firstVisibleItemPosition,
                    totalLastResult,
                    request
                ) {
                    isLoadingMore = true
                    //mBinding.pbLoading.visibility = View.VISIBLE
                }*/
        }
    }

    binding.rvPubliction.addOnScrollListener(scrollListener)

    /*adapter.onItemClickListener = { item, Etiqueta ->
        val bundle = Bundle()
        bundle.putParcelable(EAMXEnumUser.PUBLICATIONS.name, item)

        if (item.content.urlValidator()) {
            val uri = Uri.parse(item.content)
            val i = Intent(Intent.ACTION_VIEW, uri)
            startActivity(i)
        } else {
            eamxBackHandler.addFragment(
                EAMXDetailFragment.newInstance(bundle, callBack),
                R.id.contentFragmentRedSocial,
                EAMXDetailFragment::class.java.simpleName
            )
        }
    }
*/
}

fun EAMXRedSocialFragment.stopLoadMore(request: EAMXGenericResponse<EAMXPublicationsAllResponse, String, EAMXPublicationsAllRequest>) {
    if (isLoadingMore) {
        totalLastResult = request.successData?.publications?.size ?: -1
        //mBinding.pbLoading.visibility = View.GONE
        isLoadingMore = false
    }
}


