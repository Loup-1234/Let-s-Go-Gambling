package com.example.letsgogambling

class RandomSentenceGenerator{
    private val sentences = listOf(
        "Shit yourself",
        "That's a lot of copium right here",
        "Going to 1v1 God in a fight",
        "Nah I'd win",
        "Is this a gun in your pocket or are you just happy to see me ?",
        "If you're reading this, then I know your IP",
        "If you're reading this, you're a virgin"
    )
    fun generate(): String = sentences.random()
}