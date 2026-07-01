package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.UserRole

@Composable
fun MainLayout(
    userRole: UserRole,
    currentRoute: String,
    onNavigate: (String) -> Unit,
    content: @Composable () -> Unit
) {
    Scaffold(
        bottomBar = {
            MainBottomNavigation(
                userRole = userRole,
                currentRoute = currentRoute,
                onNavigate = onNavigate
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            content()
        }
    }
}

data class NavItem(
    val title: String,
    val icon: ImageVector,
    val route: String
)

@Composable
fun MainBottomNavigation(
    userRole: UserRole,
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    val items = when (userRole) {
        UserRole.TEACHER -> listOf(
            NavItem("Home", Icons.Default.Home, "teacher_dashboard"),
            NavItem("Turmas", Icons.Default.Groups, "teacher_classes"),
            NavItem("Avisos", Icons.Default.Notifications, "notifications"),
            NavItem("Perfil", Icons.Default.Person, "profile")
        )
        UserRole.PARENT -> listOf(
            NavItem("Home", Icons.Default.Home, "parent_dashboard"),
            NavItem("Filhos", Icons.Default.Face, "children"),
            NavItem("Mensagens", Icons.Default.Notifications, "messages"),
            NavItem("Perfil", Icons.Default.Person, "profile")
        )
        UserRole.ADMIN -> listOf(
            NavItem("Home", Icons.Default.Home, "school_dashboard"),
            NavItem("Gestão", Icons.Default.School, "management"),
            NavItem("Comunicados", Icons.Default.Campaign, "announcements"),
            NavItem("Perfil", Icons.Default.Person, "profile")
        )
        UserRole.SUPER_ADMIN -> listOf(
            NavItem("Home", Icons.Default.Home, "super_admin_dashboard"),
            NavItem("Escolas", Icons.Default.School, "schools_management"),
            NavItem("Config", Icons.Default.Settings, "system_settings"),
            NavItem("Perfil", Icons.Default.Person, "profile")
        )
        UserRole.STUDENT -> listOf(
            NavItem("Home", Icons.Default.Home, "student_dashboard"),
            NavItem("Boletim", Icons.Default.School, "grades"),
            NavItem("Avisos", Icons.Default.Notifications, "notifications"),
            NavItem("Perfil", Icons.Default.Person, "profile")
        )
    }

    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 16.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val selected = currentRoute == item.route
                val contentColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                
                IconButton(
                    onClick = { onNavigate(item.route) },
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
                    ) {
                        if (selected) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(item.icon, contentDescription = item.title, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(20.dp))
                            }
                        } else {
                            Icon(item.icon, contentDescription = item.title, tint = contentColor, modifier = Modifier.size(24.dp))
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal),
                            color = contentColor
                        )
                    }
                }
            }
        }
    }
}
