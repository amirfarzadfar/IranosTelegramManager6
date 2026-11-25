package ir.iranos.manager

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class Customer(val name: String, val username: String, val optedIn: Boolean)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { IranosApp() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IranosApp() {
    val context = LocalContext.current
    var tab by remember { mutableStateOf(0) }
    var campaignTitle by remember { mutableStateOf("معرفی محصول جدید ایرانوس") }
    var campaignText by remember { mutableStateOf("برای اطلاع از موجودی و قیمت محصولات روشنایی ایرانوس پیام بدهید.") }
    var customerName by remember { mutableStateOf("") }
    var customerUser by remember { mutableStateOf("") }
    var accepted by remember { mutableStateOf(false) }
    val customers = remember { mutableStateListOf(
        Customer("نمونه مشتری", "@sample_customer", true)
    ) }

    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = androidx.compose.ui.graphics.Color(0xFFB3261E),
            secondary = androidx.compose.ui.graphics.Color(0xFF263238),
            background = androidx.compose.ui.graphics.Color(0xFFF7F7F7)
        )
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text("ایرانوس", fontWeight = FontWeight.Bold)
                            Text("مدیریت پیام‌های تلگرام", fontSize = 12.sp)
                        }
                    },
                    actions = { Icon(Icons.Default.Lightbulb, contentDescription = null, modifier = Modifier.padding(end = 16.dp)) }
                )
            },
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(selected = tab == 0, onClick = { tab = 0 }, icon = { Icon(Icons.Default.Dashboard, null) }, label = { Text("خانه") })
                    NavigationBarItem(selected = tab == 1, onClick = { tab = 1 }, icon = { Icon(Icons.Default.People, null) }, label = { Text("مخاطبان") })
                    NavigationBarItem(selected = tab == 2, onClick = { tab = 2 }, icon = { Icon(Icons.Default.Edit, null) }, label = { Text("پیام") })
                    NavigationBarItem(selected = tab == 3, onClick = { tab = 3 }, icon = { Icon(Icons.Default.Schedule, null) }, label = { Text("برنامه") })
                }
            }
        ) { padding ->
            CompositionLocalProvider(LocalLayoutDirection provides androidx.compose.ui.unit.LayoutDirection.Rtl) {
                when (tab) {
                    0 -> HomeScreen(customers.size, onCompose = { tab = 2 }, modifier = Modifier.padding(padding))
                    1 -> CustomersScreen(
                        customers, customerName, { customerName = it }, customerUser, { customerUser = it },
                        accepted, { accepted = it },
                        onAdd = {
                            if (customerName.isNotBlank() && customerUser.isNotBlank() && accepted) {
                                customers.add(Customer(customerName.trim(), customerUser.trim(), true))
                                customerName = ""; customerUser = ""; accepted = false
                            }
                        },
                        modifier = Modifier.padding(padding)
                    )
                    2 -> CampaignScreen(campaignTitle, { campaignTitle = it }, campaignText, { campaignText = it },
                        onShare = {
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, campaignTitle)
                                putExtra(Intent.EXTRA_TEXT, "$campaignTitle\n\n$campaignText\n\nپخش روشنایی ایرانوس\nwww.risesara.ir")
                            }
                            context.startActivity(Intent.createChooser(intent, "اشتراک‌گذاری از طریق تلگرام"))
                        }, modifier = Modifier.padding(padding))
                    else -> ScheduleScreen(Modifier.padding(padding))
                }
            }
        }
    }
}

@Composable
fun HomeScreen(count: Int, onCompose: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("سلام، به پنل ایرانوس خوش آمدی", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Card {
            Column(Modifier.fillMaxWidth().padding(18.dp)) {
                Text("مخاطبان ثبت‌شده با رضایت دریافت پیام", color = MaterialTheme.colorScheme.secondary)
                Text("$count", fontSize = 34.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
        }
        Card {
            Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("ارسال امن و کنترل‌شده", fontWeight = FontWeight.Bold)
                Text("این نسخه پیام را آماده می‌کند و برای ارسال دستی به تلگرام یا برنامه‌های سازگار می‌فرستد. ارسال انبوه خودکار به دفترچه تلفن در آن فعال نیست.")
                Button(onClick = onCompose, modifier = Modifier.fillMaxWidth()) { Text("نوشتن پیام تبلیغاتی") }
            }
        }
        Text("اطلاعات تماس پیش‌فرض", fontWeight = FontWeight.Bold)
        Text("پخش روشنایی ایرانوس\n09156554383\nwww.risesara.ir")
    }
}

@Composable
fun CustomersScreen(
    customers: List<Customer>, name: String, onName: (String) -> Unit,
    username: String, onUsername: (String) -> Unit, accepted: Boolean, onAccepted: (Boolean) -> Unit,
    onAdd: () -> Unit, modifier: Modifier = Modifier
) {
    Column(modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("مخاطبان مجاز", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        OutlinedTextField(name, onName, label = { Text("نام مشتری") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(username, onUsername, label = { Text("آیدی تلگرام (مثلاً @name)") }, modifier = Modifier.fillMaxWidth())
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(accepted, onAccepted)
            Text("مشتری رضایت داده پیام تبلیغاتی دریافت کند")
        }
        Button(onClick = onAdd, enabled = name.isNotBlank() && username.isNotBlank() && accepted, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Default.PersonAdd, null); Spacer(Modifier.width(8.dp)); Text("افزودن مخاطب")
        }
        HorizontalDivider()
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(customers) { c ->
                ListItem(
                    headlineContent = { Text(c.name) },
                    supportingContent = { Text(c.username) },
                    trailingContent = { AssistChip(onClick = {}, label = { Text(if (c.optedIn) "رضایت ثبت شد" else "غیرفعال") }) }
                )
            }
        }
    }
}

@Composable
fun CampaignScreen(title: String, onTitle: (String) -> Unit, body: String, onBody: (String) -> Unit,
                   onShare: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("ساخت پیام تبلیغاتی", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        OutlinedTextField(title, onTitle, label = { Text("عنوان پیام") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(body, onBody, label = { Text("متن تبلیغ") }, modifier = Modifier.fillMaxWidth().weight(1f), minLines = 6)
        Text("پیش‌نمایش:", fontWeight = FontWeight.Bold)
        Card { Text("$title\n\n$body\n\nپخش روشنایی ایرانوس\nwww.risesara.ir", Modifier.padding(14.dp)) }
        Button(onClick = onShare, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Default.Send, null); Spacer(Modifier.width(8.dp)); Text("اشتراک‌گذاری و ارسال دستی")
        }
    }
}

@Composable
fun ScheduleScreen(modifier: Modifier = Modifier) {
    Column(modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("برنامه ارسال", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Card {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("زمان‌بندی در نسخه اولیه")
                Text("در این نسخه امکان تنظیم یادآور روزانه برای آماده‌سازی پیام در نظر گرفته شده است؛ ارسال خودکار پیام خصوصی فعال نیست.")
                Text("برای ارسال، پیام را بازبینی کن و خودت مخاطب رضایت‌داده را در تلگرام انتخاب کن.")
            }
        }
    }
}
