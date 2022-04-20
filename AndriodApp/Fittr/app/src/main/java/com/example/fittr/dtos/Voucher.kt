package com.example.fittr.dtos

data class Voucher (
    var id : Int,
    var Image : String,
    var title : String,
    var description : String,
    var price : Int,
    var purchaseDate : String?
)
{
    constructor(id:Int,Image:String,title: String,description: String,price: Int) :  this(id,Image,title,description,price,null)
}