package com.habitarchitect.data.local.database

import android.content.Context
import androidx.room.Room
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import timber.log.Timber
import java.security.SecureRandom

/**
 * Factory for creating encrypted Room database with SQLCipher.
 * Generates and securely stores database encryption key.
 */
object EncryptedDatabaseFactory {
    private const val PREFS_NAME = "db_encryption_prefs"
    private const val KEY_DB_PASSPHRASE = "db_passphrase"
    private const val PASSPHRASE_LENGTH = 32

    /**
     * Creates an encrypted database instance using SQLCipher.
     * The passphrase is generated once and stored in EncryptedSharedPreferences.
     */
    fun create(context: Context): HabitArchitectDatabase {
        val passphrase = getOrCreatePassphrase(context)
        val factory = SupportFactory(SQLiteDatabase.getBytes(passphrase.toCharArray()))

        return Room.databaseBuilder(
            context.applicationContext,
            HabitArchitectDatabase::class.java,
            HabitArchitectDatabase.DATABASE_NAME
        )
            .openHelperFactory(factory)
            .addMigrations(
                HabitArchitectDatabase.MIGRATION_1_2,
                HabitArchitectDatabase.MIGRATION_2_3,
                HabitArchitectDatabase.MIGRATION_3_4,
                HabitArchitectDatabase.MIGRATION_4_5,
                HabitArchitectDatabase.MIGRATION_5_6
            )
            .build()
    }

    /**
     * Gets existing passphrase or creates a new one.
     * Uses EncryptedSharedPreferences for secure storage.
     */
    private fun getOrCreatePassphrase(context: Context): String {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        val prefs = EncryptedSharedPreferences.create(
            context,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        return prefs.getString(KEY_DB_PASSPHRASE, null) ?: run {
            val newPassphrase = generatePassphrase()
            prefs.edit().putString(KEY_DB_PASSPHRASE, newPassphrase).apply()
            Timber.d("Generated new database encryption key")
            newPassphrase
        }
    }

    /**
     * Generates a cryptographically secure random passphrase.
     */
    private fun generatePassphrase(): String {
        val random = SecureRandom()
        val bytes = ByteArray(PASSPHRASE_LENGTH)
        random.nextBytes(bytes)
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
