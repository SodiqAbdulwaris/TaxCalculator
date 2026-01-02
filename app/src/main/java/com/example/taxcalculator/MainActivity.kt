
package com.example.taxcalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.taxcalculator.ui.theme.TipTimeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            TipTimeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    TaxCalculatorLayout()
                }
            }
        }
    }
}

@Composable
fun TaxCalculatorLayout() {

//Annual income state
    var incomeInput by remember {mutableStateOf("")}
    val income = incomeInput.replace(",", "").toDoubleOrNull() ?: 0.0

    val tax = calculateTax(income)

    Column(
        modifier = Modifier
            .statusBarsPadding()
            .padding(horizontal = 40.dp)
            .safeDrawingPadding()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.calculate_tax),
            modifier = Modifier
                .padding(bottom = 16.dp, top = 40.dp)
                .align(alignment = Alignment.Start)
        )
        EditNumberField(
            value = incomeInput,
            onValueChange = { incomeInput = it },
            label = R.string.annual_income,
            leadingIcon = R.drawable.money,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.tax_amount, tax),
            style = MaterialTheme.typography.displaySmall,
            modifier = Modifier.padding(top = 15.dp)
        )
        Spacer(modifier = Modifier.height(150.dp))
    }
}

@Composable
fun EditNumberField(
    value: String,
    @StringRes label : Int,
    @DrawableRes leadingIcon: Int,
    keyboardOptions: KeyboardOptions,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier){


    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = stringResource(label)) },
        leadingIcon = { Icon(painter = painterResource(leadingIcon), null) },
        singleLine = true,
        keyboardOptions = keyboardOptions,
        modifier = Modifier
            .padding(bottom = 15.dp)
            .fillMaxWidth()
    )
}

/*
 Calculates tax based on Nigeria 2026 Tax Act progressive rates.
 Tax rates:
    0% for first ₦800,000
    15% for ₦800,001 to ₦3,000,000
    18% for ₦3,000,001 to ₦12,000,000
    21% for ₦12,000,001 to ₦25,000,000
    23% for ₦25,000,001 to ₦50,000,000
    25% for above ₦50,000,000
*/

private fun calculateTax(annualIncome: Double): String {
    val tax = when {
        annualIncome <= 800_000 -> 0.0
        annualIncome <= 3_000_000 -> {
            (annualIncome - 800_000) * 0.15
        }
        annualIncome <= 12_000_000 -> {
            (2_200_000 * 0.15) + (annualIncome - 3_000_000) * 0.18
        }
        annualIncome <= 25_000_000 -> {
            (2_200_000 * 0.15) + (9_000_000 * 0.18) + (annualIncome - 12_000_000) * 0.21
        }
        annualIncome <= 50_000_000 -> {
            (2_200_000 * 0.15) + (9_000_000 * 0.18) + (13_000_000 * 0.21) + (annualIncome - 25_000_000) * 0.23
        }
        else -> {
            (2_200_000 * 0.15) + (9_000_000 * 0.18) + (13_000_000 * 0.21) + (25_000_000 * 0.23) + (annualIncome - 50_000_000) * 0.25
        }
    }
    return "₦${String.format("%,.2f", tax)}"
}

@Preview(showBackground = true)
@Composable
fun TaxCalculatorLayoutPreview() {
    TipTimeTheme {
        TaxCalculatorLayout()
    }
}
