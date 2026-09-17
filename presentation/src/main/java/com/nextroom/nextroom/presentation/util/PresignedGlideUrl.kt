package com.nextroom.nextroom.presentation.util

import com.bumptech.glide.load.model.GlideUrl

/**
 * presigned URL 전용 [GlideUrl]. 서명(쿼리스트링)을 뺀 URL을 캐시 키로 쓴다.
 *
 * 서버는 `/themes`를 호출할 때마다 같은 이미지에 대해 새 서명을 발급한다. 기본 [GlideUrl]은
 * 쿼리스트링을 포함한 전체 URL을 캐시 키로 쓰기 때문에, 서명이 바뀌면 같은 이미지인데도
 * 캐시가 전부 미스가 나고 매번 네트워크를 타게 된다. 서명이 만료된 뒤라면 그대로 403이다.
 *
 * [equals]/[hashCode]는 의도적으로 전체 URL 기준을 유지한다. Glide의 `HttpGlideUrlLoader`가
 * 내부 ModelCache를 모델 동일성으로 조회하는데, 서명이 달라도 같은 모델로 취급하면 만료된
 * 서명이 담긴 URL을 재사용해 버려서 오히려 실패한다. 캐시 키만 서명에 무관하게 만들고,
 * 실제 요청에 쓰이는 URL은 항상 호출부가 넘긴 그대로여야 한다.
 */
class PresignedGlideUrl(private val url: String) : GlideUrl(url) {

    override fun getCacheKey(): String = url.substringBefore('?')

    override fun equals(other: Any?): Boolean {
        return other is PresignedGlideUrl && url == other.url
    }

    override fun hashCode(): Int = url.hashCode()
}
