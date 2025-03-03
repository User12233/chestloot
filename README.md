## Chest loot
`/placeloot` - place the chest loot, player must be admin in config (required to look down)

## Config explain
`LootTable` is list of items for give this to player

`amountOfRareItems` amount of items which are 10 %, required to be like ["default","default","rare","rare","rare"] size of this list is 5, but we need to mark rare items, so we put into value 2, cause we need to choose last rare item from the end of list

`chestPositions` chest positions which will respawning every 9000 ticks

`admins` players that allowed to use command /placeloot

### Config located in .minecraft\saves\map\serverconfig\chestloot-server.toml
