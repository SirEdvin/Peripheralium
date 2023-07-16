# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

- Minecart entity support for `extractStorage`
- `tint` handling logic
- `AABB` and `IArgument` extension methods
- ItemStack lua formatting
- Interfaces for upgrade that contains other upgrades. Useful for different render tricks and other staff

### Fixed

- Scanning method names [UnlimitedPeripheralWorks#22](https://github.com/SirEdvin/UnlimitedPeripheralWorks/issues/22)
- Issue with block tooltip generations
- `block` scan always being empty [UnlimitedPeripheralWorks#22](https://github.com/SirEdvin/UnlimitedPeripheralWorks/issues/22)

### Changed

- Some turtle and pocket interfaces location

## [0.6.4] - 2023-07-09

### Added

- Ability to customize item created from BaseNBTBlock
- `keySet` method for registries
- `isSubSet` method for NBTUtil
- Ability to change update flag for `pushToClient` method
- `triggerRender` now is more cross-plat than before
- CC:T 1.106.1 port
- Stateful turtle and pocket upgrades

### Changed

- TextRecord now required to be mutable
- Fluid and FluidStack now LuaRepresentable

### Removed

- concept of initial operation cooldown which is no longer relevant (finally!)

## [0.6.3] - 2023-06-24

### Added

- `1.20.1` support
- New xplat fluid system

## [0.6.2] - 2023-06-13

### Fixed

- Storing items inside turtle via peripheral owner system

## [0.6.1] - 2023-06-13

### Fixed

- Dependencies definition

## [0.6.0] - 2023-06-13

### Added

- `1.20` support
- Peripheralium upgrade template for smithing recipes

## [0.5.7] - 2023-06-12

### Added

- A lot of data generation helpers

## [0.5.6] - 2023-06-04

### Added

- `getPeripheral` xplat API
- `health` for LivingEntity interpretation everywhere

### Changed

- Scanning now is ability with infinite scanning possibilities

### Removed

- AreaInteractionMode, which was used only for scanning logic

## [0.5.5] - 2023-05-28

### Changed

- Fabric version now also use FakePlayer from API

## [0.5.4] - 2023-05-24

### Removed

- Forge entity inventory extractor, because it leads to incorrect behaviors. Entity inventory is a mess of slots, so direct exposition of this inventory is not correct approach

## [0.5.3] - 2023-05-23

### Breaking

- Refactoring of storage extraction logic, allows to extract from entities and make extract target types more determinated

### Added

- Tags generation from blocks, items and entities

### Changed

- Make fluid information for forge and fabric tweaked in child classes

### Fixed

- ForgeFluidStorage plugins methods for pull/push fluids

## [0.5.2] - 2023-05-16

### Breaking

- Operations now list instead of array

### Added

- PeripheralOwner now provides access to inventory, if possible
- PocketPeripheralOwner.pocket is now public
- XPlat API for retrieving turtle and pocket upgrades
- PeripheralItem/PeripheralBlockItem with tooltip logic
- Plugin for scanning
- CreativeTab for peripheralium
- Fuel logic for pocket computers
- Extra BlockEntity tools, like storing ownership
- New platform methods: isOre, createTurtle, createPocket, createBlockEntity

### Changed

- Xplat registries are now lazy
- Representation `forItemStack` now supports detail mods

### Removed

- All basic configuration

## [0.5.1] - 2023-05-08

### Added

- 1.19.4 support
- Forge support

## [0.4.20] - 2023-04-27

### Added

- `onFirstAttach` and `onLastDetach` hooks for plugins
- `nbt` field for item details

## [0.4.19] - 2023-04-25

### Added

- Many methods for working with trade entities

### Changed

- Mob effects migrated to normal ID format
- ItemStack now will include enchantments

## [0.4.17] - 2022-12-26

### Added

- `asBlockPos` for relative-facing logic

### Fixed

- Plugin interconnection logic (from Turtlematic issue)[https://github.com/SirEdvin/Turtlematic/issues/2]
- A lot of thinks in NBTBlock logic

### Changed

- `PeripheralBlockEntity` now can ignore peripheral providing part

## [0.4.3] - 2022-11-02

### Added

- Add cross-mod resource - peripheralium
- Recipe builders for smelting and shapeless recipes
- BlockEntity support as library part
- Plugins for some nice base integrations
- Base event logic for plugins
