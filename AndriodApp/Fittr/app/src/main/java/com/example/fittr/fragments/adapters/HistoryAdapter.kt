package com.example.fittr.fragments.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fittr.R
import com.example.fittr.databinding.HistoryCardviewBinding
import com.example.fittr.databinding.VoucherCardviewBinding
import com.example.fittr.dtos.Trip
import com.example.fittr.dtos.Voucher
import com.example.fittr.fragments.BikeFragment

class HistoryAdapter (val context: Context, val trips: ArrayList<Trip>) : RecyclerView.Adapter<HistoryAdapter.MyViewHolder>() {

    lateinit var binding: HistoryCardviewBinding

    companion object {
        val TAG: String = HistoryAdapter::class.java.simpleName
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryAdapter.MyViewHolder {
        binding = HistoryCardviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return trips.size
    }

    override fun onBindViewHolder(holder: HistoryAdapter.MyViewHolder, position: Int) {
        val trip = trips[position]
        holder.setData(trip, position)
    }

    inner class MyViewHolder(binding: HistoryCardviewBinding) : RecyclerView.ViewHolder(binding.root) {

        var currentTrip: Trip? = null
        var currentPosition: Int = 0

        fun setData(trip:Trip?, pos: Int) {

            when(trip?.activity)
            {
                BikeFragment.activityEnum.WALK -> { binding.activityType.setImageResource(R.drawable.ic_baseline_directions_walk_24) }
                BikeFragment.activityEnum.CYCLE -> { binding.activityType.setImageResource(R.drawable.ic_baseline_directions_bike_24) }
                BikeFragment.activityEnum.RUN -> {  binding.activityType.setImageResource(R.drawable.ic_baseline_directions_run_24) }
                else -> {}
            }

            binding.kmsCount.setText(String.format("%.2f",trip?.km))
            binding.coinsCount.setText(trip?.coins.toString())

//            Picasso.get().load(new!!.image).into(itemView.newpic)
//            if(itemView.newpic==null)
//            {
//                itemView.newpic.newpic.visibility= View.GONE
//            }
            this.currentTrip = trip
            this.currentPosition = pos
        }

    }




}