package com.kimochisoft.dynamicform

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.core.widget.addTextChangedListener
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.kimochisoft.dynamicform.data.Elements
import com.kimochisoft.dynamicform.data.SuccessfulCallback
import com.kimochisoft.dynamicform.managers.DynamicFormManager
import kotlinx.android.synthetic.main.activity_main.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    private var dynamicFormManager: DynamicFormManager? = null
    private var progressDialog: AlertDialog? = null
    var elementList = mutableListOf<Elements>()
    var id :String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        dynamicFormManager = DynamicFormManager(this)
        val rootLayout = this.findViewById<LinearLayout>(R.id.content_forms)[0]
        val scrollContainer = rootLayout.findViewById<ScrollView>(R.id.scrollView)[0]
        val container = scrollContainer.findViewById<LinearLayout>(R.id.container_form)

        button_get_json.setOnClickListener {
            onButtonClickedGetJson(container as LinearLayout)
        }
        button_generate_json.setOnClickListener {
            onButtonClickedGenerateJson()
        }

        onButtonClickedGetJson(container  as LinearLayout)
    }

    private fun createViews(rootLayout: LinearLayout) {
        val layout = (rootLayout as View).findViewById<LinearLayout>(R.id.container_form)
        val length = elementList.size
        for (i in 0 until length) {
            val editText = EditText(this@MainActivity)
            val textView = TextView(this@MainActivity)
            val calendarView = CalendarView(this@MainActivity)
            val seek = SeekBar(this@MainActivity)
            val lParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            val layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            val lParamsLabel = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            val lParamsEditText = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            lParams.setMargins(0, 10, 0, 10)
            layoutParams.topMargin = 16
            lParamsEditText.bottomMargin = 18
            textView.layoutParams = lParamsLabel
            textView.gravity = Gravity.START
            textView.typeface = Typeface.DEFAULT_BOLD
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20F)
            editText.layoutParams = lParamsEditText
            editText.imeOptions = EditorInfo.IME_ACTION_DONE
            layoutParams.setMargins(90, 0, 80, 40)
            calendarView.layoutParams = layoutParams
            lParams.setMargins(50, 50, 50, 50)
            seek.layoutParams = lParams

            if (calendarView.parent != null) (calendarView.parent as ViewGroup).removeView(calendarView)
            if (editText.parent != null) (editText.parent as ViewGroup).removeView(editText)
            if (seek.parent != null) (seek.parent as ViewGroup).removeView(seek)
            if (textView.parent != null) (textView.parent as ViewGroup).removeView(textView)

            textView.text = elementList[i].fieldNameView.toString().replace("\"", "")
            layout.addView(textView)
            if (elementList[i].componentType.toString().replace("\"", "") == "text") {
                editText.inputType = InputType.TYPE_CLASS_TEXT
                editText.setText(elementList[i].valueString.toString().replace("\"", ""))
                editText.addTextChangedListener(afterTextChanged = {
                    setDataText(editText.text.toString(), i)
                })
                layout.addView(editText)
            }
            if (elementList[i].componentType.toString().replace("\"", "") == "text_multiline") {
                editText.inputType = InputType.TYPE_TEXT_FLAG_MULTI_LINE
                editText.setText(elementList[i].valueString.toString().replace("\"", ""))
                editText.addTextChangedListener(afterTextChanged = {
                    setDataMultiline(editText.text.toString(), i)
                })
                layout.addView(editText)
            }
            if (elementList[i].componentType.toString().replace("\"", "") == "numeric") {
                editText.inputType = InputType.TYPE_CLASS_NUMBER
                editText.setText(elementList[i].valueInt.toString().replace("\"", ""))
                editText.addTextChangedListener(afterTextChanged = {
                    setDataNumeric(editText.text.toString(), i)
                })
                layout.addView(editText)
            }
            if (elementList[i].componentType.toString().replace("\"", "") == "calendar") {
                if (elementList[i].valueString == null) {
                    calendarView.setDate(Date().time,false,true)
                    setDataCalendar(calendarView, i)
                } else{
                    val f = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                    try {
                        val d: Date? = f.parse(elementList[i].valueString.toString().replace("\"", ""))
                        calendarView.setDate(d!!.time,false,true)
                    } catch (e: ParseException) {
                        calendarView.setDate(Date().time,false,true)
                        setDataCalendar(calendarView, i)
                        e.printStackTrace()
                    }
                }
                calendarView.setOnDateChangeListener {calendarView, _, _, _ -> (setDataCalendar(calendarView, i))}
                layout.addView(calendarView)
            }
            if (elementList[i].componentType.toString().replace("\"", "") == "slider") {
                if (elementList[i].max != null && elementList[i].max != null) {
                    seek.max = elementList[i].max.toString().toInt()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        seek.min = elementList[i].min.toString().toInt()
                    }
                }
                if (elementList[i].valueInt != null) {
                    seek.progress = elementList[i].valueInt!!.toInt()
                    setDataSlider(elementList[i].valueInt.toString(), i)
                }
                seek.setOnSeekBarChangeListener(
                    object : SeekBar.OnSeekBarChangeListener {
                        override fun onProgressChanged(
                            seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                        }
                        override fun onStartTrackingTouch(seekBar: SeekBar) {
                        }
                        override fun onStopTrackingTouch(seekBar: SeekBar) {
                            Toast.makeText(this@MainActivity,
                                "Progress: " + seekBar.progress,
                                Toast.LENGTH_SHORT).show()
                            setDataSlider(seekBar.progress.toString(), i)
                        }
                    })
                layout.addView(seek)
            }

        }
    }
    private fun showLoader() {
        val dialogBuilder = AlertDialog.Builder(this)
        progressDialog = dialogBuilder.setCancelable(false)
            .setView(R.layout.layout_progress_dialog)
            .create()

        progressDialog?.show()
    }

    private fun dismissLoader() {
        if (progressDialog?.isShowing == true) {
            progressDialog?.dismiss()
            progressDialog = null
        }
    }

    private fun showErrorMessage(exception: Exception) {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage(exception.message)
            .setCancelable(false)
            .setPositiveButton(getString(R.string.accept)) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun showInformativeMessage(message: String, successfulCallback: SuccessfulCallback<Any?>) {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage(message)
            .setCancelable(false)
            .setPositiveButton(getString(R.string.accept)) { dialog, _ ->
                dialog.dismiss()
                successfulCallback(null)
            }
            .create()
            .show()
    }

    private fun onButtonClickedGenerateJson() {
        showLoader()
        val length = elementList.size
        var orderJson = "{\r\n"
        orderJson = orderJson.plus("  \"elements\": {\r\n")
        for (i in 0 until length) {
            if (i == length - 1) {
                orderJson = orderJson.plus("  \"${elementList[i].fieldNameKey.toString().replace("\"", "")}\": ${elementList[i].valueString.toString()}\r\n")
            } else {
                orderJson = orderJson.plus("  \"${elementList[i].fieldNameKey.toString().replace("\"", "")}\": ${elementList[i].valueString.toString()},\r\n")
            }
        }
        orderJson = orderJson.plus( "  },\r\n")
        orderJson = orderJson.plus( "\"id\": ${id}\r\n")
        orderJson = orderJson.plus( "}")

        val jsonParser = JsonParser()
        val jsonObject = jsonParser.parse(orderJson) as JsonObject
        dynamicFormManager?.saveDynamicForm(jsonObject, {
            dismissLoader()
            jsonRequest.text = jsonObject.toString()
            showInformativeMessage(it, {})
        }, {
            dismissLoader()
            showErrorMessage(it)
        })
    }

    private fun onButtonClickedGetJson(rootLayout: LinearLayout) {
        showLoader()
        elementList.clear()
        rootLayout.removeAllViews();
        rootLayout.invalidate();
        dynamicFormManager?.getDynamicForm({
            dismissLoader()
            jsonResponse.text = it.toString()
            id = it.get("id").asString
            for (i in 0 until it.get("elements").asJsonArray.size()) {
                try {
                    elementList.add(Elements(
                        it.get("elements").asJsonArray[i].asJsonObject.get("component_type").toString(),
                        it.get("elements").asJsonArray[i].asJsonObject.get("field_name_key").toString(),
                        it.get("elements").asJsonArray[i].asJsonObject.get("field_name_view").toString(),
                        it.get("elements").asJsonArray[i].asJsonObject.get("max")?.toString()?.toInt(),
                        it.get("elements").asJsonArray[i].asJsonObject.get("min")?.toString()?.toInt(),
                        it.get("elements").asJsonArray[i].asJsonObject.get("ordinal").toString().toInt(),
                        null,
                        it.get("elements").asJsonArray[i].asJsonObject.get("value")?.toString()?.toInt()))
                }catch (e: Exception) {
                    elementList.add(Elements(
                        it.get("elements").asJsonArray[i].asJsonObject.get("component_type").toString(),
                        it.get("elements").asJsonArray[i].asJsonObject.get("field_name_key").toString(),
                        it.get("elements").asJsonArray[i].asJsonObject.get("field_name_view").toString(),
                        it.get("elements").asJsonArray[i].asJsonObject.get("max")?.toString()?.toInt(),
                        it.get("elements").asJsonArray[i].asJsonObject.get("min")?.toString()?.toInt(),
                        it.get("elements").asJsonArray[i].asJsonObject.get("ordinal").toString().toInt(),
                        it.get("elements").asJsonArray[i].asJsonObject.get("value")?.toString(),
                        null))
                }
            }
            createViews(rootLayout)
        }, {
            dismissLoader()
            showErrorMessage(it)
        })
    }

    private fun setDataText(value: String, position: Int) {
        elementList[position].valueString = ""
        elementList[position].valueString = "\"$value\""
    }

    private fun setDataNumeric(value: String, position: Int) {
        elementList[position].valueString = ""
        elementList[position].valueString = value
    }

    private fun setDataMultiline(value: String, position: Int) {
        elementList[position].valueString = ""
        elementList[position].valueString = "\"$value\""
    }

    private fun setDataSlider(value: String, position: Int) {
        elementList[position].valueString = value
    }

    @SuppressLint("SimpleDateFormat")
    private fun setDataCalendar(value: CalendarView, position: Int) {
        val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val formattedDate: String = df.format(value.date)
        elementList[position].valueString = "\"$formattedDate\""
    }
}
