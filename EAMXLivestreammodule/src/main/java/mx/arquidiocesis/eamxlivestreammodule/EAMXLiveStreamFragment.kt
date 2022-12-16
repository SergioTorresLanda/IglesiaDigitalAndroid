package mx.arquidiocesis.eamxlivestreammodule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxcommonutils.util.getViewModel
import mx.arquidiocesis.eamxcommonutils.util.openYoutubeApi
import mx.arquidiocesis.eamxlivestreammodule.adapter.EAMXVideoAdapter
import mx.arquidiocesis.eamxlivestreammodule.databinding.EamxLiveStreamFragmentBinding
import mx.arquidiocesis.eamxlivestreammodule.repository.LiveStreamRepository
import mx.arquidiocesis.eamxlivestreammodule.viewmodel.LiveVideoViewModel

class EAMXLiveStreamFragment : FragmentBase() {
    companion object {
        fun newInstance(): EAMXLiveStreamFragment {
            var liveStream = EAMXLiveStreamFragment()
            return liveStream
        }
    }

    private val viewModelLive: LiveVideoViewModel by lazy {
        getViewModel {
            LiveVideoViewModel(LiveStreamRepository(context = requireContext()))
        }
    }

    lateinit var mBinding: EamxLiveStreamFragmentBinding

    private lateinit var adapterVideo: EAMXVideoAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = EamxLiveStreamFragmentBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        iniciar()

    }


    private fun iniciar() {

        showLoader()
        viewModelLive.getVideos()
        initObservers()
    }

    private fun initObservers() {
        viewModelLive.videoResponse.observe(viewLifecycleOwner) {
            it?.let {l->
                adapterVideo = EAMXVideoAdapter(
                    context = requireContext(),
                    featuredList = l,
                ) { item ->
                    openYoutubeApi(item.streaming_url)
                }
                mBinding.rvVideos.apply {
                    layoutManager =
                        LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                    adapter = adapterVideo
                }
            }
            hideLoader()
        }

        viewModelLive.errorResponse.observe(viewLifecycleOwner) {
            UtilAlert.Builder()
                .setMessage("Aviso")
                .setMessage(it)
                .build().show(childFragmentManager, "")
            hideLoader()
        }
    }

}