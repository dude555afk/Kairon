// Adapted from LastChat (https://github.com/Cocolalilal/LastChat), AGPL-3.0. Package paths and integration were modified.
package com.inspiredandroid.kai.ui.lastchat

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** The production LastChat modal drawer surface shared by Android and iOS. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LastChatModalDrawerSheet(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    ModalDrawerSheet(
        modifier = modifier,
        drawerShape = RoundedCornerShape(topEnd = 32.dp, bottomEnd = 32.dp),
        drawerContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        content = content,
    )
}
