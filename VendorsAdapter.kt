package com.example.petdoorz.model.vendors


import androidx.recyclerview.widget.RecyclerView
import android.content.Context
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.example.petdoorz.R
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.squareup.picasso.Picasso

class VendorsAdapter(
    private val context: Context,
    private val lists: List<VendorsData>,
    private val itemClickListener: ItemClickListener
) : RecyclerView.Adapter<VendorsAdapter.RecyclerViewAdapter>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewAdapter {
        val view = LayoutInflater.from(context).inflate(R.layout.item_vendor, parent, false)
        return RecyclerViewAdapter(view, itemClickListener)
    }

    override fun onBindViewHolder(holder: RecyclerViewAdapter, position: Int) {
        val item = lists[position]
        holder.tvName.text = item?.user_detail?.company_name
        holder.tvDistance.text = item?.distanceString
        holder.tvPhone.text = item?.user_detail?.phone

        Picasso.get()
            .load(item.logoUrl)
            .error(R.drawable.ic_baseline_image_24)
            .into(holder.ivItem)
    }

    override fun getItemCount(): Int {
        return lists.size
    }

    inner class RecyclerViewAdapter(itemView: View, itemClickListener: ItemClickListener) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var ivItem: ImageView
        var tvName: TextView
        var tvDistance: TextView
        var tvPhone: TextView

        private var openItem: LinearLayout
        private var itemClickListener: ItemClickListener
        override fun onClick(v: View) {
            itemClickListener.onItemClick(v, bindingAdapterPosition)
        }

        init {
            openItem = itemView.findViewById(R.id.openItem)
            tvName = itemView.findViewById(R.id.tvName)
            ivItem = itemView.findViewById(R.id.ivItem)
            tvDistance = itemView.findViewById(R.id.tvDistance)
            tvPhone = itemView.findViewById(R.id.tvPhone)
            this.itemClickListener = itemClickListener
            openItem.setOnClickListener(this)
        }
    }

    interface ItemClickListener {
        fun onItemClick(view: View?, position: Int)
    }
}
