// This file should be located at: app/src/main/java/dev/dwikiy/todo/MainActivity.kt
package dev.dwikiy.todo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import dev.dwikiy.todo.TodoListScreen
import dev.dwikiy.todo.ui.theme.TodoTheme // Make sure this matches your theme's package

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TodoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // The only job of MainActivity is to call your main screen
                    TodoListScreen()
                }
            }
        }
    }
}