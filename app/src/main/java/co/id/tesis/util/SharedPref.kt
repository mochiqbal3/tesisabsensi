package co.id.distriboost.util

import android.content.Context
import android.content.SharedPreferences
import co.id.tesis.R
import co.id.tesis.model.Auth
import co.id.tesis.model.Profile
import com.google.gson.Gson


class SharedPref(internal var mContex: Context, internal var filename: String) {
    internal var sharedpreferences: SharedPreferences
    internal var editor: SharedPreferences.Editor


    init {
        sharedpreferences = mContex.getSharedPreferences(filename, 0)
        editor = sharedpreferences.edit()
    }

    fun setString(address: String, value: String) {
        this.editor.putString(address, value)
        this.editor.commit();
    }

    fun setBoolean(address: String, value: Boolean?) {

        this.editor.putBoolean(address, value!!)
        this.editor.commit();
    }

    fun setLogin(login: Auth, email: String) {
        if (login.success) {
            this.editor.putBoolean(mContex.getString(R.string.key_is_login), true)
            var gson = Gson()
            this.editor.putString(mContex.getString(R.string.key_user), gson.toJson(login.data))
        } else {
            this.editor.putBoolean(mContex.getString(R.string.key_is_login), false)
            this.editor.putString(mContex.getString(R.string.key_user), "")
        }
        this.editor.commit()
    }

    fun getProfile(): Profile {
        val user : Profile
        val gson = Gson()
        user = gson.fromJson(sharedpreferences.getString(mContex.getString(R.string.key_user),""),Profile::class.java)
        return user
    }


    fun setInt(address: String, value: Int) {

        this.editor.putInt(address, value)
        this.editor.commit();
    }

    fun setFloat(address: String, value: Int) {

        this.editor.putFloat(address, value.toFloat())
        this.editor.commit();
    }

    fun setLong(address: String, value: Int) {

        this.editor.putLong(address, value.toLong())
        this.editor.commit();
    }


    fun getString(address: String, defVal: String?): String? {
        var value = sharedpreferences.getString(address, defVal)
        return value
    }

    fun getBoolean(address: String, defVal: Boolean): Boolean {
        return sharedpreferences.getBoolean(address, defVal)
    }

    fun getFloat(address: String, defVal: Float?): Float? {
        return sharedpreferences.getFloat(address, defVal!!)
    }

    fun getInt(address: String, defVal: Int): Int {
        return sharedpreferences.getInt(address, defVal)
    }

    fun getLong(address: String, defVal: Long?): Long? {
        return sharedpreferences.getLong(address, defVal!!)
    }


}