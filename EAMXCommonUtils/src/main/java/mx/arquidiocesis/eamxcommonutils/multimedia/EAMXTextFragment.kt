package mx.arquidiocesis.eamxcommonutils.multimedia


import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import mx.arquidiocesis.eamxcommonutils.R
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.databinding.FragmentPdfBinding
import mx.arquidiocesis.eamxcommonutils.databinding.FragmentPlayerBinding
import mx.arquidiocesis.eamxcommonutils.databinding.FragmentTextBinding
import mx.arquidiocesis.eamxcommonutils.util.loadByUrl
import java.io.ByteArrayOutputStream

class EAMXTextFragment : Fragment() {

    lateinit var binding: FragmentTextBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentTextBinding.inflate(inflater, container, false)
        val text = arguments?.getString("text")
        val img = arguments?.getString("img")

        if (text != null) {
            binding.text.setText(text.toString())
        } else {
            binding.text.setText("Ha ocurrido un error, intente nuevamente.")
        }
        if (img != null) {
            binding.imageT.loadByUrl(img)
            binding.imageT.setOnClickListener {
                binding.imageT.buildDrawingCache();
                val image: Bitmap = binding.imageT.getDrawingCache()
                val share = Intent(Intent.ACTION_SEND)
                share.type = "image/*"
                share.putExtra(Intent.EXTRA_STREAM, getImageUri(requireActivity(), image))
                startActivity(Intent.createChooser(share, "Compartir con"))
            }

        } else {

        }
        return binding.root
    }

    /*override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }*/

    fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            inContext.contentResolver,
            inImage,
            "Title",
            null
        )
        return Uri.parse(path)
    }

    private fun initView() {
        //showLoader("lOADER")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //callBack.restoreToolbar()
    }
}