package com.example.data

object FitnessCatalog {
    val exercises: List<ExerciseInfo> = listOf(
        ExerciseInfo(
            id = "wall_pushup",
            titleAr = "تمرين الضغط على الحائط",
            titleEn = "Wall Push-up",
            category = ExerciseCategory.CHEST,
            targetMusclesAr = "الصدر الأمامي، الأكتاف الأمامية، العضلة ثلاثية الرؤوس (الترايسبس)",
            difficultyAr = "مبتدئ",
            equipmentAr = "حائط ثابت فقط — بدون أدوات",
            startingPositionAr = "قف مواجهًا للحائط على بعد ذراع واحدة، وضع راحتي يديك على الحائط بعرض أوسع قليلًا من الكتفين وعلى مستوى الصدر.",
            stepsAr = listOf(
                "شد عضلات البطن وحافظ على استقامة الظهر والرقبة في خط واحد.",
                "اثنِ المرفقين ببطء لتقريب الصدر نحو الحائط بزاوية 45 درجة مع الجسم.",
                "توقف ثانية واحدة قبل ملامسة الحائط مع بقاء الكعبين على الأرض.",
                "ادفع الحائط برفق للعودة إلى وضعية البداية مع استقامة الذراعين دون قفل المرفقين."
            ),
            breathingAr = "استنشق الهواء أثناء النزول نحو الحائط، وأخرج الزفير بانتظام أثناء الدفع للخلف.",
            commonMistakesAr = listOf(
                "فتح المرفقين للخارج بزاوية 90 درجة مما يضغط على مفصل الكتف.",
                "تقوس أسفل الظهر أو ترهل البطن أثناء الحركة."
            ),
            mistakeCorrectionsAr = listOf(
                "أبقِ المرفقين قريبين من الجذع بزاوية مريحة حوالي 45 درجة.",
                "شد عضلات الجذع والأرداف قليلًا للحفاظ على استقامة العمود الفقري."
            ),
            beginnerAlternativeAr = "اقترب خطوة صغيرة من الحائط لتقليل المقاومة وعدد التكرارات إلى 6–8 مرات.",
            safetyNoteAr = "توقف فورًا إذا شعرت بألم حاد في الكتف أو الرسغ؛ يُنفذ التمرين بسلاسة دون اندفاع.",
            defaultSets = 3,
            defaultRepsOrSeconds = 12,
            isTimedSeconds = false,
            restSeconds = 45,
            statReward = StatType.STRENGTH,
            xpReward = 30,
            motionPattern = MotionPattern.WALL_PUSHUP
        ),
        ExerciseInfo(
            id = "incline_pushup",
            titleAr = "تمرين الضغط المائل",
            titleEn = "Incline Push-up",
            category = ExerciseCategory.CHEST,
            targetMusclesAr = "الصدر السفلي والأوسط، الأكتاف، الترايسبس، عضلات الجذع",
            difficultyAr = "مبتدئ إلى متوسط",
            equipmentAr = "طاولة ثابتة، مقعد قوي، أو حافة سرير متينة",
            startingPositionAr = "ضع يديك على حافة السطح المرتفع بعرض الكتفين، وارجع بقدميك للخلف حتى يشكل جسمك خطًا مستقيمًا مائلًا.",
            stepsAr = listOf(
                "تأكد من ثبات السطح وعدم انزلاقه قبل البدء.",
                "انزل بصدرك ببطء نحو الحافة مع إبقاء الرأس على استقامة الظهر.",
                "اثبت لحظة عند اقتراب الصدر من السطح.",
                "ادفع جسمك للأعلى بقوة متوازنة حتى تعود لوضعية البداية."
            ),
            breathingAr = "شهيق منتظم أثناء النزول، وزفير سلس أثناء الدفع للأعلى.",
            commonMistakesAr = listOf(
                "هبوط الوركين للأسفل قبل الصدر.",
                "النزول السريع بدون تحكم عضلي."
            ),
            mistakeCorrectionsAr = listOf(
                "تخيّل جسمك لوحًا مستقيمًا من الرأس حتى الكعبين.",
                "عد ثانيتين أثناء النزول وثانية أثناء الصعود."
            ),
            beginnerAlternativeAr = "استخدم سطحًا أعلى مثل طاولة مرتفعة أو عد لتمرين الضغط على الحائط.",
            safetyNoteAr = "تجنب استخدام كراسي خفيفة أو قابلة للانزلاق، ولا تضغط رغم وجود ألم في المعصم.",
            defaultSets = 3,
            defaultRepsOrSeconds = 10,
            isTimedSeconds = false,
            restSeconds = 50,
            statReward = StatType.STRENGTH,
            xpReward = 35,
            motionPattern = MotionPattern.INCLINE_PUSHUP
        ),
        ExerciseInfo(
            id = "pushup",
            titleAr = "تمرين الضغط الكامل",
            titleEn = "Push-up",
            category = ExerciseCategory.CHEST,
            targetMusclesAr = "الصدر، الترايسبس، الأكتاف الأمامية، عضلات البطن العميقة",
            difficultyAr = "متوسط",
            equipmentAr = "حصيرة تمرين اختيارية — وزن الجسم فقط",
            startingPositionAr = "اتخذ وضعية البلانك المرتفع مع وضع اليدين تحت الكتفين أو أوسع قليلًا، والقدمين متقاربتين أو بعرض الحوض.",
            stepsAr = listOf(
                "ثبت النظر للأسفل قليلًا للأمام للحفاظ على استقامة الرقبة.",
                "انزل بالجسم كقطعة واحدة حتى يقترب الصدر من الأرض.",
                "حافظ على المرفقين بزاوية 45 درجة نحو الخلف.",
                "ادفع الأرض بقوة متساوية عبر راحتي اليدين للعودة للأعلى."
            ),
            breathingAr = "شهيق أثناء النزول، وزفير واضح عند الدفع للأعلى.",
            commonMistakesAr = listOf(
                "رفع الوركين للأعلى أو هبوطهما للأسفل.",
                "نصف مدى حركة دون ثني كافٍ للمرفقين."
            ),
            mistakeCorrectionsAr = listOf(
                "شغّل عضلات البطن والأرداف طوال التكرار.",
                "اخفض الصدر بمقدار مريح ومتحكم به ثم اصعد بسلاسة."
            ),
            beginnerAlternativeAr = "نفّذ الضغط على الركبتين أو الضغط المائل على سطح مرتفع.",
            safetyNoteAr = "أدفئ المعصمين والأكتاف قبل البدء، وتوقف إذا شعرت بوخز في المفصل.",
            defaultSets = 3,
            defaultRepsOrSeconds = 10,
            isTimedSeconds = false,
            restSeconds = 60,
            statReward = StatType.STRENGTH,
            xpReward = 45,
            motionPattern = MotionPattern.PUSHUP
        ),
        ExerciseInfo(
            id = "squat",
            titleAr = "تمرين القرفصاء",
            titleEn = "Squat",
            category = ExerciseCategory.LEGS,
            targetMusclesAr = "الفخذ الأمامي، عضلات الأرداف، أوتار الركبة، الجذع",
            difficultyAr = "مبتدئ",
            equipmentAr = "بدون أدوات",
            startingPositionAr = "قف مع مباعدة القدمين بعرض الكتفين وتوجيه أصابع القدم للخارج قليلًا، والصدر مرفوع.",
            stepsAr = listOf(
                "ابدأ بدفع الوركين للخلف كما لو كنت تجلس على كرسي.",
                "اثنِ الركبتين بالتوازي مع اتجاه أصابع القدمين.",
                "انزل حتى يصبح الفخذان موازيين للأرض تقريبًا أو حسب مرونتك المريحة.",
                "ادفع الأرض بكامل القدم واصعد مع شد عضلات الأرداف في الأعلى."
            ),
            breathingAr = "شهيق أثناء النزول للأسفل، وزفير أثناء الصعود.",
            commonMistakesAr = listOf(
                "انحناء الركبتين للداخل نحو بعضهما.",
                "رفع الكعبين عن الأرض أو تقويس الظهر."
            ),
            mistakeCorrectionsAr = listOf(
                "ادفع الركبتين برفق للخارج ليتبعان خط الأصابع.",
                "حافظ على ثبات الكعبين على الأرض وانظر للأمام."
            ),
            beginnerAlternativeAr = "اجلس برفق على حافة كرسي ثابت ثم قف (Box/Chair Squat) لبناء التوازن.",
            safetyNoteAr = "لا تجبر جسمك على عمق يسبب ألمًا في الركبة أو أسفل الظهر.",
            defaultSets = 3,
            defaultRepsOrSeconds = 15,
            isTimedSeconds = false,
            restSeconds = 50,
            statReward = StatType.STRENGTH,
            xpReward = 40,
            motionPattern = MotionPattern.SQUAT
        ),
        ExerciseInfo(
            id = "reverse_lunge",
            titleAr = "تمرين الطعن الخلفي",
            titleEn = "Reverse Lunge",
            category = ExerciseCategory.LEGS,
            targetMusclesAr = "الأرداف، الفخذ الأمامي والخلفي، عضلات التوازن",
            difficultyAr = "مبتدئ إلى متوسط",
            equipmentAr = "بدون أدوات (يمكن الاستعانة بكرسي للتوازن)",
            startingPositionAr = "قف باستقامة مع مباعدة القدمين بعرض الوركين، واليدين على الخصر أو أمام الصدر.",
            stepsAr = listOf(
                "تراجع خطوة مريحة للخلف بالقدم اليمنى على مشط القدم.",
                "انزل بالوركين للأسفل حتى تقترب زاوية الركبتين من 90 درجة.",
                "حافظ على بقاء الجذع مستقيمًا ووزنك موزعًا بثبات على القدم الأمامية.",
                "ادفع بكعب القدم الأمامية للعودة لوضع الوقوف، ثم بدّل الساق."
            ),
            breathingAr = "شهيق أثناء الخطوة والنزول، وزفير أثناء العودة للوقوف.",
            commonMistakesAr = listOf(
                "ضرب الركبة الخلفية بقوة على الأرض.",
                "ضيق المسافة الجانبية بين القدمين مما يضعف التوازن."
            ),
            mistakeCorrectionsAr = listOf(
                "انزل ببطء وتوقف قبل ملامسة الأرض بسنتيمترين.",
                "حافظ على مسافة عرض الحوض بين الساقين كما لو كنت على مسارين متوازيين."
            ),
            beginnerAlternativeAr = "امسك بظهر كرسي أو الحائط للتوازن وقلل مدى النزول.",
            safetyNoteAr = "راقب استقرار الركبة الأمامية فوق الكاحل وتجنب ميلانها للداخل.",
            defaultSets = 3,
            defaultRepsOrSeconds = 10,
            isTimedSeconds = false,
            restSeconds = 50,
            statReward = StatType.MOBILITY,
            xpReward = 40,
            motionPattern = MotionPattern.REVERSE_LUNGE
        ),
        ExerciseInfo(
            id = "glute_bridge",
            titleAr = "تمرين جسر الأرداف",
            titleEn = "Glute Bridge",
            category = ExerciseCategory.LEGS,
            targetMusclesAr = "عضلات الأرداف، أوتار الركبة، أسفل الظهر الداعم",
            difficultyAr = "مبتدئ",
            equipmentAr = "حصيرة تمرين أو سجادة مريحة",
            startingPositionAr = "استلقِ على ظهرك مع ثني الركبتين وتثبيت القدمين على الأرض بعرض الوركين، والذراعان بجانبك.",
            stepsAr = listOf(
                "اضغط برفق أسفل الظهر نحو الأرض قبل الرفع.",
                "ادفع الأرض بالكعبين وارفع الوركين للأعلى حتى يستقيم الخط من الكتفين للركبتين.",
                "اثبت في الأعلى لمدة ثانيتين مع شد عضلات الأرداف.",
                "أنزل الوركين ببطء إلى الأرض دون استعجال."
            ),
            breathingAr = "زفير أثناء رفع الوركين للأعلى، وشهيق عند النزول.",
            commonMistakesAr = listOf(
                "المبالغة في رفع الحوض وتقويس أسفل الظهر.",
                "وضع القدمين بعيدًا جدًا أو قريبًا جدًا من الجسم."
            ),
            mistakeCorrectionsAr = listOf(
                "توقف عند استقامة الفخذين مع الجذع فقط.",
                "اضبط القدمين بحيث تكون الساقان عموديتين تقريبًا في أعلى الحركة."
            ),
            beginnerAlternativeAr = "قلل ارتفاع الرفع قليلًا وزد فترة الراحة بين المجموعات.",
            safetyNoteAr = "تمرين لطيف جدًا على المفاصل؛ تجنب الضغط على الرقبة وحافظ على ارتخاء الكتفين.",
            defaultSets = 3,
            defaultRepsOrSeconds = 15,
            isTimedSeconds = false,
            restSeconds = 40,
            statReward = StatType.STRENGTH,
            xpReward = 35,
            motionPattern = MotionPattern.GLUTE_BRIDGE
        ),
        ExerciseInfo(
            id = "calf_raises",
            titleAr = "تمرين رفع السمانة",
            titleEn = "Calf Raises",
            category = ExerciseCategory.LEGS,
            targetMusclesAr = "عضلات السمانة (بطة الساق)، أوتار الكاحل",
            difficultyAr = "مبتدئ",
            equipmentAr = "حائط أو كرسي للتوازن",
            startingPositionAr = "قف مستقيمًا مع توجيه القدمين للأمام، ويمكنك لمس الحائط بأطراف أصابعك للتوازن.",
            stepsAr = listOf(
                "ارفع كعبيك ببطء عن الأرض بالارتكاز على مقدمة القدمين.",
                "اصعد لأعلى نقطة مريحة وحافظ على ثبات الكاحل.",
                "اثبت في الأعلى لمدة ثانية كاملة.",
                "أنزل الكعبين ببطء وتحكم حتى يلامسا الأرض."
            ),
            breathingAr = "زفير عند الرفع للأعلى، وشهيق عند النزول البطيء.",
            commonMistakesAr = listOf(
                "القفز أو الارتداد السريع بدون تحكم.",
                "انحناء الكاحل للخارج."
            ),
            mistakeCorrectionsAr = listOf(
                "تحرك بإيقاع بطيء: ثانيتان صعودًا وثانيتان نزولًا.",
                "وزّع الضغط على قاعدة الإبهام وبقية الأصابع بالتساوي."
            ),
            beginnerAlternativeAr = "نفذه مع سند اليدين على الحائط أو أثناء الجلوس على كرسي.",
            safetyNoteAr = "تجنب التمدد المفرط إذا كان لديك ألم في وتر أخيل.",
            defaultSets = 3,
            defaultRepsOrSeconds = 15,
            isTimedSeconds = false,
            restSeconds = 35,
            statReward = StatType.STAMINA,
            xpReward = 25,
            motionPattern = MotionPattern.CALF_RAISE
        ),
        ExerciseInfo(
            id = "plank",
            titleAr = "تمرين البلانك",
            titleEn = "Plank",
            category = ExerciseCategory.ABS,
            targetMusclesAr = "عضلات البطن العميقة، أسفل الظهر، الكتفان، الأرداف",
            difficultyAr = "مبتدئ إلى متوسط",
            equipmentAr = "حصيرة تمرين",
            startingPositionAr = "ارتكز على الساعدين وأطراف أصابع القدمين مع وضع المرفقين تحت الكتفين مباشرة.",
            stepsAr = listOf(
                "ارفع الجسم ليشكل خطًا مستقيمًا من الرأس إلى الكعبين.",
                "انظر نحو الأرض بين يديك لإبقاء الرقبة مسترخية.",
                "شد البطن بلطف كأنك تستعد لحماية المعدة وتنفس بانتظام.",
                "حافظ على الثبات للمدة المحددة ثم انزل بهدوء."
            ),
            breathingAr = "لا تحبس أنفاسك؛ تنفس ببطء وهدوء عبر الأنف والفم طوال الثبات.",
            commonMistakesAr = listOf(
                "هبوط أسفل الظهر وتقوسه.",
                "رفع الوركين عاليًا على شكل هرم."
            ),
            mistakeCorrectionsAr = listOf(
                "شد البطن والأرداف معًا ودفع الساعدين برفق للأرض.",
                "أنهِ المجموعة قبل أن يبدأ ظهرك بالتقوس للحفاظ على جودة الأداء."
            ),
            beginnerAlternativeAr = "ارتكز على الركبتين بدلًا من أصابع القدمين أو نفذ البلانك على سطح مرتفع.",
            safetyNoteAr = "الجودة أهم من المدة الطويلة؛ أوقف العداد فور اهتزاز أسفل الظهر.",
            defaultSets = 3,
            defaultRepsOrSeconds = 30,
            isTimedSeconds = true,
            restSeconds = 45,
            statReward = StatType.DISCIPLINE,
            xpReward = 40,
            motionPattern = MotionPattern.PLANK
        ),
        ExerciseInfo(
            id = "bird_dog",
            titleAr = "تمرين التوازن المتقاطع (Bird Dog)",
            titleEn = "Bird Dog",
            category = ExerciseCategory.BACK,
            targetMusclesAr = "عضلات الظهر المستقيمة، البطن العميقة، الأرداف، استقرار الكتف",
            difficultyAr = "مبتدئ",
            equipmentAr = "حصيرة تمرين",
            startingPositionAr = "ارتكز على اليدين والركبتين بحيث تكون اليدان تحت الكتفين والركبتان تحت الوركين، والظهر مستويًا.",
            stepsAr = listOf(
                "شد عضلات البطن بهدوء لمنع تمايل الحوض.",
                "مد الذراع اليمنى للأمام والساق اليسرى للخلف في نفس الوقت بمستوى الظهر.",
                "اثبت لمدة ثانيتين مع إبقاء الوركين متوازيين مع الأرض.",
                "عد ببطء لوضعية البداية ثم بدّل بين الذراع اليسرى والساق اليمنى."
            ),
            breathingAr = "زفير أثناء مد الذراع والساق، وشهيق عند العودة لوضعية البداية.",
            commonMistakesAr = listOf(
                "رفع الساق أعلى من مستوى الظهر مما يقوس الفقرات القطنية.",
                "استعجال الحركة وفقدان التوازن."
            ),
            mistakeCorrectionsAr = listOf(
                "تخيّل أن هناك كوب ماء متزن على أسفل ظهرك.",
                "مدّ للخلف والأمام وليس للأعلى."
            ),
            beginnerAlternativeAr = "ارفع الساق فقط أو الذراع فقط في البداية قبل الجمع بينهما.",
            safetyNoteAr = "ضع منشفة تحت الركبتين إذا شعرت بعدم ارتياح على الأرض الصلبة.",
            defaultSets = 3,
            defaultRepsOrSeconds = 10,
            isTimedSeconds = false,
            restSeconds = 40,
            statReward = StatType.MOBILITY,
            xpReward = 35,
            motionPattern = MotionPattern.BIRD_DOG
        ),
        ExerciseInfo(
            id = "jumping_jacks",
            titleAr = "تمرين القفز المتفتح",
            titleEn = "Jumping Jacks",
            category = ExerciseCategory.CARDIO,
            targetMusclesAr = "القلب والأوعية الدموية، الساقان، الأكتاف، الجسم كاملًا",
            difficultyAr = "مبتدئ إلى متوسط",
            equipmentAr = "حذاء رياضي مريح ومساحة صغيرة آمنة",
            startingPositionAr = "قف باستقامة مع ضم القدمين وإرخاء الذراعين بجانب الجسم.",
            stepsAr = listOf(
                "اقفز برفق مع فتح القدمين بعرض أوسع من الكتفين.",
                "في الوقت نفسه، ارفع الذراعين من الجانبين حتى فوق الرأس.",
                "اهبط بخفة على مقدمة القدمين مع انثناء طفيف جدًا في الركبتين.",
                "اقفز عائدًا لضم القدمين وخفض الذراعين بإيقاع منتظم."
            ),
            breathingAr = "تنفس بإيقاع مستمر وسلس؛ شهيق عند الفتح وزفير عند الضم.",
            commonMistakesAr = listOf(
                "الهبوط القاسي على الكعبين مع ركبتين مفرودتين تمامًا.",
                "الشد المفرط في الرقبة والكتفين."
            ),
            mistakeCorrectionsAr = listOf(
                "اجعل الهبوط مرنًا وهادئًا لحماية الركبتين.",
                "حافظ على كتفيك منخفضين بعيدًا عن الأذنين."
            ),
            beginnerAlternativeAr = "نفذ الخطوة الجانبية بدون قفز (Step Jacks): افتح قدمًا واحدة في كل مرة مع رفع الذراعين.",
            safetyNoteAr = "إذا كانت المفاصل حساسة للقفز، استخدم بديل الخطوة الجانبية فورًا.",
            defaultSets = 3,
            defaultRepsOrSeconds = 30,
            isTimedSeconds = true,
            restSeconds = 40,
            statReward = StatType.STAMINA,
            xpReward = 35,
            motionPattern = MotionPattern.JUMPING_JACKS
        ),
        ExerciseInfo(
            id = "walking",
            titleAr = "المشي السريع المنظم",
            titleEn = "Walking",
            category = ExerciseCategory.CARDIO,
            targetMusclesAr = "عضلة القلب، الساقان، الوركان، تحسين الحالة المزاجية",
            difficultyAr = "مبتدئ — مناسب للجميع",
            equipmentAr = "حذاء مريح للمشي",
            startingPositionAr = "قف باستقامة، الكتفان مسترخيان للخلف، والنظر للأمام وليس للأسفل.",
            stepsAr = listOf(
                "ابدأ بخطوات هادئة لمدة دقيقة لإحماء المفاصل.",
                "ارفع الإيقاع لسرعة نشطة تسمح لك بالحديث بجمل قصيرة دون لهث شديد.",
                "حرك الذراعين بتناغم طبيعي مع انثناء المرفقين قليلًا.",
                "اهبط بالكعب أولًا ثم دحرج القدم بسلاسة نحو الأصابع."
            ),
            breathingAr = "تنفس عميق ومنتظم عبر الأنف مع إيقاع الخطوات.",
            commonMistakesAr = listOf(
                "انحناء الرقبة للأسفل بسبب النظر للهاتف أثناء المشي.",
                "أخذ خطوات طويلة جدًا تسبب إجهاد الساق."
            ),
            mistakeCorrectionsAr = listOf(
                "ارفع الذقن والنظر للأفق على بعد أمتار أمامك.",
                "اعتمد خطوات طبيعية سريعة بدلًا من إطالة الخطوة."
            ),
            beginnerAlternativeAr = "ابدأ بـ 5 دقائق داخل المنزل أو الحديقة وزد المدة تدريجيًا.",
            safetyNoteAr = "اختر مسارًا آمنًا ومضاءً، واشرب الماء عند الحاجة خاصة في الطقس الدافئ.",
            defaultSets = 1,
            defaultRepsOrSeconds = 60,
            isTimedSeconds = true,
            restSeconds = 30,
            statReward = StatType.STAMINA,
            xpReward = 40,
            motionPattern = MotionPattern.WALKING
        ),
        ExerciseInfo(
            id = "stretching",
            titleAr = "إطالة شاملة للجسم",
            titleEn = "Stretching",
            category = ExerciseCategory.FLEXIBILITY,
            targetMusclesAr = "العضلات الخلفية للفخذ، الظهر، الصدر، الكتفان، الرقبة",
            difficultyAr = "مبتدئ — استشفاء ومرونة",
            equipmentAr = "حصيرة مريحة",
            startingPositionAr = "اجلس أو قف في مكان هادئ بعد الإحماء أو نهاية التمرين.",
            stepsAr = listOf(
                "ابدأ بإطالة الصدر والكتفين بشبك اليدين برفق خلف الظهر لـ 15 ثانية.",
                "انتقل لإطالة جانب الجذع برفع ذراع واحدة للأعلى والميل اللطيف للجانب المعاكس.",
                "اجلس ومد ساقًا واحدة للأمام وانحنِ برفق من الورك لإطالة العضلة الخلفية دون شد مؤلم.",
                "اختم بوضعية الطفل (Child's Pose) لإراحة أسفل الظهر والتنفس العميق."
            ),
            breathingAr = "شهيق عميق عبر الأنف، وزفير بطيء جدًا لتشجيع العضلة على الاسترخاء.",
            commonMistakesAr = listOf(
                "الاهتزاز أو النبض العنيف أثناء الإطالة.",
                "الوصول لمرحلة الألم بدلًا من الشد اللطيف المريح."
            ),
            mistakeCorrectionsAr = listOf(
                "اثبت بسكون في كل وضعية لمدة 15–25 ثانية.",
                "توقف عند أول إحساس بشد مريح وخفيف فقط."
            ),
            beginnerAlternativeAr = "نفذ الإطالات أثناء الجلوس على كرسي وبمدى حركة صغير جدًا.",
            safetyNoteAr = "الإطالة الثابتة لا ينبغي أن تسبب ألمًا في المفاصل أبدًا.",
            defaultSets = 2,
            defaultRepsOrSeconds = 30,
            isTimedSeconds = true,
            restSeconds = 25,
            statReward = StatType.RECOVERY,
            xpReward = 30,
            motionPattern = MotionPattern.STRETCHING
        ),
        ExerciseInfo(
            id = "shoulder_circles",
            titleAr = "دوائر الأكتاف وإحماء المفاصل",
            titleEn = "Shoulder Circles & Warm-up",
            category = ExerciseCategory.SHOULDERS,
            targetMusclesAr = "عضلات الكتف الدوارة، أعلى الظهر، الرقبة",
            difficultyAr = "مبتدئ",
            equipmentAr = "بدون أدوات",
            startingPositionAr = "قف أو اجلس باستقامة مع إرخاء الذراعين على الجانبين.",
            stepsAr = listOf(
                "ارفع الذراعين للجانبين بمستوى الكتفين ببطء.",
                "ارسم دوائر صغيرة هادئة للأمام 10 مرات.",
                "اعكس الاتجاه وارسم دوائر للخلف 10 مرات مع تقريب لوحي الكتف برفق.",
                "أرخِ الذراعين وحرك الكتفين للأعلى وللخلف بهدوء."
            ),
            breathingAr = "تنفس حر ومسترخٍ دون شد الرقبة.",
            commonMistakesAr = listOf(
                "رفع الكتفين باتجاه الأذنين.",
                "سرعة زائدة بدون وعي بالحركة."
            ),
            mistakeCorrectionsAr = listOf(
                "حافظ على الرقبة طويلة ومسترخية.",
                "اجعل الدوائر متوسطة وبطيئة لتهيئة المفصل."
            ),
            beginnerAlternativeAr = "ارسم دوائر أصغر مع ثني المرفقين قليلًا.",
            safetyNoteAr = "ممتاز للإحماء قبل تمارين الصدر والذراعين وكذلك لفك تيبس الجلوس الطويل.",
            defaultSets = 2,
            defaultRepsOrSeconds = 25,
            isTimedSeconds = true,
            restSeconds = 25,
            statReward = StatType.MOBILITY,
            xpReward = 25,
            motionPattern = MotionPattern.SHOULDER_CIRCLES
        ),
        ExerciseInfo(
            id = "chair_triceps_dip",
            titleAr = "تمارين الذراعين والترايسبس الآمنة",
            titleEn = "Chair Triceps Dip",
            category = ExerciseCategory.ARMS,
            targetMusclesAr = "العضلة ثلاثية الرؤوس (خلف الذراع)، الأكتاف الأمامية",
            difficultyAr = "مبتدئ إلى متوسط",
            equipmentAr = "كرسي ثابت مستند إلى حائط",
            startingPositionAr = "اجلس على حافة كرسي ثابت مسندًا إياه للحائط، وضع يديك على الحافة بجانب الوركين، والركبتان مثنيتان.",
            stepsAr = listOf(
                "تقدم بالوركين قليلًا أمام الكرسي مع بقاء الظهر قريبًا من الحافة.",
                "اثنِ المرفقين للخلف ببطء لنزول بسيط ومريح (لا تتجاوز 90 درجة).",
                "ادفع الكرسي براحتي اليدين للعودة للأعلى.",
                "حافظ على الكتفين للأسفل وللخلف طوال التمرين."
            ),
            breathingAr = "شهيق عند النزول، وزفير عند الدفع للأعلى.",
            commonMistakesAr = listOf(
                "الابتعاد كثيرًا عن الكرسي مما يضعف ثبات الكتف.",
                "النزول العميق جدًا الذي يضغط على مقدمة الكتف."
            ),
            mistakeCorrectionsAr = listOf(
                "أبقِ ظهرك قريبًا جدًا من حافة الكرسي.",
                "اكتفِ بنزول متوسط ومريح يركز على خلف الذراع."
            ),
            beginnerAlternativeAr = "اضغط بيديك مقابل بعضهما أمام الصدر لـ 15 ثانية أو نفذ Wall Push-up بقبضة ضيقة.",
            safetyNoteAr = "تأكد تمامًا من أن الكرسي مسند لحائط ولا يتحرك، وتوقف عند أي انزعاج في الكتف.",
            defaultSets = 3,
            defaultRepsOrSeconds = 10,
            isTimedSeconds = false,
            restSeconds = 45,
            statReward = StatType.STRENGTH,
            xpReward = 35,
            motionPattern = MotionPattern.TRICEPS_DIP
        )
    )

    fun exercisesForCategory(category: ExerciseCategory?): List<ExerciseInfo> {
        if (category == null) return exercises
        return when (category) {
            ExerciseCategory.WARMUP -> exercises.filter {
                it.id in setOf("shoulder_circles", "jumping_jacks", "walking")
            }
            ExerciseCategory.RECOVERY -> exercises.filter {
                it.id in setOf("stretching", "bird_dog", "walking")
            }
            else -> exercises.filter { it.category == category }
        }
    }

    fun findById(id: String): ExerciseInfo? = exercises.find { it.id == id }
}
