package mx.arquidiocesis.eamxredsocialmodule.model

import android.os.Parcel
import android.os.Parcelable
import androidx.versionedparcelable.VersionedParcelize


@VersionedParcelize
data class Autor(
    val FIIDEMPLEADO: Int,
    val nombre: String?,
    val imagen: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(FIIDEMPLEADO)
        parcel.writeString(nombre)
        parcel.writeString(imagen)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Autor> {
        override fun createFromParcel(parcel: Parcel): Autor {
            return Autor(parcel)
        }

        override fun newArray(size: Int): Array<Autor?> {
            return arrayOfNulls(size)
        }
    }
}