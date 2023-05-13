package site.siredvin.peripheralium.api.config

interface IOperationAbilityConfig {
    val isInitialCooldownEnabled: Boolean
    val initialCooldownSensetiveLevel: Int
    val cooldownTrasholdLevel: Int
}