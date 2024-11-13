package com.lumko.teachme.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class EmailSignUp() : Parcelable  {

    @SerializedName("email")
    lateinit var email: String

    @SerializedName("password")
    lateinit var password: String

    @SerializedName("password_confirmation")
    lateinit var passwordConfirmation: String

    @SerializedName("account_type")
    var accountType: Int = 0

    @SerializedName("id_number")
    lateinit var idNumber: String

    @SerializedName("institution_name")
    lateinit var institutionName: String

    @SerializedName("course")
    lateinit var course: String

    // Use ByteArray to store file data for sending via API
  /*  @SerializedName("qualification")
    var qualification: ByteArray? = null

    @SerializedName("cv")
    var cv: ByteArray? = null

    @SerializedName("id_document")
    var idDocument: ByteArray? = null

    @SerializedName("proof_of_address")
    var proofOfAddress: ByteArray? = null

    @SerializedName("bank_account_letter")
    var bankAccountLetter: ByteArray? = null
*/
    constructor(parcel: Parcel) : this() {
        email = parcel.readString()!!
        password = parcel.readString()!!
        passwordConfirmation = parcel.readString()!!
        accountType = parcel.readInt()
        idNumber = parcel.readString()!!
        institutionName = parcel.readString()!!
        course = parcel.readString()!!
  /*      qualification = parcel.createByteArray()
        cv = parcel.createByteArray()
        idDocument = parcel.createByteArray()
        proofOfAddress = parcel.createByteArray()
        bankAccountLetter = parcel.createByteArray()*/
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(email)
        parcel.writeString(password)
        parcel.writeString(passwordConfirmation)
        parcel.writeInt(accountType)
        parcel.writeString(idNumber)
        parcel.writeString(institutionName)
        parcel.writeString(course)
     /*   parcel.writeByteArray(qualification)
        parcel.writeByteArray(cv)
        parcel.writeByteArray(idDocument)
        parcel.writeByteArray(proofOfAddress)
        parcel.writeByteArray(bankAccountLetter) */
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<EmailSignUp> {
        override fun createFromParcel(parcel: Parcel): EmailSignUp {
            return EmailSignUp(parcel)
        }

        override fun newArray(size: Int): Array<EmailSignUp?> {
            return arrayOfNulls(size)
        }
    }
}