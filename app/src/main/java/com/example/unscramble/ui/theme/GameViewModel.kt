package com.example.unscramble.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.unscramble.data.MAX_NO_OF_WORDS
import com.example.unscramble.data.SCORE_INCREASE
import com.example.unscramble.data.allWords
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class GameViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    var usedWords: MutableSet<String> = mutableSetOf()
    lateinit var currentWord: String
    var userGuess by mutableStateOf("")


    init {
        resetGame()
    }
    fun resetGame() {
        usedWords.clear()
        _uiState.value = GameUiState(currentScrambledWord = pickRandomWord())
    }

    fun checkUserGuess() {
        if(currentWord.equals(userGuess,ignoreCase = true)) {
            var updatedScore = _uiState.value.score.plus(SCORE_INCREASE)
            updateUiState(updatedScore)
        }
        else {
            _uiState.update { currentState ->
                currentState.copy(
                    isGuessedWordWrong = true
                )
            }
        }
        updatedUserGuess("")
    }

    fun updatedUserGuess(guessedWord: String) {
        userGuess = guessedWord
    }

    fun skipword() {
        updateUiState(_uiState.value.score)
    }

    private fun updateUiState(updatedScore: Int) {
        if(_uiState.value.currentWordCount == MAX_NO_OF_WORDS) {
            _uiState.update { currentState->
                currentState.copy(
                    isGameOver = true,
                    isGuessedWordWrong = false,
                    score = updatedScore
                )
            }
        }
        else {
            _uiState.update { currentState->
                currentState.copy(
                    isGuessedWordWrong = false,
                    currentWordCount = _uiState.value.currentWordCount.inc(),
                    score = updatedScore,
                    currentScrambledWord = pickRandomWord()
                )
            }
        }
    }

    fun pickRandomWord(): String {
        currentWord = allWords.random()
        if(usedWords.contains(currentWord))
            return pickRandomWord()
        usedWords.add(currentWord)
        return shuffleWord(currentWord)
    }

    fun shuffleWord(word: String): String {
        var temp = word.toCharArray()
        while(String(temp) == word)
            temp.shuffle()
        return String(temp)
    }
}