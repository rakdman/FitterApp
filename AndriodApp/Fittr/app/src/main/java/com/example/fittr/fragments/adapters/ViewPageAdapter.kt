package com.example.fittr.fragments.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter


class ViewPageAdapter(supportFragmentManager: FragmentManager) : FragmentPagerAdapter(supportFragmentManager,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val fragemntsList = ArrayList<Fragment>()
    private val fragmentsTitleList = ArrayList<String>()

    override fun getCount(): Int {
        return fragemntsList.size
    }

    override fun getItem(position: Int): Fragment {
        return fragemntsList[position]
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return fragmentsTitleList[position]
    }



    fun addFragment(fragment: Fragment,title:String)
    {
        fragemntsList.add(fragment)
        fragmentsTitleList.add(title)
    }

}