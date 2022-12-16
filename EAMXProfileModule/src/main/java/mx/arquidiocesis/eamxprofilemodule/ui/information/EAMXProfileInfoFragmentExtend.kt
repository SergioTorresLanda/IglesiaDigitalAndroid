package mx.arquidiocesis.eamxprofilemodule.ui.information

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.gson.Gson
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXTypeObject
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences
import mx.arquidiocesis.eamxprofilemodule.R
import mx.arquidiocesis.eamxprofilemodule.databinding.EamxProfileInfoFragmentBinding
import mx.arquidiocesis.eamxprofilemodule.model.DataWithDescriptionCode
import mx.arquidiocesis.eamxprofilemodule.model.local.DataModelSharedPreferences
import mx.arquidiocesis.eamxprofilemodule.model.userdetail.LifeStatusModel
import mx.arquidiocesis.eamxprofilemodule.repository.CATALOG_LIFE_STATE
import mx.arquidiocesis.eamxprofilemodule.repository.LocalRepository

fun initElements(
    binding: EamxProfileInfoFragmentBinding,
    activity: FragmentActivity?,
    viewProfile: EAMXProfileInfoFragment,
) {
    val user = eamxcu_preferences.getData(EAMXEnumUser.USER_PROFILE.name, EAMXTypeObject.STRING_OBJECT) as String

    binding.apply {
        rvThemesInterest.apply {
            layoutManager =
                StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
        }

        spStyleLife.setOnTouchListener { v, _ ->
            hideKeyboard(activity, v)
            false
        }

        spTopics.setOnTouchListener { v, _ ->
            hideKeyboard(activity, v)
            false
        }

        spServicesChurch.setOnTouchListener { v, _ ->
            hideKeyboard(activity, v)
            false
        }
        swResponsibleCommunity.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                tvSwResposibleCommunity.text = "Sí"
                //etSearchCommunity.visibility = View.VISIBLE
            } else {
                tvSwResposibleCommunity.text = "No"
                // etSearchCommunity.visibility = View.GONE
                //etSearchCommunity.setText("")
                //idComunnity = 0
            }
        }
        rgDiocesanOrReligious.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbYes -> {
                    etSearchChurch.visibility = View.VISIBLE
                    llResponsibleCommunity.visibility = View.GONE
                }
                R.id.rbYesC -> {
                    llResponsibleCommunity.visibility = View.VISIBLE
                    etSearchChurch.visibility = View.GONE
                    rvChurch.visibility = View.GONE
                }
                R.id.rbNo -> {
                    etSearchChurch.visibility = View.GONE
                    llResponsibleCommunity.visibility = View.GONE
                    rvChurch.visibility = View.GONE
                    viewProfile.churchAdapter.clear()
                }
            }
        }

        spStyleLife.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long,
                ) {
                    if (!viewProfile.isLoading) {
                        binding.btnSave.text = "Guardar"
                        val item = binding.spStyleLife.selectedItem as DataWithDescriptionCode
                        viewProfile.setView(item.description,
                            true,
                            spStyleLife.isEnabled,
                            item.code)
                    } else {
                        viewProfile.isLoading = false
                    }
                }
            }
    }


}

fun hideKeyboard(activity: FragmentActivity?, view: View) {
    val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

