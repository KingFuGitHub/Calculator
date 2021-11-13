package com.bignerdranch.android.calculator

import android.annotation.SuppressLint
import android.content.Context
import android.os.*
import android.text.method.ScrollingMovementMethod
import android.view.MotionEvent
import android.view.View
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat.FROM_HTML_MODE_COMPACT
import androidx.core.text.HtmlCompat.fromHtml
import com.bignerdranch.android.calculator.databinding.ActivityMainBinding
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import kotlinx.coroutines.*
import org.mariuszgromada.math.mxparser.Expression
import java.lang.Runnable
import java.text.DecimalFormat
import java.util.*
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var input: TextView
    private lateinit var output: TextView
    private lateinit var currentString: TextView
    private lateinit var currentLength: TextView
    private var currentLengthValue = 0
    private var currentStringValue = ""
    private var debugTestVariable = 1
    private var autoCalculate = false
    private val SHORT_HAPTIC_FEEDBACK_DURATION = 5L
    var runnable: Runnable? = null
    var handler = Handler(Looper.getMainLooper())
    var temp = false

    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        input = binding.tvInput
        output = binding.tvOutput
        currentString = binding.tvCurrentString
        currentLength = binding.tvCurrentLength

        binding.tvExponent.text = fromHtml("x<sup>b</sup>", FROM_HTML_MODE_COMPACT)
        output.movementMethod = ScrollingMovementMethod()

        binding.btnClear.setOnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    this.performHapticFeedback()
                    animation(binding.tvClear)
                }
            }

            v?.onTouchEvent(event) ?: true
        }

        binding.btnClear.setOnLongClickListener {
            this.performHapticFeedback()
            animation(binding.tvClear)
            if (debugTestVariable % 2 == 0) {
                currentLength.setTextColor(ContextCompat.getColor(this, R.color.transparent))
                currentString.setTextColor(ContextCompat.getColor(this, R.color.transparent))
            } else {
                currentLength.setTextColor(ContextCompat.getColor(this, R.color.white))
                currentString.setTextColor(ContextCompat.getColor(this, R.color.white))
            }
            debugTestVariable += 1
            true
        }

        binding.btnClear.setOnClickListener {
            input.text = ""
            output.text = ""

            currentLengthValue = 0
            currentStringValue = ""

            currentString.text = "String: $currentStringValue"
            currentLength.text = "Length: $currentLengthValue"

            autoCalculate = false
        }

        binding.btnLeftBracket.setOnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    this.performHapticFeedback()
                    animation(binding.tvLeftBracket)
                }
            }

            v?.onTouchEvent(event) ?: true
        }

        binding.btnLeftBracket.setOnClickListener {
            if (currentStringValue in listOf(
                    ")",
                    "0)",
                    "0",
                    "1",
                    "2",
                    "3",
                    "4",
                    "5",
                    "6",
                    "7",
                    "8",
                    "9"
                )
            ) {
                input.text = addToInputText("×(")
            } else {
                input.text = addToInputText("(")
            }
            showResult2()
            showResult3()
        }

        binding.btnRightBracket.setOnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    this.performHapticFeedback()
                    animation(binding.tvRightBracket)
                }
            }

            v?.onTouchEvent(event) ?: true
        }

        binding.btnRightBracket.setOnClickListener {
            if (input.length() > 0 && currentStringValue !in listOf(
                    "(",
                    "-",
                    "+",
                    "×",
                    "÷",
                    "×(",
                    "×0",
                    "^(",
                )
            ) {
                if (currentStringValue in listOf(".", "0.")) {
                    input.text = addToInputText("0)")
                } else {
                    input.text = addToInputText(")")
                }
            }
            showResult2()
            showResult3()
        }

        binding.btn0.setOnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    this.performHapticFeedback()
                    animation(binding.tv0)
                }
            }
            v?.onTouchEvent(event) ?: true
        }

        binding.btn0.setOnClickListener {
            if (input.length() > 0) {
                if (currentStringValue in listOf(")", "0)")) {
                    input.text = addToInputText("×0")
                } else {
                    input.text = addToInputText("0")
                }
            }
            showResult2()
            showResult3()
        }

        binding.btn1.setOnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    this.performHapticFeedback()
                    animation(binding.tv1)
                }
            }

            v?.onTouchEvent(event) ?: true
        }

        binding.btn1.setOnClickListener {
            if (currentStringValue in listOf(")")) {
                input.text = addToInputText("×1")
            } else {
                input.text = addToInputText("1")
            }
            showResult2()
            showResult3()
        }

        binding.btn2.setOnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    this.performHapticFeedback()
                    animation(binding.tv2)
                }
            }

            v?.onTouchEvent(event) ?: true
        }

        binding.btn2.setOnClickListener {
            if (currentStringValue in listOf(")")) {
                input.text = addToInputText("×2")
            } else {
                input.text = addToInputText("2")
            }
            showResult2()
            showResult3()
        }

        binding.btn3.setOnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    this.performHapticFeedback()
                    animation(binding.tv3)
                }
            }

            v?.onTouchEvent(event) ?: true
        }

        binding.btn3.setOnClickListener {
            if (currentStringValue in listOf(")")) {
                input.text = addToInputText("×3")
            } else {
                input.text = addToInputText("3")
            }
            showResult2()
            showResult3()
        }

        binding.btn4.setOnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    this.performHapticFeedback()
                    animation(binding.tv4)
                }
            }

            v?.onTouchEvent(event) ?: true
        }

        binding.btn4.setOnClickListener {
            if (currentStringValue in listOf(")")) {
                input.text = addToInputText("×4")
            } else {
                input.text = addToInputText("4")
            }
            showResult2()
            showResult3()
        }

        binding.btn5.setOnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    this.performHapticFeedback()
                    animation(binding.tv5)
                }
            }

            v?.onTouchEvent(event) ?: true
        }

        binding.btn5.setOnClickListener {
            if (currentStringValue in listOf(")")) {
                input.text = addToInputText("×5")
            } else {
                input.text = addToInputText("5")
            }
            showResult2()
            showResult3()
        }

        binding.btn6.setOnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    this.performHapticFeedback()
                    animation(binding.tv6)
                }
            }

            v?.onTouchEvent(event) ?: true
        }

        binding.btn6.setOnClickListener {
            if (currentStringValue in listOf(")")) {
                input.text = addToInputText("×6")
            } else {
                input.text = addToInputText("6")
            }
            showResult2()
            showResult3()
        }

        binding.btn7.setOnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    this.performHapticFeedback()
                    animation(binding.tv7)
                }
            }

            v?.onTouchEvent(event) ?: true
        }

        binding.btn7.setOnClickListener {
            if (currentStringValue in listOf(")")) {
                input.text = addToInputText("×7")
            } else {
                input.text = addToInputText("7")
            }
            showResult2()
            showResult3()
        }

        binding.btn8.setOnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    this.performHapticFeedback()
                    animation(binding.tv8)
                }
            }

            v?.onTouchEvent(event) ?: true
        }

        binding.btn8.setOnClickListener {
            if (currentStringValue in listOf(")")) {
                input.text = addToInputText("×8")
            } else {
                input.text = addToInputText("8")
            }
            showResult2()
            showResult3()
        }

        binding.btn9.setOnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    this.performHapticFeedback()
                    animation(binding.tv9)
                }
            }

            v?.onTouchEvent(event) ?: true
        }

        binding.btn9.setOnClickListener {
            if (currentStringValue in listOf(")")) {
                input.text = addToInputText("×9")
            } else {
                input.text = addToInputText("9")
            }
            showResult2()
            showResult3()
        }

        binding.btnDecimal.setOnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    this.performHapticFeedback()
                    animation(binding.tvDecimal)
                }
            }

            v?.onTouchEvent(event) ?: true
        }

        binding.btnDecimal.setOnClickListener {
            if (currentStringValue !in listOf(".", "0.", "^")) {
                if (input.length() == 0 || currentStringValue in listOf(
                        "^(",
                        "+",
                        "-",
                        "×",
                        "÷",
                        "(",
                        "×(",
                        "0^("
                    )
                ) {
                    input.text = addToInputText("0.")
                } else if (currentStringValue == ")") {
                    input.text = addToInputText("×0.")
                } else if (currentStringValue !in listOf(".", "0.")) {
                    input.text = addToInputText(".")
                }
            }
            showResult2()
            showResult3()
        }

        binding.btnSubtraction.setOnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    this.performHapticFeedback()
                    animation(binding.tvSubtraction)
                }
            }

            v?.onTouchEvent(event) ?: true
        }

        binding.btnSubtraction.setOnClickListener {
            if (currentStringValue !in listOf("-", "+", "×", "÷", "^")) {
                if (currentStringValue in listOf(".", "0.")) {
                    input.text = addToInputText("0-")
                } else {
                    input.text = addToInputText("-")
                }
            }
            showResult2()
            showResult3()
        }

        binding.btnAddition.setOnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    this.performHapticFeedback()
                    animation(binding.tvAddition)
                }
            }

            v?.onTouchEvent(event) ?: true
        }

        binding.btnAddition.setOnClickListener {
            if (input.length() > 0 && currentStringValue !in listOf(
                    "-",
                    "+",
                    "×",
                    "÷",
                    "^(",
                    "^",
                    "0+",
                    "(",
                    "×("
                )
            ) {
                if (currentStringValue == ".") {
                    input.text = addToInputText("0+")
                } else {
                    input.text = addToInputText("+")
                }
            }
            showResult2()
            showResult3()
        }

        binding.btnMultiplication.setOnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    this.performHapticFeedback()
                    animation(binding.tvMultiplication)
                }
            }

            v?.onTouchEvent(event) ?: true
        }

        binding.btnMultiplication.setOnClickListener {
            if (input.length() > 0 && currentStringValue !in listOf(
                    "-",
                    "+",
                    "×",
                    "÷",
                    "^(",
                    "^",
                    "(",
                    "×("
                )
            ) {
                if (currentStringValue == ".") {
                    input.text = addToInputText("0×")
                } else {
                    input.text = addToInputText("×")
                }
            }
            showResult2()
            showResult3()
        }

        binding.btnDivision.setOnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    this.performHapticFeedback()
                    animation(binding.tvDivision)
                }
            }

            v?.onTouchEvent(event) ?: true
        }

        binding.btnDivision.setOnClickListener {
            if (input.length() > 0 && currentStringValue !in listOf(
                    "-",
                    "+",
                    "×",
                    "÷",
                    "^(",
                    "^",
                    "(",
                    "×("
                )
            ) {
                if (currentStringValue == ".") {
                    input.text = addToInputText("0÷")
                } else {
                    input.text = addToInputText("÷")
                }
            }
            showResult2()
            showResult3()
        }

        binding.btnExponent.setOnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    this.performHapticFeedback()
                    animation(binding.tvExponent)
                }
            }

            v?.onTouchEvent(event) ?: true
        }

        binding.btnExponent.setOnClickListener {
            if (input.length() > 0 && currentStringValue !in listOf(
                    "-",
                    "+",
                    "×",
                    "÷",
                    "^(",
                    "^",
                    "(",
                    "×("
                )
            ) {
                if (currentStringValue in listOf(".", "0.")) {
                    input.text = addToInputText("0^(")
                } else {
                    input.text = addToInputText("^(")
                }
            }
            showResult2()
            showResult3()
        }

        binding.btnEqual.setOnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    this.performHapticFeedback()
                    animation(binding.tvEqual)
                }
            }

            v?.onTouchEvent(event) ?: true
        }

        binding.btnEqual.setOnClickListener {
            showResult()
        }

        binding.ivBackspace.setOnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_UP -> {
                    runnable = Runnable {
                        handler.removeCallbacks(runnable!!)
                        runnable?.let { handler.postDelayed(it, 0) }
                    }
                    handler.postDelayed(runnable!!, 0)
                }
            }

            v?.onTouchEvent(event) ?: true
        }

        binding.ivBackspace.setOnLongClickListener {
            this.performHapticFeedback()
            animation(binding.ivBackspace)
            runnable = Runnable {
                if (input.length() > 0) {
                    input.text = input.text.substring(0, input.length() - 1)
//                currentString = input.text.substring(0, input.length())
                    currentLengthValue = input.length()
                    currentLength.text = "Length: $currentLengthValue"
                    currentStringValue = if (input.length() == 0) {
                        ""
                    } else {
                        input.text.substring(input.length() - 1)
                    }
                    currentString.text = "String: $currentStringValue"
                    if (input.length() == 0) {
                        output.text = ""
                        autoCalculate = false
                    }
                    showResult2()
                    showResult3()
                    runnable?.let { handler.postDelayed(it, 200) }
                }
            }
            handler.postDelayed(runnable!!, 0)


            true

        }



        binding.ivBackspace.setOnClickListener {
            this.performHapticFeedback()
            animation(binding.ivBackspace)
            if (input.length() > 0) {
                input.text = input.text.substring(0, input.length() - 1)
//                currentString = input.text.substring(0, input.length())
                currentLengthValue = input.length()
                currentLength.text = "Length: $currentLengthValue"
                currentStringValue = if (input.length() == 0) {
                    ""
                } else {
                    input.text.substring(input.length() - 1)
                }
                currentString.text = "String: $currentStringValue"
                if (input.length() == 0) {
                    output.text = ""
                    autoCalculate = false
                }
                showResult2()
                showResult3()
            }

        }


    }


    @SuppressLint("SetTextI18n")
    private fun addToInputText(buttonValue: String): String {
        if (temp) {
            handler.removeCallbacks(runnable!!)
        }
        YoYo.with(Techniques.FadeIn).duration(0).playOn(output)
        if (buttonValue == "0" && input.text.substring(input.length() - 1) == "÷") {
            output.text = "Infinity"
        }
        if (buttonValue in listOf(
                "+",
                "-",
                "×(",
                "×",
                "÷",
                "^(",
                "0^(",
                "0+",
                "0-",
                "0×",
                "0÷"
            ) || currentLengthValue > 19
        ) {
            autoCalculate = true
        }
        currentStringValue = buttonValue
        currentString.text = "String: $currentStringValue"
        currentLengthValue += currentStringValue.length
        currentLength.text = "Length: $currentLengthValue"
        binding.scrollView.fullScroll(ScrollView.FOCUS_DOWN)
        return "${input.text}$currentStringValue"
    }

    private fun getInputExpression(): String {
        var expression = input.text.replace(Regex("÷"), "/")
        expression = expression.replace(Regex("×"), "*")
        expression = expression.replace(Regex(","), "")
        return expression
    }

    @SuppressLint("SetTextI18n")
    private fun showResult() {
        if (input.length() > 0 && autoCalculate) {
            try {
                val expression = getInputExpression()
                val result = Expression(expression).calculate()
                if (result.isNaN()) {
                    Toast.makeText(this@MainActivity, "Invalid format used.", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    if (result.isNaN()) {

                    }
                    if (output.length() > 19) {
                        runnable = Runnable {
                            runnable?.let { handler.postDelayed(it, 0) }
                            input.text = DecimalFormat("0.######E+0").format(result).toString()
                        }
                        handler.postDelayed(runnable!!, 250)
                    } else {

                        runnable = Runnable {
                            runnable?.let { handler.postDelayed(it, 0) }
                            input.text =
                                DecimalFormat("###,###,###,###.##################").format(result)
                                    .toString()
                        }
                        handler.postDelayed(runnable!!, 250)
//                    input.text = DecimalFormat.getNumberInstance(Locale.US).format(result)
                    }

                    YoYo.with(Techniques.SlideOutUp).duration(500).playOn(output)

                    runnable = Runnable {
                        runnable?.let { handler.postDelayed(it, 0) }
                        output.text = ""
                    }
                    handler.postDelayed(runnable!!, 300)

//                    output.text = ""
                    currentStringValue = input.text.substring(input.length() - 1)
                    currentString.text = "String: $currentStringValue"
                    currentLengthValue = input.text.length
                    currentLength.text = "Length: $currentLengthValue"
                    temp = true
                }
            } catch (e: Exception) {
                output.text = "Error"
                output.setTextColor(ContextCompat.getColor(this, R.color.red))
            }
        }
    }

    private fun showResult2() {

        if (autoCalculate) {
            val expression = getInputExpression()
            val result2 = Expression(expression).calculate()
            try {
                if (result2.isNaN()) {

                } else if (output.length() > 19) {
                    input.text = DecimalFormat("0.######E+0").format(result2).toString()
                } else {
                    output.text =
                        DecimalFormat("###,###,###,###.##################").format(result2)
                            .toString()
//                    output.text = DecimalFormat.getNumberInstance(Locale.US).format(result2)
                }
            } catch (e: Exception) {

            }
        }

    }

    private fun showResult3() {
        if (autoCalculate) {
            if (input.text == "") {
                output.text = ""
                autoCalculate = false
            }
            val expression = getInputExpression()
            val result3 = Expression(expression).calculate()
            try {
                if (result3.isNaN()) {

                } else if (output.length() > 19) {
                    output.text = DecimalFormat("0.######E+0").format(result3).toString()
                } else {
                    output.text =
                        DecimalFormat("###,###,###,###.##################").format(result3)
                            .toString()
//                    output.text = DecimalFormat.getNumberInstance(Locale.US).format(result3)
                }
                input.setTextColor(ContextCompat.getColor(this, R.color.white))
            } catch (e: Exception) {

            }
        }
    }

    private fun Context.performHapticFeedback() {
        val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val vibrationEffect =
                VibrationEffect.createOneShot(5, VibrationEffect.DEFAULT_AMPLITUDE)
            vibrator.vibrate(vibrationEffect)
        } else {
            vibrator.vibrate(TimeUnit.MILLISECONDS.toMillis(SHORT_HAPTIC_FEEDBACK_DURATION))
        }
    }

    private fun animation(view: View) {
        YoYo.with(Techniques.RubberBand).duration(400).playOn(view)
    }


}