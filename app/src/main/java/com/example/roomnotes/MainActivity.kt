package com.example.roomnotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.roomnotes.ViewModel.NoteViewModel
import com.example.roomnotes.ViewModel.NoteViewModelFactory
import com.example.roomnotes.data.AppDatabase
import com.example.roomnotes.data.Note
import com.example.roomnotes.data.NoteRepository
import com.example.roomnotes.ui.theme.RoomNotesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inisialisasi Database, Repo, dan Factory
        val db = AppDatabase.getDatabase(applicationContext)
        val repo = NoteRepository(db.noteDao())
        val factory = NoteViewModelFactory(repo)

        setContent {
            RoomNotesTheme {
                val vm: NoteViewModel = viewModel(factory = factory)
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NoteScreen(vm)
                }
            }
        }
    }
}

// --- BAGIAN 1: NoteScreen (Dipindah ke sini sesuai PDF) ---
@Composable
fun NoteScreen(vm: NoteViewModel) {
    val notes by vm.notes.collectAsState()
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var editingId by remember { mutableStateOf<Long?>(null) }

    Column(modifier = Modifier.padding(16.dp)) {
        // Judul Form
        Text(
            text = if (editingId == null) "Tambah Note" else "Edit Note",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Input Title
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Input Description
        OutlinedTextField(
            value = desc,
            onValueChange = { desc = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Tombol Save/Update & Cancel
        Row {
            Button(onClick = {
                if (editingId == null) {
                    vm.insert(title, desc)
                } else {
                    vm.update(editingId ?: 0L, title, desc)
                    editingId = null
                }
                title = ""
                desc = ""
            }) {
                Text(if (editingId == null) "Save" else "Update")
            }

            Spacer(modifier = Modifier.width(8.dp))

            if (editingId != null) {
                OutlinedButton(onClick = {
                    editingId = null
                    title = ""
                    desc = ""
                }) {
                    Text("Cancel")
                }
            }
        }

        // Garis Pemisah (Divider)
        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

        // Judul List
        Text("List Notes", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))

        // List Data
        if (notes.isEmpty()) {
            Text("Belum ada data")
        } else {
            LazyColumn {
                items(notes) { note ->
                    NoteItem(
                        note = note,
                        onClick = {
                            // Masuk mode edit
                            editingId = note.id
                            title = note.title
                            desc = note.description
                        },
                        onDelete = { vm.delete(note) }
                    )
                }
            }
        }
    }
}

// --- BAGIAN 2: NoteItem (Dipindah ke sini sesuai PDF) ---
@Composable
fun NoteItem(note: Note, onClick: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = note.title, style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = note.description, style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}