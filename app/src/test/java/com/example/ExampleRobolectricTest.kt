package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.AgeGroup
import com.example.data.AppDatabase
import com.example.data.ExerciseCategory
import com.example.data.FitnessCatalog
import com.example.data.FitnessLevel
import com.example.data.LevelingRepository
import com.example.data.ProgressionCatalog
import com.example.data.QuestStatus
import com.example.data.UserPreferencesStore
import com.example.data.VideoPlayerType
import com.example.data.VideoSectionCategory
import com.example.data.VideoVerificationStatus
import com.example.data.WellnessCatalog
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    private lateinit var context: Context
    private lateinit var db: AppDatabase
    private lateinit var repo: LevelingRepository

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repo = LevelingRepository(db, UserPreferencesStore(context))
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun `read app name string from context`() {
        val appName = context.getString(R.string.app_name)
        assertEquals("Real Life Leveling", appName)
    }

    @Test
    fun `catalogs contain all required categories and exercises`() {
        ExerciseCategory.entries.forEach { cat ->
            assertTrue("Category $cat should have exercises", FitnessCatalog.exercisesForCategory(cat).isNotEmpty())
        }
        assertTrue(FitnessCatalog.exercises.size >= 12)
        assertTrue(WellnessCatalog.glowUpLessons.size >= 25)
        assertTrue(WellnessCatalog.nutritionMeals.size >= 10)
        assertEquals(8, WellnessCatalog.mindGuides.size)
        assertEquals(7, ProgressionCatalog.levelTiers.size)
    }

    @Test
    fun `awakening quest completion postpone and json roundtrip work properly`() = runTest {
        repo.ensureSeeded()
        repo.completeAwakening("علي", AgeGroup.TEEN_13_17, FitnessLevel.BEGINNER)

        val before = repo.appSnapshotFlow.first()
        assertTrue(before.profile.awakeningDone)
        assertEquals("علي", before.profile.playerName)
        val xpBefore = before.profile.currentXp

        // Postponing a quest must NEVER deduct XP
        repo.postponeQuest("quest_read")
        val afterPostpone = repo.appSnapshotFlow.first()
        assertEquals(xpBefore, afterPostpone.profile.currentXp)
        assertEquals(
            QuestStatus.POSTPONED.name,
            afterPostpone.quests.first { it.id == "quest_read" }.statusName
        )

        // Completing a quest grants XP, coins, and updates Boss Challenge
        val grant = repo.completeQuest("quest_wake_up")
        assertNotNull(grant)
        val afterComplete = repo.appSnapshotFlow.first()
        assertTrue(afterComplete.profile.currentXp > xpBefore)
        assertEquals(1, afterComplete.profile.completedQuestsCount)
        assertEquals(1, afterComplete.bossChallenges.first { it.id == "boss_discipline_week" }.currentCount)

        // Video URL validation: YouTube valid vs NHS HTML page fallback
        val ytValidation = repo.validateVideoSource("https://www.youtube.com/watch?v=dQw4w9WgXcQ")
        assertTrue(ytValidation.embedAllowed)
        assertEquals(VideoPlayerType.YOUTUBE_EMBED, ytValidation.playerType)
        assertEquals(VideoVerificationStatus.VERIFIED, ytValidation.status)

        val nhsValidation = repo.validateVideoSource("https://www.nhs.uk/live-well/exercise/strength-and-flex-exercise-plan/")
        assertFalse(nhsValidation.embedAllowed)
        assertEquals(VideoPlayerType.INTERACTIVE_GUIDE_ONLY, nhsValidation.playerType)
        assertEquals(VideoVerificationStatus.EMBED_RESTRICTED, nhsValidation.status)

        // Save custom video and test JSON export/import roundtrip
        repo.saveVideoItem(null, "تمرين مخصص", VideoSectionCategory.FITNESS, "squat", "https://youtu.be/dQw4w9WgXcQ", "تجربة")
        val exported = repo.exportJsonBackup()
        repo.resetAllData()
        val resetSnap = repo.appSnapshotFlow.first()
        assertFalse(resetSnap.profile.awakeningDone)

        assertTrue(repo.importJsonBackup(exported))
        val restored = repo.appSnapshotFlow.first()
        assertTrue(restored.profile.awakeningDone)
        assertEquals("علي", restored.profile.playerName)
        assertTrue(restored.videos.any { it.title == "تمرين مخصص" })
    }
}
