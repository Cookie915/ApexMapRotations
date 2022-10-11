package com.cooksmobilesolutions.apexmaprotations.compose

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cooksmobilesolutions.apexmaprotations.R
import kotlin.math.cos
import kotlin.math.sin

/**
 * Bubble Menu
 * @author calebcookdev@gmail.com
 *
 * Expandable bubble with clickable icons distributed around it.
 * @param mainIcon Icon used for collapsed button
 * @param size Size of circle diameter, in dp
 * @param bubbleItemSize Size of Menu Items, upper limit is size parameter
 * @param bubbleMenuItems List of state to use for each BubbleMenuItem, Items will be displayed the order they were passed in, following the Cartesian plane, and based on what quadrants you make available.
 * @param quadrants Data class holding flags that determine what quadrants of a cartesian plane to distribute BubbleItems around
 */
@Composable
fun BubbleMenu(
    mainIcon: Int,
    size: Dp,
    bubbleItemSize: Dp,
    bubbleMenuItems: MutableList<BubbleMenuItemState>,
    quadrants: BubbleMenuQuadrants,
    contentDescription: String,
    borderColor: Color = Color.Black,
) {
    var expanded by remember {
        mutableStateOf(false)
    }
    val expandedTransition =
        updateTransition(targetState = expanded, label = "BubbleMenu Expand Transition")

    val iconScale by expandedTransition.animateFloat(
        label = "BubbleMenu Icon Scale Animation",
        transitionSpec = {
            tween(
                durationMillis = 300,
                easing = FastOutSlowInEasing
            )
        }
    ) {
        when (it) {
            true -> 1f
            false -> 0.8f
        }
    }

    val visibility by expandedTransition.animateFloat(label = "BubbleMenu Visibility Animation") { isExpanded ->
        when (isExpanded) {
            true -> 1f
            false -> 0f
        }
    }

    // Increments to place each BubbleMenuItem in radians
    val increment by remember {
        derivedStateOf {
            if (bubbleMenuItems.size > 0) Math.toRadians(
                (quadrants.activeCount().times(90)).div(bubbleMenuItems.size)
            ) else 0
        }
    }
    Box(
        modifier = Modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        //  current radian at which we place menuBubbleItem
        var curRadian = 0.0
        var itemPlaceCount = 0
        quadrants.quadrants.forEachIndexed { quadrantNumber, active ->
            val qNum = quadrantNumber + 1
            if (active == 0) {
                when (quadrantNumber) {
                    0 -> {
                        curRadian = Math.toRadians(90.0)
                    }
                    1 -> {
                        curRadian = Math.toRadians(180.0)
                    }
                    2 -> {
                        curRadian = Math.toRadians(270.0)
                    }
                    3 -> {
                        curRadian = Math.toRadians(360.0)
                    }
                }
            } else {
                while (curRadian <= Math.toRadians(qNum.times(90.0)) && itemPlaceCount < bubbleMenuItems.size) {
                    val menuItemData = bubbleMenuItems[itemPlaceCount]
                    val offset by expandedTransition.animateOffset(
                        label = "BubbleMenu Offset Animation",
                        transitionSpec = { tween(300, 0, FastOutSlowInEasing) }
                    ) { expanded ->
                        when (expanded) {
                            true -> Offset(
                                x = size.value.times(1.5).times(cos(curRadian)).times(0.9)
                                    .toFloat(),
                                y = size.value.times(1.5).times(sin(curRadian)).times(0.9)
                                    .toFloat()
                            )
                            false -> Offset.Zero
                        }
                    }
                    MenuBubbleItem(
                        modifier = Modifier
                            .size(bubbleItemSize)
                            .offset(offset.x.dp, -offset.y.dp)
                            .alpha(visibility),
                        state = menuItemData,
                    )
                    curRadian += increment.toDouble()
                    itemPlaceCount++
                }
            }
        }
        OutlinedButton(
            modifier = Modifier
                .size(size)
                .scale(iconScale),
            onClick = {
                expanded = !expanded
            },
            colors =
            ButtonDefaults.outlinedButtonColors(
                backgroundColor = Color.Transparent
            ),
            shape = CircleShape,
            border = BorderStroke(1.dp, borderColor),
            contentPadding = PaddingValues(4.dp),
        ) {
            Icon(
                painter = painterResource(id = mainIcon),
                contentDescription = contentDescription,
                tint = Color.Unspecified
            )
        }
    }
}


/**
 * Bubble menu quadrants
 * @author calebcookdev@gmail.com
 * @param quadrants list of length 4 with a value of either 0 or 1, representing the active state of the quadrant
 * @property quadrants list of length 4 with a value of either 0 or 1, representing the active state of the quadrant
 * @constructor Create empty Bubble menu quadrants
 */
data class BubbleMenuQuadrants(
    var quadrants: List<Int> = listOf(1, 0, 0, 0),
) {
    //  Returns number of enabled quadrants
    fun activeCount(): Double {
        var returnCount = 0.0
        quadrants.forEach {
            if (it == 1) returnCount++
        }
        return returnCount
    }
}

/**
 * Menu bubble item state
 *
 * @param icon drawable to use for MenuItem
 * @param label option label to display above MenuItem
 * @param borderColor color of border surrounding MenuItem
 * @param showLabel flag to show label
 * @param onMenuItemClick optional function to be invoked when MenuItem is clicked
 * @property icon drawable to use for MenuItem
 * @property label option label to display above MenuItem
 * @property borderColor color of border surrounding MenuItem
 * @property showLabel flag to show label
 * @property onMenuItemClick optional function to be invoked when MenuItem is clicked
 * @constructor Create empty Menu bubble item state
 */
data class BubbleMenuItemState(
    val icon: Int,
    val label: String,
    val borderColor: Color = Color.Black,
    val showLabel: Boolean = false,
    val onMenuItemClick: ((BubbleMenuItemState) -> Unit)? = null,
)

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MenuBubbleItem(
    modifier: Modifier,
    state: BubbleMenuItemState,
) {
    Box(modifier = modifier.background(Color.Transparent)) {
        Card(
            onClick = { state.onMenuItemClick?.let { it(state) } },
            modifier = Modifier.background(Color.Transparent),
            shape = CircleShape,
            border = BorderStroke(1.dp, state.borderColor),
            backgroundColor = Color.Transparent,
            elevation = 0.dp,
        ) {
            Icon(
                modifier = Modifier.scale(0.7f),
                painter = painterResource(id = state.icon),
                contentDescription = state.label,
                tint = Color.Unspecified
            )
        }
        if (state.showLabel) {
            Text(
                text = state.label,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (-18).dp),
                overflow = TextOverflow.Visible,
                fontWeight = FontWeight.Light,
                fontSize = 12.sp,
                maxLines = 1
            )
        }
    }
}

class BubbleMenuView @JvmOverloads constructor(
    context: Context,
    attr: AttributeSet? = null,
    defStyle: Int = 0,
) : AbstractComposeView(context, attr, defStyle) {
    var mainIcon by mutableStateOf(
        R.drawable.ic_alarm
    )
    var size by mutableStateOf(
        100.dp
    )
    var bubbleItemSize by mutableStateOf(50.dp)
    var bubbleMenuItems = mutableStateListOf(BubbleMenuItemState(R.drawable.ic_alarm, "") {})
    var quadrants by mutableStateOf(BubbleMenuQuadrants())
    var contentDescriptionString by mutableStateOf("")

    @Composable
    override fun Content() {
        val viewGroup = this.parent as ViewGroup
        viewGroup.clipChildren = false
        viewGroup.clipToPadding = false
        BubbleMenu(
            mainIcon = mainIcon,
            size = size,
            bubbleItemSize = bubbleItemSize,
            bubbleMenuItems = bubbleMenuItems,
            quadrants = quadrants,
            contentDescription = contentDescriptionString
        )
    }
}