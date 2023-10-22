package ch.ost.rj.mge.isstracker.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ch.ost.rj.mge.isstracker.R
import ch.ost.rj.mge.isstracker.databinding.FragmentHomeBinding
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import org.osmdroid.config.Configuration.*
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.io.IOException
import androidx.fragment.app.FragmentTransaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class HomeFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var map : GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val mapFragment = childFragmentManager.fragments[0] as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    fun getPosData(url: String, onResponse: (Double, Double) -> Unit){
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        GlobalScope.launch(Dispatchers.IO){
            try{
                val response = client.newCall(request).execute()
                if(response.isSuccessful){
                    val responseBody = response.body?.string()
                    val jObject = JSONObject(JSONObject(responseBody).get("iss_position").toString());
                    val latitude = jObject.get("latitude").toString().toDouble()
                    val longitude = jObject.get("longitude").toString().toDouble()

                    withContext(Dispatchers.Main){
                        onResponse(latitude, longitude)
                    }
                }
            } catch (e: Exception){
                e.printStackTrace()
            }
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        getPosData("http://api.open-notify.org/iss-now.json") {latitude, longitude ->
            val position = LatLng(latitude,longitude)
            googleMap.addMarker(
                MarkerOptions()
                    .position(position)
                    .title("Marker")
            )
        }
        /**/
    }


}