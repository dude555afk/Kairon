// Adapted from LastChat (https://github.com/Cocolalilal/LastChat), AGPL-3.0.
// Kairon retains its existing color schemes; these are presentation-only design tokens.
package com.inspiredandroid.kai.ui.lastchat
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
object AppSpacing { val xxs=4.dp; val xs=8.dp; val sm=12.dp; val md=16.dp; val lg=24.dp; val xl=28.dp; val xxl=32.dp }
object AppSize { val ChromePill=48.dp; val ChromeBar=56.dp; val ComposerAction=36.dp; val Icon=24.dp }
object AppSurface { const val GLASS_ALPHA_DARK=0.34f; const val GLASS_ALPHA_LIGHT=0.28f; const val SOFT_EDGE_ALPHA=0.6f; val SoftEdgeWidth=1.dp; val TonalElevation=0.dp; fun fill(s:ColorScheme)=s.surfaceContainer; fun softEdgeColor(s:ColorScheme)=s.outlineVariant.copy(alpha=SOFT_EDGE_ALPHA); fun resolve(c:Color,b:Boolean,d:Boolean):Color { val o=c.copy(alpha=1f); if(!b)return o; return o.copy(alpha=if(d)GLASS_ALPHA_DARK else GLASS_ALPHA_LIGHT) } }
object AppShapes { val CardLarge=RoundedCornerShape(28.dp); val CardMedium=RoundedCornerShape(24.dp); val CardSmall=RoundedCornerShape(16.dp); val ButtonPill=RoundedCornerShape(50); val ButtonRounded=RoundedCornerShape(20.dp); val ButtonSquared=RoundedCornerShape(12.dp); val InputField=RoundedCornerShape(24.dp); val SearchField=ButtonPill; val Chip=RoundedCornerShape(12.dp); val Tag=RoundedCornerShape(50); val Dialog=RoundedCornerShape(28.dp); val BottomSheet=RoundedCornerShape(topStart=28.dp,topEnd=28.dp); val Avatar=RoundedCornerShape(50); val IconButton=RoundedCornerShape(50); val Indicator=RoundedCornerShape(8.dp); val ListItem=RoundedCornerShape(24.dp); val ListItemFirst=RoundedCornerShape(24.dp,24.dp,10.dp,10.dp); val ListItemMiddle=RoundedCornerShape(10.dp); val ListItemLast=RoundedCornerShape(10.dp,10.dp,24.dp,24.dp); val CardLargeInner12=RoundedCornerShape(16.dp); val CardLargeInner8=RoundedCornerShape(20.dp); val CardMediumInner12=RoundedCornerShape(12.dp); val CardSmallInner8=RoundedCornerShape(8.dp); val MessageBubbleInner=RoundedCornerShape(8.dp); val MessageOutgoing=RoundedCornerShape(topStart=24.dp,topEnd=24.dp,bottomStart=24.dp,bottomEnd=6.dp); val MessageIncoming=RoundedCornerShape(topStart=24.dp,topEnd=24.dp,bottomStart=6.dp,bottomEnd=24.dp) }
val LastChatShapes=Shapes(extraSmall=RoundedCornerShape(8.dp),small=RoundedCornerShape(12.dp),medium=RoundedCornerShape(16.dp),large=RoundedCornerShape(24.dp),extraLarge=RoundedCornerShape(28.dp))
fun buildLastChatTypography(f:FontFamily=FontFamily.SansSerif)=Typography(displayLarge=st(f,FontWeight.Bold,57,64,-0.25f),displayMedium=st(f,FontWeight.Bold,45,52,0f),displaySmall=st(f,FontWeight.SemiBold,36,44,0f),headlineLarge=st(f,FontWeight.SemiBold,32,40,0f),headlineMedium=st(f,FontWeight.SemiBold,28,36,0f),headlineSmall=st(f,FontWeight.SemiBold,24,32,0f),titleLarge=st(f,FontWeight.SemiBold,22,28,0f),titleMedium=st(f,FontWeight.Medium,16,24,0.15f),titleSmall=st(f,FontWeight.Medium,14,20,0.1f),bodyLarge=st(f,FontWeight.Medium,16,24,0.5f),bodyMedium=st(f,FontWeight.Medium,14,20,0.25f),bodySmall=st(f,FontWeight.Medium,12,16,0.4f),labelLarge=st(f,FontWeight.Medium,14,20,0.1f),labelMedium=st(f,FontWeight.Medium,12,16,0.5f),labelSmall=st(f,FontWeight.Medium,11,16,0.5f))
private fun st(f:FontFamily,w:FontWeight,s:Int,l:Int,letter:Float)=TextStyle(fontFamily=f,fontWeight=w,fontSize=s.sp,lineHeight=l.sp,letterSpacing=letter.sp)