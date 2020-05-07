package com.example.laba7

import android.os.Parcel
import android.os.Parcelable

class Item : Parcelable {
    var name: String? = null
    var price: Float = 0.toFloat()
    var quantity: Int = 0
    var id: Int = 0

    constructor(Name: String) {
        this.name = Name
        this.price = 0.0f
        this.quantity = 0
        this.id = 0
    }

    constructor(Name: String, Price: Float, Quantity: Int) : this(Name) {
        this.price = Price
        this.quantity = Quantity
    }

    constructor(id: Int, Name: String, Price: Float, Quantity: Int) : this(Name, Price, Quantity) {
        this.id = id
    }

    protected constructor(`in`: Parcel) {
        this.id = `in`.readInt()
        this.name = `in`.readString()
        this.price = `in`.readFloat()
        this.quantity = `in`.readInt()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(this.id)
        dest.writeString(this.name)
        dest.writeFloat(this.price)
        dest.writeInt(this.quantity)
    }

    companion object {

        val CREATOR: Parcelable.Creator<Item> = object : Parcelable.Creator<Item> {
            override fun createFromParcel(source: Parcel): Item {
                return Item(source)
            }

            override fun newArray(size: Int): Array<Item> {
                return arrayOfNulls(size)
            }
        }
    }
}
