package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.CampusAiService
import com.example.data.db.AppDatabase
import com.example.data.model.AssignmentEntity
import com.example.data.model.CampusEventEntity
import com.example.data.model.CareerEntity
import com.example.data.model.CommunityGroupEntity
import com.example.data.model.CommunityPostEntity
import com.example.data.model.ExamEntity
import com.example.data.model.ExpenseEntity
import com.example.data.model.ExpenseGroupEntity
import com.example.data.model.LostFoundEntity
import com.example.data.model.MarketplaceEntity
import com.example.data.model.NotificationItemEntity
import com.example.data.model.QuizQuestionEntity
import com.example.data.model.SettlementEntity
import com.example.data.model.StudyTaskEntity
import com.example.data.model.SubjectNoteEntity
import com.example.data.model.UserEntity
import com.example.data.repository.CampusRepository
import com.example.ml.AcademicPerformancePrediction
import com.example.ml.ExpenseForecast
import com.example.ml.StudentMlPredictor
import com.example.splitzy.BalanceSummary
import com.example.splitzy.CategorySpend
import com.example.splitzy.DebtRelation
import com.example.splitzy.SplitzyCalculator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)

class CampusViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CampusRepository

    init {
        val database = AppDatabase.getInstance(application)
        repository = CampusRepository(database)
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            com.example.data.db.DatabaseSeeder.seedInitialData(database)
        }
    }


    private val _isUserLoggedIn = MutableStateFlow(true)
    val isUserLoggedIn: StateFlow<Boolean> = _isUserLoggedIn.asStateFlow()

    val currentUser: StateFlow<UserEntity?> = repository.currentUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun loginDefaultStudent() {
        _isUserLoggedIn.value = true
    }

    fun registerAndLoginStudent(
        name: String,
        phone: String,
        email: String,
        college: String,
        university: String,
        course: String,
        studentId: String,
        year: String,
        semester: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val existing = currentUser.value
            val updatedUser = (existing ?: UserEntity()).copy(
                name = name,
                phone = phone,
                email = email,
                college = college,
                university = university,
                course = course,
                studentId = studentId,
                year = year,
                semester = semester,
                isLoggedIn = true
            )
            repository.insertUser(updatedUser)
            _isUserLoggedIn.value = true
            logActivity("person", "Student Logged In", "Verified via mobile $phone at $college ($university).", "Campus", 50)
            onSuccess()
        }
    }

    fun logoutStudent() {
        _isUserLoggedIn.value = false
    }

    val allUsers: StateFlow<List<UserEntity>> = repository.allUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    val expenses: StateFlow<List<ExpenseEntity>> = repository.allExpenses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val expenseGroups: StateFlow<List<ExpenseGroupEntity>> = repository.allGroups
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val settlements: StateFlow<List<SettlementEntity>> = repository.allSettlements
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val balanceSummary: StateFlow<BalanceSummary> = expenses
        .combine(currentUser) { expList, user ->
            SplitzyCalculator.calculateUserBalances(expList, user?.name ?: "You")
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), BalanceSummary(0.0, 0.0, 0.0, 0.0))

    val groupSettlements: StateFlow<List<DebtRelation>> = expenses
        .combine(currentUser) { expList, _ ->
            SplitzyCalculator.computeGroupSettlements(expList)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categorySpends: StateFlow<List<CategorySpend>> = expenses
        .combine(currentUser) { expList, _ ->
            SplitzyCalculator.computeCategorySpends(expList)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    val studyTasks: StateFlow<List<StudyTaskEntity>> = repository.allTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val exams: StateFlow<List<ExamEntity>> = repository.allExams
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val subjectNotes: StateFlow<List<SubjectNoteEntity>> = repository.allNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val assignments: StateFlow<List<AssignmentEntity>> = repository.allAssignments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val quizQuestions: StateFlow<List<QuizQuestionEntity>> = repository.getQuizQuestions("ALL")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    val communityPosts: StateFlow<List<CommunityPostEntity>> = repository.activePosts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val reportedPosts: StateFlow<List<CommunityPostEntity>> = repository.reportedPosts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val communityGroups: StateFlow<List<CommunityGroupEntity>> = repository.communityGroups
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val campusEvents: StateFlow<List<CampusEventEntity>> = repository.campusEvents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    val lostAndFoundItems: StateFlow<List<LostFoundEntity>> = repository.activeLostFound
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val reportedLostFound: StateFlow<List<LostFoundEntity>> = repository.reportedLostFound
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val marketplaceItems: StateFlow<List<MarketplaceEntity>> = repository.activeMarketplace
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val reportedMarketplace: StateFlow<List<MarketplaceEntity>> = repository.reportedMarketplace
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val careerItems: StateFlow<List<CareerEntity>> = repository.careerItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications: StateFlow<List<NotificationItemEntity>> = repository.notifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    val activities: StateFlow<List<com.example.data.model.ActivityEntity>> = repository.allActivities
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val badges: StateFlow<List<com.example.data.model.BadgeEntity>> = repository.allBadges
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val quizHistory: StateFlow<List<com.example.data.model.QuizHistoryEntity>> = repository.quizHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userResume: StateFlow<com.example.data.model.ResumeEntity?> = repository.userResume
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)


    val grievances: StateFlow<List<com.example.data.model.CampusGrievanceEntity>> = repository.allGrievances
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())



    val campusScore: StateFlow<Int> = combine(
        currentUser,
        studyTasks,
        expenses,
        activities
    ) { user, tasks, expList, actList ->
        val studyFactor = 80
        val attendanceFactor = user?.attendancePercent ?: 90
        val budgetFactor = if (user != null && user.monthlyBudget > 0) {
            val spent = expList.sumOf { it.amount }
            if (spent <= user.monthlyBudget) 85 else 65
        } else 70
        val skillsFactor = 85
        val activityFactor = (actList.size * 10).coerceIn(60, 95)

        ((studyFactor * 0.25) + (attendanceFactor * 0.25) + (budgetFactor * 0.20) + (skillsFactor * 0.15) + (activityFactor * 0.15)).toInt().coerceIn(0, 100)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 84)


    val userXp: StateFlow<Int> = activities.combine(badges) { acts, bdgs ->
        val actsXp = acts.sumOf { it.xpEarned }
        val bdgsXp = bdgs.filter { it.isUnlocked }.sumOf { it.xpValue }
        actsXp + bdgsXp + 450
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 720)

    val userLevel: StateFlow<Int> = userXp.combine(currentUser) { xp, _ ->
        (xp / 150 + 1).coerceIn(1, 10)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 4)


    private val _isShieldActive = MutableStateFlow(true)
    val isShieldActive: StateFlow<Boolean> = _isShieldActive.asStateFlow()


    private val _userTokens = MutableStateFlow(4850)
    val userTokens: StateFlow<Int> = _userTokens.asStateFlow()

    private val _rewardsCatalog = MutableStateFlow<List<com.example.data.model.CampusRewardItem>>(
        listOf(
            com.example.data.model.CampusRewardItem(
                id = "rew_cap",
                name = "Classic Scholar Snapback Cap",
                description = "Adjustable UV-protection brim with sleek matte finish campus crest.",
                category = "Accessories",
                tokenCost = 1500,
                emoji = "🧢",
                originalPriceTag = "₹499",
                stockLeft = 25
            ),
            com.example.data.model.CampusRewardItem(
                id = "rew_bottle",
                name = "Insulated Stainless Campus Flask (750ml)",
                description = "24hr Cold / 12hr Hot double-wall thermal flask with temperature display.",
                category = "Merch",
                tokenCost = 2500,
                emoji = "🍶",
                originalPriceTag = "₹699",
                stockLeft = 14
            ),
            com.example.data.model.CampusRewardItem(
                id = "rew_tshirt",
                name = "Scholario Premium Cotton T-Shirt",
                description = "100% Organic breathable cotton with embroidered Campus scholar logo.",
                category = "Wearables",
                tokenCost = 5000,
                emoji = "👕",
                originalPriceTag = "₹899",
                stockLeft = 18
            ),
            com.example.data.model.CampusRewardItem(
                id = "rew_hoodie",
                name = "Campus Winter Fleece Hoodie",
                description = "Heavyweight brushed fleece with kangaroo pocket and thumb holes.",
                category = "Wearables",
                tokenCost = 7500,
                emoji = "🧥",
                originalPriceTag = "₹1,899",
                stockLeft = 9
            ),
            com.example.data.model.CampusRewardItem(
                id = "rew_shoes",
                name = "Campus Sneaker Edition (Running/Casual)",
                description = "Ergonomic high-rebound cushioning with anti-slip campus grip sole.",
                category = "Footwear",
                tokenCost = 10000,
                emoji = "👟",
                originalPriceTag = "₹2,499",
                stockLeft = 7
            ),
            com.example.data.model.CampusRewardItem(
                id = "rew_backpack",
                name = "Pro Scholar Tech Backpack (Waterproof)",
                description = "Dedicated 16-inch laptop chamber, USB charging port & anti-theft zipper.",
                category = "Gear",
                tokenCost = 12500,
                emoji = "🎒",
                originalPriceTag = "₹3,299",
                stockLeft = 5
            ),
            com.example.data.model.CampusRewardItem(
                id = "rew_earbuds",
                name = "Campus ANC Wireless Gaming Earbuds",
                description = "45ms Ultra-low latency, Active Noise Cancellation & 40H playtime.",
                category = "Electronics",
                tokenCost = 15000,
                emoji = "🎧",
                originalPriceTag = "₹3,999",
                stockLeft = 4
            )
        )
    )
    val rewardsCatalog: StateFlow<List<com.example.data.model.CampusRewardItem>> = _rewardsCatalog.asStateFlow()

    fun redeemReward(reward: com.example.data.model.CampusRewardItem, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        if (_userTokens.value < reward.tokenCost) {
            onError("Insufficient tokens! You need ${reward.tokenCost - _userTokens.value} more tokens.")
            return
        }
        _userTokens.value -= reward.tokenCost
        val coupon = "CAMPUS-${reward.id.takeLast(4).uppercase()}-${(1000..9999).random()}"
        _rewardsCatalog.value = _rewardsCatalog.value.map { item ->
            if (item.id == reward.id) {
                item.copy(
                    stockLeft = (item.stockLeft - 1).coerceAtLeast(0),
                    isRedeemed = true,
                    couponCode = coupon
                )
            } else item
        }
        viewModelScope.launch {
            repository.addNotification(
                com.example.data.model.NotificationItemEntity(
                    title = "🎉 Reward Redeemed: ${reward.name}",
                    message = "Congratulations! Use voucher code $coupon to claim your ${reward.name} at the campus store.",
                    category = "ANNOUNCEMENT",
                    timeAgo = "Just now"
                )
            )
            logActivity("rewards", "Claimed Reward: ${reward.name}", "Redeemed with ${reward.tokenCost} tokens. Voucher: $coupon", "Campus", 50)
        }
        onSuccess(coupon)
    }

    fun awardActivityTokens(amount: Int, reason: String) {
        _userTokens.value += amount
        viewModelScope.launch {
            repository.addNotification(
                com.example.data.model.NotificationItemEntity(
                    title = "🪙 +$amount Campus Tokens Earned!",
                    message = "You earned $amount tokens for $reason. Keep using the app to unlock T-shirts, Shoes & Caps!",
                    category = "ANNOUNCEMENT",
                    timeAgo = "Just now"
                )
            )
        }
    }


    private val _securityIncidents = MutableStateFlow<List<com.example.data.model.SecurityThreatIncident>>(
        listOf(
            com.example.data.model.SecurityThreatIncident(
                id = "sec_01",
                threatType = "SQL Injection Payload Filtered",
                severity = "HIGH",
                attackerIp = "185.220.101.42 (Tor Exit Node)",
                attackerLocation = "Frankfurt, Hessen, Germany",
                deviceFingerprint = "Automated Python-requests / Scrapy Bot",
                attackVector = "Room ORM Parameterized Query Defense",
                timeAgo = "12 mins ago",
                status = "Blocked & Quarantined",
                isBlocked = true
            ),
            com.example.data.model.SecurityThreatIncident(
                id = "sec_02",
                threatType = "Suspicious Geolocation Remote Probe",
                severity = "CRITICAL",
                attackerIp = "45.154.255.89",
                attackerLocation = "Bucharest, Romania",
                deviceFingerprint = "Headless Chrome Linux x86_64",
                attackVector = "Brute Force Auth Token Probe",
                timeAgo = "2 hours ago",
                status = "IP Auto-Blacklisted (0 Access)",
                isBlocked = true
            )
        )
    )
    val securityIncidents: StateFlow<List<com.example.data.model.SecurityThreatIncident>> = _securityIncidents.asStateFlow()

    fun toggleSecurityShield() {
        _isShieldActive.value = !_isShieldActive.value
    }

    fun simulateThreatIntrusionTest() {
        viewModelScope.launch {
            val randomIps = listOf(
                Pair("194.26.29.112", "Kyiv, Ukraine"),
                Pair("103.251.167.20", "Jakarta, Indonesia"),
                Pair("185.191.171.3", "London, United Kingdom"),
                Pair("91.240.118.242", "Amsterdam, Netherlands")
            )
            val chosen = randomIps.random()
            val newIncident = com.example.data.model.SecurityThreatIncident(
                id = "sec_${System.currentTimeMillis()}",
                threatType = "Unauthorized Session Tamper Attempt",
                severity = "CRITICAL",
                attackerIp = chosen.first,
                attackerLocation = chosen.second,
                deviceFingerprint = "Kali Linux ARM64 (Frida Hooking Attempt)",
                attackVector = "App Integrity & Memory Tamper Defense",
                timeAgo = "Just now",
                status = "BLOCKED: Threat Quarantined",
                isBlocked = true
            )
            _securityIncidents.value = listOf(newIncident) + _securityIncidents.value


            repository.addNotification(
                com.example.data.model.NotificationItemEntity(
                    title = "🚨 Security Alert: Attack Blocked!",
                    message = "Intrusion attempt blocked from IP ${chosen.first} (${chosen.second}). Sandbox shield defended your data.",
                    category = "SECURITY",
                    timeAgo = "Just now"
                )
            )

            logActivity("shield", "Security Alert Triggered", "Threat blocked from ${chosen.second} (${chosen.first}).", "Admin", 25)
        }
    }

    fun clearSecurityLogs() {
        _securityIncidents.value = emptyList()
    }


    val academicRiskPrediction: StateFlow<AcademicPerformancePrediction?> = combine(
        currentUser,
        studyTasks,
        assignments
    ) { user, tasks, assignList ->
        if (user != null) {
            StudentMlPredictor.predictAcademicRisk(user, tasks, assignList)
        } else null
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val expenseForecast: StateFlow<ExpenseForecast?> = combine(
        expenses,
        currentUser
    ) { expList, user ->
        StudentMlPredictor.forecastExpenses(expList, user?.monthlyBudget ?: 8000.0)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)


    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                sender = "ai",
                message = "👋 Hi Alex! I am **Campus AI**. Ask me to explain concepts, solve syllabus doubts, compute Splitzy bills, create study plans, or review your resume!"
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    private val _aiDoubtResult = MutableStateFlow<String?>(null)
    val aiDoubtResult: StateFlow<String?> = _aiDoubtResult.asStateFlow()

    private val _aiStudyPlanResult = MutableStateFlow<String?>(null)
    val aiStudyPlanResult: StateFlow<String?> = _aiStudyPlanResult.asStateFlow()

    private val _aiExpenseInsight = MutableStateFlow<String?>(null)
    val aiExpenseInsight: StateFlow<String?> = _aiExpenseInsight.asStateFlow()

    private val _aiCareerResult = MutableStateFlow<String?>(null)
    val aiCareerResult: StateFlow<String?> = _aiCareerResult.asStateFlow()

    private val _aiResumeCritique = MutableStateFlow<String?>(null)
    val aiResumeCritique: StateFlow<String?> = _aiResumeCritique.asStateFlow()



    fun addExpense(
        title: String,
        amount: Double,
        category: String,
        date: String,
        description: String,
        paidBy: String,
        groupId: String?,
        groupName: String?,
        splitType: String,
        participantsJson: String
    ) {
        viewModelScope.launch {
            repository.addExpense(
                ExpenseEntity(
                    title = title,
                    amount = amount,
                    category = category,
                    date = date,
                    description = description,
                    paidBy = paidBy,
                    groupId = groupId,
                    groupName = groupName,
                    splitType = splitType,
                    participantsJson = participantsJson
                )
            )
            repository.addNotification(
                NotificationItemEntity(
                    title = "Expense Added in Splitzy",
                    message = "$paidBy added '$title' (₹${amount.toInt()}) in ${groupName ?: "Personal"}.",
                    category = "SPLITZY"
                )
            )
        }
    }

    fun deleteExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
        }
    }

    fun addGroup(name: String, category: String, members: String) {
        viewModelScope.launch {
            val id = "grp_" + System.currentTimeMillis()
            repository.addGroup(
                ExpenseGroupEntity(
                    id = id,
                    name = name,
                    category = category,
                    membersJson = members
                )
            )
        }
    }

    fun recordSettlement(payer: String, receiver: String, amount: Double, group: String?, note: String) {
        viewModelScope.launch {
            repository.addSettlement(
                SettlementEntity(
                    payerName = payer,
                    receiverName = receiver,
                    amount = amount,
                    groupName = group,
                    note = note,
                    isCompleted = true
                )
            )
        }
    }

    fun toggleTask(task: StudyTaskEntity) {
        viewModelScope.launch {
            repository.updateTask(task.copy(isCompleted = !task.isCompleted))
        }
    }

    fun addTask(title: String, subject: String, dueDate: String, priority: String, estimatedMinutes: Int) {
        viewModelScope.launch {
            repository.addTask(
                StudyTaskEntity(
                    title = title,
                    subject = subject,
                    dueDate = dueDate,
                    priority = priority,
                    estimatedMinutes = estimatedMinutes
                )
            )
        }
    }

    fun deleteTask(id: Long) {
        viewModelScope.launch {
            repository.deleteTask(id)
        }
    }

    fun addExam(code: String, name: String, date: String, time: String, venue: String, syllabus: String) {
        viewModelScope.launch {
            repository.addExam(
                ExamEntity(
                    subjectCode = code,
                    subjectName = name,
                    date = date,
                    time = time,
                    venue = venue,
                    syllabus = syllabus
                )
            )
        }
    }

    fun addNote(subject: String, title: String, chapter: String, summary: String, tags: String) {
        viewModelScope.launch {
            repository.addNote(
                SubjectNoteEntity(
                    subject = subject,
                    title = title,
                    chapter = chapter,
                    summary = summary,
                    tags = tags
                )
            )
        }
    }

    fun addAssignment(title: String, subject: String, deadline: String, maxMarks: Int, instructions: String) {
        viewModelScope.launch {
            repository.addAssignment(
                AssignmentEntity(
                    title = title,
                    subject = subject,
                    deadline = deadline,
                    maxMarks = maxMarks,
                    instructions = instructions
                )
            )
        }
    }

    fun updateAssignmentStatus(assignment: AssignmentEntity, newStatus: String) {
        viewModelScope.launch {
            repository.updateAssignment(assignment.copy(status = newStatus))
        }
    }

    fun likePost(id: Long) {
        viewModelScope.launch {
            repository.likePost(id)
        }
    }

    fun addPost(title: String, content: String, groupTag: String) {
        viewModelScope.launch {
            val user = currentUser.value
            repository.addPost(
                CommunityPostEntity(
                    authorName = user?.name ?: "Alex Rivera",
                    authorRole = user?.role ?: "Student",
                    title = title,
                    content = content,
                    groupTag = groupTag
                )
            )
            repository.addNotification(
                NotificationItemEntity(
                    title = "Post Published",
                    message = "Your post '$title' is live in $groupTag.",
                    category = "COMMUNITY"
                )
            )
        }
    }

    fun reportPost(id: Long, reason: String) {
        viewModelScope.launch {
            repository.reportPost(id, reason)
        }
    }

    fun dismissPostReport(id: Long) {
        viewModelScope.launch {
            repository.dismissPostReport(id)
        }
    }

    fun deletePost(id: Long) {
        viewModelScope.launch {
            repository.deletePost(id)
        }
    }

    fun toggleEventRegistration(id: Long) {
        viewModelScope.launch {
            repository.toggleEventRegistration(id)
        }
    }

    fun addLostFound(type: String, itemName: String, category: String, desc: String, loc: String, date: String, contact: String) {
        viewModelScope.launch {
            repository.addLostFound(
                LostFoundEntity(
                    type = type,
                    itemName = itemName,
                    category = category,
                    description = desc,
                    location = loc,
                    date = date,
                    contactInfo = contact
                )
            )
        }
    }

    fun reportLostFound(id: Long, reason: String) {
        viewModelScope.launch {
            repository.reportLostFound(id, reason)
        }
    }

    fun resolveLostFound(id: Long) {
        viewModelScope.launch {
            repository.markLostFoundResolved(id)
        }
    }

    fun deleteLostFound(id: Long) {
        viewModelScope.launch {
            repository.deleteLostFound(id)
        }
    }

    fun addMarketplaceItem(title: String, price: Double, category: String, condition: String, location: String, desc: String) {
        viewModelScope.launch {
            val user = currentUser.value
            repository.addMarketplaceItem(
                MarketplaceEntity(
                    title = title,
                    price = price,
                    category = category,
                    condition = condition,
                    location = location,
                    description = desc,
                    sellerName = user?.name ?: "Alex Rivera"
                )
            )
        }
    }

    fun reportMarketplace(id: Long) {
        viewModelScope.launch {
            repository.reportMarketplaceItem(id)
        }
    }

    fun deleteMarketplace(id: Long) {
        viewModelScope.launch {
            repository.deleteMarketplaceItem(id)
        }
    }

    fun updateUserProfile(user: UserEntity) {
        viewModelScope.launch {
            repository.updateUser(user)
        }
    }

    fun markNotificationRead(id: Long) {
        viewModelScope.launch {
            repository.markNotificationRead(id)
        }
    }

    fun markAllNotificationsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsRead()
        }
    }



    fun askDoubt(subject: String, question: String, difficulty: String) {
        viewModelScope.launch {
            _isAiLoading.value = true
            val response = CampusAiService.askDoubt(subject, question, difficulty)
            _aiDoubtResult.value = response
            _isAiLoading.value = false
        }
    }

    fun generateStudyPlan(subjects: String, days: Int, hours: Int) {
        viewModelScope.launch {
            _isAiLoading.value = true
            val response = CampusAiService.generateStudyPlan(subjects, days, hours)
            _aiStudyPlanResult.value = response
            _isAiLoading.value = false
        }
    }

    fun runAiExpenseAnalysis() {
        viewModelScope.launch {
            _isAiLoading.value = true
            val currentExpenses = expenses.value
            val budget = currentUser.value?.monthlyBudget ?: 8000.0
            val response = CampusAiService.analyzeExpenses(currentExpenses, budget)
            _aiExpenseInsight.value = response
            _isAiLoading.value = false
        }
    }

    fun runCareerGuidance(degree: String, skills: String, interests: String) {
        viewModelScope.launch {
            _isAiLoading.value = true
            val response = CampusAiService.getCareerGuidance(degree, skills, interests)
            _aiCareerResult.value = response
            _isAiLoading.value = false
        }
    }

    fun deleteNotification(id: Long) {
        viewModelScope.launch {
            repository.deleteNotification(id)
        }
    }

    fun logActivity(iconName: String, title: String, description: String, category: String, xpEarned: Int = 15) {
        viewModelScope.launch {
            repository.addActivity(
                com.example.data.model.ActivityEntity(
                    iconName = iconName,
                    title = title,
                    description = description,
                    category = category,
                    xpEarned = xpEarned,
                    timeAgo = "Just now"
                )
            )
        }
    }

    fun deleteActivity(id: Long) {
        viewModelScope.launch {
            repository.deleteActivity(id)
        }
    }

    fun unlockBadge(badgeId: String) {
        viewModelScope.launch {
            repository.unlockBadge(badgeId, "Today")
            logActivity("emoji_events", "Badge Unlocked!", "Congratulations on unlocking a new achievement badge!", "Achievements", 50)
        }
    }

    fun saveResume(resume: com.example.data.model.ResumeEntity) {
        viewModelScope.launch {
            repository.saveResume(resume)
            logActivity("work", "Resume Profile Updated", "Saved latest education, skills, and projects.", "Career", 25)
        }
    }

    fun submitQuizResult(subject: String, topic: String, score: Int, totalQuestions: Int, difficulty: String) {
        viewModelScope.launch {
            val pct = ((score.toDouble() / totalQuestions.coerceAtLeast(1)) * 100).toInt()
            val xpEarned = score * 10
            repository.recordQuizHistory(
                com.example.data.model.QuizHistoryEntity(
                    subject = subject,
                    topic = topic,
                    score = score,
                    totalQuestions = totalQuestions,
                    percentage = pct,
                    difficulty = difficulty,
                    xpEarned = xpEarned,
                    date = "Today"
                )
            )
            logActivity("quiz", "Quiz Completed: $subject", "Scored $score/$totalQuestions ($pct%) on $topic.", "Study", xpEarned)
        }
    }

    fun askDoubt(subject: String, question: String, difficulty: String, language: String = "English") {
        viewModelScope.launch {
            _isAiLoading.value = true
            val response = CampusAiService.askDoubt(subject, question, difficulty, language)
            _aiDoubtResult.value = response
            _isAiLoading.value = false
            logActivity("auto_stories", "AI Doubt Solved: $subject", "Queried concepts regarding $question in $language.", "Study", 10)
        }
    }

    fun auditResumeWithAi(resume: com.example.data.model.ResumeEntity) {
        viewModelScope.launch {
            _isAiLoading.value = true
            val response = CampusAiService.auditResume(resume)
            _aiResumeCritique.value = response
            _isAiLoading.value = false
        }
    }

    fun sendChatMessage(text: String) {
        if (text.isBlank()) return
        val userMsg = ChatMessage(sender = "user", message = text.trim())
        _chatMessages.value = _chatMessages.value + userMsg

        viewModelScope.launch {
            _isAiLoading.value = true
            val reply = CampusAiService.chatGeneral(text)
            val aiMsg = ChatMessage(sender = "ai", message = reply)
            _chatMessages.value = _chatMessages.value + aiMsg
            _isAiLoading.value = false
        }
    }

    fun clearChat() {
        _chatMessages.value = listOf(
            ChatMessage(
                sender = "ai",
                message = "Conversation cleared. How can I assist your campus routine now?"
            )
        )
    }


    fun reportCampusGrievance(
        title: String,
        category: String,
        location: String,
        description: String,
        photoEmoji: String,
        photoTag: String,
        department: String,
        priority: String
    ) {
        viewModelScope.launch {
            val user = currentUser.value
            val grievance = com.example.data.model.CampusGrievanceEntity(
                title = title,
                category = category,
                location = location,
                description = description,
                photoEmoji = photoEmoji,
                photoTag = photoTag,
                reportedByName = user?.name ?: "Alex Rivera",
                reportedByRoll = user?.studentId ?: "STU-2024-8842",
                reportedByPhone = user?.phone ?: "+91 9876543210",
                department = department,
                priority = priority,
                status = "PENDING"
            )
            repository.addGrievance(grievance)
            repository.addNotification(
                com.example.data.model.NotificationItemEntity(
                    title = "Campus Issue Ticket Raised: $title",
                    message = "Your grievance has been submitted to the $department HOD and Maintenance Cell.",
                    category = "ANNOUNCEMENT"
                )
            )
            logActivity("report_problem", "Campus Grievance Filed", "Reported $title ($category at $location).", "Campus", 25)
        }
    }

    fun updateGrievanceStatus(
        id: Long,
        status: String,
        remarks: String,
        hodName: String
    ) {
        viewModelScope.launch {
            val dateStr = if (status == "RESOLVED") "Today, Just now" else "In Progress"
            repository.resolveGrievance(id, status, remarks, hodName, dateStr)
            repository.addNotification(
                com.example.data.model.NotificationItemEntity(
                    title = "HOD Update: Issue ${if (status == "RESOLVED") "Resolved ✅" else "Under Action 🛠️"}",
                    message = "HOD $hodName remarked: '$remarks'",
                    category = "ANNOUNCEMENT"
                )
            )
            logActivity("check_circle", "Grievance Status Updated", "HOD marked issue #$id as $status.", "Admin", 30)
        }
    }

    fun resolveGrievance(id: Long, remarks: String, hodName: String) {
        updateGrievanceStatus(id, "RESOLVED", remarks, hodName)
    }

    fun deleteGrievance(id: Long) {
        viewModelScope.launch {
            repository.deleteGrievance(id)
        }
    }


    private val _selectedAcademicYear = MutableStateFlow("All")
    val selectedAcademicYear: StateFlow<String> = _selectedAcademicYear.asStateFlow()

    fun setSelectedAcademicYear(year: String) {
        _selectedAcademicYear.value = year
    }

    fun updateUserAcademicYear(newYear: String, newSemester: String) {
        viewModelScope.launch {
            val user = currentUser.value ?: return@launch
            val updated = user.copy(year = newYear, semester = newSemester)
            repository.insertUser(updated)
            _selectedAcademicYear.value = newYear
            logActivity("school", "Academic Year Updated", "Switched syllabus to $newYear ($newSemester).", "Study", 30)
        }
    }
}

