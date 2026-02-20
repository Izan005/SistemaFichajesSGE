package com.example.sistemafichajessge.ui.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sistemafichajessge.FichajesViewModelProvider
import com.example.sistemafichajessge.data.model.Registry
import com.example.sistemafichajessge.data.model.User
import com.example.sistemafichajessge.ui.navigation.FichajesNavDestination

object HomeScreen : FichajesNavDestination {
    override val route = "home"

    const val userIdArg = "id_user"

    val routeWithArgs = "$route/{$userIdArg}"
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(factory = FichajesViewModelProvider.Factory),
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    navigateToTasks: (Int) -> Unit
){
    val user by viewModel.userRecieved.collectAsStateWithLifecycle()

    val departmentUser by viewModel.departmentUser.collectAsStateWithLifecycle()

    val registriesByUser by viewModel.registriesByUser.collectAsStateWithLifecycle()

    val averageHoursWorked by viewModel.averageWorkedHours.collectAsStateWithLifecycle()

    val averageHoursBreak by viewModel.averageHoursBreak.collectAsStateWithLifecycle()

    val averageOvertimeHours by viewModel.averageOvertimeHours.collectAsStateWithLifecycle()

    val daysWorkedInCurrentMonth = viewModel.daysWorkedInCurrentMonth()

    val missingDaysInMonth = viewModel.missingDaysInMonth()

    val totalTardinessDaysInCurrentMonth = viewModel.tardinessInCurrentMonth()

    val daysWorkedInCurrentWeek = viewModel.daysWorkedInCurrentWeek()

    val missingDaysInCurrentWeek = viewModel.missingDaysInCurrentWeek()

    val getWeeklyStats = viewModel.getWeeklyStats()

    val getMonthlyStats = viewModel.getMonthlyStats()

    Box(
        modifier = Modifier.fillMaxSize()
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            //verticalArrangement = Arrangement.Top,
            modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState())
        ) {
            SubTopBar(
                user = user,
                logOut = navigateBack
            )

            UserCard(
                userName = user?.name ?: "Cargando...",
                departmentName = departmentUser?.name ?: "Cargando...",
                job = user?.job ?: "Cargando...",
                user = user,
                viewModel = viewModel
            )

            Spacer(modifier = Modifier.height(20.dp))

//        RegistriesCard(
//            registries = registriesByUser,
//            user = user,
//            viewModel = viewModel
//        )

            StatsCard(
                title = "Estadísticas Generales",
                avgWorkedHours = averageHoursWorked,
                avgRestHours = averageHoursBreak,
                avgExtraHours = averageOvertimeHours,
                daysWorkedInCurrentMonth = daysWorkedInCurrentMonth,
                missingDaysInMonth = missingDaysInMonth,
                tardinessInCurrentMonth = totalTardinessDaysInCurrentMonth,
                daysWorkedInCurrentWeek = daysWorkedInCurrentWeek,
                missingDaysInCurrentWeek = missingDaysInCurrentWeek
            )

            Spacer(modifier = Modifier.height(20.dp))

            HistoryCard(
                title = "Historial para este mes",
                hoursDo = getMonthlyStats.first,
                overtimeHours = getMonthlyStats.second,
                restHours = getMonthlyStats.third
            )

            Spacer(modifier = Modifier.height(20.dp))

            HistoryCard(
                title = "Historial para esta semana",
                hoursDo = getWeeklyStats.first,
                overtimeHours = getWeeklyStats.second,
                restHours = getWeeklyStats.third
            )

            Spacer(modifier = Modifier.height(20.dp))

        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.align(Alignment.BottomEnd)
                .padding(20.dp)
                .shadow(elevation = 5.dp, shape = CircleShape)
        ){
            Box(
                contentAlignment = Alignment.Center,
                modifier =
                    Modifier.background(color = Color(0xFF2C3E50), CircleShape)
                    .clip(CircleShape)
                    .size(80.dp)
                    .padding(20.dp)
                        .clickable {
                            navigateToTasks(user?.id ?: 0)
                        }
            ){
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Crear Tareas",
                    tint = Color.White
                )
            }
        }

    }

}

@Composable
fun SubTopBar(
    user: User?,
    logOut: () -> Unit,
    modifier: Modifier = Modifier
){
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxWidth()
            .background(color = Color(0xFF2C3E50))

    ) {
        Text(
            text = user?.name ?: "Cargando...",
            modifier = modifier
                .weight(0.5F)
                .padding(start = 25.dp, top =  12.dp)
            ,style = MaterialTheme.typography.titleMedium,
            //fontSize = 15.sp
        )
        TextButton(
            onClick = { logOut() },
            modifier = modifier.weight(0.5F),
            colors = ButtonDefaults.textButtonColors(
                contentColor = Color.White
            )
        ) {
            Text(
                text = "Cerrar Sesión",
                style = MaterialTheme.typography.titleSmall,
                color = Color.White,

            )
        }
    }
}

@Composable
fun UserCard(
    userName: String,
    departmentName: String,
    job: String,
    modifier: Modifier = Modifier,
    user: User?,
    viewModel: HomeViewModel

) {


    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)

        ) {

            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = userName.first().uppercase(),
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = userName,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "$departmentName, $job",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            Divider(
                color = Color.LightGray,
                modifier = Modifier.width(20.dp).height(1.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Jornada laboral de 9:00 a 15:00\nLibrando Lunes y Viernes",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.secondary
            )

            Spacer(modifier = Modifier.height(24.dp))


        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RegistriesCard(
    registries: List<Registry?>,
    user: User?,
    viewModel : HomeViewModel
){

    val workerDaysList = viewModel.workedDaysList

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(workerDaysList) { (day, month, year) ->

            //val date = viewModel.getDateOfRegistryTest(registry?.dateRegistry ?: 0)

            Text("User: ${user?.id ?: "Cargando..."}, Date: $day, $month, $year")
        }
    }
}

@Composable
fun StatsCard(
    title: String,
    avgWorkedHours: Double,
    avgRestHours: Double,
    avgExtraHours: Double,
    daysWorkedInCurrentMonth: Int,
    missingDaysInMonth: Int,
    tardinessInCurrentMonth: Int,
    daysWorkedInCurrentWeek: Int,
    missingDaysInCurrentWeek: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // TÍTULO
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(24.dp))

            Divider()

            Spacer(modifier = Modifier.height(24.dp))


            StatItem(
                label = "Media de horas trabajadas por día",
                value = avgWorkedHours
            )

            Spacer(modifier = Modifier.height(16.dp))

            StatItem(
                label = "Media de horas de descanso",
                value = avgRestHours
            )

            Spacer(modifier = Modifier.height(16.dp))

            StatItem(
                label = "Media de horas extra",
                value = avgExtraHours
            )

            Spacer(modifier = Modifier.height(16.dp))

            StatItem(
                label = "Dias trabajados en el mes en curso",
                value = daysWorkedInCurrentMonth.toDouble()
            )

            Spacer(modifier = Modifier.height(16.dp))

            StatItem(
                label = "Dias faltados en el mes en curso",
                value = missingDaysInMonth.toDouble()
            )

            Spacer(modifier = Modifier.height(16.dp))

            StatItem(
                label = "Retrasos totales en el mes en curso",
                value = tardinessInCurrentMonth.toDouble()
            )

            Spacer(modifier = Modifier.height(16.dp))

            StatItem(
                label = "Dias trabajados en la semana en curso",
                value = daysWorkedInCurrentWeek.toDouble()
            )

            Spacer(modifier = Modifier.height(16.dp))

            StatItem(
                label = "Días faltados en la semana en curso",
                value = missingDaysInCurrentWeek.toDouble()
            )
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    value: Double
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = value.toString(),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun HistoryCard(
    title: String,
    hoursDo: Double,
    overtimeHours: Double,
    restHours: Double,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // TÍTULO
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(24.dp))

            Divider()

            Spacer(modifier = Modifier.height(24.dp))

            StatItem(
                label = "Horas en jornada realizadas",
                value = hoursDo
            )

            Spacer(modifier = Modifier.height(24.dp))

            StatItem(
                label = "Horas extra realizadas",
                value = overtimeHours
            )

            Spacer(modifier = Modifier.height(24.dp))

            StatItem(
                label = "Horas descansadas",
                value = restHours
            )

        }
    }
}
