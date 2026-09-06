package com.nextroom.nextroom.data.db

import androidx.datastore.core.Serializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

object AppSettingsSerializer : Serializer<AppSettings> {

    /** 삭제된 설정 항목이 저장되어 있어도 초기화되지 않도록 알 수 없는 키는 무시한다 */
    private val json = Json { ignoreUnknownKeys = true }

    override val defaultValue: AppSettings = AppSettings()

    override suspend fun readFrom(input: InputStream): AppSettings {
        return try {
            json.decodeFromString(
                deserializer = AppSettings.serializer(),
                string = input.readBytes().decodeToString(),
            )
        } catch (exception: SerializationException) {
            exception.printStackTrace()
            defaultValue
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun writeTo(t: AppSettings, output: OutputStream) {
        output.write(
            json.encodeToString(
                serializer = AppSettings.serializer(),
                value = t,
            ).encodeToByteArray(),
        )
    }
}
