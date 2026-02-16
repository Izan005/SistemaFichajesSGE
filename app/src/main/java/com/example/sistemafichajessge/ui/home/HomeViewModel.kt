package com.example.sistemafichajessge.ui.home

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateListOf

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.sistemafichajessge.data.datatypes.EnumMonths
import com.example.sistemafichajessge.data.datatypes.Quadruple
import com.example.sistemafichajessge.data.dbSingleton.FichajesApplication
import com.example.sistemafichajessge.data.model.Department
import com.example.sistemafichajessge.data.model.Registry
import com.example.sistemafichajessge.data.model.User
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.WeekFields
import java.util.Locale


@RequiresApi(Build.VERSION_CODES.O)
class HomeViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val userRepository = (application as FichajesApplication).userRepo

    private val departmentRepository = (application as FichajesApplication).departmentRepo

    private val registryRepository = (application as FichajesApplication).registryRepo


    private val userId: Int = checkNotNull(savedStateHandle[HomeScreen.userIdArg])


    val userRecieved: StateFlow<User?> = userRepository.getUser(userId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val listDepartment: StateFlow<List<Department>> = departmentRepository.getAllDepartments()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    val departmentUser: StateFlow<Department?> =
        combine(userRecieved, listDepartment) {user, departments ->
            departments.find { it.id == user?.departmentId }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    val registriesByUser: StateFlow<List<Registry>> = registryRepository.getRegistriesByUserId(userId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    val workedDaysList = mutableStateListOf<Triple<Int, Int, Int>>()

    val averageWorkedHours: StateFlow<Double> = registriesByUser
        .map { registries ->
            calculateAverageHoursWorked(registries)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = 0.0
        )

    val averageHoursBreak: StateFlow<Double> = registriesByUser
        .map {registries ->
            calculateAverageHoursBreak(registries)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = 0.0
        )

    val averageOvertimeHours: StateFlow<Double> = registriesByUser
        .map { registries ->
            calculateAverageOvertimeHours(registries)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = 0.0
        )


    /** FUNCIÓN PARA DEBUG **/
    @RequiresApi(Build.VERSION_CODES.O)
    fun getDateOfRegistryTest(date: Long): String {

        val ldt = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(date),
            ZoneId.systemDefault()
        )

        return  "${ldt.hour}, ${ldt.dayOfMonth}, ${ldt.monthValue}, ${ldt.year}"

    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun getDateOfRegistryQ(date: Long): Quadruple<Int, Int, Int, Int> {

        val ldt = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(date),
            ZoneId.systemDefault()
        )

        return Quadruple(
            first = ldt.hour,
            second = ldt.dayOfMonth,
            third = ldt.monthValue,
            fourth = ldt.year
        ) //"${ldt.hour}, ${ldt.dayOfMonth}, ${ldt.monthValue}, ${ldt.year}"

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getDateOfRegistryT(date: Long): Triple<Int, Int, Int> {

        val ldt = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(date),
            ZoneId.systemDefault()
        )

        return Triple(
            first = ldt.dayOfMonth,
            second = ldt.monthValue,
            third = ldt.year
        )

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun groupWorkedDays(){

        viewModelScope.launch {

            registriesByUser.collect { registries ->

                workedDaysList.clear()

                registriesByUser.value.forEach { registry ->

                    val regDate = getDateOfRegistryT(date = registry.dateRegistry)

                    if(!workedDaysList.contains(regDate)){
                        workedDaysList.add(regDate)
                    }
                }
            }
        }
    }

    fun calculateAverageHoursWorked(registries: List<Registry>): Double {
        if (registries.isEmpty()) return 0.0

        // 1. Agrupamos los registros por fecha (día/mes/año) usando el Triple de getDateOfRegistryT()
        val registriesByDay = registries.groupBy { registry ->
            getDateOfRegistryT(registry.dateRegistry)
        }

        var totalHoursAllDays = 0.0

        // 2. Para cada día, calculamos la jornada
        registriesByDay.forEach { (_, dayRegistries) ->
            val firstEntry = dayRegistries.minOf { it.dateRegistry }
            val lastExit = dayRegistries.maxOf { it.dateRegistry }

            val diffInMs = lastExit - firstEntry
            // ms a horas: / (1000 * 60 * 60)
            val hoursInDay = diffInMs.toDouble() / 3_600_000.0
            totalHoursAllDays += hoursInDay
        }

        // 3. Retornamos la media
        return totalHoursAllDays / registriesByDay.size
    }


    fun calculateAverageHoursBreak(registries: List<Registry>): Double{
        if (registries.isEmpty()) return 0.0

        // 1. Agrupamos los registros por día
        val registriesByDay = registries.groupBy { registry ->
            getDateOfRegistryT(registry.dateRegistry)
        }

        var totalHoursBreakAllDays = 0.0

        // 2. Procesamos cada día para sumar sus periodos de descanso
        registriesByDay.forEach { (_, dayRegistries) ->
            // Ordenamos los registros del día por tiempo para procesar los intervalos correctamente
            val sortedRegs = dayRegistries.sortedBy { it.dateRegistry }
            var dailyBreakMs = 0L
            var startTime: Long? = null

            for (reg in sortedRegs) {
                when (reg.type) {
                    "break_start" -> startTime = reg.dateRegistry
                    "break_end" -> {
                        if (startTime != null) {
                            dailyBreakMs += (reg.dateRegistry - startTime)
                            startTime = null // Reset para el siguiente posible descanso del mismo día
                        }
                    }
                }
            }
            // Convertimos los ms del día a horas y sumamos al total
            totalHoursBreakAllDays += (dailyBreakMs.toDouble() / 3_600_000.0)
        }

        // 3. Retornamos la media (Total horas descanso / Días totales con actividad)
        return totalHoursBreakAllDays / registriesByDay.size

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun calculateAverageOvertimeHours(registries: List<Registry>): Double {
        if (registries.isEmpty()) return 0.0

        // 1. Agrupamos los registros por día
        val registriesByDay = registries.groupBy { registry ->
            getDateOfRegistryT(registry.dateRegistry)
        }

        var totalOvertimeHours = 0.0

        registriesByDay.forEach { (_, dayRegistries) ->
            val firstEntryMs = dayRegistries.minOf { it.dateRegistry }
            val lastExitMs = dayRegistries.maxOf { it.dateRegistry }

            val ldtEntry = LocalDateTime.ofInstant(Instant.ofEpochMilli(firstEntryMs), ZoneId.systemDefault())
            val ldtExit = LocalDateTime.ofInstant(Instant.ofEpochMilli(lastExitMs), ZoneId.systemDefault())

            // Regla A: Si es Lunes (1) o Viernes (5), todo el tiempo trabajado es extra
            val dayOfWeek = ldtEntry.dayOfWeek.value // 1=Lunes, 7=Domingo
            if (dayOfWeek == 1 || dayOfWeek == 5) {
                val totalDayHours = (lastExitMs - firstEntryMs).toDouble() / 3_600_000.0
                totalOvertimeHours += totalDayHours
            } else {
                // Regla B: Horas fuera del rango 9:00 - 15:00

                // Definimos los límites de ese día concreto
                val limitStart = ldtEntry.withHour(9).withMinute(0).withSecond(0).withNano(0)
                val limitEnd = ldtEntry.withHour(15).withMinute(0).withSecond(0).withNano(0)

                val limitStartMs = limitStart.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                val limitEndMs = limitEnd.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

                // Extra antes de las 9:00
                if (firstEntryMs < limitStartMs) {
                    val beforeMs = minOf(lastExitMs, limitStartMs) - firstEntryMs
                    totalOvertimeHours += (beforeMs.coerceAtLeast(0).toDouble() / 3_600_000.0)
                }

                // Extra después de las 15:00
                if (lastExitMs > limitEndMs) {
                    val afterMs = lastExitMs - maxOf(firstEntryMs, limitEndMs)
                    totalOvertimeHours += (afterMs.coerceAtLeast(0).toDouble() / 3_600_000.0)
                }
            }
        }

        // Retornamos la media total
        return totalOvertimeHours / registriesByDay.size
    }

    fun daysWorkedInCurrentMonth(): Int {

        val registry = workedDaysList

        val now = getDateOfRegistryT(System.currentTimeMillis())

        if (registry.isEmpty()) return 0

        var workedDays = 0

        registry.forEach { reg ->

            if (reg.second == now.second && reg.third == now.third)
                workedDays++

        }

        return workedDays
    }

    fun missingDaysInMonth(): Int {

        val daysWorked = daysWorkedInCurrentMonth()

        val now = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(System.currentTimeMillis()),
            ZoneId.systemDefault()
        )

        val currentMonthNum = now.monthValue

        val totalMonthDays = EnumMonths.entries.find { it.num == currentMonthNum }?.days ?: 30

        return (totalMonthDays - daysWorked).coerceAtLeast(0)
    }

    fun tardinessInCurrentMonth(): Int {
        val registry = registriesByUser

        var totalTardiness = 0

        val now = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(System.currentTimeMillis()),
            ZoneId.systemDefault())

        registry.value.forEach { reg ->

            val regDate = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(reg.dateRegistry),
                ZoneId.systemDefault()
            )

            if (reg.type == "entry" && regDate.hour > 9 && regDate.monthValue == now.monthValue){
                totalTardiness++
            }
        }

        return totalTardiness
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun daysWorkedInCurrentWeek(): Int {
        // 1. Obtenemos la fecha actual y su número de semana
        val now = LocalDateTime.now()
        val weekFields = WeekFields.of(Locale.getDefault())
        val currentWeek = now.get(weekFields.weekOfWeekBasedYear())
        val currentYear = now.year

        var count = 0

        // 2. Recorremos la lista de días únicos trabajados
        workedDaysList.forEach { triple ->
            // triple.first = día, triple.second = mes, triple.third = año
            val dateInList = LocalDateTime.of(triple.third, triple.second, triple.first, 0, 0)

            val weekOfDate = dateInList.get(weekFields.weekOfWeekBasedYear())
            val yearOfDate = dateInList.year

            // 3. Si coincide la semana y el año, sumamos un día
            if (weekOfDate == currentWeek && yearOfDate == currentYear) {
                count++
            }
        }

        return count
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun missingDaysInCurrentWeek(): Int {
        // 1. Obtenemos los días que ya ha trabajado esta semana
        val daysWorkedThisWeek = daysWorkedInCurrentWeek()

        // 2. Definimos el objetivo de días laborales (ejemplo: 5 días por semana)
        // Si tu empresa cuenta de lunes a domingo, cambia este valor a 7
        val targetDaysPerWeek = 5

        // 3. Calculamos la diferencia
        val missingDays = targetDaysPerWeek - daysWorkedThisWeek

        // 4. Devolvemos el resultado asegurándonos de que no sea negativo
        // (por si alguien trabaja horas extra en sábado/domingo)
        return missingDays.coerceAtLeast(0)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getWeeklyStats(): Triple<Double, Double, Double> {
        val registries = registriesByUser.value
        if (registries.isEmpty()) return Triple(0.0, 0.0, 0.0)

        // 1. Obtenemos los datos de la semana actual
        val now = LocalDateTime.now()
        val weekFields = WeekFields.of(Locale.getDefault())
        val currentWeek = now.get(weekFields.weekOfWeekBasedYear())
        val currentYear = now.year

        // 2. Filtramos registros que pertenezcan a esta semana
        val weeklyRegistries = registries.filter {
            val ldt = LocalDateTime.ofInstant(Instant.ofEpochMilli(it.dateRegistry), ZoneId.systemDefault())
            ldt.get(weekFields.weekOfWeekBasedYear()) == currentWeek && ldt.year == currentYear
        }

        if (weeklyRegistries.isEmpty()) return Triple(0.0, 0.0, 0.0)

        // Agrupamos por día para calcular jornadas
        val registriesByDay = weeklyRegistries.groupBy { getDateOfRegistryT(it.dateRegistry) }

        var effectiveHours = 0.0
        var overtimeHours = 0.0
        var breakHours = 0.0

        registriesByDay.forEach { (_, dayRegs) ->
            val sortedRegs = dayRegs.sortedBy { it.dateRegistry }
            val firstEntry = sortedRegs.minOf { it.dateRegistry }
            val lastExit = sortedRegs.maxOf { it.dateRegistry }

            val ldtEntry = LocalDateTime.ofInstant(Instant.ofEpochMilli(firstEntry), ZoneId.systemDefault())
            val dayOfWeek = ldtEntry.dayOfWeek.value // 1=Lunes, 5=Viernes

            // --- CÁLCULO DE HORAS EFECTIVAS Y EXTRA ---
            if (dayOfWeek == 1 || dayOfWeek == 5) {
                // Lunes y Viernes: Todo es extra
                overtimeHours += (lastExit - firstEntry).toDouble() / 3_600_000.0
            } else {
                // Días normales: Rango 9:00 a 15:00
                val limitStartMs = ldtEntry.withHour(9).withMinute(0).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                val limitEndMs = ldtEntry.withHour(15).withMinute(0).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

                // Horas Efectivas (dentro del rango 9-15)
                val effectiveStart = maxOf(firstEntry, limitStartMs)
                val effectiveEnd = minOf(lastExit, limitEndMs)
                if (effectiveEnd > effectiveStart) {
                    effectiveHours += (effectiveEnd - effectiveStart).toDouble() / 3_600_000.0
                }

                // Horas Extra (antes de las 9)
                if (firstEntry < limitStartMs) {
                    overtimeHours += (minOf(lastExit, limitStartMs) - firstEntry).toDouble() / 3_600_000.0
                }
                // Horas Extra (después de las 15)
                if (lastExit > limitEndMs) {
                    overtimeHours += (lastExit - maxOf(firstEntry, limitEndMs)).toDouble() / 3_600_000.0
                }
            }

            // --- CÁLCULO DE HORAS DE DESCANSO ---
            var tempStart: Long? = null
            sortedRegs.forEach { reg ->
                if (reg.type == "break_start") tempStart = reg.dateRegistry
                else if (reg.type == "break_end" && tempStart != null) {
                    breakHours += (reg.dateRegistry - tempStart!!).toDouble() / 3_600_000.0
                    tempStart = null
                }
            }
        }

        // Retornamos Triple(Efectivas, Extra, Descanso)
        return Triple(effectiveHours, overtimeHours, breakHours)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getMonthlyStats(): Triple<Double, Double, Double> {
        val registries = registriesByUser.value
        if (registries.isEmpty()) return Triple(0.0, 0.0, 0.0)

        // 1. Obtenemos el mes y año actuales
        val now = LocalDateTime.now()
        val currentMonth = now.month
        val currentYear = now.year

        // 2. Filtramos registros que pertenezcan a este mes y año
        val monthlyRegistries = registries.filter {
            val ldt = LocalDateTime.ofInstant(Instant.ofEpochMilli(it.dateRegistry), ZoneId.systemDefault())
            ldt.month == currentMonth && ldt.year == currentYear
        }

        if (monthlyRegistries.isEmpty()) return Triple(0.0, 0.0, 0.0)

        // El resto de la lógica se mantiene igual, ya que procesamos día a día
        val registriesByDay = monthlyRegistries.groupBy { getDateOfRegistryT(it.dateRegistry) }

        var effectiveHours = 0.0
        var overtimeHours = 0.0
        var breakHours = 0.0

        registriesByDay.forEach { (_, dayRegs) ->
            val sortedRegs = dayRegs.sortedBy { it.dateRegistry }
            val firstEntry = sortedRegs.minOf { it.dateRegistry }
            val lastExit = sortedRegs.maxOf { it.dateRegistry }

            val ldtEntry = LocalDateTime.ofInstant(Instant.ofEpochMilli(firstEntry), ZoneId.systemDefault())
            val dayOfWeek = ldtEntry.dayOfWeek.value // 1=Lunes, 7=Domingo

            // --- CÁLCULO DE HORAS SEGÚN TU REGLA DE NEGOCIO ---
            // Lunes (1) y Viernes (5) -> Todo es extra
            if (dayOfWeek == 1 || dayOfWeek == 5) {
                overtimeHours += (lastExit - firstEntry).toDouble() / 3_600_000.0
            } else {
                // Días normales: Horas efectivas entre 09:00 y 15:00
                val limitStartMs = ldtEntry.withHour(9).withMinute(0).withSecond(0).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                val limitEndMs = ldtEntry.withHour(15).withMinute(0).withSecond(0).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

                // Horas Efectivas
                val effectiveStart = maxOf(firstEntry, limitStartMs)
                val effectiveEnd = minOf(lastExit, limitEndMs)
                if (effectiveEnd > effectiveStart) {
                    effectiveHours += (effectiveEnd - effectiveStart).toDouble() / 3_600_000.0
                }

                // Horas Extra (antes de las 9)
                if (firstEntry < limitStartMs) {
                    overtimeHours += (minOf(lastExit, limitStartMs) - firstEntry).toDouble() / 3_600_000.0
                }
                // Horas Extra (después de las 15)
                if (lastExit > limitEndMs) {
                    overtimeHours += (lastExit - maxOf(firstEntry, limitEndMs)).toDouble() / 3_600_000.0
                }
            }

            // --- CÁLCULO DE DESCANSOS ---
            var tempStart: Long? = null
            sortedRegs.forEach { reg ->
                if (reg.type == "break_start") tempStart = reg.dateRegistry
                else if (reg.type == "break_end" && tempStart != null) {
                    breakHours += (reg.dateRegistry - tempStart!!).toDouble() / 3_600_000.0
                    tempStart = null
                }
            }
        }

        return Triple(effectiveHours, overtimeHours, breakHours)
    }

    init {
        groupWorkedDays()
    }

}