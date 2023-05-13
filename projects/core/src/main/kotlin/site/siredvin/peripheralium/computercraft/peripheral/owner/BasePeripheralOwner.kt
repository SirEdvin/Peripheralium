package site.siredvin.peripheralium.computercraft.peripheral.owner

import site.siredvin.peripheralium.api.config.IOperationAbilityConfig
import site.siredvin.peripheralium.api.peripheral.IOwnerAbility
import site.siredvin.peripheralium.computercraft.peripheral.ability.PeripheralOwnerAbility
import site.siredvin.peripheralium.api.peripheral.IPeripheralOwner
import site.siredvin.peripheralium.computercraft.peripheral.ability.OperationAbility

abstract class BasePeripheralOwner : IPeripheralOwner {
    private val _abilities: MutableMap<PeripheralOwnerAbility<*>, IOwnerAbility>

    init {
        _abilities = HashMap()
    }

    override val abilities: Collection<IOwnerAbility>
        get() = _abilities.values

    override fun <T : IOwnerAbility> attachAbility(ability: PeripheralOwnerAbility<T>, abilityImplementation: T) {
        if (_abilities.containsKey(ability))
            throw IllegalArgumentException("Ability $ability already registered")
        _abilities[ability] = abilityImplementation
    }

    override fun <T : IOwnerAbility> getAbility(ability: PeripheralOwnerAbility<T>): T? {
        @Suppress("UNCHECKED_CAST")
        return _abilities[ability] as T?
    }

    fun attachOperations(reduceRate: Double = 1.0, config: IOperationAbilityConfig) {
        val operationAbility = OperationAbility(this, reduceRate = reduceRate, config = config)
        attachAbility(PeripheralOwnerAbility.OPERATION, operationAbility)
    }
}