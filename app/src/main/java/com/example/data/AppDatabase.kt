package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [NoteEntity::class, ChatMessageEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun studyDao(): StudyDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "study_os_db"
                )
                .addCallback(DatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database.studyDao())
                    }
                }
            }

            suspend fun populateDatabase(dao: StudyDao) {
                // Pre-seed some chat history for AI Tutor to make it look active
                dao.insertChatMessage(
                    ChatMessageEntity(
                        sender = "user",
                        message = "Explain the Golden Ratio in UI design and show me how to apply it to a 12-column grid layout."
                    )
                )
                dao.insertChatMessage(
                    ChatMessageEntity(
                        sender = "ai",
                        message = "The Golden Ratio (Φ ≈ 1.618) provides a mathematical basis for aesthetic balance. In UI design, it's often used to determine the hierarchy between the primary content area and the sidebar.\n\nTotal Width: 1440px\nMain Content: 1440 / 1.618 = ~890px\nSidebar: 1440 - 890 = ~550px\n\nApplying this to a 12-column grid, you typically use an 8+4 or 7+5 column split to maintain a ratio close to 1.618."
                    )
                )

                // Seed graphic arts study notes
                dao.insertNotes(
                    listOf(
                        NoteEntity(
                            id = 1,
                            title = "Grid Systems in Swiss Design",
                            summary = "Exploring the mathematical precision of the Basel school and its modern application in digital UI...",
                            content = "The grid system is an organizational device that allows for a structured approach to visual communication. In the context of Swiss Design (the International Typographic Style), it represents more than just a layout tool—it is a philosophy of clarity, objectivity, and rationalist order.\n\nMathematical Precision:\nThe hallmark of the Swiss grid is the use of the A-series paper sizes and a strict vertical rhythm. Designers like Brockmann emphasized that by dividing a page into a system of rows and columns, one could achieve a harmony that feels both natural and scientifically rigorous.\n\nModern digital interfaces, such as the one you are viewing, utilize this same underlying logic through Tailwind's spacing scales and CSS grid systems.",
                            category = "LAYOUT",
                            imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuC6dtzxWWK7G3N0ZZ5aqveEqnhOz2dtL1gXHTL3g_wi7dhSjaZPv5wVusg6YtpqKsf77Z_f6nkDyYdKW595aUNXtVZwkJ-IJ3hezkwY5fg3qsWCDh3uMRS5TJEHUOkX0NLjiXgHFtn5-U1CAQ6SQ3H84d7I8strLOP6XvM7jhkScuEJTL5CUoJk9H8zLzV_zDnOvIny7nD1FmvmU6obsmMiYB1bI5qSTu9zXAuvPlktGEeeI9KvhSjcb1_1MKa8qTVovwmwUcxtEhc",
                            masteryLevel = 84,
                            examRelevance = 9.2,
                            tags = "swiss-design,grid-systems,typography,modernism,bauhaus",
                            timestampText = "2h ago",
                            isMastered = true,
                            editHistory = "Draft Finalized|Today, 2:14 PM;AI Content Enhancement|Today, 11:05 AM;Initial Draft Created|Oct 14, 9:20 AM"
                        ),
                        NoteEntity(
                            id = 2,
                            title = "Color Psychology: Indigo",
                            summary = "Why tech giants favor electric indigo for intelligence-based interfaces and AI tools...",
                            content = "Indigo represents higher intuition, intelligence, and focused attention. In UI design, electric values of indigo (such as primary brand colors) act as semantic markers of advanced intelligence or 'AI' agency, providing users with a cue that they are interfacing with cognitive systems.\n\nUsing contrast effectively:\nPair electric indigo with extreme dark obsidian values (like #000000 and #080808) to make actions pop while maintaining a modern, high-tech aesthetic.",
                            category = "COLOR",
                            imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDjuEnKgnur8VZOn0FP_vR6cuAu21SgZ8j3of7DEe2FHg7ha6VeQ-jj4oVD7WmApWhhGs4PZDHmDtdZ5238ALmf8M8Y-93LLSqdkJlyvkKY9pnoKtgJPIa9qo0qzK-fXy_F7TVWEsGkk7I9uBBEI9z1Ro15qhtJSIBXN4DVooUFQxjWItO2fMDVUCrff6ViJBiNEwAJkMGsJCagonLk8IGXBYDEwhgMkWHMynlRBzOWFoBt3nkJHC7Z8omprveqKQYZLF2ZXM0Pi2s",
                            masteryLevel = 60,
                            examRelevance = 8.4,
                            tags = "indigo,color-psychology,ai-branding,contrast",
                            timestampText = "Yesterday",
                            isMastered = false,
                            editHistory = "Color Theme Defined|Yesterday, 4:00 PM;Initial Generation|Yesterday, 9:15 AM"
                        ),
                        NoteEntity(
                            id = 3,
                            title = "The Golden Ratio in Typography",
                            summary = "Scaling font hierarchies using the Fibonacci sequence for cognitive ease and flow...",
                            content = "By scaling typography sizes using the Phi constant (1.618), text layouts gain a mathematically proven reading rhythm. For example, if your body text is 16sp, your primary headers should scale near 26sp, and display fonts to 42sp, matching natural optical proportions.\n\nApplying natural symmetry:\nApplying golden ratios to leading (line height) and margin values creates spacing tracks that allow content to breathe and feel cohesive without heavy design layout noise.",
                            category = "TYPE",
                            imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCpdp7nDDbIWE0ekPA78K6WNGT7VaYIKG5W1swwoPgbpBagAZz6u4THTI2efhac0IsEdT0SyS5kExvpjVeNxWsFUD7IQL8dL4F8gcSPbphn904JAYrM3L2UgNpoSvvPH7F51vhZ3lpV7LaWKPoHXqgSSOTk30G2I0pr-_cPL-1zSwIEEJDP9rkf4qAfmXysMmm8z8B1WR5kYAqGy1eBNi0MWPU05L-HvY1UGouyaSj_2l3U--SEKPPCLbFfj1a14PGwZL0n4gg3BY4",
                            masteryLevel = 45,
                            examRelevance = 7.9,
                            tags = "fibonacci,typography,scale,golden-ratio,readability",
                            timestampText = "Oct 12",
                            isMastered = false,
                            editHistory = "Initial Theory Draft|Oct 12, 10:30 AM"
                        ),
                        NoteEntity(
                            id = 4,
                            title = "Bento Grid Evolution",
                            summary = "Tracing the UI pattern from Apple's marketing to the standard dashboard layout of 2024...",
                            content = "Originating as responsive widgets within marketing slides, the Bento Grid utilizes hierarchical boxes to segregate data components. Its modular structure fits high-density metrics and interactive elements perfectly, representing the high point of responsive digital layout system designs.\n\nKey Principles of Bento layouts:\n- Diverse box sizes styled as glass panels\n- Sized symmetrically onto a dense underlying row/col setup\n- Strong focus on visual scanning via icons and monospaced indicators",
                            category = "TRENDS",
                            imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDBp_IX8xO2iDezGSI5B0_24Whjn0aNaDQHe_JSiZENgSq7OvqYnaAjGmypfbjeVY7-TzuRoY8lnA7S1got7mF6u2_iB-x0CS9kBW7sPNBxQVVPTg9wqbAqmuJIZpSoyt33flno58nD4r5WFpn_QlpN68qL7V9sRLvtw_z1aJ-zTKNmtSpeNC3N0fA1o9qMKHeXK2mSk9LmGau7wOcDj3SJI48DdiumKd_vZedHQjYhol4DGBVnhhm9f7BzA6jIlg5Kb7Z_jiVef0Y",
                            masteryLevel = 95,
                            examRelevance = 6.8,
                            tags = "bento-grid,ux-trends,hierarchical-layout,dashboard",
                            timestampText = "Oct 10",
                            isMastered = true,
                            editHistory = "Completed Analysis|Oct 10, 11:15 AM"
                        )
                    )
                )
            }
        }
    }
}
