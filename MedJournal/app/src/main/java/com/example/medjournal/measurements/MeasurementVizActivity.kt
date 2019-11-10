package com.example.medjournal.measurements

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medjournal.R
import com.example.medjournal.adapters.MeasurementHistoryRvAdapter
import com.example.medjournal.adapters.MeasurementMenuRvAdapter
import com.example.medjournal.models.MeasurementData
import com.example.medjournal.utils.ChartValueDateFormatter
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList

class MeasurementVizActivity : AppCompatActivity() {
    lateinit var measurement_type: String
    private lateinit var database: DatabaseReference
    private val measurementObjects = ArrayList<MeasurementData>()
    lateinit var yVals : ArrayList<Entry>
    private lateinit var tempLineChart : LineChart

    private lateinit var recyclerView: RecyclerView
    private lateinit var rvAdapter : MeasurementHistoryRvAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager

    companion object {
        const val TAG = "MeasurementVizActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_measurement_viz)

        measurement_type = intent.getStringExtra("measurement_type")!!
        // FIXME: do not concatenate in setText
        findViewById<TextView>(R.id.measurement_activity_header).setText("Your Statistics for " + measurement_type + ":")

        val dropdown1: Spinner = findViewById(R.id.period_for_graph_spinner)

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            this,
            R.array.period_choices_for_measurement_graphs,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            dropdown1.adapter = adapter
        }

        val dropdown2: Spinner = findViewById(R.id.health_indicator_spinner)

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            this,
            R.array.measurement_types_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            dropdown2.adapter = adapter
        }

        database = FirebaseDatabase.getInstance().reference

        // Set up the linechart
        yVals = ArrayList<Entry>()
        val set1: LineDataSet = LineDataSet(yVals, "temp")

        set1.color = Color.BLUE
        set1.setCircleColor(Color.BLUE)
        set1.lineWidth = 1f
        set1.circleRadius = 3f
        set1.setDrawCircleHole(false)
        set1.valueTextSize = 0f
        set1.setDrawFilled(false)

        val data : LineData = LineData(set1)

        tempLineChart = findViewById(com.example.medjournal.R.id.measurement_lineChart)
        tempLineChart.data = data
        val xAxis = tempLineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        tempLineChart.description.isEnabled = false
        tempLineChart.legend.isEnabled = false
        tempLineChart.setPinchZoom(true)
        tempLineChart.xAxis.enableGridDashedLine(5f, 5f, 0f)
        tempLineChart.axisRight.enableGridDashedLine(5f, 5f, 0f)
        tempLineChart.axisLeft.enableGridDashedLine(5f, 5f, 0f)
        xAxis.valueFormatter = ChartValueDateFormatter("yyyy.MM.dd")

        tempLineChart.notifyDataSetChanged()
        // -------------------------------- GRAPH SETUP ENDS -----------------------------------

        // -------------------------- MEASUREMENT HISTORY RV SETUP -----------------------------

        recyclerView = findViewById<RecyclerView>(R.id.measurement_history_rv)
        linearLayoutManager = LinearLayoutManager(this@MeasurementVizActivity)
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = MeasurementHistoryRvAdapter(measurementObjects, this@MeasurementVizActivity)
        recyclerView.layoutParams.height = 800
    }

    override fun onStart() {
        super.onStart()
        setUpDataCallbacks()
    }

    private fun setUpDataCallbacks() {
        val uid = FirebaseAuth.getInstance().uid ?: "UserX"
        val myRef = database.child("measurements").child(uid)
        val measurementDataListener = object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "loadMeasurementData:onCancelled", databaseError.toException())
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                measurementObjects.clear()
                Log.e("Firebase+MPChart:", "list has : ${snapshot.childrenCount} elements")
                for (ds in snapshot.getChildren()) {
                    val temp: MeasurementData = ds.getValue(MeasurementData::class.java)!!
                    // filter by MeasurementType
                    if (temp.typeOfMeasurement.equals(measurement_type)) {
                        val format = java.text.SimpleDateFormat(
                            "EE MMM dd HH:mm:ss z yyyy",
                            Locale.ENGLISH
                        )
                        val d = format.parse(temp.datetimeEntered)!!
                        yVals.add(Entry(d.time.toFloat(), temp.measuredVal))
                        measurementObjects.add(temp)
                    }
                }
                tempLineChart.notifyDataSetChanged()
                tempLineChart.data = LineData(LineDataSet(yVals, "temp"))
                tempLineChart.invalidate()
                tempLineChart.refreshDrawableState()
            }

        }

        myRef.addValueEventListener(measurementDataListener)
    }
}
