package io.healthkathon.jkb.core.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import bpjs_jkb.core.generated.resources.HankenGrotesk_Black
import bpjs_jkb.core.generated.resources.HankenGrotesk_BlackItalic
import bpjs_jkb.core.generated.resources.HankenGrotesk_Bold
import bpjs_jkb.core.generated.resources.HankenGrotesk_BoldItalic
import bpjs_jkb.core.generated.resources.HankenGrotesk_ExtraBold
import bpjs_jkb.core.generated.resources.HankenGrotesk_ExtraBoldItalic
import bpjs_jkb.core.generated.resources.HankenGrotesk_ExtraLight
import bpjs_jkb.core.generated.resources.HankenGrotesk_ExtraLightItalic
import bpjs_jkb.core.generated.resources.HankenGrotesk_Italic
import bpjs_jkb.core.generated.resources.HankenGrotesk_Light
import bpjs_jkb.core.generated.resources.HankenGrotesk_LightItalic
import bpjs_jkb.core.generated.resources.HankenGrotesk_Medium
import bpjs_jkb.core.generated.resources.HankenGrotesk_MediumItalic
import bpjs_jkb.core.generated.resources.HankenGrotesk_Regular
import bpjs_jkb.core.generated.resources.HankenGrotesk_SemiBold
import bpjs_jkb.core.generated.resources.HankenGrotesk_SemiBoldItalic
import bpjs_jkb.core.generated.resources.HankenGrotesk_Thin
import bpjs_jkb.core.generated.resources.HankenGrotesk_ThinItalic
import bpjs_jkb.core.generated.resources.Outfit_Black
import bpjs_jkb.core.generated.resources.Outfit_Bold
import bpjs_jkb.core.generated.resources.Outfit_ExtraBold
import bpjs_jkb.core.generated.resources.Outfit_ExtraLight
import bpjs_jkb.core.generated.resources.Outfit_Light
import bpjs_jkb.core.generated.resources.Outfit_Medium
import bpjs_jkb.core.generated.resources.Outfit_Regular
import bpjs_jkb.core.generated.resources.Outfit_SemiBold
import bpjs_jkb.core.generated.resources.Outfit_Thin
import bpjs_jkb.core.generated.resources.Res
import org.jetbrains.compose.resources.Font

@Composable
fun jkbTypography(): Typography {
    val outfitFont = FontFamily(
        Font(Res.font.Outfit_Thin, FontWeight.Thin),
        Font(Res.font.Outfit_ExtraLight, FontWeight.ExtraLight),
        Font(Res.font.Outfit_Light, FontWeight.Light),
        Font(Res.font.Outfit_Regular, FontWeight.Normal),
        Font(Res.font.Outfit_Medium, FontWeight.Medium),
        Font(Res.font.Outfit_SemiBold, FontWeight.SemiBold),
        Font(Res.font.Outfit_Bold, FontWeight.Bold),
        Font(Res.font.Outfit_ExtraBold, FontWeight.ExtraBold),
        Font(Res.font.Outfit_Black, FontWeight.Black)
    )
    val hankenGroteskFamily = FontFamily(
        fonts = listOf(
            Font(
                Res.font.HankenGrotesk_Thin,
                FontWeight.Thin,
                FontStyle.Normal
            ),
            Font(
                Res.font.HankenGrotesk_ThinItalic,
                FontWeight.Thin,
                FontStyle.Italic
            ),
            Font(
                Res.font.HankenGrotesk_ExtraLight,
                FontWeight.ExtraLight,
                FontStyle.Normal
            ),
            Font(
                Res.font.HankenGrotesk_ExtraLightItalic,
                FontWeight.ExtraLight,
                FontStyle.Italic
            ),
            Font(
                Res.font.HankenGrotesk_Light,
                FontWeight.Light,
                FontStyle.Normal
            ),
            Font(
                Res.font.HankenGrotesk_LightItalic,
                FontWeight.Light,
                FontStyle.Italic
            ),
            Font(
                Res.font.HankenGrotesk_Regular,
                FontWeight.Normal,
                FontStyle.Normal
            ),
            Font(
                Res.font.HankenGrotesk_Italic,
                FontWeight.Normal,
                FontStyle.Italic
            ),
            Font(
                Res.font.HankenGrotesk_Medium,
                FontWeight.Medium,
                FontStyle.Normal
            ),
            Font(
                Res.font.HankenGrotesk_MediumItalic,
                FontWeight.Medium,
                FontStyle.Italic
            ),
            Font(
                Res.font.HankenGrotesk_SemiBold,
                FontWeight.SemiBold,
                FontStyle.Normal
            ),
            Font(
                Res.font.HankenGrotesk_SemiBoldItalic,
                FontWeight.SemiBold,
                FontStyle.Italic
            ),
            Font(
                Res.font.HankenGrotesk_Bold,
                FontWeight.Bold,
                FontStyle.Normal
            ),
            Font(
                Res.font.HankenGrotesk_BoldItalic,
                FontWeight.Bold,
                FontStyle.Italic
            ),
            Font(
                Res.font.HankenGrotesk_ExtraBold,
                FontWeight.ExtraBold,
                FontStyle.Normal
            ),
            Font(
                Res.font.HankenGrotesk_ExtraBoldItalic,
                FontWeight.ExtraBold,
                FontStyle.Italic
            ),
            Font(
                Res.font.HankenGrotesk_Black,
                FontWeight.Black,
                FontStyle.Normal
            ),
            Font(
                Res.font.HankenGrotesk_BlackItalic,
                FontWeight.Black,
                FontStyle.Italic
            )
        )
    )
    val baseline = Typography()
    return Typography(
        displayLarge = baseline.displayLarge.copy(fontFamily = outfitFont),
        displayMedium = baseline.displayMedium.copy(fontFamily = outfitFont),
        displaySmall = baseline.displaySmall.copy(fontFamily = outfitFont),
        headlineLarge = baseline.headlineLarge.copy(fontFamily = outfitFont),
        headlineMedium = baseline.headlineMedium.copy(fontFamily = outfitFont),
        headlineSmall = baseline.headlineSmall.copy(fontFamily = outfitFont),
        titleLarge = baseline.titleLarge.copy(fontFamily = outfitFont),
        titleMedium = baseline.titleMedium.copy(fontFamily = outfitFont),
        titleSmall = baseline.titleSmall.copy(fontFamily = outfitFont),
        bodyLarge = baseline.bodyLarge.copy(fontFamily = hankenGroteskFamily),
        bodyMedium = baseline.bodyMedium.copy(fontFamily = hankenGroteskFamily),
        bodySmall = baseline.bodySmall.copy(fontFamily = hankenGroteskFamily),
        labelLarge = baseline.labelLarge.copy(fontFamily = hankenGroteskFamily),
        labelMedium = baseline.labelMedium.copy(fontFamily = hankenGroteskFamily),
        labelSmall = baseline.labelSmall.copy(fontFamily = hankenGroteskFamily),
    )
}
