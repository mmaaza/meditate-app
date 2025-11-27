package com.medita.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.unit.sp
import com.medita.R

@OptIn(ExperimentalTextApi::class)
private fun ralewayFont(weight: FontWeight) = Font(
    resId = R.font.raleway_variablefont_wght,
    weight = weight,
    variationSettings = FontVariation.Settings(FontVariation.weight(weight.weight))
)

val Raleway = FontFamily(
    ralewayFont(FontWeight.Thin),
    ralewayFont(FontWeight.ExtraLight),
    ralewayFont(FontWeight.Light),
    ralewayFont(FontWeight.Normal),
    ralewayFont(FontWeight.Medium),
    ralewayFont(FontWeight.SemiBold),
    ralewayFont(FontWeight.Bold),
    ralewayFont(FontWeight.ExtraBold),
    ralewayFont(FontWeight.Black)
)

// Set of Material typography styles to start with
val Typography = Typography(
    headlineLarge = TextStyle(
        fontFamily = Raleway,
        fontWeight = FontWeight.Black,
        fontSize = 48.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = Raleway,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = Raleway,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = Raleway,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    )
)
