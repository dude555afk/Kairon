package com.inspiredandroid.kai.ui.chat.composables


import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.Spacer

import androidx.compose.foundation.layout.defaultMinSize

import androidx.compose.foundation.layout.fillMaxWidth

import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.default.Add

import androidx.compose.material.icons.default.Menu

import androidx.compose.material3.Icon

import androidx.compose.material3.IconButton

import androidx.compose.material3.MaterialTheme

import androidx.compose.runtime.Composable

import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier

import androidx.compose.ui.unit.dp

import com.inspiredandroid.kai.ui.handCursor

import com.inspiredandroid.kai.ui.chat.ChatActions

import com.inspiredandroid.kai.ui.chat.composables.ServiceSelector


@Composable

internal fun TopBar(
    actions: ChatActions,
    availableServices: androidx.compose.runtime.ImmutableList<com.inspiredandroid.kai.data.ServiceEntry> = androidx.compose.runtime.ImmutableList.empty(),
    onSelectService: (String) -> Unit = {},
    onNewChat: () -> Unit = {},
    onOpenDrawer: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 56.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Left: menu icon opens drawer
        IconButton(
            modifier = Modifier.handCursor(),
            onClick = onOpenDrawer,
        ) {
            Icon(
                imageVector = Menu,
                contentDescription = "Open navigation drawer",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(24.dp),
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Center: model pill
        if (availableServices.isNotEmpty()) {
            ServiceSelector(
                services = availableServices,
                onSelectService = onSelectService,
            )
        } else {
            Spacer(modifier = Modifier.defaultMinSize(minHeight = 34.dp))
        }

        Spacer(modifier = Modifier.weight(1f))

        // Right: New Chat
        IconButton(
            modifier = Modifier.handCursor(),
            onClick = onNewChat,
        ) {
            Icon(
                imageVector = Add,
                contentDescription = "New chat",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(24.dp),
            )
        }
    }
}
