import TipKit.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlin.Exception

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RabbitApp() {
    val rabbitHole = remember { RabbitHole(Config.shared.wsURL) }
    
    var savedVol by remember { mutableStateOf<Double?>(null) }
    var disclaimerShown by remember { mutableStateOf(false) }
    var settingsOpen by remember { mutableStateOf(false) }

    val sharedModelContainer = run {
        val schema = Schema(listOf(
            // Item::class
        ))
        val modelConfiguration = ModelConfiguration(schema, isStoredInMemoryOnly = false)

        try {
            ModelContainer(schema, listOf(modelConfiguration))
        } catch (error: Exception) {
            throw RuntimeException("Could not create ModelContainer: $error")
        }
    }

    LaunchedEffect(Unit) {
        Tips.configure()
    }

    MaterialTheme {
        Box {
            when {
                !rabbitHole.hasCredentials && !rabbitHole.isAuthenticated || !disclaimerShown -> {
                    RegisterView()
                }
                rabbitHole.isAuthenticated -> {
                    ContentView()
                }
                else -> {
                    AuthenticatingView()
                }
            }

            Column {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    IconButton(onClick = { settingsOpen = !settingsOpen }) {
                        Icon(painter = painterResource("gear"), contentDescription = null)
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
            }
        }
        if (settingsOpen) {
            SettingsPane(isOpen = { settingsOpen })
        }

        LaunchedEffect(savedVol) {
            savedVol?.let {
                rabbitHole.rabbitPlayer.audioPlayer?.setVolume(it.toFloat(), 0.2f)
            }
        }
    }
}

