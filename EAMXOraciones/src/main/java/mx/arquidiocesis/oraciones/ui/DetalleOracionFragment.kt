package mx.arquidiocesis.oraciones.ui

import android.content.*
import android.graphics.Bitmap
import android.net.Uri
import android.net.Uri.parse
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.arquidiocesis.oraciones.databinding.FragmentDetalleOracionBinding
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.fragment_detalle_oracion.*
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.common.EAMXHome
import mx.arquidiocesis.eamxcommonutils.util.EAMXFirebaseManager
import mx.arquidiocesis.eamxcommonutils.util.getViewModel
import mx.arquidiocesis.eamxcommonutils.util.navigation.NavigationFragment
import mx.arquidiocesis.oraciones.repository.Repository
import mx.arquidiocesis.oraciones.viewmodel.OracionDetallesViewModel
import java.io.ByteArrayOutputStream
import java.time.LocalDateTime

class DetalleOracionFragment : FragmentBase() {

    companion object {
        fun newInstance(): DetalleOracionFragment {
            var detalleFragment = DetalleOracionFragment()
            return detalleFragment
        }
    }

    lateinit var binding: FragmentDetalleOracionBinding
    private val oracionDetalleViewModel: OracionDetallesViewModel by lazy {
        getViewModel {
            OracionDetallesViewModel(Repository(requireContext()))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetalleOracionBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            EAMXFirebaseManager(it).setLogEvent("screen_view", Bundle().apply {
                putString("screen_class", "Oraciones_Oracion")
            })
        }
        btnBack.setOnClickListener {
            NavigationFragment.Builder()
                .setActivity(requireActivity())
                .setView(requireView().parent as ViewGroup)
                .setFragment(OracionesFragment.newInstance())
                .setAllowStack(true)
                .build().nextWithReplace()
            //activity?.onBackPressed()
        }
        val idOracion = requireArguments().getInt("idOracion")
        oracionDetalleViewModel.obtenerDetalle(idOracion)
        showLoader()
        initObservers()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun initObservers() {

        oracionDetalleViewModel.response.observe(viewLifecycleOwner) { oracionModel ->
            binding.tvDetalleOracionText.text = oracionModel.description
            callBack = (activity as EAMXHome)
            callBack.showToolbar(true, oracionModel.name)
            Glide.with(requireContext()).load(parse(oracionModel.image_url)).apply(RequestOptions()).into(binding.ivDetalleOracion)
            binding.ivDetalleOracion.isSelected
            hideLoader()
            binding.shareImg.setOnClickListener {
                binding.ivDetalleOracion.buildDrawingCache();
                val image: Bitmap = binding.ivDetalleOracion.getDrawingCache()
                val share = Intent(Intent.ACTION_SEND)
                share.type = "image/*"
                share.putExtra(Intent.EXTRA_STREAM, getImageUri(requireActivity(), image))
                startActivity(Intent.createChooser(share, "Compartir con"))
            }
            binding.shareText.setOnClickListener {
                //binding.tvDetalleOracionText.setTextIsSelectable(true)
                val text = binding.tvDetalleOracionText.getText().toString()
                val share = Intent(Intent.ACTION_SEND)
                share.type = "text/*"
                share.putExtra(Intent.EXTRA_TEXT, text)
                startActivity(Intent.createChooser(share, "Compartir con"))
            }

            oracionDetalleViewModel.errorResponse.observe(viewLifecycleOwner) {
                println("ErrorResponse")
                hideLoader()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val datetime = LocalDateTime.now()
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            inContext.contentResolver,
            inImage,
            "Oracion_${datetime.hour}_${datetime.minute}_${datetime.second}",
            null
        )
        return parse(path)
    }
}


