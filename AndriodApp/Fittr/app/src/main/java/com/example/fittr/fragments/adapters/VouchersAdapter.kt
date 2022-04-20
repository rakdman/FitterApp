package com.example.fittr.fragments.adapters

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import androidx.viewbinding.ViewBindings
import com.example.fittr.MainActivity
import com.example.fittr.R
import com.example.fittr.databinding.VoucherBoughtCardviewBinding
import com.example.fittr.databinding.VoucherCardviewBinding
import com.example.fittr.dtos.Voucher
import com.google.android.material.dialog.MaterialAlertDialogBuilder

import java.util.*
import kotlin.collections.ArrayList
import android.util.Base64
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.fittr.util.Variables
import org.json.JSONObject

class VouchersAdapter (val context: Context, val vouchers: ArrayList<Voucher> , val purchased : Boolean) : RecyclerView.Adapter<VouchersAdapter.MyViewHolder>() {

    lateinit var binding: ViewBinding

    companion object {
        val  NOTPURCHASED = 1;
        val  PURCHASED = 2;
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        if(viewType == NOTPURCHASED)
        {
            binding = VoucherCardviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return MyViewHolderNotPurchased(binding as VoucherCardviewBinding);
        }
        else
        {
            binding = VoucherBoughtCardviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return MyViewHolderPurchased(binding as VoucherBoughtCardviewBinding);
        }


    }

    override fun getItemCount(): Int {
        return vouchers.size
    }

    override fun getItemViewType(position: Int): Int {
        if(purchased)
        {
            return PURCHASED;
        }
        else
        {
            return NOTPURCHASED
        }
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if(getItemViewType(position)== NOTPURCHASED)
        {
            val holderNotPurchased = holder as MyViewHolderNotPurchased
            val vouchers = vouchers[position]
            holderNotPurchased.setData(vouchers, position)
        }
        else{
            val holderPurchased = holder as MyViewHolderPurchased
            val vouchers = vouchers[position]
            holderPurchased.setData(vouchers, position)
        }
    }


    abstract inner class MyViewHolder(binding : ViewBinding) : RecyclerView.ViewHolder(binding.root)
    {

    }


    inner class MyViewHolderPurchased(binding: VoucherBoughtCardviewBinding) : MyViewHolder(binding)
    {

        var currentVoucher: Voucher? = null
        var currentPosition: Int = 0


        init {

        }

        fun setData(voucher:Voucher?, pos: Int) {

            (binding as VoucherBoughtCardviewBinding).title.setText(voucher?.title);
            val imageBytes = Base64.decode(voucher?.Image, Base64.DEFAULT)
            val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            (binding as VoucherCardviewBinding).image.setImageBitmap(decodedImage)

            this.currentVoucher = voucher
            this.currentPosition = pos
        }

    }

    inner class MyViewHolderNotPurchased(binding: VoucherCardviewBinding) : MyViewHolder(binding) {

        var currentVoucher: Voucher? = null
        var currentPosition: Int = 0

        init {
            itemView.setOnClickListener {
                //TODO Voucher Card View is Clicked
                val ins = MainActivity.getInstance()
                val builder = MaterialAlertDialogBuilder(context)
                builder.setTitle("Item Purchase")
                builder.setIcon(R.drawable.cyclist)
                builder.setMessage("Are you sure you want to purchase this item?")
                builder.setPositiveButton("Buy") { dialog, which ->

                    if(ins!!.getCoinsCount()<currentVoucher!!.price)
                    {
                        Toast.makeText(context,"Insufficient Funds To buy this item",Toast.LENGTH_SHORT).show()
                    }
                    else
                    {
                        buyVoucher(Variables.userId!!,currentVoucher!!.id,currentVoucher!!.price)
                    }
                }

                builder.setNegativeButton("Cancel") { dialog, which ->

                }
                builder.show()
            }
        }

        fun setData(voucher:Voucher?, pos: Int) {

            (binding as VoucherCardviewBinding).title.setText(voucher?.title);
            (binding as VoucherCardviewBinding).description.setText(voucher?.description);
            (binding as VoucherCardviewBinding).price.setText(voucher?.price.toString())

            val imageBytes = Base64.decode(voucher?.Image, Base64.DEFAULT)
            val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            (binding as VoucherCardviewBinding).image.setImageBitmap(decodedImage)

//            Picasso.get().load(new!!.image).into(itemView.newpic)
//            if(itemView.newpic==null)
//            {
//                itemView.newpic.newpic.visibility= View.GONE
//            }
            this.currentVoucher = voucher
            this.currentPosition = pos
        }

        fun buyVoucher(userId : Int , voucherId : Int,price:Int)
        {
            val queue = Volley.newRequestQueue(context)
            var url="${Variables.url}/purchasevoucher/${userId}"
            val builder = Uri.parse(url).buildUpon()
            val params = mapOf<String, Any>(
                "voucherId" to voucherId
            )
            val jsonObject = JSONObject(params)

            val request = object : JsonObjectRequest(
                Request.Method.POST, builder.toString(), jsonObject,
                Response.Listener { response ->
                    var strResp = response.toString()
                    val jsonObj: JSONObject = JSONObject(strResp)
                    MainActivity.getInstance()?.setCoinsCount(MainActivity.getInstance()?.getCoinsCount()!!-price)
                    Toast.makeText(context,"Item Bought",Toast.LENGTH_SHORT).show()

                },
                Response.ErrorListener { error ->
                    Toast.makeText(context,"Purchase Failed", Toast.LENGTH_SHORT).show()
                }) {

            }
            queue.add(request)
        }
    }
}
