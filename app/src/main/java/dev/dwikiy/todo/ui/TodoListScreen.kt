// file: ui/TodoListScreen.kt
package dev.dwikiy.todo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.dwikiy.todo.ui.TodoItemCard
import dev.dwikiy.todo.ui.TodoViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListScreen(viewModel: TodoViewModel = viewModel()) {
    val todos by viewModel.todos.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Todo List") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Kegiatan")
            }
        }
    ) { padding ->
        if (todos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Belum ada kegiatan.\nTekan + untuk menambahkan.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(todos) { todo ->
                    TodoItemCard(
                        todo = todo,
                        onToggleDone = { viewModel.toggleDone(todo.id) },
                        onDelete = { viewModel.deleteTodo(todo.id) }
                    )
                }
            }
        }

        if (showDialog) {
            AddTodoDialogWithPicker(
                onDismiss = { showDialog = false },
                onConfirm = { title, date ->
                    viewModel.addTodo(title, date)
                    showDialog = false
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTodoDialogWithPicker(
    onDismiss: () -> Unit,
    onConfirm: (String, Date) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(Date()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    // Format tanggal sesuai requirement: dd:MM:yyyy – HH:mm
    val dateFormatter = remember {
        SimpleDateFormat("dd:MM:yyyy", Locale.getDefault())
    }
    val timeFormatter = remember {
        SimpleDateFormat("HH:mm", Locale.getDefault())
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tambah Kegiatan Baru") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Input nama kegiatan
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Nama Kegiatan") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Pilih tanggal
                OutlinedButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Tanggal: ${dateFormatter.format(selectedDate)}")
                }

                // Pilih waktu
                OutlinedButton(
                    onClick = { showTimePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Waktu: ${timeFormatter.format(selectedDate)}")
                }

                // Preview format lengkap
                Text(
                    text = "Format: ${dateFormatter.format(selectedDate)} – ${timeFormatter.format(selectedDate)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(title, selectedDate) },
                enabled = title.isNotBlank()
            ) {
                Text("Tambah")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )

    // Date Picker Dialog
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate.time
        )
        
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val calendar = Calendar.getInstance()
                            calendar.timeInMillis = millis
                            
                            // Preserve time, update date
                            val currentCalendar = Calendar.getInstance()
                            currentCalendar.time = selectedDate
                            
                            calendar.set(Calendar.HOUR_OF_DAY, currentCalendar.get(Calendar.HOUR_OF_DAY))
                            calendar.set(Calendar.MINUTE, currentCalendar.get(Calendar.MINUTE))
                            
                            selectedDate = calendar.time
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Batal")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // Time Picker Dialog
    if (showTimePicker) {
        val calendar = Calendar.getInstance()
        calendar.time = selectedDate
        
        val timePickerState = rememberTimePickerState(
            initialHour = calendar.get(Calendar.HOUR_OF_DAY),
            initialMinute = calendar.get(Calendar.MINUTE),
            is24Hour = true
        )
        
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("Pilih Waktu") },
            text = {
                TimePicker(state = timePickerState)
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val newCalendar = Calendar.getInstance()
                        newCalendar.time = selectedDate
                        newCalendar.set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                        newCalendar.set(Calendar.MINUTE, timePickerState.minute)
                        selectedDate = newCalendar.time
                        showTimePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Batal")
                }
            }
        )
    }
}