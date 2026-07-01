package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class StudentAttendance(
    val id: String,
    val name: String,
    val number: String,
    val frequency: String,
    val isFrequencyGood: Boolean,
    var status: AttendanceStatus? = null
)

enum class AttendanceStatus {
    PRESENT, ABSENT, JUSTIFIED, LATE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(
    onNavigateBack: () -> Unit
) {
    var students by remember {
        mutableStateOf(
            listOf(
                StudentAttendance("1", "Ana Beatriz Silva", "Nº 01", "100% Freq.", true, AttendanceStatus.PRESENT),
                StudentAttendance("2", "Bruno Fernandes", "Nº 02", "100% Freq.", true, AttendanceStatus.PRESENT),
                StudentAttendance("3", "Carlos Oliveira", "Nº 03", "100% Freq.", true, AttendanceStatus.PRESENT),
                StudentAttendance("4", "Daniela Mendes", "Nº 04", "100% Freq.", true, AttendanceStatus.PRESENT)
            )
        )
    }

    var showWarningDialog by remember { mutableStateOf(false) }
    var highestFaultsMessage by remember { mutableStateOf("") }
    
    // Simulating saved faults
    var savedFaults by remember { mutableStateOf(mapOf<String, Int>("1" to 0, "2" to 0, "3" to 0, "4" to 0)) }

    if (showWarningDialog) {
        AlertDialog(
            onDismissRequest = { showWarningDialog = false },
            title = { Text("Aviso de Faltas") },
            text = { Text(highestFaultsMessage) },
            confirmButton = {
                Button(onClick = { showWarningDialog = false }) {
                    Text("OK")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("8º Ano B", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Text("Matutino • Sala 12", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp)
            ) {
                Button(
                    onClick = { 
                        // Update faults based on current status
                        val newFaults = savedFaults.toMutableMap()
                        var anyFault = false
                        students.forEach { student ->
                            if (student.status == AttendanceStatus.ABSENT) {
                                newFaults[student.id] = (newFaults[student.id] ?: 0) + 1
                                anyFault = true
                            }
                        }
                        savedFaults = newFaults
                        
                        // Find highest faults
                        val maxFaults = newFaults.values.maxOrNull() ?: 0
                        if (maxFaults > 0) {
                            val mostAbsents = newFaults.filterValues { it == maxFaults }.keys
                            val names = students.filter { it.id in mostAbsents }.joinToString { it.name }
                            highestFaultsMessage = "Alunos com mais faltas ($maxFaults faltas):\n$names"
                            showWarningDialog = true
                        } else {
                            // No faults or everyone present
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Salvar chamada", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // Date Selector
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(Icons.Default.ChevronLeft, contentDescription = "Previous", tint = MaterialTheme.colorScheme.primary)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("HOJE", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            Text("Segunda, 12 de Outubro", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(Icons.Default.ChevronRight, contentDescription = "Next", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }

            item {
                // Stats Bento Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard("Presentes", "${students.count { it.status == AttendanceStatus.PRESENT }}", "/ ${students.size}", MaterialTheme.colorScheme.tertiary, modifier = Modifier.weight(1f))
                    StatCard("Faltas", "${students.count { it.status == AttendanceStatus.ABSENT }}", "", MaterialTheme.colorScheme.error, modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard("Justificados", "${students.count { it.status == AttendanceStatus.JUSTIFIED }}", "", MaterialTheme.colorScheme.onSecondaryContainer, modifier = Modifier.weight(1f))
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            val pct = if(students.isNotEmpty()) (students.count { it.status == AttendanceStatus.PRESENT } * 100f / students.size) else 100f
                            Text("Presença", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(String.format("%.1f%%", pct), style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            item {
                // Search
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = "",
                        onValueChange = {},
                        placeholder = { Text("Buscar aluno...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        modifier = Modifier.weight(1f),
                        shape = CircleShape
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = { /*TODO*/ },
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                    ) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter", tint = MaterialTheme.colorScheme.primary)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("LISTA DE ALUNOS", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
            }

            items(students) { student ->
                StudentAttendanceCard(
                    student = student,
                    onStatusChange = { newStatus ->
                        students = students.map {
                            if (it.id == student.id) it.copy(status = newStatus) else it
                        }
                    }
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun StatCard(label: String, value: String, suffix: String, valueColor: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(value, style = MaterialTheme.typography.headlineMedium, color = valueColor, fontWeight = FontWeight.Bold)
                if (suffix.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(suffix, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(bottom = 4.dp))
                }
            }
        }
    }
}

@Composable
fun StudentAttendanceCard(
    student: StudentAttendance,
    onStatusChange: (AttendanceStatus) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(student.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("${student.number} • ", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            student.frequency,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (student.isFrequencyGood) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AttendanceButton(
                    label = "P",
                    subLabel = "PRES",
                    isSelected = student.status == AttendanceStatus.PRESENT,
                    activeColor = MaterialTheme.colorScheme.primaryContainer,
                    activeContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    activeBorderColor = MaterialTheme.colorScheme.primary,
                    onClick = { onStatusChange(AttendanceStatus.PRESENT) },
                    modifier = Modifier.weight(1f)
                )
                AttendanceButton(
                    label = "F",
                    subLabel = "FALTA",
                    isSelected = student.status == AttendanceStatus.ABSENT,
                    activeColor = MaterialTheme.colorScheme.error,
                    activeContentColor = MaterialTheme.colorScheme.onError,
                    activeBorderColor = MaterialTheme.colorScheme.onErrorContainer,
                    onClick = { onStatusChange(AttendanceStatus.ABSENT) },
                    modifier = Modifier.weight(1f)
                )
                AttendanceButton(
                    label = "J",
                    subLabel = "JUST",
                    isSelected = student.status == AttendanceStatus.JUSTIFIED,
                    activeColor = MaterialTheme.colorScheme.tertiaryContainer,
                    activeContentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    activeBorderColor = MaterialTheme.colorScheme.tertiary,
                    onClick = { onStatusChange(AttendanceStatus.JUSTIFIED) },
                    modifier = Modifier.weight(1f)
                )
                AttendanceButton(
                    label = "A",
                    subLabel = "ATEN",
                    isSelected = student.status == AttendanceStatus.LATE,
                    activeColor = MaterialTheme.colorScheme.primary,
                    activeContentColor = MaterialTheme.colorScheme.onPrimary,
                    activeBorderColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    onClick = { onStatusChange(AttendanceStatus.LATE) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun AttendanceButton(
    label: String,
    subLabel: String,
    isSelected: Boolean,
    activeColor: Color,
    activeContentColor: Color,
    activeBorderColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = if (isSelected) activeColor else MaterialTheme.colorScheme.surface
    val contentColor = if (isSelected) activeContentColor else MaterialTheme.colorScheme.onSurface
    val borderColor = if (isSelected) activeBorderColor else MaterialTheme.colorScheme.outlineVariant

    Surface(
        modifier = modifier
            .height(56.dp)
            .clickable(onClick = onClick),
        color = containerColor,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(label, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = contentColor)
            Text(subLabel, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = contentColor.copy(alpha = 0.7f))
        }
    }
}
