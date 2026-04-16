package com.fredy.gamevault.features.community.data.datasources.remote.mapper

import com.fredy.gamevault.features.community.data.datasources.remote.model.PostResponse
import com.fredy.gamevault.features.community.domain.entities.Post
import com.fredy.gamevault.features.community.domain.entities.PostType

fun PostResponse.toDomain(): Post = Post(
    id = id,
    userId = userId,
    authorName = authorName,
    gameName = gameName,
    title = title,
    content = content,
    postType = PostType.fromString(postType),
    reactionsCount = reactionsCount,
    hasReacted = hasReacted,
    createdAt = createdAt,
    updatedAt = updatedAt
)