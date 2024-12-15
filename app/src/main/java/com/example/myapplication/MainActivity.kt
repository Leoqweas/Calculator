package com.example.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var tvInput: TextView

    private var lastNumeric: Boolean = false // Флаг: последний символ был цифрой
    private var lastDot: Boolean = false     // Флаг: была введена точка

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Инициализация TextView
        tvInput = findViewById(R.id.tvInput)

        // Установка обработчиков кнопок
        setButtonListeners()
    }

    private fun setButtonListeners() {
        // Кнопки с цифрами и точкой
        val digitButtons = listOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9, R.id.btnDot
        )
        digitButtons.forEach { id ->
            findViewById<Button>(id).setOnClickListener { onDigitPressed((it as Button).text.toString()) }
        }

        // Кнопки с операторами
        val operatorButtons = listOf(
            R.id.btnAdd, R.id.btnSubtract, R.id.btnMultiply, R.id.btnDivide
        )
        operatorButtons.forEach { id ->
            findViewById<Button>(id).setOnClickListener { onOperatorPressed((it as Button).text.toString()) }
        }

        // Кнопка очистки
        findViewById<Button>(R.id.btnClear).setOnClickListener { onClear() }

        // Кнопка равно
        findViewById<Button>(R.id.btnEquals).setOnClickListener { onEqual() }
    }

    // Метод для ввода цифр и точки
    private fun onDigitPressed(digit: String) {
        if (tvInput.text.toString() == "0" && digit != ".") {
            // Если на экране 0, заменяем его на введенную цифру
            tvInput.text = digit
        } else {
            // Добавляем цифру или точку
            if (digit == "." && lastDot) {
                Toast.makeText(this, "Ошибка: точка уже введена!", Toast.LENGTH_SHORT).show()
            } else {
                tvInput.append(digit)
                if (digit == ".") lastDot = true
            }
        }
        lastNumeric = true
    }

    // Метод для ввода операторов
    private fun onOperatorPressed(operator: String) {
        if (lastNumeric && !endsWithOperator(tvInput.text.toString())) {
            tvInput.append(operator)
            lastNumeric = false
            lastDot = false
        } else {
            Toast.makeText(this, "Ошибка: введите число перед оператором!", Toast.LENGTH_SHORT).show()
        }
    }

    // Метод для очистки экрана
    private fun onClear() {
        tvInput.text = "0" // Устанавливаем 0 при очистке
        lastNumeric = false
        lastDot = false
    }

    // Метод для вычисления результата
    private fun onEqual() {
        if (!lastNumeric) {
            Toast.makeText(this, "Ошибка: завершите ввод числа!", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val input = tvInput.text.toString()
            val result = evaluateExpression(input)
            tvInput.text = result.toString()
        } catch (e: Exception) {
            Toast.makeText(this, "Ошибка вычисления: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // Метод для вычисления выражения
    private fun evaluateExpression(expression: String): Double {
        var expressionCopy = expression
        if (expressionCopy.startsWith("-")) {
            expressionCopy = "0$expressionCopy" // Добавляем 0 перед отрицательным числом
        }

        val tokens = Regex("([-+/*])").split(expressionCopy).filter { it.isNotBlank() }
        val operators = Regex("[-+/*]").findAll(expressionCopy).map { it.value }.toList()

        if (tokens.size - 1 != operators.size) throw IllegalArgumentException("Некорректный ввод")

        var result = tokens[0].toDouble()

        for (i in operators.indices) {
            val nextNumber = tokens[i + 1].toDouble()
            when (operators[i]) {
                "+" -> result += nextNumber
                "-" -> result -= nextNumber
                "*" -> result *= nextNumber
                "/" -> {
                    if (nextNumber == 0.0) throw ArithmeticException("Деление на ноль!")
                    result /= nextNumber
                }
            }
        }

        return result
    }

    // Метод для проверки, заканчивается ли строка оператором
    private fun endsWithOperator(value: String): Boolean {
        return value.endsWith("+") || value.endsWith("-") || value.endsWith("*") || value.endsWith("/")
    }
}
