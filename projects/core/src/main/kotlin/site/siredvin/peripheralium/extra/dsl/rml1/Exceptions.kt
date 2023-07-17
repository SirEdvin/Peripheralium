package site.siredvin.peripheralium.extra.dsl.rml1

open class RMLParsingException(message: String) : Exception(message)

class ArgumentParsingException(message: String) : RMLParsingException(message)

class LexemeDoesNotExistsException(message: String) : RMLParsingException(message)
class IncorrectInstructionException(message: String) : RMLParsingException(message)
