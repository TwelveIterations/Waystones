**Note: ** Durability costs have been moved to warp requirements.
1. Either regenerate your warp requirements configuration by removing the section from your config file,
2. or add `[source_is_warp_stone] add_durability_cost(80)` to them to restore previous behaviour

- Added `enableDurability` config option (default: on)
- Added `add_durability_cost` warp requirement (default: `[source_is_warp_stone] add_durability_cost(80)`)
- Increased warp stone durability to 10000 to allow for a bigger range in `add_durability_cost`
  - With the default cost of 80 durability, this still results in roughly 128 teleports