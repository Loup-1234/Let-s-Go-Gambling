package com.example.letsgogambling

import kotlin.random.Random

// Generates random D&D-themed sentences.
class RandomSentenceGenerator {
    private data class WordLists(
        val subjects: List<String>,
        val verbs: List<String>,
        val objects: List<String>,
        val adverbs: List<String>,
        val prepositions: List<String>,
        val conjunctions: List<String>,
        val nouns: List<String>,
        val adjectives: List<String>
    )

    // Word lists for sentence generation.
    private val dndWords = WordLists(
        subjects = listOf(
        "The brave knight", "A cunning rogue", "The wise wizard", "A fearsome dragon",
        "The ancient lich", "A goblin raiding party", "The paladin who shit himself",
        "The bard with a lot of copium right here", "The cleric going to 1v1 God in a fight",
        "The barbarian saying 'Nah I'd win'",
        "The ranger asking 'Is this a gun in your pocket or are you just happy to see me ?'",
        "The sorcerer who knows your IP if you're reading this",
        "The warlock who calls you a virgin if you're reading this"
        ),

        verbs = listOf(
        "attacked", "explored", "cast a spell on", "guarded", "discovered", "fled from",
        "shat upon", "copiumed on", "challenged to a divine duel",
        "confidently declared victory over", "flirted with", "doxxed", "insulted"
        ),

        objects = listOf(
        "the dark dungeon", "a hidden treasure", "the magical artifact",
        "an unsuspecting village", "the ancient ruins", "a powerful incantation",
        "the tavern floor", "a pile of scrolls", "the heavens themselves",
        "a surprised beholder", "a confused guard", "the entire internet", "a snickering imp"
        ),

        adverbs = listOf(
        "fiercely", "stealthily", "magically", "bravely", "cautiously", "suddenly",
        "explosively", "excessively", "boldly", "arrogantly", "suggestively", "ominously", "mockingly"
        ),

        prepositions = listOf(
        "within", "atop", "beneath", "beyond", "towards", "against", "in front of",
        "with", "before", "despite", "near", "throughout", "at"
        ),

        conjunctions = listOf(
        "and", "but", "so", "because", "while", "although", "yet"
        ),

        nouns = listOf(
        "castle", "forest", "mountain", "cavern", "spellbook", "curse", "latrine",
        "potion", "pantheon", "ego", "codpiece", "database", "scroll of insults"
        ),

        adjectives = listOf(
        "enchanted", "cursed", "mysterious", "dangerous", "legendary", "forgotten",
        "stinky", "overflowing", "almighty", "unbeatable", "bulging", "all-knowing", "scathing"
        )
    )

    // Extension function to pick a random element from a list.
    private fun <T> List<T>.pickRandom(): T = this[Random.nextInt(size)]

    // Generates a random sentence.
    // recursionDepth: Controls the depth of recursive sentence generation.
    fun generate(recursionDepth: Int = 0): String {
        val sb = StringBuilder()

        // Start with a subject and a verb.
        sb.append(dndWords.subjects.pickRandom()).append(" ").append(dndWords.verbs.pickRandom())

        // Optionally add an adverb.
        if (Random.nextBoolean()) sb.append(" ").append(dndWords.adverbs.pickRandom())

        // Build and append the object phrase.
        val (objectPhrase, adjectiveInObject) = buildObjectPhrase()
        sb.append(" ").append(objectPhrase)

        // Optionally add a prepositional phrase.
        if (Random.nextDouble() < 0.6) {
            sb.append(" ").append(buildPrepositionalPhrase(adjectiveInObject))
        }

        // Optionally add a conjunction and another recursively generated sentence part.
        if (recursionDepth < MAX_RECURSION_DEPTH && Random.nextDouble() < 0.1) {
            sb.append(" ").append(dndWords.conjunctions.pickRandom()).append(" ").append(generate(recursionDepth + 1).replaceFirstChar { it.lowercase() })
        }

        // Clean up and ensure the sentence ends with a period.
        val sentence = sb.toString().replace(Regex("\\s+"), " ").trim()
        return sentence.takeIf { it.endsWith(".") } ?: "$sentence."
    }

    // Builds the object part of the sentence.
    // Returns the phrase and a boolean indicating if an adjective was used.
    private fun buildObjectPhrase(): Pair<String, Boolean> {
        // Optionally pick an adjective.
        val adjective = dndWords.adjectives.pickRandom().takeIf { Random.nextDouble() < 0.7 }
        // Combine the adjective (if any) with a random object.
        val phrase = listOfNotNull(
            adjective,
            dndWords.objects.pickRandom()
        ).joinToString(" ")
        return Pair(phrase, adjective != null)
    }

    // Builds a prepositional phrase.
    // adjectiveUsedInObjectPhrase: Prevents using an adjective if one was already used in the object phrase.
    private fun buildPrepositionalPhrase(adjectiveUsedInObjectPhrase: Boolean): String =
        listOfNotNull(
            dndWords.prepositions.pickRandom(),
            pickRandomArticle(),
            dndWords.adjectives.pickRandom().takeIf { Random.nextDouble() < 0.7 && !adjectiveUsedInObjectPhrase },
            dndWords.nouns.pickRandom()
        ).joinToString(" ")

    // Picks a random article ("a", "the") or no article.
    private fun pickRandomArticle(): String? = when (Random.nextInt(3)) {
            0 -> "a"
            1 -> "the"
            else -> null
    }

    companion object {
        // Maximum depth for recursive sentence generation.
        private const val MAX_RECURSION_DEPTH = 5
    }
}
