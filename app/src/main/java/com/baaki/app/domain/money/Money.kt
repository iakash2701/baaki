package com.baaki.app.domain.money

import kotlin.math.abs

/**
 * Represents a monetary amount in Indian Paise (1 INR = 100 Paise).
 * All financial arithmetic must use this type to avoid floating-point errors.
 */
@JvmInline
value class Money(val paise: Long) : Comparable<Money> {

    operator fun plus(other: Money) = Money(paise + other.paise)
    operator fun minus(other: Money) = Money(paise - other.paise)
    operator fun unaryMinus() = Money(-paise)

    operator fun times(factor: Int) = Money(paise * factor)
    operator fun times(factor: Long) = Money(paise * factor)

    /** Multiply by a percentage (e.g. 18.0 for 18%) and return result in paise, half-up rounded */
    fun applyPercent(percent: Double): Money {
        val raw = paise * percent / 100.0
        return Money(Math.round(raw))
    }

    /** Multiply quantity × unit price (both in paise) */
    fun times(qty: Double): Money {
        return Money(Math.round(paise * qty))
    }

    val isNegative get() = paise < 0
    val isZero get() = paise == 0L
    val isPositive get() = paise > 0

    override fun compareTo(other: Money) = paise.compareTo(other.paise)

    override fun toString() = formatINR()

    fun formatINR(): String {
        val absVal = abs(paise)
        val rupees = absVal / 100
        val paiseRemainder = absVal % 100

        val formatted = formatIndianNumber(rupees)
        val sign = if (paise < 0) "-" else ""

        return if (paiseRemainder == 0L) {
            "${sign}₹$formatted"
        } else {
            "${sign}₹$formatted.${paiseRemainder.toString().padStart(2, '0')}"
        }
    }

    /** Compact display: ₹1.2L, ₹2.5Cr */
    fun formatCompact(): String {
        val absVal = abs(paise)
        val rupees = absVal / 100.0
        val sign = if (paise < 0) "-" else ""
        return when {
            rupees >= 1_00_00_000 -> "${sign}₹${"%.1f".format(rupees / 1_00_00_000)}Cr"
            rupees >= 1_00_000 -> "${sign}₹${"%.1f".format(rupees / 1_00_000)}L"
            rupees >= 1_000 -> "${sign}₹${"%.0f".format(rupees / 1_000)}K"
            else -> formatINR()
        }
    }

    companion object {
        val ZERO = Money(0L)

        fun fromRupees(rupees: Double): Money = Money(Math.round(rupees * 100))
        fun fromRupees(rupees: Long): Money = Money(rupees * 100)
        fun fromRupees(rupees: Int): Money = Money(rupees.toLong() * 100)

        /** Parse "120000.50" as Money */
        fun fromString(s: String): Money {
            return try {
                fromRupees(s.trim().toDouble())
            } catch (e: NumberFormatException) {
                ZERO
            }
        }

        private fun formatIndianNumber(n: Long): String {
            if (n == 0L) return "0"
            val s = n.toString()
            if (s.length <= 3) return s
            val last3 = s.takeLast(3)
            val rest = s.dropLast(3)
            val groups = rest.reversed().chunked(2).reversed().joinToString(",") { it.reversed() }
            return "$groups,$last3"
        }
    }
}

fun Long.paise() = Money(this)
fun Int.paise() = Money(this.toLong())
fun Double.rupees() = Money.fromRupees(this)
fun Long.rupees() = Money.fromRupees(this)
fun Int.rupees() = Money.fromRupees(this)
