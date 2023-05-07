package com.adamsolomon.bcs_421_final_project

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ExpenseAdapter(private var expenseList: List<Expense>) :

    RecyclerView.Adapter<ExpenseAdapter.MyViewHolder>() {

        private lateinit var listener:  ExpenseAdapterListener

        inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val nameView: TextView = itemView.findViewById(R.id.recyclerViewNameItem)
            val priceView: TextView = itemView.findViewById(R.id.recyclerViewPriceItem)
            val locationView: TextView = itemView.findViewById(R.id.recyclerViewLocationItem)
            val businessNameView: TextView = itemView.findViewById(R.id.recyclerViewBusniessNameItem)
            val expenseCategoryView: TextView = itemView.findViewById(R.id.recyclerViewExpenseCategoryItem)
            val dateView: TextView = itemView.findViewById(R.id.recyclerViewDateItem)
            val descripitonView: TextView = itemView.findViewById(R.id.recyclerViewDescriptionItem)
            val notesView: TextView = itemView.findViewById(R.id.recyclerViewNotesItem)

        }


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.expenseview, parent, false)
            return MyViewHolder(view)


        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val expense = expenseList[position]
            holder.nameView.text = expense.name
            holder.priceView.text=expense.price.toString()
            holder.locationView.text=expense.expenseLocation
            holder.businessNameView.text=expense.businessName
            holder.expenseCategoryView.text=expense.expenseCategory
            holder.dateView.text= expense.date.toString()
            holder.descripitonView.text=expense.description
            holder.notesView.text=expense.notes

        }


        override fun getItemCount(): Int {
            return expenseList.size

        }

        fun setData(list: List<Expense>) {
            expenseList = list
            notifyDataSetChanged()
        }

        interface ExpenseAdapterListener {
            fun onClick(position: Int)

        }

        fun setOnItemClickListener(_listener:  ExpenseAdapterListener) {
            listener = _listener
        }
    }
