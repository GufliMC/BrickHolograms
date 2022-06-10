# BrickCreatures

An extension for [Minestom](https://github.com/Minestom/Minestom) to create persistent NPCs.

## Install

Get the [release](https://github.com/GufliMC/BrickCreatures/releases)
and place it in the extension folder of your minestom server.

### Dependencies

* [BrickI18n](https://github.com/GufliMC/BrickI18n)

## Usage

### Creatures

A creature contains all the necessary information that makes the entity unique (name, skin, traits, ...). A single
creature can be used for multiple spawns, changing a creature will also update all spawns.

| Command                                        | Permission                              |
|------------------------------------------------|-----------------------------------------|
| /bc creature list                              | brickcreatures.creature.list            |
| /bc creature create (name) (entitytype)        | brickcreatures.creature.create          |
| /bc creature delete (creature)                 | brickcreatures.creature.delete          |
| /bc creature edit customname (creature) (name) | brickcreatures.creature.edit.customname |
| /bc creature edit skin (creature) (player)     | brickcreatures.creature.edit.skin       |

### Spawns

A spawn is a position in the world where an persistentCreature is spawned with a specific creature. You can create
multiple spawns with the same creature.

| Command                            | Permission                         |
|------------------------------------|------------------------------------|
| /bc spawn list                     | brickcreatures.spawn.list          |
| /bc spawn create (name) (creature) | brickcreatures.spawn.create        |
| /bc spawn delete (spawn)           | brickcreatures.spawn.delete        |
| /bc spawn edit lookhere (spawn)    | brickcreatures.spawn.edit.lookhere |
| /bc spawn edit tphere (spawn)      | brickcreatures.spawn.edit.tphere   |

## Database

You can change the database settings in the `config.json`.

```json
{
  "database": {
    "dsn": "jdbc:h2:file:./extensions/BrickCreatures/data/database.h2",
    "username": "dbuser",
    "password": "dbuser"
  }
}
```

MySQL is supported, use the following format:

````
"dsn": "jdbc:mysql://<hostname>:<ip>/<database>"
````

## API

### Gradle

```
repositories {
    maven { url "https://repo.jorisg.com/snapshots" }
}

dependencies {
    implementation 'com.guflimc.brick.creatures:api:1.0-SNAPSHOT'
}
```

### Usage

Check the [javadocs](https://guflimc.github.io/BrickCreatures/)

