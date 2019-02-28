package com.loftydevelopment.helloworldquiz

import android.support.design.widget.TabLayout
import android.support.v7.app.AppCompatActivity

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore

import kotlinx.android.synthetic.main.activity_score.*
import java.util.*

class ScoreActivity : AppCompatActivity() {

    /**
     * The [android.support.v4.view.PagerAdapter] that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * [android.support.v4.app.FragmentStatePagerAdapter].
     */
    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    private var db: FirebaseFirestore? = null

    private var scoreList: MutableList<Score>? = null
    private var personalScoreList: MutableList<Score>? = null
    private var weeklyScoreList: MutableList<Score>? = null

    private var userUid = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_score)

        setSupportActionBar(toolbar)

        Toast.makeText(this, "Loading scores...", Toast.LENGTH_SHORT).show()

        userUid = intent.getStringExtra("uid")

        db = FirebaseFirestore.getInstance()

        //Retrieve score data from FireStore database
        scoreList = ArrayList()
        personalScoreList = ArrayList()
        weeklyScoreList = ArrayList()

        db!!.collection("scores").get()
            .addOnSuccessListener { queryDocumentSnapshots ->

                if (!queryDocumentSnapshots.isEmpty) {

                    val list = queryDocumentSnapshots.documents

                    for (d in list) {

                        val s = d.toObject(Score::class.java)

                        scoreList!!.add(s!!)

                        if(s.uid == userUid){
                            personalScoreList!!.add(s!!)
                        }

                        val currentDateBefore7Days = Calendar.getInstance()
                        currentDateBefore7Days.add(Calendar.DATE, -7)

                        if(!s.date!!.before(currentDateBefore7Days.time)){
                            weeklyScoreList!!.add(s!!)
                        }

                    }

                }

                generateTop100Scores()

                // Create the adapter that will return a fragment for each of the three
                // primary sections of the activity.
                mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

                // Set up the ViewPager with the sections adapter.
                container.adapter = mSectionsPagerAdapter

                container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
                tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))

            }

    }

    private fun generateTop100Scores(){

        scoreList!!.sort()
        personalScoreList!!.sort()
        weeklyScoreList!!.sort()

        if(scoreList!!.size > 100){
            scoreList = (scoreList as ArrayList<Score>).subList(0, 100)
        }

        if(personalScoreList!!.size > 100){
            personalScoreList = (personalScoreList as ArrayList<Score>).subList(0, 100)
        }

        if(weeklyScoreList!!.size > 100){
            weeklyScoreList = (weeklyScoreList as ArrayList<Score>).subList(0, 100)
        }

    }

    /**
     * A [FragmentPagerAdapter] that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1)
        }

        override fun getCount(): Int {
            // Show 3 total pages.
            return 3
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    class PlaceholderFragment : Fragment() {

        var scoreActvity: ScoreActivity? = null

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

            var tabNum = arguments!!.getInt(ARG_SECTION_NUMBER)

            scoreActvity = activity as ScoreActivity?

            val rootView = inflater.inflate(R.layout.fragment_score, container, false)
            val lv = rootView.findViewById(R.id.section_list) as ListView

            if(tabNum != null){

                when(tabNum){
                    1 -> { lv.adapter = ListAdapter(context!!, scoreActvity!!.personalScoreList!!) }
                    2 -> { lv.adapter = ListAdapter(context!!, scoreActvity!!.weeklyScoreList!!) }
                    3 -> { lv.adapter = ListAdapter(context!!, scoreActvity!!.scoreList!!) }
                    else -> { lv.adapter = ListAdapter(context!!, scoreActvity!!.scoreList!!) }
                }

            }else {
                lv.adapter = ListAdapter(context!!, scoreActvity!!.scoreList!!)
            }

            return rootView
        }

        companion object {
            /**
             * The fragment argument representing the section number for this
             * fragment.
             */
            private val ARG_SECTION_NUMBER = "section_number"

            /**
             * Returns a new instance of this fragment for the given section
             * number.
             */
            fun newInstance(sectionNumber: Int): PlaceholderFragment {
                val fragment = PlaceholderFragment()
                val args = Bundle()
                args.putInt(ARG_SECTION_NUMBER, sectionNumber)
                fragment.arguments = args
                return fragment
            }
        }
    }
}
