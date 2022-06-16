# BrickHolograms

An extension for [Minestom](https://github.com/Minestom/Minestom) to create (persistent) holograms.

## Install

Get the [release](https://github.com/GufliMC/BrickHolograms/releases)
and place it in the extension folder of your minestom server.

### Dependencies

* [BrickI18n](https://github.com/GufliMC/BrickI18n)
* [BrickPlaceholders](https://github.com/GufliMC/BrickPlaceholders)
* [BrickWorlds](https://github.com/GufliMC/BrickWorlds)

## Usage

| Command                               | Permission                |
|---------------------------------------|---------------------------|
| /bh create (name)                     | brickholograms.create     |
| /bh delete (hologram)                 | brickholograms.delete     |
| /bh addline (hologram) (text)         | brickholograms.addline    |
| /bh removeline (hologram) (index)     | brickholograms.removeline |
| /bh setline (hologram) (index) (text) | brickholograms.setline    |
| /bh tphere (hologram)                 | brickholograms.tphere     |
| /bh setitem (hologram)                | brickholograms.setitem    |
| /bh unsetitem (hologram)              | brickholograms.unsetitem  |


## Database

You can change the database settings in the `config.json`.

```json
{
  "database": {
    "dsn": "jdbc:h2:file:./extensions/BrickHolograms/data/database.h2",
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
    implementation 'com.guflimc.brick.holograms:minestom-api:+'
}
```

### Usage

Check the [javadocs](https://guflimc.github.io/BrickHolograms/)

