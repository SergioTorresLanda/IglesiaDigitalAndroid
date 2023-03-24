package mx.arquidiocesis.eamxevent.ui

import android.view.View
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_event.*
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXProfile
import mx.arquidiocesis.eamxcommonutils.common.EAMXTypeObject
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences

fun EventFragment.setupInit(){
    var guest = eamxcu_preferences.getData(
        EAMXEnumUser.GUEST.name,
        EAMXTypeObject.BOOLEAN_OBJECT
    ) as Boolean
    val profile = eamxcu_preferences.getData(
        EAMXEnumUser.USER_PROFILE.name,
        EAMXTypeObject.STRING_OBJECT
    ) as String
    if (guest) {
        tvNewEvent.visibility = View.GONE
    }
    when (profile) {
        EAMXProfile.Devoted.rol, EAMXProfile.Priest.rol
        -> {
            tvNewEvent.visibility = View.GONE
        }
    }
}

fun EventFragment.setupRecyclerView() {
    rvEvents.adapter = adapter
    rvEvents.layoutManager = LinearLayoutManager(context)
    rvEvents.itemAnimator = DefaultItemAnimator()
    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
        }
    }
}