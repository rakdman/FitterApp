package com.example.fittr.fragments.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fittr.databinding.LeaderboardCardviewBinding
import com.example.fittr.dtos.User


class LeaderboardAdapter (val context: Context, val users: ArrayList<User>) : RecyclerView.Adapter<LeaderboardAdapter.MyViewHolder>() {

    lateinit var binding: LeaderboardCardviewBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderboardAdapter.MyViewHolder {
        binding = LeaderboardCardviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return users.size
    }

    override fun onBindViewHolder(holder: LeaderboardAdapter.MyViewHolder, position: Int) {
        val user = users[position]
        holder.setData(user, position)
    }

    inner class MyViewHolder(binding: LeaderboardCardviewBinding) : RecyclerView.ViewHolder(binding.root) {

        var currentUser: User? = null
        var currentPosition: Int = 0

        fun setData(user: User?, pos: Int) {

            binding.userName.setText(user?.Name);
            binding.userKm.setText(String.format("%.2f",user?.Kms));
            binding.userLevel.setText(user?.Level.toString());

            this.currentUser = user
            this.currentPosition = pos
        }

    }

}