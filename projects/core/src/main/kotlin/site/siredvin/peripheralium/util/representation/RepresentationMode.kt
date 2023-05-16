package site.siredvin.peripheralium.util.representation

/**
 * Mostly used for describe level of detalization for represenation of any object to lua
 * BASE - only base information
 * DETAILED - every piece of information except NBT tag and internal information leaking
 * FULL - every piece of information, including NBT and internal information
 */
enum class RepresentationMode {
    BASE, DETAILED, FULL
}