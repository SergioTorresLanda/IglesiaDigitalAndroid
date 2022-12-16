package mx.arquidiocesis.eamxgeneric.customviews

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import mx.arquidiocesis.eamxcommonutils.util.getViewModel
import mx.arquidiocesis.eamxcommonutils.util.loadByUrl
import mx.arquidiocesis.eamxgeneric.databinding.FragmentPrayDialogBinding
import mx.arquidiocesis.eamxgeneric.repository.MainRepository
import mx.arquidiocesis.eamxgeneric.repository.MainRepository2
import mx.arquidiocesis.eamxgeneric.viewmodel.TokenViewModel

class PrayDialogFragment(val listener: (Int) -> Unit) : DialogFragment() {

    lateinit var binding: FragmentPrayDialogBinding
    var id: Int? = 0

    private val tokenViewModel: TokenViewModel by lazy {
        getViewModel {
            TokenViewModel(MainRepository2(requireContext()))
        }
    }

    companion object {
        fun newInstance(id: Int?, listener: (Int) -> Unit): PrayDialogFragment {
            val prayDialogFragment = PrayDialogFragment(listener)
            prayDialogFragment.id = id
            return prayDialogFragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPrayDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        id?.let {
            tokenViewModel.getPrayDetail(it)
        }

        initObservers()

    }

    fun initObservers() {
        tokenViewModel.prayResponse.observe(viewLifecycleOwner) { response ->
            binding.apply {
                tvPrayTitle.text = response.name

                response.image_url?.let { ivPray.loadByUrl(it) }

                tvPray.text = response.description

                btnVer.setOnClickListener {
                    listener(response.id)
                    dismiss()
                }

                btnClose.setOnClickListener {
                    dialog?.dismiss()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }
}