package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class AppScreen {
    DASHBOARD,
    NOTES,
    FLASHCARDS,
    QUIZ_ENGINE,
    AI_TUTOR,
    ANALYTICS,
    SETTINGS,
    SUPPORT
}

class StudyViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getInstance(application, viewModelScope)
    private val repository = StudyRepository(database.studyDao())

    // UI state flows
    val allNotes: StateFlow<List<NoteEntity>> = repository.allNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val chatMessages: StateFlow<List<ChatMessageEntity>> = repository.allChatMessages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Navigation and screen management
    private val _currentScreen = MutableStateFlow(AppScreen.DASHBOARD)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    // Selected Note Management (for Editor)
    private val _selectedNoteId = MutableStateFlow(1)
    val selectedNoteId: StateFlow<Int> = _selectedNoteId.asStateFlow()

    val selectedNote: StateFlow<NoteEntity?> = combine(allNotes, _selectedNoteId) { notes, selectedId ->
        notes.find { it.id == selectedId }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Note Editor Input States
    private val _editTitle = MutableStateFlow("")
    val editTitle: StateFlow<String> = _editTitle.asStateFlow()

    private val _editContent = MutableStateFlow("")
    val editContent: StateFlow<String> = _editContent.asStateFlow()

    // AI States
    private val _isGeneratingAI = MutableStateFlow(false)
    val isGeneratingAI: StateFlow<Boolean> = _isGeneratingAI.asStateFlow()

    private val _aiResultText = MutableStateFlow<String?>(null)
    val aiResultText: StateFlow<String?> = _aiResultText.asStateFlow()

    // AI Tutor States
    private val _tutorInput = MutableStateFlow("")
    val tutorInput: StateFlow<String> = _tutorInput.asStateFlow()

    private val _isTutorTyping = MutableStateFlow(false)
    val isTutorTyping: StateFlow<Boolean> = _isTutorTyping.asStateFlow()

    // Context Control Switches
    private val _notesMemoryEnabled = MutableStateFlow(true)
    val notesMemoryEnabled: StateFlow<Boolean> = _notesMemoryEnabled.asStateFlow()

    private val _quizHistoryEnabled = MutableStateFlow(false)
    val quizHistoryEnabled: StateFlow<Boolean> = _quizHistoryEnabled.asStateFlow()

    // Streak tracker
    private val _activeStreak = MutableStateFlow(14)
    val activeStreak: StateFlow<Int> = _activeStreak.asStateFlow()

    // Intelligence Hub generated custom plans
    private val _customPlanText = MutableStateFlow<String?>(null)
    val customPlanText: StateFlow<String?> = _customPlanText.asStateFlow()

    // Quiz parameters
    private val _selectedAnswers = MutableStateFlow<Map<Int, Int>>(emptyMap()) // Question Index to Selection Index
    val selectedAnswers: StateFlow<Map<Int, Int>> = _selectedAnswers.asStateFlow()

    private val _quizChecked = MutableStateFlow(false)
    val quizChecked: StateFlow<Boolean> = _quizChecked.asStateFlow()

    // Search queries
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        // Automatically sync inputs when selected note changes
        viewModelScope.launch {
            selectedNote.collect { note ->
                if (note != null) {
                    _editTitle.value = note.title
                    _editContent.value = note.content
                }
            }
        }
    }

    fun setScreen(screen: AppScreen) {
        _currentScreen.value = screen
        _aiResultText.value = null
    }

    fun selectNote(id: Int) {
        _selectedNoteId.value = id
        _aiResultText.value = null
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateEditTitle(title: String) {
        _editTitle.value = title
    }

    fun updateEditContent(content: String) {
        _editContent.value = content
    }

    fun saveCurrentNoteEdits() {
        val current = selectedNote.value ?: return
        viewModelScope.launch {
            repository.updateNote(
                current.copy(
                    title = _editTitle.value,
                    content = _editContent.value,
                    summary = if (_editContent.value.length > 80) _editContent.value.substring(0, 80) + "..." else _editContent.value
                )
            )
        }
    }

    // AI Notes Assistant functions (Summarize, Gen Quiz, Explain)
    fun triggerNoteSummary() {
        val currentText = _editContent.value
        val noteTitle = _editTitle.value
        if (currentText.isBlank()) return
        
        viewModelScope.launch {
            _isGeneratingAI.value = true
            _aiResultText.value = null
            
            val systemPrompt = "You are an expert graphic arts study assistant. Please provide an elegant, bulleted executive summary of this note in monospaced or monospac-friendly markdown. Keep it punchy."
            val response = GeminiService.generateContent(
                prompt = "Please summarize the following note titled '$noteTitle':\n\n$currentText",
                systemInstruction = systemPrompt
            )
            
            _aiResultText.value = response
            _isGeneratingAI.value = false
        }
    }

    fun triggerNoteExplanation() {
        val currentText = _editContent.value
        val noteTitle = _editTitle.value
        if (currentText.isBlank()) return

        viewModelScope.launch {
            _isGeneratingAI.value = true
            _aiResultText.value = null

            val systemPrompt = "You are a master graphic designer and educator. Explain the scientific design concepts within the note in simple, highly illuminating terms."
            val response = GeminiService.generateContent(
                prompt = "Please explain the deep concepts within this note titled '$noteTitle':\n\n$currentText",
                systemInstruction = systemPrompt
            )

            _aiResultText.value = response
            _isGeneratingAI.value = false
        }
    }

    fun triggerNoteQuiz() {
        val currentText = _editContent.value
        val noteTitle = _editTitle.value
        if (currentText.isBlank()) return

        viewModelScope.launch {
            _isGeneratingAI.value = true
            _aiResultText.value = null

            val systemPrompt = "You are an examiner testing graphic packaging principles. Please construct 3 high-fidelity multiple-choice questions from this text."
            val response = GeminiService.generateContent(
                prompt = "Please write a 3-question multiple choice test on this note titled '$noteTitle':\n\n$currentText",
                systemInstruction = systemPrompt
            )

            _aiResultText.value = response
            _isGeneratingAI.value = false
        }
    }

    fun clearAIResult() {
        _aiResultText.value = null
    }

    // AI Tutor Chat Handling
    fun updateTutorInput(input: String) {
        _tutorInput.value = input
    }

    fun sendTutorMessage() {
        val msgText = _tutorInput.value.trim()
        if (msgText.isBlank()) return

        _tutorInput.value = ""
        viewModelScope.launch {
            // Post user message
            repository.insertChatMessage(ChatMessageEntity(sender = "user", message = msgText))
            
            _isTutorTyping.value = true

            // Gather context notes if enabled
            val contextNotesText = if (_notesMemoryEnabled.value) {
                allNotes.value.joinToString("\n\n") { "Note: ${it.title}\n${it.content}" }
            } else ""

            val systemPrompt = "You are the 'GOD MODE' AI Tutor. You help graphic design students achieve cognitive mastery. " +
                    "Explain concepts with absolute technical precision. Combine insights from their study notes. Speak directly, " +
                    "concisely, using markdown format. Context notes available: \n$contextNotesText"

            val response = GeminiService.generateContent(prompt = msgText, systemInstruction = systemPrompt)
            
            // Post AI response
            repository.insertChatMessage(ChatMessageEntity(sender = "ai", message = response))
            _isTutorTyping.value = false
        }
    }

    fun clearChat() {
        viewModelScope.launch {
            repository.clearChatHistory()
        }
    }

    // Sidebar Switched Toggles
    fun toggleNotesMemory() {
        _notesMemoryEnabled.value = !_notesMemoryEnabled.value
    }

    fun toggleQuizHistory() {
        _quizHistoryEnabled.value = !_quizHistoryEnabled.value
    }

    // Intelligence Hub plan generation
    fun generateIntelligencePlan() {
        viewModelScope.launch {
            _isGeneratingAI.value = true
            _customPlanText.value = null
            
            val systemPrompt = "You are the core God Mode cognitive processor. Generate a highly custom study routine schedule based on the student's mastery logs. Be concise and authoritative."
            val notesList = allNotes.value.joinToString(", ") { "${it.title} (${it.masteryLevel}% mastery)" }
            val response = GeminiService.generateContent(
                prompt = "Create a custom rapid learning path for notes: $notesList. Focus on weak areas like typography constraints.",
                systemInstruction = systemPrompt
            )
            
            _customPlanText.value = response
            _isGeneratingAI.value = false
        }
    }

    fun createNewSession() {
        // Re-set or customize status parameters to show active state
        _activeStreak.value = _activeStreak.value + 1
        setScreen(AppScreen.AI_TUTOR)
        _tutorInput.value = "Let's start a brand new study session on Layout Hierarchy."
    }

    // Quiz action handlers
    fun selectQuizAnswer(questionIndex: Int, answerIndex: Int) {
        val current = _selectedAnswers.value.toMutableMap()
        current[questionIndex] = answerIndex
        _selectedAnswers.value = current
    }

    fun checkQuiz() {
        _quizChecked.value = true
        _activeStreak.value = _activeStreak.value + 1
    }

    fun resetQuiz() {
        _selectedAnswers.value = emptyMap()
        _quizChecked.value = false
    }

    fun triggerNewNote(title: String, category: String, content: String) {
        viewModelScope.launch {
            repository.insertNote(
                NoteEntity(
                    title = title,
                    summary = if (content.length > 50) content.take(50) + "..." else content,
                    content = content,
                    category = category,
                    imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDBp_IX8xO2iDezGSI5B0_24Whjn0aNaDQHe_JSiZENgSq7OvqYnaAjGmypfbjeVY7-TzuRoY8lnA7S1got7mF6u2_iB-x0CS9kBW7sPNBxQVVPTg9wqbAqmuJIZpSoyt33flno58nD4r5WFpn_QlpN68qL7V9sRLvtw_z1aJ-zTKNmtSpeNC3N0fA1o9qMKHeXK2mSk9LmGau7wOcDj3SJI48DdiumKd_vZedHQjYhol4DGBVnhhm9f7BzA6jIlg5Kb7Z_jiVef0Y",
                    masteryLevel = 33,
                    examRelevance = 8.0,
                    tags = "graphic-design,new-concept,study",
                    timestampText = "Just now",
                    isMastered = false,
                    editHistory = "Initial Draft Created|Just now"
                )
            )
            // Go to notes screen and focus
            _currentScreen.value = AppScreen.NOTES
        }
    }
}
