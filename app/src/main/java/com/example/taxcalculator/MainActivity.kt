package com.example.taxcalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taxcalculator.ui.theme.TaxCalculatorTheme
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TaxCalculatorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TaxCalculatorApp()
                }
            }
        }
    }
}

data class TaxBandResult(
    val title: String,
    val taxableAmount: Double,
    val rateLabel: String,
    val taxAmount: Double
)

data class TaxCalculationResult(
    val annualIncome: Double,
    val annualTax: Double,
    val monthlyTax: Double,
    val effectiveRate: Double,
    val taxBands: List<TaxBandResult>
)

@Composable
fun TaxCalculatorApp() {
    var incomeInput by rememberSaveable { mutableStateOf("") }
    val annualIncome = incomeInput.toDoubleOrNull() ?: 0.0
    val calculationResult = remember(annualIncome) {
        calculateTax(annualIncome)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .safeDrawingPadding(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            TitleSection()
        }

        item {
            DisplayCard(incomeInput = incomeInput, annualIncome = annualIncome)
        }

        item {
            SummaryCard(calculationResult = calculationResult)
        }

        item {
            ActionButtons(
                onClear = { incomeInput = "" },
                onBackspace = {
                    if (incomeInput.isNotEmpty()) {
                        incomeInput = incomeInput.dropLast(1)
                    }
                }
            )
        }

        item {
            CalculatorPad(
                onNumberClick = { value ->
                    incomeInput = appendValue(incomeInput, value)
                }
            )
        }


        item {
            TaxBreakdownSection(calculationResult = calculationResult)
        }
    }
}

@Composable
fun TitleSection() {
    Column {
        Text(
            text = "Nigeria Tax Calculator",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
    }
}

@Composable
fun DisplayCard(
    incomeInput: String,
    annualIncome: Double
) {
    val displayValue = if (incomeInput.isBlank()) "0" else incomeInput

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Annual Taxable Income",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "${formatNaira(annualIncome)}",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Enter your annual taxable income",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun SummaryCard(calculationResult: TaxCalculationResult) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Tax Result",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            ResultRow(
                title = "Annual tax to pay",
                value = formatNaira(calculationResult.annualTax)
            )
            ResultRow(
                title = "Estimated monthly tax",
                value = formatNaira(calculationResult.monthlyTax)
            )
            ResultRow(
                title = "Effective tax rate",
                value = "${String.format("%.2f", calculationResult.effectiveRate)}%"
            )
        }
    }
}

@Composable
fun ResultRow(
    title: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.End
        )
    }
}

@Composable
fun ActionButtons(
    onClear: () -> Unit,
    onBackspace: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = onClear,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
        ) {
            Text(text = "Clear")
        }

        Button(
            onClick = onBackspace,
            modifier = Modifier.weight(1f)
        ) {
            Text(text = "Delete")
        }
    }
}

@Composable
fun CalculatorPad(
    onNumberClick: (String) -> Unit
) {
    val buttonRows = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("000", "0", "00")
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        buttonRows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                row.forEach { value ->
                    CalculatorButton(
                        text = value,
                        modifier = Modifier.weight(1f),
                        onClick = { onNumberClick(value) }
                    )
                }
            }
        }
    }
}

@Composable
fun CalculatorButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(64.dp),
        shape = RoundedCornerShape(18.dp)
    ) {
        Text(
            text = text,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}


@Composable
fun TaxBreakdownSection(calculationResult: TaxCalculationResult) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Tax Breakdown",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        calculationResult.taxBands.forEach { band ->
            TaxBandCard(band = band)
        }
    }
}

@Composable
fun TaxBandCard(band: TaxBandResult) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = band.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Taxable amount: ${formatNaira(band.taxableAmount)}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Rate: ${band.rateLabel}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Tax from this band: ${formatNaira(band.taxAmount)}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

fun calculateTax(annualIncome: Double): TaxCalculationResult {
    val safeIncome = annualIncome.coerceAtLeast(0.0)
    val bands = mutableListOf<TaxBandResult>()

    val taxBandDefinitions = listOf(
        Triple(800_000.0, 0.0, "First N800,000"),
        Triple(2_200_000.0, 0.15, "Next N2,200,000"),
        Triple(9_000_000.0, 0.18, "Next N9,000,000"),
        Triple(13_000_000.0, 0.21, "Next N13,000,000"),
        Triple(25_000_000.0, 0.23, "Next N25,000,000"),
        Triple(Double.MAX_VALUE, 0.25, "Income above N50,000,000")
    )

    var remainingIncome = safeIncome

    for ((bandLimit, rate, title) in taxBandDefinitions) {
        if (remainingIncome <= 0) {
            break
        }

        val taxableAmount = minOf(remainingIncome, bandLimit)
        val taxAmount = taxableAmount * rate

        bands.add(
            TaxBandResult(
                title = title,
                taxableAmount = taxableAmount,
                rateLabel = "${(rate * 100).roundToInt()}%",
                taxAmount = taxAmount
            )
        )

        remainingIncome -= taxableAmount
    }

    val annualTax = bands.sumOf { it.taxAmount }
    val monthlyTax = annualTax / 12
    val effectiveRate = if (safeIncome == 0.0) 0.0 else (annualTax / safeIncome) * 100

    return TaxCalculationResult(
        annualIncome = safeIncome,
        annualTax = annualTax,
        monthlyTax = monthlyTax,
        effectiveRate = effectiveRate,
        taxBands = if (bands.isEmpty()) {
            listOf(
                TaxBandResult(
                    title = "No tax yet",
                    taxableAmount = 0.0,
                    rateLabel = "0%",
                    taxAmount = 0.0
                )
            )
        } else {
            bands
        }
    )
}

fun appendValue(currentInput: String, newValue: String): String {
    if (currentInput == "0") {
        return newValue
    }

    if (currentInput.length >= 12) {
        return currentInput
    }

    return currentInput + newValue
}

fun formatNaira(amount: Double): String {
    return "₦${String.format("%,.2f", amount)}"
}

@Preview(showBackground = true)
@Composable
fun TaxCalculatorPreview() {
    TaxCalculatorTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            TaxCalculatorApp()
        }
    }
}
