package com.example.notesapp.feature_note.presentation

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.notesapp.core.util.TestTags.CONTENT_TEXT_FIELD
import com.example.notesapp.core.util.TestTags.NOTE_ITEM
import com.example.notesapp.core.util.TestTags.TITLE_TEXT_FIELD
import com.example.notesapp.di.AppModule
import com.example.notesapp.feature_note.presentation.add_edit_note.AddEditNoteScreen
import com.example.notesapp.feature_note.presentation.notes.NotesScreen
import com.example.notesapp.feature_note.presentation.util.Screen
import com.example.notesapp.ui.theme.NotesAppTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
@UninstallModules(AppModule::class)
class NotesEndToEndTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        hiltRule.inject()
        composeRule.setContent {
            NotesAppTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = Screen.NotesScreen.route
                ) {
                    composable(route = Screen.NotesScreen.route) {
                        NotesScreen(navController = navController)
                    }
                    composable(
                        route = Screen.AddEditNotesScreen.route + "?noteId={noteId}&noteColor={noteColor}",
                        arguments = listOf(
                            navArgument(
                                name = "noteId"
                            ) {
                                type = NavType.IntType
                                defaultValue = -1
                            },
                            navArgument(
                                name = "noteColor"
                            ) {
                                type = NavType.IntType
                                defaultValue = -1
                            }
                        )
                    ) {
                        val color = it.arguments?.getInt("noteColor") ?: -1
                        AddEditNoteScreen(
                            navController = navController,
                            noteColor = color
                        )
                    }
                }
            }
        }
    }

    @Test
    fun saveNewNote_editAfterwards() {
        composeRule.onNodeWithContentDescription("Add").performClick()

        composeRule.onNodeWithTag(TITLE_TEXT_FIELD).performTextInput("test-title")
        composeRule.onNodeWithTag(CONTENT_TEXT_FIELD).performTextInput("test-content")

        composeRule.onNodeWithContentDescription("Save").performClick()

        composeRule.onNodeWithText("test-title").assertIsDisplayed()
        composeRule.onNodeWithText("test-title").performClick()

        composeRule.onNodeWithTag(TITLE_TEXT_FIELD).assertTextEquals("test-title")
        composeRule.onNodeWithTag(CONTENT_TEXT_FIELD).assertTextEquals("test-content")

        composeRule.onNodeWithTag(TITLE_TEXT_FIELD).performTextInput("2")

        composeRule.onNodeWithContentDescription("Save").performClick()

        composeRule.onNodeWithText("test-title2").assertIsDisplayed()
    }

    @Test
    fun saveNewNotes_orderByTitleDescending() {
        for(i in 1..3) {
            composeRule.onNodeWithContentDescription("Add").performClick()

            composeRule.onNodeWithTag(TITLE_TEXT_FIELD).performTextInput(i.toString())
            composeRule.onNodeWithTag(CONTENT_TEXT_FIELD).performTextInput(i.toString())

            composeRule.onNodeWithContentDescription("Save").performClick()
        }

        composeRule.onNodeWithText("1").assertIsDisplayed()
        composeRule.onNodeWithText("2").assertIsDisplayed()
        composeRule.onNodeWithText("3").assertIsDisplayed()

        composeRule.onNodeWithContentDescription("Sort").performClick()
        composeRule.onNodeWithContentDescription("Title").performClick()
        composeRule.onNodeWithContentDescription("Descending").performClick()

        composeRule.onAllNodesWithTag(NOTE_ITEM)[0]
            .assertTextContains("3")
        composeRule.onAllNodesWithTag(NOTE_ITEM)[1]
            .assertTextContains("2")
        composeRule.onAllNodesWithTag(NOTE_ITEM)[2]
            .assertTextContains("1")
    }
}