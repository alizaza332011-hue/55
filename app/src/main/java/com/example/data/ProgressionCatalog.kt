package com.example.data

object ProgressionCatalog {

    val levelTiers: List<LevelTier> = listOf(
        LevelTier(1, "AWAKENING", "الصحوة", 0, 200),
        LevelTier(2, "BEGINNER", "المبتدئ", 200, 500),
        LevelTier(3, "DISCIPLINED", "المنضبط", 500, 1000),
        LevelTier(4, "WARRIOR", "المحارب", 1000, 1800),
        LevelTier(5, "ELITE", "النخبة", 1800, 2900),
        LevelTier(6, "ADVANCED", "المتقدم", 2900, 4500),
        LevelTier(7, "MASTER", "الأستاذ", 4500, 10000)
    )

    fun tierForLevel(level: Int): LevelTier =
        levelTiers.find { it.level == level } ?: levelTiers.last()

    fun calculateLevelForXp(xp: Int): Int {
        var resolved = 1
        for (tier in levelTiers) {
            if (xp >= tier.minXp) resolved = tier.level
        }
        return resolved
    }

    fun defaultDailyQuests(): List<QuestEntity> = listOf(
        QuestEntity(
            id = "quest_wake_up",
            title = "الاستيقاظ في الموعد",
            description = "النهوض في الوقت المحدد دون تأجيل المنبه وبدء اليوم بنشاط.",
            category = "الروتين الصباحي",
            estimatedMinutes = 5,
            xpReward = 25,
            coinsReward = 15,
            statTypeName = StatType.DISCIPLINE.name
        ),
        QuestEntity(
            id = "quest_make_bed",
            title = "ترتيب السرير",
            description = "ترتيب السرير وتهوية الغرفة كأول انتصار يومي منظم.",
            category = "البيئة والانضباط",
            estimatedMinutes = 3,
            xpReward = 15,
            coinsReward = 10,
            statTypeName = StatType.DISCIPLINE.name
        ),
        QuestEntity(
            id = "quest_brush_teeth",
            title = "تنظيف الأسنان",
            description = "تنظيف الأسنان دقيقتين صباحًا ومساءً للحفاظ على صحة الفم.",
            category = "النظافة الشخصية",
            estimatedMinutes = 4,
            xpReward = 20,
            coinsReward = 10,
            statTypeName = StatType.RECOVERY.name
        ),
        QuestEntity(
            id = "quest_skincare",
            title = "روتين البشرة",
            description = "تنظيف لطيف وترطيب البشرة واستخدام واقي الشمس نهارًا.",
            category = "مركز المظهر",
            estimatedMinutes = 5,
            xpReward = 25,
            coinsReward = 15,
            statTypeName = StatType.RECOVERY.name
        ),
        QuestEntity(
            id = "quest_drink_water",
            title = "شرب الماء الكافي",
            description = "شرب 6–8 أكواب من الماء موزعة على اليوم لدعم الطاقة والتركيز.",
            category = "الصحة والتغذية",
            estimatedMinutes = 2,
            xpReward = 20,
            coinsReward = 15,
            statTypeName = StatType.STAMINA.name
        ),
        QuestEntity(
            id = "quest_workout",
            title = "نشاط رياضي منظم",
            description = "إكمال جلسة تمرين واحدة من أكاديمية التدريب أو مشي نشط 15 دقيقة.",
            category = "اللياقة البدنية",
            estimatedMinutes = 20,
            xpReward = 45,
            coinsReward = 30,
            statTypeName = StatType.STRENGTH.name
        ),
        QuestEntity(
            id = "quest_study",
            title = "الدراسة أو العمل العميق",
            description = "إنجاز جلسة تركيز واحدة (Pomodoro) بدون مشتتات الهاتف.",
            category = "العقل والتركيز",
            estimatedMinutes = 25,
            xpReward = 40,
            coinsReward = 25,
            statTypeName = StatType.FOCUS.name
        ),
        QuestEntity(
            id = "quest_new_skill",
            title = "تعلم مهارة جديدة",
            description = "تخصيص 15 دقيقة للتدرب العملي على مهارة لغوية أو تقنية أو إبداعية.",
            category = "المعرفة والنمو",
            estimatedMinutes = 15,
            xpReward = 35,
            coinsReward = 20,
            statTypeName = StatType.KNOWLEDGE.name
        ),
        QuestEntity(
            id = "quest_read",
            title = "القراءة اليومية",
            description = "قراءة 10 صفحات من كتاب مفيد مع تدوين فكرة رئيسية واحدة.",
            category = "المعرفة",
            estimatedMinutes = 15,
            xpReward = 30,
            coinsReward = 20,
            statTypeName = StatType.KNOWLEDGE.name
        ),
        QuestEntity(
            id = "quest_sleep_prep",
            title = "الاستعداد للنوم الهادئ",
            description = "إغلاق الشاشات قبل النوم بنصف ساعة وتجهيز احتياجات الغد.",
            category = "نظام النوم",
            estimatedMinutes = 15,
            xpReward = 30,
            coinsReward = 20,
            statTypeName = StatType.RECOVERY.name
        )
    )

    fun defaultBossChallenges(): List<BossChallengeEntity> = listOf(
        BossChallengeEntity(
            id = "boss_discipline_week",
            title = "DISCIPLINE CHALLENGE — تحدي الأسبوع",
            description = "أكمل 12 مهمة صحية متوازنة خلال هذا الأسبوع دون الضغط المفرط على نفسك.",
            targetCount = 12,
            currentCount = 0,
            xpReward = 150,
            coinsReward = 120,
            joined = true,
            completed = false
        ),
        BossChallengeEntity(
            id = "boss_recovery_week",
            title = "RECOVERY GUARDIAN — حارس التعافي",
            description = "سجل نومك الصحي 5 ليالٍ وأكمل 5 مهام عناية ذاتية أو إطالة.",
            targetCount = 5,
            currentCount = 0,
            xpReward = 120,
            coinsReward = 90,
            joined = false,
            completed = false
        )
    )

    val achievements: List<AchievementDefinition> = listOf(
        AchievementDefinition("ach_awakened", "بداية الصحوة", "FIRST AWAKENING", "أنهيت شاشة الصحوة وبدأت رحلة المستوى الأول.", 25),
        AchievementDefinition("ach_first_quest", "الخطوة الأولى", "QUEST INITIATE", "أكملت أول مهمة يومية بنجاح.", 30),
        AchievementDefinition("ach_five_quests", "روح المثابرة", "QUEST HUNTER", "أكملت 5 مهام يومية متكاملة.", 50),
        AchievementDefinition("ach_ten_quests", "سيد العادات", "HABIT ARCHITECT", "أكملت 10 مهام في مسيرتك.", 80),
        AchievementDefinition("ach_first_workout", "محارب الأكاديمية", "ACADEMY ROOKIE", "أنهيت أول جلسة تدريب تفاعلية في الأكاديمية.", 50),
        AchievementDefinition("ach_sleep_tracker", "حارس النوم", "SLEEP KEEPER", "سجلت أول سجل نوم لحماية طاقتك.", 35),
        AchievementDefinition("ach_focus_master", "عقل حديدي", "DEEP FOCUS", "أكملت جلسة تركيز بومودورو كاملة.", 45),
        AchievementDefinition("ach_glow_care", "العناية الواعية", "SELF CARE", "أنجزت 5 خطوات في مركز تطوير المظهر.", 40),
        AchievementDefinition("ach_level_3", "رتبة الانضباط", "RANK DISCIPLINED", "وصلت إلى المستوى 3 (DISCIPLINED).", 100),
        AchievementDefinition("ach_boss_clear", "قاهر التحدي", "BOSS CHALLENGER", "أكملت تحدي الأسبوع الصحي بنجاح.", 120)
    )

    val rewardItems: List<RewardItemDefinition> = listOf(
        // Themes
        RewardItemDefinition("theme_electric_blue", "الأزرق الكهربائي", "ثيم النظام الافتراضي بتوهج السيان الكهربائي", RewardCategory.THEME, 0),
        RewardItemDefinition("theme_shadow_violet", "البنفسجي الملكي", "واجهة هولوغرامية بلمسات البنفسجي الداكن", RewardCategory.THEME, 80),
        RewardItemDefinition("theme_solar_gold", "الوهج الذهبي", "توهج ذهبي فاخر لأصحاب الانضباط العالي", RewardCategory.THEME, 140),
        RewardItemDefinition("theme_emerald_matrix", "الزمرد الرقمي", "تدرجات خضراء مستقبلية للتوازن والطاقة", RewardCategory.THEME, 120),
        // Backgrounds
        RewardItemDefinition("bg_obsidian_grid", "شبكة الأوبسيديان", "الخلفية الشبكية الداكنة الأساسية", RewardCategory.BACKGROUND, 0),
        RewardItemDefinition("bg_nebula_pulse", "سديم الهولوغرام", "خلفية بوهج بنفسجي وأزرق عميق", RewardCategory.BACKGROUND, 90),
        RewardItemDefinition("bg_cyber_horizon", "الأفق المستقبلي", "تدرج ليلي سينمائي بخطوط الطاقة", RewardCategory.BACKGROUND, 130),
        // Frames
        RewardItemDefinition("frame_cyber_cyan", "إطار السيان الأساسي", "إطار بطاقة اللاعب الكلاسيكي", RewardCategory.FRAME, 0),
        RewardItemDefinition("frame_royal_prism", "إطار المنشور الملكي", "إطار مزدوج بتدرج أزرق وبنفسجي", RewardCategory.FRAME, 75),
        RewardItemDefinition("frame_master_gold", "إطار الإتقان الذهبي", "إطار ذهبي متوهج للمستويات العليا", RewardCategory.FRAME, 160),
        // Titles
        RewardItemDefinition("title_awakened", "المستيقظ (AWAKENED)", "لقب بداية الرحلة من المستوى الأول", RewardCategory.TITLE, 0),
        RewardItemDefinition("title_shadow_walker", "صانع العادات", "لقب للالتزام بالروتين اليومي", RewardCategory.TITLE, 60),
        RewardItemDefinition("title_iron_mind", "صاحب العقل الهادئ", "لقب لإتقان التركيز والنوم الصحي", RewardCategory.TITLE, 100),
        RewardItemDefinition("title_true_leveler", "بطل التطوير الواقعي", "لقب التوازن في اللياقة والمعرفة والعناية", RewardCategory.TITLE, 180),
        // Effects
        RewardItemDefinition("effect_cyan_pulse", "نبض السيان القياسي", "توهج أزرق ناعم عند إتمام المهام", RewardCategory.EFFECT, 0),
        RewardItemDefinition("effect_hologram_burst", "انفجار الهولوغرام", "مؤثر إشعاعي متدرج عند الإنجاز", RewardCategory.EFFECT, 110)
    )

    fun defaultVideoLibrary(): List<VideoItemEntity> = listOf(
        VideoItemEntity(
            id = "vid_nhs_wall_pushup",
            title = "الضغط على الحائط — مرجع NHS Strength & Flex",
            categoryName = VideoSectionCategory.FITNESS.name,
            linkedContentId = "wall_pushup",
            sourceUrl = "https://www.nhs.uk/live-well/exercise/strength-and-flex-exercise-plan/",
            playerTypeName = VideoPlayerType.INTERACTIVE_GUIDE_ONLY.name,
            thumbnailLabel = "Wall Push-up • الصدر",
            description = "المصدر المرجعي من NHS يصف التمرين على صفحة ويب لا تسمح بالتضمين المباشر كـ iframe، لذلك تم تفعيل الدليل الحركي التفاعلي المدمج داخل التطبيق.",
            verificationStatusName = VideoVerificationStatus.EMBED_RESTRICTED.name,
            embedAllowed = false
        ),
        VideoItemEntity(
            id = "vid_nhs_squat",
            title = "القرفصاء الآمن — مرجع NHS Strength & Flex",
            categoryName = VideoSectionCategory.FITNESS.name,
            linkedContentId = "squat",
            sourceUrl = "https://www.nhs.uk/live-well/exercise/strength-and-flex-exercise-plan/",
            playerTypeName = VideoPlayerType.INTERACTIVE_GUIDE_ONLY.name,
            thumbnailLabel = "Squat • الأرجل",
            description = "يعرض الدليل الحركي التفاعلي مسار الركبتين والوركين وخطوات التنفس داخل التطبيق دون إخراجك لموقع خارجي.",
            verificationStatusName = VideoVerificationStatus.EMBED_RESTRICTED.name,
            embedAllowed = false
        ),
        VideoItemEntity(
            id = "vid_nhs_plank",
            title = "البلانك وثبات الجذع — دليل الحركة المدمج",
            categoryName = VideoSectionCategory.FITNESS.name,
            linkedContentId = "plank",
            sourceUrl = "https://www.nhs.uk/live-well/exercise/strength-and-flex-exercise-plan/",
            playerTypeName = VideoPlayerType.INTERACTIVE_GUIDE_ONLY.name,
            thumbnailLabel = "Plank • البطن",
            description = "رسم توضيحي متحرك يحاكي استقامة العمود الفقري وتوزيع الثقل على الساعدين.",
            verificationStatusName = VideoVerificationStatus.EMBED_RESTRICTED.name,
            embedAllowed = false
        ),
        VideoItemEntity(
            id = "vid_glow_skincare_guide",
            title = "أساسيات روتين البشرة الصباحي والمسائي",
            categoryName = VideoSectionCategory.GLOW_UP.name,
            linkedContentId = "skin_morning",
            sourceUrl = "",
            playerTypeName = VideoPlayerType.INTERACTIVE_GUIDE_ONLY.name,
            thumbnailLabel = "SKINCARE • دليل تفاعلي",
            description = "خطوات مصورة تفاعلية لتنظيف وترطيب البشرة وحمايتها بواقي الشمس بأمان.",
            verificationStatusName = VideoVerificationStatus.EMBED_RESTRICTED.name,
            embedAllowed = false
        ),
        VideoItemEntity(
            id = "vid_mind_pomodoro_guide",
            title = "دليل تقنية بومودورو والتركيز العميق",
            categoryName = VideoSectionCategory.MIND.name,
            linkedContentId = "mind_focus",
            sourceUrl = "",
            playerTypeName = VideoPlayerType.INTERACTIVE_GUIDE_ONLY.name,
            thumbnailLabel = "DEEP FOCUS • دليل تفاعلي",
            description = "شرح مرئي لدورة 25 دقيقة تركيز + 5 دقائق راحة لبناء الانضباط العقلي.",
            verificationStatusName = VideoVerificationStatus.EMBED_RESTRICTED.name,
            embedAllowed = false
        )
    )
}
