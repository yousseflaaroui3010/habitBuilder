package com.habitarchitect.data

import com.habitarchitect.domain.model.HabitTemplate
import com.habitarchitect.domain.model.HabitType

/**
 * Pre-built habit templates with evidence-based defaults.
 */
object HabitTemplates {

    val breakTemplates = listOf(
        HabitTemplate(
            id = "break_porn",
            name = "No Porn/Masturbation",
            type = HabitType.BREAK,
            category = "Health & Wellbeing",
            iconEmoji = "🚫",
            description = "Break free from pornography and compulsive masturbation",
            defaultResistanceItems = listOf(
                "My sleep quality will suffer tonight",
                "I'll struggle to wake up for Fajr/morning routine",
                "I'm depleting zinc and other nutrients I've worked to build",
                "Brain fog will affect my focus tomorrow",
                "I'll feel less confident and more ashamed",
                "I'll be more likely to objectify people around me",
                "This version of me isn't who I want to become",
                "Every time I give in, the habit gets stronger"
            ),
            defaultFrictionStrategies = listOf(
                "Install a website blocker (Cold Turkey, BlockSite)",
                "Have a friend set the blocker password",
                "Keep devices out of bedroom",
                "Never use phone/laptop in bed"
            )
        ),
        HabitTemplate(
            id = "break_smoking",
            name = "Quit Smoking",
            type = HabitType.BREAK,
            category = "Health",
            iconEmoji = "🚬",
            description = "Quit smoking cigarettes or vaping",
            defaultResistanceItems = listOf(
                "Each cigarette costs me money — that's adding up fast",
                "My lungs are healing; this would set them back",
                "The craving will pass in 3 minutes whether I smoke or not",
                "I'll smell like smoke and people will notice",
                "My teeth and skin are improving; why reverse that?",
                "I'm just feeding a chemical addiction, not a real need"
            ),
            defaultFrictionStrategies = listOf(
                "Don't buy cigarettes; never have them at home",
                "Avoid smoking areas and smoking friends initially",
                "Replace the hand-to-mouth motion (toothpick, carrot)"
            )
        ),
        HabitTemplate(
            id = "break_doomscrolling",
            name = "No Doomscrolling",
            type = HabitType.BREAK,
            category = "Productivity",
            iconEmoji = "📱",
            description = "Break the endless scroll habit",
            defaultResistanceItems = listOf(
                "I'm trading real life for curated highlight reels",
                "This content is designed to be addictive, not valuable",
                "I'll feel worse about myself afterward, not better",
                "Time spent here is time not spent on my goals",
                "The algorithm is optimizing for engagement, not my wellbeing"
            ),
            defaultFrictionStrategies = listOf(
                "Set app time limits (Screen Time, Digital Wellbeing)",
                "Remove social apps from home screen",
                "Grayscale your phone display"
            )
        ),
        HabitTemplate(
            id = "break_alcohol",
            name = "Reduce Alcohol",
            type = HabitType.BREAK,
            category = "Health",
            iconEmoji = "🍺",
            description = "Reduce or eliminate alcohol consumption",
            defaultResistanceItems = listOf(
                "Alcohol disrupts my sleep architecture",
                "Tomorrow's productivity will be lower",
                "I'll feel dehydrated and sluggish",
                "It's empty calories undermining my fitness goals"
            ),
            defaultFrictionStrategies = listOf(
                "Don't keep alcohol at home",
                "Order first at restaurants (water/mocktail)",
                "Tell friends you're taking a break"
            )
        )
    )

    val buildTemplates = listOf(
        HabitTemplate(
            id = "build_exercise",
            name = "Daily Exercise",
            type = HabitType.BUILD,
            category = "Health",
            iconEmoji = "🏃",
            description = "Build a consistent exercise routine",
            defaultMinimumVersion = "Put on my workout clothes",
            defaultStackAnchors = listOf(
                "After I pour my morning coffee",
                "After I finish work"
            ),
            defaultAttractionItems = listOf(
                "I'll feel energized and accomplished after",
                "This is building the body I want to see",
                "Exercise clears my mind better than anything",
                "I'm becoming someone who doesn't skip workouts"
            ),
            defaultRewards = listOf(
                "Post-workout smoothie",
                "10 minutes of guilt-free relaxation"
            )
        ),
        HabitTemplate(
            id = "build_reading",
            name = "Daily Reading",
            type = HabitType.BUILD,
            category = "Personal Growth",
            iconEmoji = "📖",
            description = "Read books instead of screens",
            defaultMinimumVersion = "Read one page",
            defaultStackAnchors = listOf(
                "After I get into bed",
                "During my lunch break"
            ),
            defaultAttractionItems = listOf(
                "Reading makes me more interesting and knowledgeable",
                "Each page is compound interest on my mind",
                "This is how successful people spend their time"
            ),
            defaultRewards = listOf(
                "Track pages/books completed",
                "Cozy beverage while reading"
            )
        ),
        HabitTemplate(
            id = "build_fajr",
            name = "Fajr at Mosque",
            type = HabitType.BUILD,
            category = "Spiritual",
            iconEmoji = "🕌",
            description = "Attend Fajr prayer at the mosque consistently",
            defaultMinimumVersion = "Get out of bed at first alarm",
            defaultStackAnchors = listOf("When I hear the adhan"),
            defaultAttractionItems = listOf(
                "The barakah of praying in congregation (27x reward)",
                "Starting the day with purpose and gratitude",
                "The peace of the mosque at dawn"
            ),
            defaultRewards = listOf("Peaceful walk back as the sun rises")
        ),
        HabitTemplate(
            id = "build_meditation",
            name = "Daily Meditation",
            type = HabitType.BUILD,
            category = "Mental Health",
            iconEmoji = "🧘",
            description = "Build a meditation or mindfulness practice",
            defaultMinimumVersion = "Sit and take 3 deep breaths",
            defaultStackAnchors = listOf("After I wake up", "Before bed"),
            defaultAttractionItems = listOf(
                "10 minutes of calm affects the whole day",
                "I'm training my mind like I train my body",
                "Stress doesn't control me when I meditate regularly"
            ),
            defaultRewards = listOf("Cup of tea after")
        ),
        HabitTemplate(
            id = "build_deepwork",
            name = "Deep Work Block",
            type = HabitType.BUILD,
            category = "Productivity",
            iconEmoji = "💼",
            description = "Focused work without distractions",
            defaultMinimumVersion = "Clear desk and open project file",
            defaultStackAnchors = listOf("After morning coffee", "After lunch"),
            defaultAttractionItems = listOf(
                "This is when my best work happens",
                "2 hours of deep work > 6 hours of shallow work",
                "My career advances through what I produce here"
            ),
            defaultRewards = listOf("Coffee break after the block", "Walk outside")
        ),
        HabitTemplate(
            id = "build_water",
            name = "Drink Water",
            type = HabitType.BUILD,
            category = "Health",
            iconEmoji = "💧",
            description = "Stay hydrated throughout the day",
            defaultMinimumVersion = "Take one sip",
            defaultStackAnchors = listOf(
                "First thing after waking up",
                "Before every meal"
            ),
            defaultAttractionItems = listOf(
                "Hydration improves energy and focus",
                "My skin looks better when hydrated",
                "Water is the simplest health hack"
            ),
            defaultRewards = listOf("Use a nice water bottle you enjoy")
        )
    )

    val allTemplates = breakTemplates + buildTemplates
}
