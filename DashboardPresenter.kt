package com.example.petdoorz.ui.dashboard

import android.util.Log
import com.example.petdoorz.api.ApiClient.apiClient
import com.example.petdoorz.api.ApiInterface
import com.example.petdoorz.model.vendors.VendorsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardPresenter(private val view: DashboardView) {

    fun getVendors(token: String?, search: String?, lat: Double?, lng: Double?, page: Int?) {
        view.showLoading()

        //request to server
        var perpage = 16
        val apiInterface = apiClient!!.create(ApiInterface::class.java)
        val call = apiInterface.getVendors(token, search, lat, lng, page, perpage)
        call!!.enqueue(object : Callback<VendorsResponse?> {
            override fun onResponse(
                call: Call<VendorsResponse?>,
                response: Response<VendorsResponse?>
            ) {
                view.hideLoading()
                if (response.isSuccessful && response.body() != null) {
                    if (response.body()!!.isSuccess) {
                        Log.d(tag, "onResponse successfully")
                        view.onResultVendors(response.body()!!.data, response.body()!!.pagination)
                    } else {
                        Log.d(tag, "isSuccess false")
                    }
                } else if (response.code() == 401) {
                    //
                } else {
                    Log.d(tag, "body empty")
                }
            }

            override fun onFailure(call: Call<VendorsResponse?>, t: Throwable) {
                //println("==========t.message==========")
                //println(t.localizedMessage)
            }
        })
    }

    companion object {
        private val tag = DashboardPresenter::class.java.simpleName
    }
}
