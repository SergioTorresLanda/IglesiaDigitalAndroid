package mx.arquidiocesis.eamxredsocialmodule.ui

import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import mx.arquidiocesis.eamxcommonutils.api.core.response.EAMXGenericResponse
import mx.arquidiocesis.eamxredsocialmodule.model.EAMXPublicationsAllRequest
import mx.arquidiocesis.eamxredsocialmodule.model.EAMXPublicationsAllResponse

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
            val lastVisibleItemPosition: Int = layoutManager.findLastVisibleItemPosition()
            //if(totalItemCount>1) {
                if (totalItemCount!=1&&totalItemCount - 1 == lastVisibleItemPosition && cargado) {
                    rechargePost()
                }
            //}
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
        isLoadingMore = false
    }
}


