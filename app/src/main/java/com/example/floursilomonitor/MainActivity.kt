package com.example.floursilomonitor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*

data class Silo(
    val name: String,
    val level: Float,
    val tonnes: Float,
    val capacity: Float,
    val temperature: Float,
    val min24: Float,
    val max24: Float
)

val silo11 = Silo("Silo 11", 72f, 7.2f, 10f, 22.4f, 68f, 78f)
val silo12 = Silo("Silo 12", 45f, 4.5f, 10f, 21.8f, 40f, 49f)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { FlourSiloApp() }
    }
}

@Composable
fun FlourSiloApp() {
    var page by remember { mutableStateOf("home") }
    var selected by remember { mutableStateOf(silo11) }

    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFF0878C9),
            secondary = Color(0xFF18A96B),
            background = Color(0xFFF4F7FA)
        )
    ) {
        when (page) {
            "home" -> Home(
                open = { selected = it; page = "detail" },
                navigate = { page = it }
            )
            "detail" -> Detail(selected) { page = "home" }
            "trends" -> Trends { page = "home" }
            "usage" -> Usage { page = "home" }
            "alarms" -> Alarms { page = "home" }
            "history" -> History { page = "home" }
            "overview" -> Overview { page = "home" }
            "settings" -> Settings { page = "home" }
        }
    }
}

@Composable
fun TopBar(title: String, subtitle: String = "", back: (() -> Unit)? = null) {
    Surface(color = Color(0xFF0878C9)) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (back != null) {
                IconButton(onClick = back) {
                    Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                }
            } else {
                Icon(Icons.Default.Storage, null, tint = Color.White, modifier = Modifier.size(30.dp))
            }
            Column(Modifier.weight(1f).padding(start = 7.dp)) {
                Text(title, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                if (subtitle.isNotBlank()) Text(subtitle, color = Color.White.copy(.85f), fontSize = 11.sp)
            }
            Icon(Icons.Default.Settings, null, tint = Color.White)
        }
    }
}

@Composable
fun BottomNav(current: String, nav: (String) -> Unit) {
    NavigationBar {
        listOf(
            "home" to ("Home" to Icons.Default.Home),
            "trends" to ("Trends" to Icons.Default.ShowChart),
            "history" to ("History" to Icons.Default.History),
            "alarms" to ("Alarms" to Icons.Default.Notifications),
            "settings" to ("Settings" to Icons.Default.Settings)
        ).forEach { (key, pair) ->
            NavigationBarItem(
                selected = current == key,
                onClick = { nav(key) },
                icon = { Icon(pair.second, null) },
                label = { Text(pair.first) }
            )
        }
    }
}

@Composable
fun Home(open: (Silo) -> Unit, nav: (String) -> Unit) {
    val total = silo11.tonnes + silo12.tonnes
    Scaffold(bottomBar = { BottomNav("home", nav) }) { p ->
        Column(
            Modifier.fillMaxSize().background(Color(0xFFF4F7FA)).padding(p)
        ) {
            TopBar("Flour Silo Monitor", "Monitor and track flour storage")
            Row(
                Modifier.fillMaxWidth().padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Metric("Total Stored", "${"%.1f".format(total)} t", Modifier.weight(1f))
                Metric("Capacity", "20.0 t", Modifier.weight(1f))
                Metric("Average", "58%", Modifier.weight(1f))
            }
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text("Silos", fontSize = 21.sp, fontWeight = FontWeight.Bold)
                    Text("Tap a silo for detailed information", fontSize = 11.sp, color = Color.Gray)
                }
                AssistChip(
                    onClick = {},
                    label = { Text("MONITORING") },
                    leadingIcon = { Icon(Icons.Default.Wifi, null) }
                )
            }
            LazyColumn(
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(listOf(silo11, silo12)) { silo ->
                    SiloCard(silo) { open(silo) }
                }
                item {
                    Card(shape = RoundedCornerShape(16.dp)) {
                        Column(Modifier.padding(14.dp)) {
                            Text("Quick Access", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                            Spacer(Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                QuickButton("Trends", Icons.Default.ShowChart) { nav("trends") }
                                QuickButton("Usage", Icons.Default.PieChart) { nav("usage") }
                                QuickButton("Alarms", Icons.Default.Warning) { nav("alarms") }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Metric(label: String, value: String, modifier: Modifier) {
    Card(modifier.height(78.dp), shape = RoundedCornerShape(14.dp)) {
        Column(Modifier.padding(10.dp)) {
            Text(label, fontSize = 10.sp, color = Color.Gray)
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun QuickButton(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector, action: () -> Unit) {
    OutlinedButton(onClick = action, modifier = Modifier.weight(1f)) {
        Icon(icon, null, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(4.dp))
        Text(text, fontSize = 11.sp)
    }
}

@Composable
fun SiloCard(s: Silo, open: () -> Unit) {
    Card(
        Modifier.fillMaxWidth().clickable { open() },
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            SiloDrawing(s.level, Modifier.width(120.dp).height(190.dp))
            Column(Modifier.weight(1f).padding(start = 10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(s.name, fontSize = 19.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    StatusPill(if (s.level < 30) "LOW" else "NORMAL", s.level < 30)
                }
                Spacer(Modifier.height(6.dp))
                Text("Level", fontSize = 11.sp, color = Color.Gray)
                Text("${s.level.toInt()}%", fontSize = 27.sp, fontWeight = FontWeight.Bold)
                LinearProgressIndicator(
                    progress = { s.level / 100f },
                    Modifier.fillMaxWidth().height(9.dp)
                )
                Spacer(Modifier.height(7.dp))
                Text("Stored quantity  ${s.tonnes} t", fontSize = 12.sp)
                Text("Capacity  ${s.capacity} t", fontSize = 12.sp)
                Text("Temperature  ${s.temperature} °C", fontSize = 12.sp)
                Text(
                    "Last update  ${SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())}",
                    fontSize = 10.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun StatusPill(text: String, warning: Boolean) {
    AssistChip(
        onClick = {},
        label = { Text(text, fontSize = 10.sp) },
        leadingIcon = {
            Icon(
                if (warning) Icons.Default.Warning else Icons.Default.CheckCircle,
                null,
                modifier = Modifier.size(16.dp)
            )
        }
    )
}

@Composable
fun SiloDrawing(level: Float, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val cx = w / 2
        val top = 12f
        val bodyTop = h * .20f
        val bodyBottom = h * .67f
        val coneBottom = h * .86f
        val bodyW = w * .64f
        val left = cx - bodyW / 2
        val right = cx + bodyW / 2

        drawRoundRect(
            color = Color(0xFFD9E0E6),
            topLeft = Offset(left, bodyTop),
            size = Size(bodyW, bodyBottom - bodyTop),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(14f,14f),
            style = Stroke(3f)
        )

        val fillTop = bodyBottom - (bodyBottom - bodyTop) * (level / 100f)
        drawRect(
            color = Color(0xFFE8D39A),
            topLeft = Offset(left + 3, fillTop),
            size = Size(bodyW - 6, bodyBottom - fillTop - 2)
        )

        val cone = Path().apply {
            moveTo(left, bodyBottom)
            lineTo(cx, coneBottom)
            lineTo(right, bodyBottom)
            close()
        }
        drawPath(cone, color = Color(0xFFD0D8DE), style = Stroke(3f))

        drawLine(Color(0xFF6B7B86), Offset(left - 6, top + 20), Offset(left, bodyBottom), 3f)
        drawLine(Color(0xFF6B7B86), Offset(right + 6, top + 20), Offset(right, bodyBottom), 3f)
        drawLine(Color(0xFF6B7B86), Offset(left - 6, top + 20), Offset(right + 6, top + 20), 3f)
        drawLine(Color(0xFF6B7B86), Offset(cx, coneBottom), Offset(cx, h - 12), 4f)
        drawLine(Color(0xFF6B7B86), Offset(cx - 16, h - 12), Offset(cx + 16, h - 12), 4f)

        drawRoundRect(
            color = Color(0xFF9DAAB3),
            topLeft = Offset(cx - 14, top),
            size = Size(28f, 13f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f,4f)
        )
    }
}

@Composable
fun Detail(s: Silo, back: () -> Unit) {
    var refreshed by remember { mutableStateOf(false) }
    Scaffold(bottomBar = { BottomNav("home", backNav = { back() }) }) { p ->
        Column(Modifier.fillMaxSize().background(Color(0xFFF4F7FA)).padding(p)) {
            TopBar("${s.name}", "Cone-bottom silo details", back)
            LazyColumn(
                contentPadding = PaddingValues(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Card(shape = RoundedCornerShape(16.dp)) {
                        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            SiloDrawing(s.level, Modifier.width(150.dp).height(220.dp))
                            Column(Modifier.padding(start = 12.dp)) {
                                Text("Current Level", color = Color.Gray, fontSize = 12.sp)
                                Text("${s.level.toInt()}%", fontSize = 36.sp, fontWeight = FontWeight.Bold)
                                Text("${s.tonnes} t / ${s.capacity} t", fontWeight = FontWeight.Bold)
                                Spacer(Modifier.height(8.dp))
                                Text("Temperature ${s.temperature} °C")
                                Text("Min (24h) ${s.min24.toInt()}%")
                                Text("Max (24h) ${s.max24.toInt()}%")
                            }
                        }
                    }
                }
                item {
                    Card(shape = RoundedCornerShape(16.dp)) {
                        Column(Modifier.padding(14.dp)) {
                            Text("Monitoring Parameters", fontSize = 17.sp, fontWeight = FontWeight.Bold)
                            Parameter("Level", "${s.level.toInt()}%", "Level transmitter")
                            Parameter("Temperature", "${s.temperature} °C", "Silo temperature sensor")
                            Parameter("Quantity", "${s.tonnes} t", "Calculated/estimated stored mass")
                            Parameter("Status", if (s.level < 30) "LOW LEVEL" else "NORMAL", "Alarm state")
                        }
                    }
                }
                item {
                    Button(
                        onClick = { refreshed = true },
                        Modifier.fillMaxWidth().height(52.dp)
                    ) {
                        Icon(Icons.Default.Refresh, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Refresh Reading")
                    }
                }
                if (refreshed) item {
                    Text(
                        "Reading refreshed at " +
                                SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date()),
                        color = Color(0xFF168A58),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun BottomNav(current: String, backNav: (() -> Unit)? = null) {
    NavigationBar {
        NavigationBarItem(current == "home", { backNav?.invoke() }, icon = { Icon(Icons.Default.Home, null) }, label = { Text("Home") })
        NavigationBarItem(false, {}, icon = { Icon(Icons.Default.ShowChart, null) }, label = { Text("Trends") })
        NavigationBarItem(false, {}, icon = { Icon(Icons.Default.History, null) }, label = { Text("History") })
        NavigationBarItem(false, {}, icon = { Icon(Icons.Default.Notifications, null) }, label = { Text("Alarms") })
        NavigationBarItem(false, {}, icon = { Icon(Icons.Default.Settings, null) }, label = { Text("Settings") })
    }
}

@Composable
fun Parameter(name: String, value: String, source: String) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(name, fontWeight = FontWeight.SemiBold)
            Text(source, fontSize = 10.sp, color = Color.Gray)
        }
        Text(value, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun Page(title: String, subtitle: String, back: () -> Unit, content: @Composable ColumnScope.() -> Unit) {
    Scaffold { p ->
        Column(Modifier.fillMaxSize().background(Color(0xFFF4F7FA)).padding(p)) {
            TopBar(title, subtitle, back)
            LazyColumn(
                contentPadding = PaddingValues(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item { contentColumn(content) }
            }
        }
    }
}

@Composable
private fun contentColumn(content: @Composable ColumnScope.() -> Unit) {
    Column(content = content)
}

@Composable
fun Trends(back: () -> Unit) {
    Page("Level Trends", "Silo level history", back) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterButton("Silo 11", true)
            FilterButton("Silo 12", false)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterButton("24 Hours", true)
            FilterButton("7 Days", false)
            FilterButton("30 Days", false)
        }
        ChartCard("Silo 11 Level Trend", listOf(76f,75f,74f,75f,73f,72f,71f,72f,70f,72f,73f,72f))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Metric("Current", "72%", Modifier.weight(1f))
            Metric("Min 24h", "68%", Modifier.weight(1f))
            Metric("Max 24h", "78%", Modifier.weight(1f))
        }
        ChartCard("Silo 12 Level Trend", listOf(48f,47f,46f,44f,45f,43f,42f,43f,44f,45f,44f,45f))
    }
}

@Composable
fun FilterButton(text: String, selected: Boolean) {
    Button(
        onClick = {},
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) Color(0xFF0878C9) else Color(0xFFE7EEF4),
            contentColor = if (selected) Color.White else Color(0xFF24445B)
        ),
        modifier = Modifier.height(38.dp)
    ) { Text(text, fontSize = 11.sp) }
}

@Composable
fun ChartCard(title: String, values: List<Float>) {
    Card(shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.padding(14.dp)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(Modifier.height(8.dp))
            Canvas(Modifier.fillMaxWidth().height(180.dp)) {
                val min = 0f
                val max = 100f
                val stepX = size.width / (values.size - 1)
                for (i in 0..5) {
                    val y = size.height * i / 5f
                    drawLine(Color(0xFFDCE4EA), Offset(0f, y), Offset(size.width, y), 1f)
                }
                val path = Path()
                values.forEachIndexed { i, v ->
                    val x = i * stepX
                    val y = size.height - ((v - min) / (max - min) * size.height)
                    if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                drawPath(path, Color(0xFF0878C9), style = Stroke(4f, cap = StrokeCap.Round))
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("00:00", fontSize = 9.sp, color = Color.Gray)
                Text("06:00", fontSize = 9.sp, color = Color.Gray)
                Text("12:00", fontSize = 9.sp, color = Color.Gray)
                Text("18:00", fontSize = 9.sp, color = Color.Gray)
                Text("24:00", fontSize = 9.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun Usage(back: () -> Unit) {
    Page("Storage & Usage", "Quantity, capacity and consumption", back) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterButton("Silo 11", true)
            FilterButton("Silo 12", false)
        }
        Card(shape = RoundedCornerShape(16.dp)) {
            Column(Modifier.padding(14.dp)) {
                Text("Silo 11 Storage", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text("72% occupied", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Text("7.2 t used   •   2.8 t available")
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(progress = { .72f }, Modifier.fillMaxWidth().height(18.dp))
                Spacer(Modifier.height(6.dp))
                Text("Total capacity 10.0 t")
            }
        }
        ChartCard("Daily Flour Consumption (sample)", listOf(1.1f,1.3f,1.2f,1.0f,1.4f,1.2f,1.5f))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Metric("Today's Usage", "1.1 t", Modifier.weight(1f))
            Metric("Avg / Day", "1.2 t", Modifier.weight(1f))
            Metric("Est. Days", "8", Modifier.weight(1f))
        }
    }
}

@Composable
fun Alarms(back: () -> Unit) {
    Page("Alarms", "Current and historical events", back) {
        AlarmCard("Silo 12", "Low Level Warning", "Level below 50%", false)
        AlarmCard("Silo 11", "Level Normal", "Level currently 72%", true)
        AlarmCard("Silo 12", "Temperature Normal", "21.8 °C", true)
        AlarmCard("System", "Monitoring Active", "Two silos configured", true)
    }
}

@Composable
fun AlarmCard(silo: String, title: String, detail: String, ok: Boolean) {
    Card(shape = RoundedCornerShape(14.dp)) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                if (ok) Icons.Default.CheckCircle else Icons.Default.Warning,
                null,
                tint = if (ok) Color(0xFF18A96B) else Color(0xFFE19A12),
                modifier = Modifier.size(30.dp)
            )
            Column(Modifier.padding(start = 10.dp).weight(1f)) {
                Text("$silo — $title", fontWeight = FontWeight.Bold)
                Text(detail, fontSize = 12.sp, color = Color.Gray)
            }
            Text(
                SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
                fontSize = 10.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun History(back: () -> Unit) {
    Page("History", "Recorded readings", back) {
        Card(shape = RoundedCornerShape(16.dp)) {
            Column(Modifier.padding(12.dp)) {
                Text("Recent Readings", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                Spacer(Modifier.height(8.dp))
                listOf(
                    "10:00" to "72% / 45%",
                    "06:00" to "71% / 43%",
                    "02:00" to "70% / 42%",
                    "22:00" to "69% / 41%",
                    "18:00" to "68% / 40%",
                    "14:00" to "67% / 38%"
                ).forEach { (time, values) ->
                    Row(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                        Text(time, Modifier.weight(1f), fontWeight = FontWeight.SemiBold)
                        Text("Silo 11 ${values.substringBefore(" / ")}", Modifier.weight(1f), fontSize = 12.sp)
                        Text("Silo 12 ${values.substringAfter(" / ")}", Modifier.weight(1f), fontSize = 12.sp)
                    }
                    HorizontalDivider()
                }
            }
        }
        OutlinedButton(onClick = {}, Modifier.fillMaxWidth()) {
            Icon(Icons.Default.FileDownload, null)
            Spacer(Modifier.width(8.dp))
            Text("Export History")
        }
    }
}

@Composable
fun Overview(back: () -> Unit) {
    Page("Silos Overview", "Storage system overview", back) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OverviewSilo(silo11, Modifier.weight(1f))
            OverviewSilo(silo12, Modifier.weight(1f))
        }
        Card(shape = RoundedCornerShape(16.dp)) {
            Column(Modifier.padding(14.dp)) {
                Text("Production Flow", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                Text("Silo 11  ─────┐", fontSize = 15.sp)
                Text("              ├──► Flour production", fontSize = 15.sp)
                Text("Silo 12  ─────┘", fontSize = 15.sp)
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Metric("Total Storage", "11.7 t", Modifier.weight(1f))
            Metric("Average Level", "58%", Modifier.weight(1f))
        }
    }
}

@Composable
fun OverviewSilo(s: Silo, modifier: Modifier) {
    Card(modifier, shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(s.name, fontWeight = FontWeight.Bold)
            SiloDrawing(s.level, Modifier.fillMaxWidth().height(190.dp))
            Text("${s.tonnes} / ${s.capacity} t", fontWeight = FontWeight.Bold)
            Text("${s.level.toInt()}%", color = Color(0xFF0878C9))
        }
    }
}

@Composable
fun Settings(back: () -> Unit) {
    Page("Settings", "Silo configuration and monitoring", back) {
        SettingsRow("Silo 11", "Edit silo settings")
        SettingsRow("Silo 12", "Edit silo settings")
        SettingsRow("Units", "Metric (t, °C)")
        SettingsRow("Notifications", "Alarm settings")
        SettingsRow("Data Logging", "Enabled")
        SettingsRow("Communications", "Not connected — demo mode")
        SettingsRow("About", "Flour Silo Monitor v1.0")
    }
}

@Composable
fun SettingsRow(title: String, detail: String) {
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.ChevronRight, null)
            Column(Modifier.padding(start = 8.dp)) {
                Text(title, fontWeight = FontWeight.Bold)
                Text(detail, fontSize = 11.sp, color = Color.Gray)
            }
        }
    }
}
