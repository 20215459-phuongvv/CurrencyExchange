package com.example.currencyexchange

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.example.currencyexchange.R
import java.text.NumberFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var sourceAmount: EditText
    private lateinit var targetAmount: EditText
    private lateinit var sourceCurrency: Spinner
    private lateinit var targetCurrency: Spinner

    private var exchangeRates = mapOf(
        "USD" to 1.0,
        "EUR" to 0.9256,
        "VND" to 25295.00
        // Add more currency rates here
    )

    private var isSourceAmountSelected = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sourceAmount = findViewById(R.id.sourceAmount)
        targetAmount = findViewById(R.id.targetAmount)
        sourceCurrency = findViewById(R.id.sourceCurrency)
        targetCurrency = findViewById(R.id.targetCurrency)

        ArrayAdapter.createFromResource(
            this,
            R.array.currency_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            sourceCurrency.adapter = adapter
            targetCurrency.adapter = adapter
        }

        sourceAmount.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                isSourceAmountSelected = true
            }
        }

        targetAmount.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                isSourceAmountSelected = false
            }
        }

        sourceCurrency.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                performConversion()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        targetCurrency.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                performConversion()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        sourceAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (isSourceAmountSelected) performConversion()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        targetAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!isSourceAmountSelected) performConversion()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    @SuppressLint("DefaultLocale")
    private fun performConversion() {
        val sourceValue = if (isSourceAmountSelected) sourceAmount.text.toString().toDoubleOrNull() else targetAmount.text.toString().toDoubleOrNull()
        val sourceCurrencyType = if (isSourceAmountSelected) sourceCurrency.selectedItem.toString() else targetCurrency.selectedItem.toString()
        val targetCurrencyType = if (isSourceAmountSelected) targetCurrency.selectedItem.toString() else sourceCurrency.selectedItem.toString()

        if (sourceValue != null && exchangeRates.containsKey(sourceCurrencyType) && exchangeRates.containsKey(targetCurrencyType)) {
            val conversionRate = exchangeRates[targetCurrencyType]!! / exchangeRates[sourceCurrencyType]!!
            val convertedAmount = sourceValue * conversionRate
            val formatter = NumberFormat.getNumberInstance(Locale.US)
            formatter.maximumFractionDigits = 2
            formatter.isGroupingUsed = true

            if (isSourceAmountSelected) {
                targetAmount.setText(formatter.format(convertedAmount))
            } else {
                sourceAmount.setText(formatter.format(convertedAmount))
            }
        } else {
            if (isSourceAmountSelected) targetAmount.setText("") else sourceAmount.setText("")
        }
    }
}
