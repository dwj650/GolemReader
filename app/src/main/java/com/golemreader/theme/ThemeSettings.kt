package com.golemreader.theme

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

enum class ThemeChoice(val storedValue: String) {
    FollowSystem("follow_system"),
    Light("light"),
    Dark("dark");

    companion object {
        fun fromStoredValue(value: String?): ThemeChoice =
            entries.firstOrNull { it.storedValue == value } ?: FollowSystem
    }
}

@Entity(tableName = "theme_settings")
data class ThemeSettingEntity(
    @PrimaryKey
    @ColumnInfo(name = "key")
    val key: String = THEME_CHOICE_KEY,
    val choice: String = ThemeChoice.FollowSystem.storedValue,
) {
    companion object {
        const val THEME_CHOICE_KEY = "theme_choice"
        const val HIGH_CONTRAST_KEY = "high_contrast"
        const val TEXT_SCALE_KEY = "text_scale"
        const val REDUCED_MOTION_KEY = "reduced_motion"
    }
}

@Dao
interface ThemeSettingsDao {
    @Query("SELECT * FROM theme_settings WHERE `key` = :key")
    fun get(key: String = ThemeSettingEntity.THEME_CHOICE_KEY): ThemeSettingEntity?

    @Query("SELECT * FROM theme_settings WHERE `key` = :key")
    fun observe(key: String = ThemeSettingEntity.THEME_CHOICE_KEY): Flow<ThemeSettingEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(entity: ThemeSettingEntity)
}

class ThemeSettingsRepository(
    private val dao: ThemeSettingsDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    fun currentChoice(): ThemeChoice =
        ThemeChoice.fromStoredValue(dao.get()?.choice)

    fun choiceFlow(): Flow<ThemeChoice> =
        dao.observe().map { entity -> ThemeChoice.fromStoredValue(entity?.choice) }

    fun highContrastFlow(): Flow<Boolean> =
        dao.observe(ThemeSettingEntity.HIGH_CONTRAST_KEY)
            .map { entity -> entity?.choice == true.toString() }

    fun textScaleFlow(): Flow<TextScaleStep> =
        dao.observe(ThemeSettingEntity.TEXT_SCALE_KEY)
            .map { entity -> TextScaleStep.fromStoredValue(entity?.choice) }

    fun reducedMotionFlow(): Flow<Boolean> =
        dao.observe(ThemeSettingEntity.REDUCED_MOTION_KEY)
            .map { entity -> entity?.choice == true.toString() }

    suspend fun setChoice(choice: ThemeChoice) = withContext(ioDispatcher) {
        dao.upsert(ThemeSettingEntity(choice = choice.storedValue))
    }

    suspend fun setHighContrast(enabled: Boolean) = withContext(ioDispatcher) {
        dao.upsert(
            ThemeSettingEntity(
                key = ThemeSettingEntity.HIGH_CONTRAST_KEY,
                choice = enabled.toString(),
            ),
        )
    }


    suspend fun setTextScale(step: TextScaleStep) = withContext(ioDispatcher) {
        dao.upsert(
            ThemeSettingEntity(
                key = ThemeSettingEntity.TEXT_SCALE_KEY,
                choice = step.storedValue,
            ),
        )
    }

    suspend fun setReducedMotion(enabled: Boolean) = withContext(ioDispatcher) {
        dao.upsert(
            ThemeSettingEntity(
                key = ThemeSettingEntity.REDUCED_MOTION_KEY,
                choice = enabled.toString(),
            ),
        )
    }
}
