package mx.arquidiocesis.eamxprofilemodule.ui.promises.createpromise.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import mx.arquidiocesis.eamxcommonutils.common.EAMXBaseFragment
import mx.arquidiocesis.eamxprofilemodule.R
import mx.arquidiocesis.eamxprofilemodule.databinding.EamxProfilePromisesCreateBinding

class EAMXProfilePromisesCreate : EAMXBaseFragment() {
    lateinit var vBind: EamxProfilePromisesCreateBinding
    private val adapter = RecyclerAdapter()

    override fun getLayout(): Int = R.layout.eamx_profile_promises_create

    override fun initBinding(view: View) {
        vBind = EamxProfilePromisesCreateBinding.bind(view)

    }

    override fun initDependency(savedInstanceState: Bundle?) {

    }

    override fun initObservers() {

    }

    @SuppressLint("ResourceType")
    override fun initView(view: View) {
        initListener(vBind, activity, adapter)



        /*setupRecyclerView(vBind.rvSaintsImages, requireContext())
        val adapter = EAMXProfielPromisesAdapter()

        adapter.submitList(
                arrayListOf(
                        EAMXSaintModel("SAINT_1", ContextCompat.getDrawable(requireActivity(), R.drawable.saint_001)),
                        EAMXSaintModel("SAINT_2", ContextCompat.getDrawable(requireActivity(), R.drawable.saint_002)),
                        EAMXSaintModel("SAINT_3", ContextCompat.getDrawable(requireActivity(), R.drawable.saint_003)),
                        EAMXSaintModel("SAINT_4", ContextCompat.getDrawable(requireActivity(), R.drawable.saint_004)),
                        EAMXSaintModel("SAINT_5", ContextCompat.getDrawable(requireActivity(), R.drawable.saint_005)),
                        EAMXSaintModel("SAINT_6", ContextCompat.getDrawable(requireActivity(), R.drawable.saint_006)),
                )
        )

        vBind.rvSaintsImages.adapter = adapter
        vBind.rvSaintsImages.addItemDecoration(
                EAMXSaintItemDecoration(
                        horizontalMargin = 10,
                        adjacentVisibleSize = 250
                )
        )

        vBind.rvSaintsImages.addOnScrollListener(EAMXSaintScrollListener())
        val snapHelperCenter: SnapHelper = LinearSnapHelper()
        snapHelperCenter.attachToRecyclerView(vBind.rvSaintsImages)
    }


    override fun setViewModel() {

         */


    }

    override fun setViewModel() {
    }

    companion object {
        @JvmStatic
        fun newInstance() = EAMXProfilePromisesCreate()
//        fun newInstance(callBack: EAMXHome): EAMXProfilePromisesCreate {
//            var promesasCreate = EAMXProfilePromisesCreate()
//            promesasCreate.callBack = callBack
//            return promesasCreate
//        }
    }

//    override fun onDestroyView() {
//        super.onDestroyView()
//        callBack.restoreToolbar()
//    }
}