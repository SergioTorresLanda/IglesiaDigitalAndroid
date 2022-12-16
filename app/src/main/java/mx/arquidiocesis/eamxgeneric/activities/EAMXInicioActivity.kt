package mx.arquidiocesis.eamxgeneric.activities

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXTypeObject
import mx.arquidiocesis.eamxcommonutils.model.ViewPagerModel
import mx.arquidiocesis.eamxgeneric.R
import mx.arquidiocesis.eamxgeneric.databinding.EamxInicioActivityBinding
import mx.arquidiocesis.eamxcommonutils.util.ViewPager.ViewPagerPrincipal
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences

class EAMXInicioActivity : AppCompatActivity() {
    lateinit var mBinding: EamxInicioActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = EamxInicioActivityBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        val fragment = ViewPagerPrincipal(listOf(
            ViewPagerModel(getString(R.string.onbording_inicio_1),BitmapFactory.decodeResource(resources, R.drawable.onbording_1),
                1,  listOf(),true),
            ViewPagerModel(getString(R.string.onbording_inicio_2),BitmapFactory.decodeResource(resources, R.drawable.onbording_2),
                1),
            ViewPagerModel(getString(R.string.onbording_inicio_3),BitmapFactory.decodeResource(resources, R.drawable.onbording_3),
                2)
           )){
           leaveOnBoarding()
        }
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.clInicio, fragment).commit()


    }

    private fun leaveOnBoarding() {
        eamxcu_preferences.saveData(EAMXEnumUser.SKIP.name, true)
        startActivity(Intent(baseContext, EAMXHomeActivity::class.java))
        finish()
    }
}