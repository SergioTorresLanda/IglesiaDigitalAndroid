package mx.arquidiocesis.eamxcommonutils.util.contrasresult

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.content.FileProvider
import mx.arquidiocesis.eamxcommonutils.util.eamxLog
import java.io.File
import java.util.*

class TakePhoto : ActivityResultContract<Context, Uri>() {

    private var fileImage: Uri? = null

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        return if ( resultCode == Activity.RESULT_OK) fileImage
        else null
    }

    override fun createIntent(context: Context, input: Context?): Intent {
        fileImage = FileProvider.getUriForFile(
            context,
            context.applicationContext.packageName + ".provider",
            File.createTempFile("img_profile_${Date().time}".take(15), ".png", context.cacheDir).apply {
                createNewFile()
                deleteOnExit()
            }
        )
        return Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            .putExtra(MediaStore.EXTRA_OUTPUT, fileImage)
    }

}