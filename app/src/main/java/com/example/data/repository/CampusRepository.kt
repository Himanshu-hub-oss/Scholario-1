package com.example.data.repository

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
import kotlinx.coroutines.flow.Flow

class CampusRepository(private val db: AppDatabase) {

    val currentUser: Flow<UserEntity?> = db.userDao().getUserFlow("user_default")
    val allUsers: Flow<List<UserEntity>> = db.userDao().getAllUsers()
    suspend fun updateUser(user: UserEntity) = db.userDao().updateUser(user)
    suspend fun insertUser(user: UserEntity) = db.userDao().insertUser(user)


    val allExpenses: Flow<List<ExpenseEntity>> = db.expenseDao().getAllExpenses()
    val allGroups: Flow<List<ExpenseGroupEntity>> = db.expenseDao().getAllGroups()
    val allSettlements: Flow<List<SettlementEntity>> = db.expenseDao().getAllSettlements()
    suspend fun addExpense(expense: ExpenseEntity): Long = db.expenseDao().insertExpense(expense)
    suspend fun deleteExpense(expense: ExpenseEntity) = db.expenseDao().deleteExpense(expense)
    suspend fun addGroup(group: ExpenseGroupEntity) = db.expenseDao().insertGroup(group)
    suspend fun addSettlement(settlement: SettlementEntity) = db.expenseDao().insertSettlement(settlement)
    suspend fun markSettlementCompleted(id: Long) = db.expenseDao().markSettlementCompleted(id)


    val allTasks: Flow<List<StudyTaskEntity>> = db.studyDao().getAllTasks()
    val allExams: Flow<List<ExamEntity>> = db.studyDao().getAllExams()
    val allNotes: Flow<List<SubjectNoteEntity>> = db.studyDao().getAllNotes()
    val allAssignments: Flow<List<AssignmentEntity>> = db.studyDao().getAllAssignments()
    fun getQuizQuestions(difficulty: String = "ALL"): Flow<List<QuizQuestionEntity>> = db.studyDao().getQuizQuestions(difficulty)
    suspend fun addTask(task: StudyTaskEntity) = db.studyDao().insertTask(task)
    suspend fun updateTask(task: StudyTaskEntity) = db.studyDao().updateTask(task)
    suspend fun deleteTask(id: Long) = db.studyDao().deleteTaskById(id)
    suspend fun addExam(exam: ExamEntity) = db.studyDao().insertExam(exam)
    suspend fun addNote(note: SubjectNoteEntity) = db.studyDao().insertNote(note)
    suspend fun addAssignment(assignment: AssignmentEntity) = db.studyDao().insertAssignment(assignment)
    suspend fun updateAssignment(assignment: AssignmentEntity) = db.studyDao().updateAssignment(assignment)
    suspend fun addQuizQuestions(questions: List<QuizQuestionEntity>) = db.studyDao().insertQuizQuestions(questions)


    val activePosts: Flow<List<CommunityPostEntity>> = db.communityDao().getActivePosts()
    val reportedPosts: Flow<List<CommunityPostEntity>> = db.communityDao().getReportedPosts()
    val communityGroups: Flow<List<CommunityGroupEntity>> = db.communityDao().getAllGroups()
    val campusEvents: Flow<List<CampusEventEntity>> = db.communityDao().getAllEvents()
    suspend fun addPost(post: CommunityPostEntity) = db.communityDao().insertPost(post)
    suspend fun likePost(id: Long) = db.communityDao().likePost(id)
    suspend fun reportPost(id: Long, reason: String) = db.communityDao().reportPost(id, reason)
    suspend fun deletePost(id: Long) = db.communityDao().deletePost(id)
    suspend fun dismissPostReport(id: Long) = db.communityDao().dismissReport(id)
    suspend fun addGroup(group: CommunityGroupEntity) = db.communityDao().insertGroup(group)
    suspend fun addEvent(event: CampusEventEntity) = db.communityDao().insertEvent(event)
    suspend fun toggleEventRegistration(id: Long) = db.communityDao().toggleEventRegistration(id)


    val activeLostFound: Flow<List<LostFoundEntity>> = db.servicesDao().getActiveLostFound()
    val reportedLostFound: Flow<List<LostFoundEntity>> = db.servicesDao().getReportedLostFound()
    suspend fun addLostFound(item: LostFoundEntity) = db.servicesDao().insertLostFound(item)
    suspend fun reportLostFound(id: Long, reason: String) = db.servicesDao().reportLostFound(id, reason)
    suspend fun markLostFoundResolved(id: Long) = db.servicesDao().markLostFoundResolved(id)
    suspend fun deleteLostFound(id: Long) = db.servicesDao().deleteLostFound(id)

    val activeMarketplace: Flow<List<MarketplaceEntity>> = db.servicesDao().getActiveMarketplace()
    val reportedMarketplace: Flow<List<MarketplaceEntity>> = db.servicesDao().getReportedMarketplace()
    suspend fun addMarketplaceItem(item: MarketplaceEntity) = db.servicesDao().insertMarketplaceItem(item)
    suspend fun reportMarketplaceItem(id: Long) = db.servicesDao().reportMarketplaceItem(id)
    suspend fun deleteMarketplaceItem(id: Long) = db.servicesDao().deleteMarketplaceItem(id)

    val careerItems: Flow<List<CareerEntity>> = db.servicesDao().getAllCareerItems()
    suspend fun addCareerItem(item: CareerEntity) = db.servicesDao().insertCareerItem(item)

    val notifications: Flow<List<NotificationItemEntity>> = db.servicesDao().getAllNotifications()
    suspend fun addNotification(item: NotificationItemEntity) = db.servicesDao().insertNotification(item)
    suspend fun markNotificationRead(id: Long) = db.servicesDao().markNotificationRead(id)
    suspend fun markAllNotificationsRead() = db.servicesDao().markAllNotificationsRead()
    suspend fun deleteNotification(id: Long) = db.servicesDao().deleteNotification(id)


    val allActivities: Flow<List<com.example.data.model.ActivityEntity>> = db.activityDao().getAllActivities()
    suspend fun addActivity(activity: com.example.data.model.ActivityEntity) = db.activityDao().insertActivity(activity)
    suspend fun deleteActivity(id: Long) = db.activityDao().deleteActivity(id)


    val allBadges: Flow<List<com.example.data.model.BadgeEntity>> = db.gamificationDao().getAllBadges()
    suspend fun unlockBadge(badgeId: String, date: String) = db.gamificationDao().unlockBadge(badgeId, date)
    val quizHistory: Flow<List<com.example.data.model.QuizHistoryEntity>> = db.gamificationDao().getQuizHistory()
    suspend fun recordQuizHistory(history: com.example.data.model.QuizHistoryEntity) = db.gamificationDao().insertQuizHistory(history)


    val userResume: Flow<com.example.data.model.ResumeEntity?> = db.resumeDao().getResumeFlow("default_resume")
    suspend fun saveResume(resume: com.example.data.model.ResumeEntity) = db.resumeDao().saveResume(resume)


    val allGrievances: Flow<List<com.example.data.model.CampusGrievanceEntity>> = db.grievanceDao().getAllGrievances()
    fun getGrievancesByDept(dept: String): Flow<List<com.example.data.model.CampusGrievanceEntity>> = db.grievanceDao().getGrievancesByDepartment(dept)
    suspend fun addGrievance(grievance: com.example.data.model.CampusGrievanceEntity): Long = db.grievanceDao().insertGrievance(grievance)
    suspend fun updateGrievance(grievance: com.example.data.model.CampusGrievanceEntity) = db.grievanceDao().updateGrievance(grievance)
    suspend fun resolveGrievance(id: Long, status: String, remarks: String, hodName: String, date: String) =
        db.grievanceDao().resolveGrievance(id, status, remarks, hodName, date)
    suspend fun deleteGrievance(id: Long) = db.grievanceDao().deleteGrievance(id)
}
