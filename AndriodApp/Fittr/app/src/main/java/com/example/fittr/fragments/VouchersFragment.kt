package com.example.fittr.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.fittr.MainActivity
import com.example.fittr.databinding.FragmentVouchersBinding
import com.example.fittr.dtos.Voucher
import com.example.fittr.fragments.adapters.VouchersAdapter
import com.example.fittr.util.Variables
import org.json.JSONArray
import org.json.JSONObject


class VouchersFragment : Fragment() {

    private var _binding: FragmentVouchersBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentVouchersBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getVouchers()
    }

    fun getVouchers()
    {
        val queue = Volley.newRequestQueue(context)
        var url="${Variables.url}/getallvouchers"
        val builder = Uri.parse(url).buildUpon()
        val params = mapOf<String, Any>()
        val jsonObject = JSONObject(params)
        var voucherArray = arrayListOf<Voucher>()

        val request = object : JsonObjectRequest(
            Request.Method.GET, builder.toString(), jsonObject,
            Response.Listener { response ->
                var strResp = response.toString()
                val jsonObj: JSONObject = JSONObject(strResp)
                val jsonArray: JSONArray = jsonObj.getJSONArray( "VOUCHERS")
                for(i in 0..jsonArray.length()-1) {
                    var jsonObj = jsonArray.getJSONObject(i)
                    var voucherId = jsonObj.getInt("voucherId")
                    var voucherTitle = jsonObj.getString("voucherTitle")
                    var voucherDesc = jsonObj.getString("voucherDesc")
                    var voucherCoins = jsonObj.getInt("voucherCoins")
                    var voucherImage = jsonObj.getString("voucherImage")
                    voucherArray.add(Voucher(id = voucherId,Image = voucherImage,title = voucherTitle,description = voucherDesc,price = voucherCoins))
                }
                val adapter = VouchersAdapter(requireContext(),voucherArray,false)
                binding.recycler.adapter=adapter
                val layoutManager = GridLayoutManager(requireContext(),2)
                //layoutManager.orientation= RecyclerView.VERTICAL

                binding.recycler.layoutManager=layoutManager
                binding.recycler.canScrollVertically(1)

            },
            Response.ErrorListener { error ->
                Toast.makeText(context,"Network Failure", Toast.LENGTH_SHORT).show()
            }) {

        }
        queue.add(request)
    }
}