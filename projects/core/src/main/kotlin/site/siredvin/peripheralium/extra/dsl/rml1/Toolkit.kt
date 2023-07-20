package site.siredvin.peripheralium.extra.dsl.rml1

import java.lang.NumberFormatException
import kotlin.jvm.Throws

object ArgumentParsingToolkit {
    @Suppress("MemberVisibilityCanBePrivate")
    const val ARGUMENT_SEPARATOR = ","

    @Throws(ArgumentParsingException::class)
    fun asSubstring(arguments: String, count: Int): List<String> {
        val splitArguments = arguments.split(ARGUMENT_SEPARATOR)
        if (splitArguments.size != count) {
            throw ArgumentParsingException("Not enough arguments, $count expected, ${splitArguments.size} found")
        }
        return splitArguments
    }

    @Throws(ArgumentParsingException::class)
    fun asFloats(arguments: String, count: Int): List<Float> {
        try {
            return asSubstring(arguments, count).map(String::toFloat)
        } catch (ignored: NumberFormatException) {
            throw ArgumentParsingException("All arguments should be float numbers, but $ignored")
        }
    }
}

class RMLParser {
    companion object {
        const val INSTRUCTION_SEPARATOR = ";"
        val GROUPING_REGEX = "([\\w\\W_]+)\\((.*)\\)".toRegex()
    }
    private val lexemes: MutableMap<String, RMLLexeme> = mutableMapOf()

    @Throws(ArgumentParsingException::class)
    fun parse(line: String): List<RenderInstruction> {
        return line.split(INSTRUCTION_SEPARATOR).map {
            val parsedGroup = GROUPING_REGEX.find(it) ?: throw IncorrectInstructionException("Instruction $it isn't correctly defined")
            val lexemeName = parsedGroup.groupValues[1]
            val arguments = parsedGroup.groupValues[2]
            val lexemeParser = lexemes[lexemeName] ?: throw LexemeDoesNotExistsException("Instruction $lexemeName doesn't exists")
            return@map lexemeParser.build(arguments)
        }
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun addLexeme(lexeme: RMLLexeme) {
        lexemes[lexeme.name] = lexeme
    }

    fun injectDefault() {
        addLexeme(Translate.Companion)
        addLexeme(Rotation.Companion)
        addLexeme(Scale.Companion)
    }
}
