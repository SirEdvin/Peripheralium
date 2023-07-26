# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [0.4.22] - 2023-07-26

### Fixed

- PluggablePeripheral now passed equals check correctly

## [0.4.21] - 2023-05-18

### Fixed

- PluggablePeripheral without plugins

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
