package com.example.fashionsapp.data

import android.os.Parcel
import android.os.Parcelable

data class ItemFashion(val title: String, val note: String,val review: String, val money: String, val description: String, val urlImage: String) :
    Parcelable {constructor(parcel: Parcel) : this(
    parcel.readString() ?: "",
    parcel.readString() ?: "",
    parcel.readString() ?: "",
    parcel.readString() ?: "",
    parcel.readString() ?: "",
    parcel.readString() ?: ""
)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(note)
        parcel.writeString(review)
        parcel.writeString(money)
        parcel.writeString(description)
        parcel.writeString(urlImage)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ItemFashion> {
        override fun createFromParcel(parcel: Parcel): ItemFashion {
            return ItemFashion(parcel)
        }

        override fun newArray(size: Int): Array<ItemFashion?> {
            return arrayOfNulls(size)
        }
    }}
 var fashionsList = listOf(
    ItemFashion("Roller Rabbit", "Traveler Tone","320", "$195.00","Get a little lift from these Sam Edelman sandals  Get a little lift from these Sam Edelman sandals featuring ruched straps and leather lace-up ties, while a braided ju Get a little lift from these Sam Edelman sandals featuring ruched straps and leather lace-up ties, while a braided ju Get a little lift from these Sam Edelman sandals featuring ruched straps and leather lace-up ties, while a braided juGet a little lift from these Sam Edelman sandals featuring ruched straps and leather lace-up ties, while a braided juGet a little lift from these Sam Edelman sandals featuring ruched straps and leather lace-up ties, while a braided juGet a little lift from these Sam Edelman sandals featuring ruched straps and leather lace-up ties, while a braided juGet a little lift from these Sam Edelman sandals featuring ruched straps and leather lace-up ties, while a braided juGet a little lift from these Sam Edelman sandals featuring ruched straps and leather lace-up ties, while a braided juGet a little lift from these Sam Edelman sandals featuring ruched straps and leather lace-up ties, while a braided juGet a little lift from these Sam Edelman sandals featuring ruched straps and leather lace-up ties, while a braided jufeaturing ruched straps and leather lace-up ties, while a braided jute sole makes a fresh statement for summer.", "https://www.vascara.com/uploads/cms_productmedia/2023/July/30/tui-xach-cong-so-quai-dinh-metallic---tot-0117---mau-den__71052__1690657748-medium.jpg"),
    ItemFashion("Axel Arigato", "Clean 90 Triple Sneaker","122", "$245.00","A shoe is an item of footwear intended to protect and comfort the human foot. Though the human foot can adapt to varied terrains and climate conditions, it is vulnerable, and shoes provide protection.", "https://product.hstatic.net/200000641225/product/19_bb4b8f7a4f614639a3db43fb644dc2dc_master.png"),
    ItemFashion("Rolex Watch", "Future Watch","999", "$999.00","A watch is a portable timepiece intended to be carried or worn by a person. It is designed to keep a consistent movement despite the motions caused by the person's activities.", "https://luxewatch.com.vn/wp-content/uploads/2023/04/dong-ho-Rolex-Oyster-Perpetual-Datejust-41mm-cu-chinh-hang-1.jpg"),
    ItemFashion("Nike", "Futuristic Shoes","345", "$30.00", "The shoes are sold in 3 different styles: low, mid, and high. The mid comes with a connected strap. The high-top Air Force 1s come with a velcro strap.","https://sneakerhs.com/wp-content/uploads/2021/05/giay-nike-Air-Jordan-1-Retro-High-OG-University-Blue-rep-11-gia-re-ha-noi-1-scaled-e1623842375444.jpg")
)


data class Sale(val sale: String, val code: String)
val saleList = listOf(
    Sale("50", "FSCREATION"),
    Sale("70", "DSCREATION"),
    Sale("90", "CDCREATION")
)

data class Size(val size: String)
val sizeList = listOf(
    Size("S"),
    Size("M"),
    Size("L"),
    Size("XL"),
    Size("XXL")
)