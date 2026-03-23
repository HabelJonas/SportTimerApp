package com.hblsoftware.sporttimer.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [WorkoutProfileEntity::class],
    version = 2,
    exportSchema = false
)
abstract class SportTimerDatabase : RoomDatabase() {
    abstract fun profilesDao(): ProfilesDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS workout_profiles_new (
                        id TEXT NOT NULL,
                        name TEXT NOT NULL,
                        preparationSeconds INTEGER NOT NULL,
                        workSeconds INTEGER NOT NULL,
                        restSeconds INTEGER NOT NULL,
                        rounds INTEGER NOT NULL,
                        PRIMARY KEY(id)
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    INSERT INTO workout_profiles_new (id, name, preparationSeconds, workSeconds, restSeconds, rounds)
                    SELECT id, name, preparationSeconds, workSeconds, restSeconds, rounds
                    FROM workout_profiles
                    """.trimIndent()
                )
                db.execSQL("DROP TABLE workout_profiles")
                db.execSQL("ALTER TABLE workout_profiles_new RENAME TO workout_profiles")
            }
        }
    }
}
