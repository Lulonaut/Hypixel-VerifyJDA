package de.lulonaut.bot.cache

object MessageCache {
    //Map<MessageId, UserId>
    private var cache: MutableMap<Long, Long> = HashMap()

    fun addMessage(messageId: Long, userId: Long) {
        cache[messageId] = userId
    }

    fun getUserId(messageId: Long): Long {
        return if (cache.containsKey(messageId)) cache[messageId]!! else 0
    }
}