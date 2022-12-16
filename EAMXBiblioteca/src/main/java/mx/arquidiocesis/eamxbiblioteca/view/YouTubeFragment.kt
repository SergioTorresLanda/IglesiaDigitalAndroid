package mx.arquidiocesis.eamxbiblioteca.view

import androidx.fragment.app.Fragment


class YouTubeFragment : Fragment() {


    /*
    companion object {
        fun newInstance(idYoutube: String): YouTubeFragment {
            val fragment = YouTubeFragment()
            fragment.idYoutube = idYoutube
            return fragment
        }
    }

    private var idYoutube: String = ""
    var youTubePlayer = MutableLiveData<YouTubePlayer>()
    private var myContext: FragmentActivity? = null
    override fun onAttach(activity: Activity) {
        if (activity is FragmentActivity) {
            myContext = activity
        }
        super.onAttach(activity)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_youtube, container, false)
        val APIKEY = "AIzaSyAqcJAgBOE_cPCZYlsRbc7pL1Z0vCTayok"
        val youTubePlayerFragment =
            YouTubePlayerSupportFragment.newInstance()
        val transaction =
            childFragmentManager.beginTransaction()
        transaction.replace(R.id.youtube_fragment, youTubePlayerFragment)
        transaction.commit()



        youTubePlayerFragment.initialize(APIKEY, object : YouTubePlayer.OnInitializedListener {
            override fun onInitializationSuccess(
                arg0: YouTubePlayer.Provider?,
                p1: YouTubePlayer,
                b: Boolean
            ) {
                youTubePlayer.value = p1
            }

            override fun onInitializationFailure(
                arg0: YouTubePlayer.Provider?,
                arg1: YouTubeInitializationResult?
            ) {
            }
        })

        initObservers()

        return view
    }

    fun initObservers() {
        youTubePlayer.observe(viewLifecycleOwner) { player ->
            player.setFullscreen(false)
            player.loadVideo(idYoutube)
        }
    }
*/
}