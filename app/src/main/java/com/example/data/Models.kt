package com.example.data

enum class StatType(val titleAr: String, val codeEn: String) {
    STRENGTH("القوة", "STRENGTH"),
    STAMINA("التحمل", "STAMINA"),
    MOBILITY("المرونة والحركة", "MOBILITY"),
    DISCIPLINE("الانضباط", "DISCIPLINE"),
    FOCUS("التركيز", "FOCUS"),
    KNOWLEDGE("المعرفة", "KNOWLEDGE"),
    RECOVERY("التعافي", "RECOVERY")
}

enum class AgeGroup(val titleAr: String, val sleepRangeLabel: String, val minSleepHours: Float, val maxSleepHours: Float) {
    TEEN_13_17("مراهق (13–17 سنة)", "8 – 10 ساعات يوميًا", 8f, 10f),
    YOUNG_ADULT_18_25("شاب (18–25 سنة)", "7.5 – 9 ساعات يوميًا", 7.5f, 9f),
    ADULT_26_PLUS("بالغ (26+ سنة)", "7 – 9 ساعات يوميًا", 7f, 9f)
}

enum class FitnessLevel(val titleAr: String, val setMultiplier: Int, val repScale: Float) {
    BEGINNER("مبتدئ", 2, 0.75f),
    INTERMEDIATE("متوسط", 3, 1.0f),
    ADVANCED("متقدم", 4, 1.25f)
}

enum class QuestStatus(val titleAr: String) {
    PENDING("جاهزة"),
    IN_PROGRESS("قيد التنفيذ"),
    COMPLETED("مكتملة"),
    POSTPONED("مؤجلة للراحة")
}

enum class ExerciseCategory(val titleAr: String) {
    CHEST("الصدر"),
    BACK("الظهر"),
    SHOULDERS("الأكتاف"),
    ARMS("الذراعان"),
    ABS("البطن"),
    LEGS("الأرجل"),
    FLEXIBILITY("المرونة"),
    CARDIO("اللياقة القلبية"),
    WARMUP("الإحماء"),
    RECOVERY("الاستشفاء")
}

enum class MotionPattern {
    WALL_PUSHUP,
    INCLINE_PUSHUP,
    PUSHUP,
    SQUAT,
    REVERSE_LUNGE,
    GLUTE_BRIDGE,
    CALF_RAISE,
    PLANK,
    BIRD_DOG,
    JUMPING_JACKS,
    WALKING,
    STRETCHING,
    SHOULDER_CIRCLES,
    TRICEPS_DIP
}

enum class VideoPlayerType(val titleAr: String) {
    YOUTUBE_EMBED("YouTube Embed"),
    DIRECT_MP4("MP4 مباشر"),
    INTERACTIVE_GUIDE_ONLY("دليل حركي تفاعلي")
}

enum class VideoVerificationStatus(val titleAr: String) {
    VERIFIED("موثق ويعمل"),
    EMBED_RESTRICTED("مقيد التضمين — بديل مفعّل"),
    NEEDS_CHECK("بحاجة للتحقق")
}

enum class VideoSectionCategory(val titleAr: String) {
    FITNESS("أكاديمية التدريب"),
    GLOW_UP("مركز تطوير المظهر"),
    MIND("العقل والانضباط")
}

enum class GlowUpSection(val titleAr: String, val codeEn: String) {
    SKINCARE("العناية بالبشرة", "SKINCARE"),
    HAIRCARE("العناية بالشعر", "HAIRCARE"),
    HYGIENE("النظافة الشخصية", "PERSONAL HYGIENE"),
    STYLE("الأناقة والملابس", "STYLE")
}

enum class NutritionCategory(val titleAr: String) {
    PROTEIN("البروتين"),
    CARBS("الكربوهيدرات"),
    HEALTHY_FATS("الدهون الصحية"),
    FRUITS("الفواكه"),
    VEGETABLES("الخضروات"),
    BALANCED_MEALS("الوجبات المتوازنة"),
    WATER("الماء"),
    BUDGET_MEALS("وجبات اقتصادية")
}

enum class RewardCategory(val titleAr: String) {
    BACKGROUND("خلفيات"),
    THEME("ألوان واجهة"),
    TITLE("ألقاب"),
    FRAME("إطارات الملف"),
    EFFECT("مؤثرات بصرية")
}

data class LevelTier(
    val level: Int,
    val titleEn: String,
    val titleAr: String,
    val minXp: Int,
    val nextLevelXp: Int
)

data class ExerciseInfo(
    val id: String,
    val titleAr: String,
    val titleEn: String,
    val category: ExerciseCategory,
    val targetMusclesAr: String,
    val difficultyAr: String,
    val equipmentAr: String,
    val startingPositionAr: String,
    val stepsAr: List<String>,
    val breathingAr: String,
    val commonMistakesAr: List<String>,
    val mistakeCorrectionsAr: List<String>,
    val beginnerAlternativeAr: String,
    val safetyNoteAr: String,
    val defaultSets: Int,
    val defaultRepsOrSeconds: Int,
    val isTimedSeconds: Boolean,
    val restSeconds: Int,
    val statReward: StatType,
    val xpReward: Int,
    val motionPattern: MotionPattern
)

data class GlowUpLesson(
    val id: String,
    val section: GlowUpSection,
    val titleAr: String,
    val summaryAr: String,
    val stepsAr: List<String>,
    val safetyNoteAr: String,
    val checklistItems: List<String>,
    val xpPerTask: Int
)

data class NutritionMeal(
    val id: String,
    val category: NutritionCategory,
    val titleAr: String,
    val ingredientsAr: List<String>,
    val preparationAr: String,
    val benefitsAr: String,
    val alternativesAr: String
)

data class MindGuide(
    val id: String,
    val titleAr: String,
    val subtitleAr: String,
    val keyPointsAr: List<String>,
    val actionTipAr: String
)

data class AchievementDefinition(
    val id: String,
    val titleAr: String,
    val titleEn: String,
    val descriptionAr: String,
    val xpBonus: Int
)

data class RewardItemDefinition(
    val id: String,
    val titleAr: String,
    val descriptionAr: String,
    val category: RewardCategory,
    val costCoins: Int
)
