package com.example.petdoorz.model.vendors

import com.example.petdoorz.model.pagination.PaginationData
import com.google.gson.JsonArray
import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

class VendorsResponse(
    @field:Expose @field:SerializedName("success") val isSuccess: Boolean,
    @field:Expose @field:SerializedName("message") val message: JsonArray,
    @field:Expose @field:SerializedName("data") val data: List<VendorsData>,
    @field:Expose @field:SerializedName("pagination") val pagination: PaginationData
)
