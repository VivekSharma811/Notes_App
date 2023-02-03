package com.example.notesapp.feature_note.domain.use_case

import com.example.notesapp.feature_note.data.repository.FakeNoteRepository
import com.example.notesapp.feature_note.domain.model.InvalidNoteException
import com.example.notesapp.feature_note.domain.model.Note
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import kotlin.random.Random

@OptIn(ExperimentalCoroutinesApi::class)
class AddNoteUseCaseTest {

    private lateinit var addNoteUseCase: AddNoteUseCase
    private lateinit var fakeNoteRepository: FakeNoteRepository

    @Before
    fun setUp() {
        fakeNoteRepository = FakeNoteRepository()
        addNoteUseCase = AddNoteUseCase(fakeNoteRepository)
    }

    @Test
    fun `Add note title blank, validation error`() = runBlockingTest {
        val note = Note(
            title = "",
            content = "khskhgbk",
            timestamp = System.currentTimeMillis(),
            color = Random.nextInt()
        )

        try {
            addNoteUseCase(note)
            assertThat(false).isTrue()
        } catch (e: InvalidNoteException) {
            assertThat(true).isTrue()
        }
    }

    @Test
    fun `Add note content blank, validation error`() = runBlockingTest {
        val note = Note(
            title = "vsdjkfhkfh",
            content = "",
            timestamp = System.currentTimeMillis(),
            color = Random.nextInt()
        )

        try {
            addNoteUseCase(note)
            assertThat(false).isTrue()
        } catch (e: InvalidNoteException) {
            assertThat(true).isTrue()
        }
    }

    @Test
    fun `Add note valid input, success`() = runBlockingTest {
        val note = Note(
            title = "fjhfkjhjh",
            content = "khskhgbk",
            timestamp = System.currentTimeMillis(),
            color = Random.nextInt()
        )

        try {
            addNoteUseCase(note)
            assertThat(true).isTrue()
        } catch (e: InvalidNoteException) {
            assertThat(false).isTrue()
        }
    }
}