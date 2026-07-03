Waystones is now 10 years old! Enjoy this anniversary update.

- Added a new "Epitaph" item and a corresponding "Fleeting Memorial" block
    - Having this item in your inventory while you die will consume it and spawn a Fleeting Memorial at your place of death.
    - Using a waystone will offer the Fleeting Memorial as a teleport destination. It will disappear once you teleport to it.
- Added a new "Twinbound Feather" item
    - Wave it against another player's feather to link the two.
    - As long as you both have the feather in your inventory, you can now teleport directly to each other using a waystone.
- Added a new "Portal Scroll" item
    - Originally added in Waystones for Bedrock, use the scroll to open a physical portal to a destination.
    - Players, mobs and items can all go through the portal. The portal will disappear after five minutes.
    - Use a waystone and select the new "Return to Portal" option that appears to go back to the portal.
- Added a new "Visible to Team" option that works for Vanilla scoreboard teams
- Changed teleports to no longer block the main thread while loading chunks
    - This should prevent lag spikes during teleport on slower servers
- Changed the waystone, portstone and sharestone runes to render using a regular block model
    - This allows them to be changed using resource packs
- Changed the way global waystones are shared with other players
    - Previously, a global waystone would physically activate the waystone for every player.
    - This meant you couldn't easily undo changing the visibility, since players would still have it activated even if you made it non-global later.
    - Now, global waystones don't actually count as activated, but it will still show up in everyone's list as long as it remains globally visible.
    - Players can still go and activate the waystone for themselves personally.
- Fixed portstones showing "no activated waystones" error even if they are scoped to a specific sharestone
- Fixed crash when trying to edit warp plate
- Removed Black from Group Colors as it can't be read in text fields

Already shipped in Part 1 of this update:

- Added a scrollable waystone list, replacing the previous pagination system
- Added personal aliases for waystones
    - There is a "Personal Settings" button that allows assigning an alias
    - This is useful for multiplayer cases where others have used names that are too generic
- Added group filters to the waystone selection screen
    - This includes inbuilt groups like "Global Waystones", "Favorites", and a group for each dimension
    - You can also create custom groups, assigning an icon and a text color; groups are personal and not shared
    - The first five groups will be available as filters on the waystone selection screen
    - You can reorder groups or hide/show inbuilt ones to customize what filters are shown
- Added "Favorites" - a checkbox in a waystone's Personal Settings that will add it to the inbuilt Favorites group
- Added a "Sort" button to the waystone selection, cycling between Manual, Alphabetic, and Distance
- Added a separate "Manage Waystones" screen, allowing drag-drop rearranging of the waystone list
    - Deletion has been moved to this screen as well
- Added a small dimension button overlay for Nether and End waystones to the selection list
