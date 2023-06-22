package  mx.arquidiocesis.eamxcommonutils.util.navigation

import android.os.Bundle
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

class NavigationFragment private constructor(
    private val activity: FragmentActivity,
    private val allowStack: Boolean,
    private val view: ViewGroup?,
    private val fragment: Fragment?,
) {
    class Builder {
        private var bundle : Bundle? = null
        private lateinit var activity : FragmentActivity
        private var fragment : Fragment? = null
        private var view : ViewGroup? = null
        private var allowStack : Boolean = true

        fun setBundle(bundle: Bundle) : Builder{
            this.bundle = bundle
            return this
        }

        fun setFragment(fragment: Fragment) : Builder{
            this.fragment = fragment
            return this
        }

        fun setAllowStack(allowStack : Boolean) : Builder{
            this.allowStack = allowStack
            return this
        }

        fun setView(view: ViewGroup): Builder {
            this.view = view
            return this
        }

        fun setActivity(activity: FragmentActivity): Builder {
            this.activity = activity
            return this
        }

        fun build() : NavigationFragment {
            bundle?.let { bundle ->
                this.fragment?.let {
                    it.arguments = bundle
                }
            }

            return NavigationFragment(this.activity, this.allowStack, this.view, this.fragment)
        }
    }

    fun nextWithReplace() {
        this.fragment?.let { frag ->
            this.view?.let{ vw ->
                val transaction = activity.supportFragmentManager.beginTransaction()
                if(!this.allowStack) {
                    transaction.addToBackStack(null)
                }
                transaction.replace(vw.id, frag).addToBackStack(frag.tag).commit()
            }
        }
    }

    fun back() {
        println("CLICK BACK")
        val manager = activity.supportFragmentManager
        if (manager.backStackEntryCount > 0) {
            manager.popBackStack()
        }
    }
}