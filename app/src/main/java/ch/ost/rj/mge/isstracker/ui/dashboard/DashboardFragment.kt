package ch.ost.rj.mge.isstracker.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ch.ost.rj.mge.isstracker.R
import ch.ost.rj.mge.isstracker.databinding.FragmentDashboardBinding
import com.google.android.gms.maps.SupportMapFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getPeopleInSpace(){numberOfPeople ->
            // Todo: Show number of People in space on screen
            val textView = getView()?.findViewById<TextView>(R.id.information_textView)
            textView?.text = getString(R.string.info_text)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun getPeopleInSpace(onResponse: (String) -> Unit) {
        val url = "http://api.open-notify.org/astros.json"
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        GlobalScope.launch(Dispatchers.IO){
            try{
                val response = client.newCall(request).execute()
                if(response.isSuccessful){
                    val responseBody = response.body?.string()

                    val jObject = JSONObject(responseBody)

                    val numberOfPeople = jObject.get("number").toString()

                    withContext(Dispatchers.Main){
                        onResponse(numberOfPeople)
                    }
                }
            } catch (e: Exception){
                e.printStackTrace()
            }
        }

    }
}