package com.nextroom.nextroom.domain.usecase

import javax.inject.Inject
import javax.inject.Named

class CompareVersion @Inject constructor(
    @Named("app_version") private val appVersion: String,
) {
    operator fun invoke(
        minVersion: String,
    ): UpdateStatus {
        fun isValidVersionForm(version: String): Boolean {
            if (version.isEmpty()) {
                return false
            }

            if (version.split(".").size != 3) {
                return false
            }

            return version
                .replace(".", "")
                .toIntOrNull()
                .let { it != null }
        }

        fun String.isGreaterVersion(other: String): Boolean {
            val t = this.split(".").map { it.toInt() }
            val o = other.split(".").map { it.toInt() }
            val thisVersion = KotlinVersion(t[0], t[1], t[2])

            return thisVersion.isAtLeast(o[0], o[1], o[2])
        }

        if (!isValidVersionForm(appVersion) || !isValidVersionForm(minVersion)) {
            return UpdateStatus.NOT_NEED_UPDATE // 업데이트 X
        }

        return if (appVersion.isGreaterVersion(minVersion)) {
            UpdateStatus.NOT_NEED_UPDATE
        } else {
            UpdateStatus.NEED_FORCE_UPDATE // 최소 버전보다 낮은 경우 (강제 업데이트)
        }
    }

    enum class UpdateStatus {
        NEED_FORCE_UPDATE,
        NOT_NEED_UPDATE,
    }
}
