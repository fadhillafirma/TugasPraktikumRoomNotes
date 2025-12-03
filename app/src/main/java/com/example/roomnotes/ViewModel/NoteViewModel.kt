package com.example.roomnotes.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roomnotes.data.Note
import com.example.roomnotes.data.NoteRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NoteViewModel(private val repo: NoteRepository) : ViewModel() {
    val notes: StateFlow<List<Note>> = repo.getAllNotes().stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        emptyList()
    )

    fun insert(title: String, description: String) = viewModelScope.launch {
        if (title.isBlank() && description.isBlank()) return@launch
        repo.insert(Note(title = title.trim(), description = description.trim()))
    }

    fun update(id: Long, title: String, description: String) = viewModelScope.launch {
        if (id <= 0L) return@launch
        repo.update(Note(id = id, title = title.trim(), description = description.trim()))
    }

    fun delete(note: Note) = viewModelScope.launch {
        repo.delete(note)
    }
}