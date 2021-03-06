package com.loftydevelopment.helloworldquiz

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import java.text.SimpleDateFormat

class ListAdapter(context: Context, arrayList: List<Score>, tabInd: Int) : BaseAdapter() {
    internal val sList: List<Score>
    private val mInflator: LayoutInflater
    private val tabIndex: Int

    init {
        this.sList = arrayList
        this.mInflator = LayoutInflater.from(context)
        this.tabIndex = tabInd
    }

    override fun getCount(): Int {
        return sList.size
    }

    override fun getItem(position: Int): Any {
        return sList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        val view: View?
        val vh: ListRowHolder
        if (convertView == null) {
            view = this.mInflator.inflate(R.layout.label, parent, false)
            vh = ListRowHolder(view)
            view.tag = vh
        } else {
            view = convertView
            vh = view.tag as ListRowHolder
        }

        if(tabIndex == 1){      //Handle personal score tab

            if(sList[position].quizScore!! < 0){        //Not logged in

                vh.label_name.text = sList[position].displayName
                vh.label_place.text = ""
                vh.label_score.text = ""

            }else{

                val dateFormat= SimpleDateFormat("MM/dd/yyyy")

                vh.label_name.text = dateFormat.format(sList[position].date)
                vh.label_place.text = (position + 1).toString()
                vh.label_score.text = sList[position].quizScore.toString()

            }

        }else{                  //Weekly and all time tabs look the same

            vh.label_name.text = sList[position].displayName
            vh.label_place.text = (position + 1).toString()
            vh.label_score.text = sList[position].quizScore.toString()

        }



        this.notifyDataSetChanged()

        return view
    }
}

private class ListRowHolder(row: View?) {
    val label_name: TextView
    val label_place: TextView
    val label_score: TextView

    init {
        this.label_name = row?.findViewById(R.id.textView_name) as TextView
        this.label_score = row?.findViewById(R.id.textView_score) as TextView
        this.label_place = row?.findViewById(R.id.textView_rank) as TextView
    }

}