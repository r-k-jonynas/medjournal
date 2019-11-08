package com.example.medjournal.medications


import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.findNavController

import com.example.medjournal.R
import com.example.medjournal.models.MedInfo
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_med_config.*
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class MedConfigFragment : Fragment() {

    private var textViewDate: TextView? = null
    var cal: Calendar = Calendar.getInstance()
    var reminderDateSelected1: TextView? = null
    var reminderDateSelected2: TextView? = null
    var reminderDateSelected3: TextView? = null
    var dosage: String = "1" // default dosage
    var unit: String = "pill" // default dosage unit
    var reminderTime1: String = "08:00" // default time
    var reminderTime2: String = "08:00" // default time
    var reminderTime3: String = "08:00" // default time

    companion object {
        const val TAG = "MedicationActivity"
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.medication_frequency,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        val dosageAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.medication_dosage,
            android.R.layout.simple_spinner_item
        )
        dosageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val medConfigView = inflater.inflate(R.layout.fragment_med_config, container, false)
        val spinner = medConfigView.findViewById<Spinner>(R.id.medication_reminder_time_spinner)
        val dosageSpinner =
            medConfigView.findViewById<Spinner>(R.id.medication_reminder_dosage_spinner)
        val dosageText = medConfigView.findViewById<TextView>(R.id.medication_reminder_dosage_text)
        reminderDateSelected1 = medConfigView.findViewById(R.id.tv_reminder_time_input1)
        reminderDateSelected2 = medConfigView.findViewById(R.id.tv_reminder_time_input2)
        reminderDateSelected3 = medConfigView.findViewById(R.id.tv_reminder_time_input3)
        spinner.adapter = adapter
        dosageSpinner.adapter = dosageAdapter

        // reminder time spinner
        spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when (position) {
                    0 -> {
                        reminderDateSelected1?.visibility = View.VISIBLE
                        reminderDateSelected2?.visibility = View.GONE
                        reminderDateSelected3?.visibility = View.GONE
                    }
                    1 -> {
                        reminderDateSelected1?.visibility = View.VISIBLE
                        reminderDateSelected2?.visibility = View.VISIBLE
                        reminderDateSelected3?.visibility = View.GONE
                    }
                    2 -> {
                        reminderDateSelected1?.visibility = View.VISIBLE
                        reminderDateSelected2?.visibility = View.VISIBLE
                        reminderDateSelected3?.visibility = View.VISIBLE
                    }
                }

            }

        }

        dosageText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                dosage = p0.toString()
                if (dosage.isNotEmpty() && dosage.startsWith("0")) {
                    dosage = dosage.substring(1)
                    dosageText.text = dosage
                }
                reminderDateSelected1?.text =
                    getString(takeString(dosage, unit), dosage, unit, reminderTime1)
                reminderDateSelected2?.text =
                    getString(takeString(dosage, unit), dosage, unit, reminderTime2)
                reminderDateSelected3?.text =
                    getString(takeString(dosage, unit), dosage, unit, reminderTime3)

            }
        })

        dosage = dosageText?.text.toString()

        dosageSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                unit = parent?.getItemAtPosition(position).toString()
                Log.d("Test", unit)
                reminderDateSelected1?.text =
                    getString(takeString(dosage, unit), dosage, unit, reminderTime1)
                reminderDateSelected2?.text =
                    getString(takeString(dosage, unit), dosage, unit, reminderTime2)
                reminderDateSelected3?.text =
                    getString(takeString(dosage, unit), dosage, unit, reminderTime3)

            }

        }

        reminderDateSelected1!!.setOnClickListener {
            val cal1 = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                cal1.set(Calendar.HOUR_OF_DAY, hour)
                cal1.set(Calendar.MINUTE, minute)
                reminderTime1 = SimpleDateFormat("HH:mm", Locale.US).format(cal1.time)
                reminderDateSelected1?.text =
                    getString(takeString(dosage, unit), dosage, unit, reminderTime1)
            }
            TimePickerDialog(
                requireContext(),
                timeSetListener,
                cal1.get(Calendar.HOUR_OF_DAY),
                cal1.get(Calendar.MINUTE),
                true
            ).show()
        }

        reminderDateSelected2!!.setOnClickListener {
            val cal2 = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                cal2.set(Calendar.HOUR_OF_DAY, hour)
                cal2.set(Calendar.MINUTE, minute)
                reminderTime2 = SimpleDateFormat("HH:mm", Locale.US).format(cal2.time)
                reminderDateSelected2?.text =
                    getString(takeString(dosage, unit), dosage, unit, reminderTime3)
            }
            TimePickerDialog(
                requireContext(),
                timeSetListener,
                cal2.get(Calendar.HOUR_OF_DAY),
                cal2.get(Calendar.MINUTE),
                true
            ).show()
        }

        reminderDateSelected3!!.setOnClickListener {
            val cal3 = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                cal3.set(Calendar.HOUR_OF_DAY, hour)
                cal3.set(Calendar.MINUTE, minute)
                reminderTime3 = SimpleDateFormat("HH:mm", Locale.US).format(cal3.time)
                reminderDateSelected3?.text =
                    getString(R.string.tv_take_pill, dosage, unit, reminderTime3)
            }
            TimePickerDialog(
                requireContext(),
                timeSetListener,
                cal3.get(Calendar.HOUR_OF_DAY),
                cal3.get(Calendar.MINUTE),
                true
            ).show()
        }

        textViewDate = medConfigView.findViewById(R.id.tv_schedule_start_date_input)

        textViewDate!!.text = "--/--/----"

        updateDateInView()

        // create an OnDateSetListener
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            }

        textViewDate!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                DatePickerDialog(
                    requireContext(),
                    dateSetListener,
                    // set DatePickerDialog to point to today's date when it loads up
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()
            }

        })

        val rb: RadioButton = medConfigView.findViewById(R.id.radio_number_of_days)
        val rb1: RadioButton = medConfigView.findViewById(R.id.radio_ongoing)
        var curDuration = ""

        val radioGroup: RadioGroup = medConfigView.findViewById(R.id.radio_duration)
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            val radio: RadioButton = medConfigView.findViewById(checkedId)
            if (radio.text != "Ongoing treatment") {
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Set number of days (from start date)")

                val view = layoutInflater.inflate(R.layout.popup_duration, container, false)

                val input = view.findViewById(R.id.et_duration) as TextInputEditText

                if (input.parent != null) {
                    (input.parent as ViewGroup).removeView(input)
                }

                val recallText = curDuration.split(" ")

                //Toast.makeText(requireContext(), recallText[0], Toast.LENGTH_SHORT).show()
                if (curDuration.isNotEmpty() && !curDuration.startsWith('n')) input.setText(
                    recallText[0]
                )
                var mText: String = input.text.toString()
                input.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {}

                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        mText = input.text.toString()
                        if (mText.isNotEmpty() && mText.startsWith("0")) input.setText(
                            mText.substring(1)
                        )
                    }

                })

                builder.setView(input)

                builder.setPositiveButton(
                    "OK"
                ) { _, _ ->

                    if (mText.isEmpty()) mText = "14"
                    rb.text = getString(R.string.radio_number_of_days_changed, mText)
                    curDuration = rb.text.toString()
                    rb.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.design_default_color_primary
                        )
                    )

                    rb.setOnClickListener {
                        if (rb.text != getString(R.string.radio_text_number_of_days)) {
                            curDuration = rb.text.toString()
                            rb1.isChecked = true
                            rb.isChecked = true
                            rb.text = curDuration
                            rb.setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.design_default_color_primary
                                )
                            )
                        }
                    }
                }
                builder.setNegativeButton(
                    "Cancel"
                ) { dialog, _ ->
                    dialog.cancel()
                    if (rb.text == getString(R.string.radio_text_number_of_days)) {
                        rb1.isChecked = true
                    }
                }

                builder.show()
            } else {
                rb.text = getString(R.string.radio_text_number_of_days)
                rb.setTextColor(Color.BLACK)
                rb.isChecked = false

            }
        }

        var selectedDays = arrayListOf<String>()

        val radioGroup2: RadioGroup = medConfigView.findViewById(R.id.radio_days)
        val radioEveryDay: RadioButton = medConfigView.findViewById(R.id.radio_every_day)
        val radioSpecific: RadioButton = medConfigView.findViewById(R.id.radio_specific_days)
        radioGroup2.setOnCheckedChangeListener { _, checkedId ->
            val radio: RadioButton = medConfigView.findViewById(checkedId)
            Log.d("RB1", radio.text.toString())
            if (radio.text.toString()[0] == 'E') {
                radioSpecific.text = getString(R.string.tv_specific_days)
                radioSpecific.setTextColor(Color.BLACK)
            } else {
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Select days of the week:")

                val view = layoutInflater.inflate(R.layout.popup_days_of_week, container, false)

                val chk1: CheckBox = view.findViewById(R.id.chk1)
                val chk2: CheckBox = view.findViewById(R.id.chk2)
                val chk3: CheckBox = view.findViewById(R.id.chk3)
                val chk4: CheckBox = view.findViewById(R.id.chk4)
                val chk5: CheckBox = view.findViewById(R.id.chk5)
                val chk6: CheckBox = view.findViewById(R.id.chk6)
                val chk7: CheckBox = view.findViewById(R.id.chk7)

                if (chk1.parent != null && chk2.parent != null && chk3.parent != null
                    && chk4.parent != null && chk5.parent != null && chk6.parent != null
                    && chk7.parent != null
                ) {
                    (chk1.parent as ViewGroup).removeView(chk1)
                    (chk2.parent as ViewGroup).removeView(chk2)
                    (chk3.parent as ViewGroup).removeView(chk3)
                    (chk4.parent as ViewGroup).removeView(chk4)
                    (chk5.parent as ViewGroup).removeView(chk5)
                    (chk6.parent as ViewGroup).removeView(chk6)
                    (chk7.parent as ViewGroup).removeView(chk7)
                }

                val linearLayout = LinearLayout(requireContext())
                linearLayout.orientation = LinearLayout.VERTICAL
                linearLayout.addView(chk1)
                linearLayout.addView(chk2)
                linearLayout.addView(chk3)
                linearLayout.addView(chk4)
                linearLayout.addView(chk5)
                linearLayout.addView(chk6)
                linearLayout.addView(chk7)

                builder.setView(linearLayout)

                selectedDays = ArrayList()
                builder.setPositiveButton(
                    "OK"
                ) { _, _ ->
                    if (chk1.isChecked) selectedDays.add(chk1.text.toString())
                    if (chk2.isChecked) selectedDays.add(chk2.text.toString())
                    if (chk3.isChecked) selectedDays.add(chk3.text.toString())
                    if (chk4.isChecked) selectedDays.add(chk4.text.toString())
                    if (chk5.isChecked) selectedDays.add(chk5.text.toString())
                    if (chk6.isChecked) selectedDays.add(chk6.text.toString())
                    if (chk7.isChecked) selectedDays.add(chk7.text.toString())

                    if (selectedDays.isEmpty()) {
                        radioEveryDay.isChecked = true
                        Toast.makeText(
                            requireContext(), "Please select at least " +
                                    "one day of the week", Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val allDays = StringBuilder()
                        for (s in selectedDays) allDays.append("$s ")
                        //Toast.makeText(requireContext(), allDays, Toast.LENGTH_SHORT).show()
                        radioSpecific.text = allDays
                        radioSpecific.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.design_default_color_primary
                            )
                        )
//                        radio_specific.setOnClickListener {
//                            radio_every_day.isChecked = true
//                            radio_specific.isChecked = true
//                        }


                    }
                }
                builder.setNegativeButton(
                    "Cancel"
                ) { dialog, _ ->
                    radioEveryDay.isChecked = true
                    dialog.cancel()
                }

                builder.show()

            }
        }

        val doneButton: Button = medConfigView.findViewById(R.id.med_config_done_button)

        doneButton.setOnClickListener {

            val uid = FirebaseAuth.getInstance().uid ?: ""
            val medName = arguments?.getString("med_name")
            val times = getTimes(spinner.selectedItem.toString())
            val amount = medication_reminder_dosage_text.text.toString().toInt()
            val startDate = textViewDate?.text.toString()
            val timesArray = arrayListOf(reminderTime1, reminderTime2, reminderTime3)
            val duration = getDuration(rb, rb.isChecked)
            val days = selectedDays

            val medication = MedInfo(
                uid,
                medName ?: "null",
                times,
                amount,
                unit,
                timesArray,
                startDate,
                duration,
                days
            )

            saveMedInfoToFirebaseDatabase(medication, medConfigView)

//            val bundle = bundleOf(
//                "uid" to "sample_uid",
//                "med_name" to arguments?.getString("med_name"),
//                "times" to getTimes(spinner.selectedItem.toString()), // 1 2 or 3
//                "amount" to medication_reminder_dosage_text.text.toString().toInt(),
//                "unit" to unit,
//                "start_date" to textViewDate?.text.toString(),
//                "duration" to getDuration(rb, rb.isChecked)
//            )
//            //bundle.putSerializable("times", Date(1))
//            //bundle.putSerializable("start_date", Date(2))
//            bundle.putStringArrayList(
//                "times_array",
//                arrayListOf(reminderTime1, reminderTime2, reminderTime3)
//            )
//            bundle.putStringArrayList("days", selectedDays)
//
//            medConfigView.findNavController()
//                .navigate(R.id.action_medConfigFragment_to_homeActivity, bundle)
        }

        return medConfigView
    }

    private fun updateDateInView() {
        val myFormat = "MM/dd/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        textViewDate!!.text = sdf.format(cal.time)
    }

    private fun takeString(dosage: String, unit: String) =
        if (unit.length <= 2 || dosage.isEmpty() || dosage.toInt() < 2)
            R.string.tv_take_pill else
            R.string.tv_take_pill_plural

    private fun getTimes(spinnerText: String) =
        when (spinnerText) {
            (resources.getStringArray(R.array.medication_frequency))[0] -> 1
            (resources.getStringArray(R.array.medication_frequency))[1] -> 2
            else -> 3
        }

    private fun getDuration(rb: RadioButton, inputBool: Boolean) =
        when (inputBool) {
            false -> 0
            else -> (rb.text.split(" "))[0].toInt()
        }

    private fun saveMedInfoToFirebaseDatabase(medication: MedInfo, view: View) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/medications/$uid/${medication.med_name}")

        ref.setValue(medication)
            .addOnSuccessListener {
                Log.d(TAG, "Finally we saved the user medication info to Firebase Database")

                view.findNavController()
                    .navigate(R.id.action_medConfigFragment_to_homeActivity)

            }
            .addOnFailureListener {
                Log.d(TAG, "Failed to set medication value to database: ${it.message}")
                Toast.makeText(requireContext(), "Bad Internet Connection", Toast.LENGTH_SHORT)
                    .show()
            }
    }
}
