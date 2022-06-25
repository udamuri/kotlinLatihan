package com.example.petdoorz.ui.dashboard

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.petdoorz.R
import com.example.petdoorz.activity.login.LoginActivity
import com.example.petdoorz.databinding.FragmentDashboardBinding
import com.example.petdoorz.helpers.HelpersData
import com.example.petdoorz.helpers.SharedPrefManager
import com.example.petdoorz.model.login.LoginData
import com.example.petdoorz.model.pagination.PaginationData
import com.example.petdoorz.model.vendors.VendorsAdapter
import com.example.petdoorz.model.vendors.VendorsData
import com.example.petdoorz.widget.SpacingItemDecoration
import com.example.petdoorz.widget.Tools
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener
import java.util.*

class DashboardFragment : Fragment(), DashboardView {

    private var _binding: FragmentDashboardBinding? = null

    private var loginForResult: ActivityResultLauncher<Intent>? = null

    private var helpersData: HelpersData? = null

    var token: String? = null
    private var search: String? = null
    private var shimmerFrameLayout: ShimmerFrameLayout? = null
    private var ibBack: ImageButton? = null
    private var ibPrev: ImageButton? = null
    private var ibNext: ImageButton? = null
    private var etSearch: EditText? = null
    private var tvPageNumber: TextView? = null
    private var currentPage = 0
    private var hasMorePages = false

    private var presenter: DashboardPresenter? = null
    private var adapter: VendorsAdapter? = null
    private var itemClickListener: VendorsAdapter.ItemClickListener? = null
    private var items: List<VendorsData>? = null
    private var recyclerView: RecyclerView? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    // Receiver
    private val getResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) {
            if(it.resultCode == Activity.RESULT_OK){
                //val value = it.data?.getStringExtra("input")
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        loginForResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { }

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        if (SharedPrefManager.getInstance(requireActivity())!!.isLoggedIn) {
            val user: LoginData = SharedPrefManager.getInstance(requireActivity())!!.data

            token = user.type + " " +user.token

            helpersData = HelpersData(requireActivity())

            shimmerFrameLayout = binding!!.shimmerFrameLayout
            ibBack = binding!!.ibBack
            etSearch = binding!!.etSearch
            ibPrev = binding!!.ibPrev
            ibNext = binding!!.ibNext
            tvPageNumber = binding!!.tvPageNumber

            recyclerView = binding!!.iRecyclerView
            recyclerView!!.layoutManager = GridLayoutManager(requireActivity(), 2)
            recyclerView!!.addItemDecoration(SpacingItemDecoration(2, Tools.dpToPx(requireActivity(), 10), true))
            recyclerView!!.setHasFixedSize(true)
            presenter = DashboardPresenter(this)
            getData(token, 1)

            itemClickListener = object : VendorsAdapter.ItemClickListener {
                override fun onItemClick(view: View?, position: Int) {
                    //edit(position);
                }
            }
            etSearch!!.setText(search)
            etSearch!!.addTextChangedListener(object : TextWatcher {
                var timer = Timer()
                var DELAY: Long = 1000 // in ms
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable) {
                    if (s.isNotEmpty()) {
                        timer.cancel()
                        timer = Timer()
                        timer.schedule(object : TimerTask() {
                            override fun run() {
                                requireActivity().runOnUiThread(object : TimerTask() {
                                    override fun run() {
                                        search = s.toString()
                                        getData(token, 1)
                                    }
                                })
                            }
                        }, DELAY)
                    }
                }
            })
            ibPrev!!.setOnClickListener {
                if (currentPage > 1) {
                    getData(token, currentPage - 1)
                }
            }
            ibNext!!.setOnClickListener {
                if (hasMorePages) {
                    getData(token, currentPage + 1)
                }
            }


        } else {
            val intent = Intent(activity, LoginActivity::class.java)
            loginForResult!!.launch(intent)
            activity?.finish()
        }

        return root
    }

    fun getData(token: String?, page: Int?) {
        // GET CURRENT LOCATION
        val mFusedLocation: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(
                requireActivity()
            )

        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            presenter?.getVendors(token, search, 0.0, 0.0, page)
            return
        }

        mFusedLocation.lastLocation.addOnSuccessListener(
            (requireActivity() as Activity?)!!
        ) { location: Location ->
            presenter?.getVendors(token, search, location.latitude, location.longitude, page)
        }


    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun showLoading() {
        shimmerFrameLayout!!.visibility = View.VISIBLE
        recyclerView!!.visibility = View.GONE
    }

    override fun hideLoading() {
        shimmerFrameLayout!!.visibility = View.GONE
        recyclerView!!.visibility = View.VISIBLE
    }

    override fun onResultVendors(data: List<VendorsData>?, pagination: PaginationData?) {
        currentPage = pagination?.currentPage!!
        hasMorePages = pagination?.hasMorePages!!
        tvPageNumber!!.text = currentPage.toString()
        adapter = VendorsAdapter(requireActivity(), data!! as List<VendorsData>, itemClickListener!!)
        adapter!!.notifyDataSetChanged()
        recyclerView!!.adapter = adapter
        items = data as List<VendorsData>?
    }
}
