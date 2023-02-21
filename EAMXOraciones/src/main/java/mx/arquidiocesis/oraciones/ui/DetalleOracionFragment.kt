package mx.arquidiocesis.oraciones.ui

import android.content.*
import android.content.Context.ACTIVITY_SERVICE
import android.content.Context.CLIPBOARD_SERVICE
import android.graphics.Bitmap
import android.net.Uri
import android.net.Uri.parse
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.loader.content.CursorLoader
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.arquidiocesis.oraciones.databinding.FragmentDetalleOracionBinding
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.fragment_detalle_oracion.*
import kotlinx.android.synthetic.main.fragment_detalle_oracion.view.*
import kotlinx.android.synthetic.main.item_oracion.*
import mx.arquidiocesis.eamxcommonutils.application.AppMyConstants
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.common.EAMXHome
import mx.arquidiocesis.eamxcommonutils.util.getViewModel
import mx.arquidiocesis.eamxcommonutils.util.navigation.NavigationFragment
import mx.arquidiocesis.oraciones.adapter.OracionDetalleAdapter
import mx.arquidiocesis.oraciones.repository.Repository
import mx.arquidiocesis.oraciones.viewmodel.OracionDetallesViewModel
import java.io.ByteArrayOutputStream
import android.content.ContentResolver as ContentResolver1

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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

    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            inContext.contentResolver,
            inImage,
            "Title",
            null
        )
        return parse(path)
    }
}


