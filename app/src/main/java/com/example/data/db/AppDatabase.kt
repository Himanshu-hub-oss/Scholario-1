package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.AssignmentEntity
import com.example.data.model.CampusEventEntity
import com.example.data.model.CampusGrievanceEntity
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
import com.example.data.model.ActivityEntity
import com.example.data.model.BadgeEntity
import com.example.data.model.QuizHistoryEntity
import com.example.data.model.ResumeEntity
import com.example.data.model.SettlementEntity
import com.example.data.model.StudyTaskEntity
import com.example.data.model.SubjectNoteEntity
import com.example.data.model.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        ExpenseEntity::class,
        ExpenseGroupEntity::class,
        SettlementEntity::class,
        StudyTaskEntity::class,
        ExamEntity::class,
        SubjectNoteEntity::class,
        AssignmentEntity::class,
        QuizQuestionEntity::class,
        CommunityPostEntity::class,
        CommunityGroupEntity::class,
        CampusEventEntity::class,
        LostFoundEntity::class,
        MarketplaceEntity::class,
        CareerEntity::class,
        NotificationItemEntity::class,
        ActivityEntity::class,
        BadgeEntity::class,
        QuizHistoryEntity::class,
        ResumeEntity::class,
        CampusGrievanceEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun studyDao(): StudyDao
    abstract fun communityDao(): CommunityDao
    abstract fun servicesDao(): ServicesDao
    abstract fun activityDao(): ActivityDao
    abstract fun gamificationDao(): GamificationDao
    abstract fun resumeDao(): ResumeDao
    abstract fun grievanceDao(): GrievanceDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "campusmate_plus.db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                DatabaseSeeder.seedInitialData(getInstance(context))
                            }
                        }

                        override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
                            super.onDestructiveMigration(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                DatabaseSeeder.seedInitialData(getInstance(context))
                            }
                        }
                    }).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
