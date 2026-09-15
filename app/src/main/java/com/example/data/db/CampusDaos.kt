package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
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

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    fun getUserFlow(userId: String = "user_default"): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUser(userId: String = "user_default"): UserEntity?

    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)
}

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses ORDER BY timestamp DESC")
    fun getAllExpenses(): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE groupId = :groupId ORDER BY timestamp DESC")
    fun getExpensesByGroup(groupId: String): Flow<List<ExpenseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity): Long

    @Delete
    suspend fun deleteExpense(expense: ExpenseEntity)

    @Query("SELECT * FROM expense_groups")
    fun getAllGroups(): Flow<List<ExpenseGroupEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroup(group: ExpenseGroupEntity)

    @Query("SELECT * FROM settlements ORDER BY timestamp DESC")
    fun getAllSettlements(): Flow<List<SettlementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettlement(settlement: SettlementEntity)

    @Query("UPDATE settlements SET isCompleted = 1 WHERE id = :id")
    suspend fun markSettlementCompleted(id: Long)
}

@Dao
interface StudyDao {
    @Query("SELECT * FROM study_tasks ORDER BY isCompleted ASC, timestamp DESC")
    fun getAllTasks(): Flow<List<StudyTaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: StudyTaskEntity)

    @Update
    suspend fun updateTask(task: StudyTaskEntity)

    @Query("DELETE FROM study_tasks WHERE id = :id")
    suspend fun deleteTaskById(id: Long)

    @Query("SELECT * FROM exams ORDER BY date ASC")
    fun getAllExams(): Flow<List<ExamEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExam(exam: ExamEntity)

    @Query("SELECT * FROM subject_notes ORDER BY downloadsCount DESC")
    fun getAllNotes(): Flow<List<SubjectNoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: SubjectNoteEntity)

    @Query("SELECT * FROM assignments ORDER BY deadline ASC")
    fun getAllAssignments(): Flow<List<AssignmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssignment(assignment: AssignmentEntity)

    @Update
    suspend fun updateAssignment(assignment: AssignmentEntity)

    @Query("SELECT * FROM quiz_questions WHERE difficulty = :difficulty OR :difficulty = 'ALL'")
    fun getQuizQuestions(difficulty: String = "ALL"): Flow<List<QuizQuestionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizQuestions(questions: List<QuizQuestionEntity>)
}

@Dao
interface CommunityDao {
    @Query("SELECT * FROM community_posts WHERE isReported = 0 ORDER BY timestamp DESC")
    fun getActivePosts(): Flow<List<CommunityPostEntity>>

    @Query("SELECT * FROM community_posts WHERE isReported = 1 ORDER BY timestamp DESC")
    fun getReportedPosts(): Flow<List<CommunityPostEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: CommunityPostEntity)

    @Query("UPDATE community_posts SET likesCount = likesCount + 1, isLikedByMe = 1 WHERE id = :id")
    suspend fun likePost(id: Long)

    @Query("UPDATE community_posts SET isReported = 1, reportReason = :reason WHERE id = :id")
    suspend fun reportPost(id: Long, reason: String)

    @Query("DELETE FROM community_posts WHERE id = :id")
    suspend fun deletePost(id: Long)

    @Query("UPDATE community_posts SET isReported = 0, reportReason = NULL WHERE id = :id")
    suspend fun dismissReport(id: Long)

    @Query("SELECT * FROM community_groups")
    fun getAllGroups(): Flow<List<CommunityGroupEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroup(group: CommunityGroupEntity)

    @Query("SELECT * FROM campus_events ORDER BY date ASC")
    fun getAllEvents(): Flow<List<CampusEventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: CampusEventEntity)

    @Query("UPDATE campus_events SET isRegistered = CASE WHEN isRegistered = 1 THEN 0 ELSE 1 END WHERE id = :id")
    suspend fun toggleEventRegistration(id: Long)
}

@Dao
interface ServicesDao {
    @Query("SELECT * FROM lost_and_found WHERE isReported = 0 ORDER BY timestamp DESC")
    fun getActiveLostFound(): Flow<List<LostFoundEntity>>

    @Query("SELECT * FROM lost_and_found WHERE isReported = 1 ORDER BY timestamp DESC")
    fun getReportedLostFound(): Flow<List<LostFoundEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLostFound(item: LostFoundEntity)

    @Query("UPDATE lost_and_found SET isReported = 1, reportReason = :reason WHERE id = :id")
    suspend fun reportLostFound(id: Long, reason: String)

    @Query("UPDATE lost_and_found SET isResolved = 1 WHERE id = :id")
    suspend fun markLostFoundResolved(id: Long)

    @Query("DELETE FROM lost_and_found WHERE id = :id")
    suspend fun deleteLostFound(id: Long)

    @Query("SELECT * FROM marketplace_items WHERE isReported = 0 ORDER BY timestamp DESC")
    fun getActiveMarketplace(): Flow<List<MarketplaceEntity>>

    @Query("SELECT * FROM marketplace_items WHERE isReported = 1 ORDER BY timestamp DESC")
    fun getReportedMarketplace(): Flow<List<MarketplaceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMarketplaceItem(item: MarketplaceEntity)

    @Query("UPDATE marketplace_items SET isReported = 1 WHERE id = :id")
    suspend fun reportMarketplaceItem(id: Long)

    @Query("DELETE FROM marketplace_items WHERE id = :id")
    suspend fun deleteMarketplaceItem(id: Long)

    @Query("SELECT * FROM career_items ORDER BY id ASC")
    fun getAllCareerItems(): Flow<List<CareerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCareerItem(item: CareerEntity)

    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<NotificationItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationItemEntity)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markNotificationRead(id: Long)

    @Query("UPDATE notifications SET isRead = 1")
    suspend fun markAllNotificationsRead()

    @Query("DELETE FROM notifications WHERE id = :id")
    suspend fun deleteNotification(id: Long)
}

@Dao
interface ActivityDao {
    @Query("SELECT * FROM activities ORDER BY timestamp DESC")
    fun getAllActivities(): Flow<List<com.example.data.model.ActivityEntity>>

    @Query("SELECT * FROM activities WHERE category = :category ORDER BY timestamp DESC")
    fun getActivitiesByCategory(category: String): Flow<List<com.example.data.model.ActivityEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: com.example.data.model.ActivityEntity)

    @Query("DELETE FROM activities WHERE id = :id")
    suspend fun deleteActivity(id: Long)
}

@Dao
interface GamificationDao {
    @Query("SELECT * FROM badges ORDER BY isUnlocked DESC, id ASC")
    fun getAllBadges(): Flow<List<com.example.data.model.BadgeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBadges(badges: List<com.example.data.model.BadgeEntity>)

    @Query("UPDATE badges SET isUnlocked = 1, unlockedDate = :date WHERE id = :badgeId")
    suspend fun unlockBadge(badgeId: String, date: String)

    @Query("SELECT * FROM quiz_history ORDER BY timestamp DESC")
    fun getQuizHistory(): Flow<List<com.example.data.model.QuizHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizHistory(history: com.example.data.model.QuizHistoryEntity)
}

@Dao
interface ResumeDao {
    @Query("SELECT * FROM resumes WHERE id = :id LIMIT 1")
    fun getResumeFlow(id: String = "default_resume"): Flow<com.example.data.model.ResumeEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveResume(resume: com.example.data.model.ResumeEntity)
}

@Dao
interface GrievanceDao {
    @Query("SELECT * FROM campus_grievances ORDER BY timestamp DESC")
    fun getAllGrievances(): Flow<List<com.example.data.model.CampusGrievanceEntity>>

    @Query("SELECT * FROM campus_grievances WHERE department = :dept ORDER BY timestamp DESC")
    fun getGrievancesByDepartment(dept: String): Flow<List<com.example.data.model.CampusGrievanceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGrievance(grievance: com.example.data.model.CampusGrievanceEntity): Long

    @Update
    suspend fun updateGrievance(grievance: com.example.data.model.CampusGrievanceEntity)

    @Query("UPDATE campus_grievances SET status = :status, hodRemarks = :remarks, resolvedByHodName = :hodName, resolvedDate = :date WHERE id = :id")
    suspend fun resolveGrievance(id: Long, status: String, remarks: String, hodName: String, date: String)

    @Query("DELETE FROM campus_grievances WHERE id = :id")
    suspend fun deleteGrievance(id: Long)
}

