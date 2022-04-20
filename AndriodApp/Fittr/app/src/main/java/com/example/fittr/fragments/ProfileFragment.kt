package com.example.fittr.fragments

import android.Manifest
import android.R.attr
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.media.Image
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.fittr.MainActivity
import com.example.fittr.R
import com.example.fittr.databinding.FragmentProfileBinding
import com.example.fittr.fragments.adapters.ViewPageAdapter
import com.example.fittr.util.Variables
import com.google.android.gms.location.LocationServices
import org.json.JSONObject
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import android.graphics.Bitmap

import android.provider.MediaStore

import android.R.attr.data
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream
import java.util.*


class ProfileFragment : Fragment(), EasyPermissions.PermissionCallbacks {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    val REQUEST_CODE_DEVICE_STORAGE = 120
    val REQUEST_FOR_IMAGE = 405

    companion object
    {
        var ins : ProfileFragment? = null
        fun getInstance() : ProfileFragment?
        {
            return ins
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ins = this
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ViewPageAdapter(childFragmentManager)
        adapter.addFragment(HistoryFragment(),"")
        adapter.addFragment(BoughtVouchersFragment(),"")
        binding.viewPager.adapter=adapter
        binding.tabs.setupWithViewPager(binding.viewPager)
        binding.tabs.getTabAt(0)!!.setIcon(R.drawable.ic_baseline_accessibility_24)
        binding.tabs.getTabAt(1)!!.setIcon(R.drawable.ic_baseline_voucher_24)

        binding.changePhoto.setOnClickListener {
            changePhoto()
        }

        binding.profileImage.setOnClickListener {
            val dialogFragment : DialogFragment = ImageFragment()
            dialogFragment.show(activity?.supportFragmentManager!!,"Profile Image")
        }


    }

    private fun changePhoto()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        {
            if(!hasPermissions(this.requireContext()))
            {
                requestPermissions()
            }
            else
            {
                openGalleryForImage()
            }
        }
    }

    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_FOR_IMAGE)
    }

    fun getCurrentImage() : Drawable
    {
        return binding.profileImage.drawable
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_FOR_IMAGE){
            val selectedImage=data?.data!!
            val filePathColum=arrayOf(MediaStore.Images.Media.DATA)
            val cursor= context?.contentResolver?.query(selectedImage,filePathColum,null,null,null)
            cursor?.moveToFirst()
            val coloumnIndex=cursor?.getColumnIndex(filePathColum[0])
            val picturePath=cursor?.getString(coloumnIndex!!)

            var bitmap=MediaStore.Images.Media.getBitmap(context?.contentResolver,selectedImage)

            var encodeCode=imageToString(bitmap!!)
            uploadImage(Variables.userId!!,encodeCode,selectedImage)


        }
    }


    fun imageToString(bitmap: Bitmap):String {

        if (Build.VERSION.SDK_INT >= 26) {
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            var imageBytes = byteArrayOutputStream.toByteArray()
            val encodedImage = Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray())
            return encodedImage
        }
        return ""
    }


    private fun hasPermissions(context:Context) : Boolean =
        EasyPermissions.hasPermissions(
            context,
            Manifest.permission.READ_EXTERNAL_STORAGE
    )

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun requestPermissions() {
        EasyPermissions.requestPermissions(
            this,
            "You Need to Allow This Permission",
            REQUEST_CODE_DEVICE_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        openGalleryForImage()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            requestPermissions()
        }
    }

    override fun onResume() {
        super.onResume()
        var ins = MainActivity.getInstance()
        binding.distance.text = String.format("%.2f",ins?.getKms())
        binding.coins.text = ins?.getCoinsCount().toString()
        binding.level.text = ins?.getCurrentLevel().toString()
    }

    fun uploadImage(userId:Int, image:String, selectedImage : Uri)
    {
        val queue = Volley.newRequestQueue(context)
        var url="${Variables.url}/uploadimage/${userId}"
        val builder = Uri.parse(url).buildUpon()
        val params = mapOf<String, Any>(
            "image" to image
        )
        val jsonObject = JSONObject(params)

        val request = object : JsonObjectRequest(
            Request.Method.PATCH, builder.toString(), jsonObject,
            Response.Listener { response ->
                var strResp = response.toString()
                val jsonObj: JSONObject = JSONObject(strResp)
                Toast.makeText(context,"Image Uploaded", Toast.LENGTH_SHORT).show()
                binding.profileImage.setImageURI(selectedImage) // handle chosen image
            },
            Response.ErrorListener { error ->
                Toast.makeText(context,"Registeration Failed", Toast.LENGTH_SHORT).show()
            }) {

        }
        queue.add(request)
    }





}