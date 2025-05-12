# Inventario Virtual de Medallas

Plugin para Minecraft que crea un inventario virtual donde se almacenan automáticamente ítems especiales como medallas o coleccionables.

## Características

- Los ítems especificados en la configuración se añaden automáticamente a un cofre virtual cuando el jugador los recoge
- Los ítems nunca ocupan espacio en el inventario normal del jugador
- Compatible con ítems vanilla y de mods (como Cobblemon)
- Soporte para namespaces con guiones (como `cobblemon-progress-items:rain_badge`)
- Compatible con ItemsAdder para ítems personalizados y fondos de GUI
- Configuración personalizable mediante YAML
- Comando `/medallas` para acceder al inventario virtual
- Capacidad para ver el inventario virtual de otros jugadores
- Integración con PlaceholderAPI - cada ítem configurado genera automáticamente su propio placeholder
- Persistencia de datos (los ítems se guardan incluso cuando el jugador se desconecta)

## Instalación

1. Descarga el archivo `.jar` de la última versión
2. Coloca el archivo en la carpeta `plugins/` de tu servidor
3. Asegúrate de tener PlaceholderAPI instalado si quieres usar los placeholders
4. (Opcional) Instala ItemsAdder si quieres usar ítems personalizados y GUI con texturas personalizadas
5. Reinicia el servidor

## Configuración

El archivo de configuración `config.yml` se crea automáticamente cuando se inicia el plugin por primera vez. Puedes editar los siguientes parámetros:

### Ejemplo de configuración

```yaml
# Configuración del Inventario Virtual
inventario:
  nombre: "Medallas Virtuales"  # Nombre del inventario
  tamaño: 27  # Tamaño del inventario (9, 27, o 54 slots)
  
  # Configuración de apariencia (ItemsAdder)
  itemsadder:
    # Establece a 'true' para usar texturas personalizadas en el menú
    habilitado: false
    # ID del ítem personalizado para el fondo del menú (ej: "custom_gui_background")
    fondo_menu: "custom_gui_background"
    # ID del ítem personalizado para representar los slots vacíos (ej: "custom_gui_empty_slot")
    slot_vacio: "custom_gui_empty_slot"

# Lista de ítems que serán capturados por el inventario virtual
items:
  # Items de Minecraft vanilla
  - DIAMOND
  - EMERALD
  - GOLD_INGOT
  
  # Items de mods (ejemplos)
  - cobblemon:boulder_badge
  - cobblemon:cascade_badge
  
  # Items de mods con namespaces que contienen guiones (ejemplos)
  - cobblemon-progress-items:rain_badge
  - cobblemon-progress-items:boulder_badge
  
  # Items de ItemsAdder (ejemplos)
  - itemsadder:ruby
  - itemsadder:sapphire
  
mensajes:
  item_añadido: "&a¡Has conseguido un nuevo ítem para tu colección de medallas!"
  inventario_abierto: "&6Abriendo tu colección de medallas virtuales..."

# Configuración avanzada
debug: false  # Establece a 'true' para ver información de depuración en la consola
```

## Comandos

- `/medallas` - Abre tu propio inventario virtual de medallas
- `/medallas <jugador>` - Abre el inventario virtual de medallas de otro jugador
  - Alias: `/medallasvirtuales`, `/vm`
- `/mvs reload` - Recarga la configuración del plugin (útil después de editar config.yml)
  - Alias: `/medallassystem reload`, `/virtualsystem reload`
- `/mvs help` - Muestra la ayuda con todos los comandos disponibles

## Guía para IDs de ítems

### Formatos soportados

El plugin soporta varios formatos de IDs para los ítems:

1. **Ítems vanilla**: Usa el ID en mayúsculas (ej: `DIAMOND`, `EMERALD`, `GOLD_INGOT`)

2. **Ítems de mods con namespaces simples**: Usa el formato `namespace:id` (ej: `cobblemon:boulder_badge`)

3. **Ítems de mods con namespaces que contienen guiones**: Escribe el ID exactamente como aparece en el juego, incluyendo los guiones (ej: `cobblemon-progress-items:rain_badge`)

4. **Ítems de ItemsAdder**: Usa el formato `itemsadder:id` (ej: `itemsadder:ruby`)

### Cómo verificar los IDs exactos

Para encontrar el ID exacto de un ítem:

1. En el juego, puedes usar el comando `/data get entity @s SelectedItem` mientras sostienes el ítem para ver su ID completo.

2. Para ítems de mods con namespaces que contienen guiones, asegúrate de escribir el ID exactamente como aparece, por ejemplo: `cobblemon-progress-items:rain_badge`.

3. Si tienes problemas, activa el modo debug en config.yml (`debug: true`) para ver más información en la consola.

## Placeholders

Este plugin se integra con PlaceholderAPI para proporcionar placeholders dinámicos basados en los ítems configurados:

- `%mv_ITEM%` - Muestra la cantidad del ítem en el inventario virtual (ej: `%mv_DIAMOND%`)
- `%mv_has_ITEM%` - Muestra "true" o "false" dependiendo de si el jugador tiene el ítem (ej: `%mv_has_DIAMOND%`)
- `%mv_total%` - Muestra el número total de ítems diferentes en el inventario virtual

Para ítems de mods o ItemsAdder, reemplaza los dos puntos `:` con un guión bajo `_`. Por ejemplo:
- `%mv_cobblemon_boulder_badge%` - Para obtener la cantidad de "cobblemon:boulder_badge"
- `%mv_itemsadder_ruby%` - Para obtener la cantidad de "itemsadder:ruby"

Para ítems con namespaces que contienen guiones, reemplaza tanto los dos puntos `:` como los guiones `-` con guiones bajos `_`. Por ejemplo:
- `%mv_cobblemon_progress_items_rain_badge%` - Para "cobblemon-progress-items:rain_badge"

## Permisos

Este plugin no utiliza permisos específicos. Todos los jugadores pueden usar el comando `/medallas`.

## Solución de problemas

Si tienes problemas con ítems que no se detectan correctamente:

1. Establece `debug: true` en el archivo config.yml para ver más información de depuración en la consola.

2. Asegúrate de escribir el ID exacto del ítem en el archivo config.yml, especialmente para ítems con namespaces complejos.

3. Si usas placeholders, asegúrate de haber reemplazado todos los caracteres especiales (como `:` y `-`) con guiones bajos `_`.

4. Después de modificar la configuración, usa el comando `/mvs reload` para aplicar los cambios sin reiniciar el servidor.

## Soporte

Si tienes algún problema o sugerencia, por favor, crea un issue en el repositorio del proyecto.

---

# Virtual Medal Inventory

A Minecraft plugin that creates a virtual inventory where special items like medals or collectibles are automatically stored.

## Features

- Items specified in the configuration are automatically added to a virtual chest when the player collects them
- Items never take up space in the player's normal inventory
- Compatible with vanilla items and mods (like Cobblemon)
- Support for namespaces with hyphens (like `cobblemon-progress-items:rain_badge`)
- Compatible with ItemsAdder for custom items and GUI backgrounds
- Customizable configuration via YAML
- `/medallas` command to access the virtual inventory
- Ability to view other players' virtual inventories
- PlaceholderAPI integration - each configured item automatically generates its own placeholder
- Data persistence (items are saved even when the player disconnects)

## Installation

1. Download the `.jar` file of the latest version
2. Place the file in the `plugins/` folder of your server
3. Make sure you have PlaceholderAPI installed if you want to use placeholders
4. (Optional) Install ItemsAdder if you want to use custom items and GUI with custom textures
5. Restart the server

## Configuration

The `config.yml` configuration file is automatically created when the plugin is started for the first time. You can edit the following parameters:

### Configuration Example

```yaml
# Virtual Inventory Configuration
inventario:
  nombre: "Medallas Virtuales"  # Inventory name
  tamaño: 27  # Inventory size (9, 27, or 54 slots)
  
  # Appearance configuration (ItemsAdder)
  itemsadder:
    # Set to 'true' to use custom textures in the menu
    habilitado: false
    # Custom item ID for menu background (e.g.: "custom_gui_background")
    fondo_menu: "custom_gui_background"
    # Custom item ID to represent empty slots (e.g.: "custom_gui_empty_slot")
    slot_vacio: "custom_gui_empty_slot"

# List of items that will be captured by the virtual inventory
items:
  # Vanilla Minecraft items
  - DIAMOND
  - EMERALD
  - GOLD_INGOT
  
  # Mod items (examples)
  - cobblemon:boulder_badge
  - cobblemon:cascade_badge
  
  # Mod items with namespaces containing hyphens (examples)
  - cobblemon-progress-items:rain_badge
  - cobblemon-progress-items:boulder_badge
  
  # ItemsAdder items (examples)
  - itemsadder:ruby
  - itemsadder:sapphire
  
mensajes:
  item_añadido: "&a¡Has conseguido un nuevo ítem para tu colección de medallas!"  # "You've got a new item for your medal collection!"
  inventario_abierto: "&6Abriendo tu colección de medallas virtuales..."  # "Opening your virtual medal collection..."

# Advanced configuration
debug: false  # Set to 'true' to see debugging information in the console
```

## Commands

- `/medallas` - Opens your own virtual medal inventory
- `/medallas <player>` - Opens another player's virtual medal inventory
  - Aliases: `/medallasvirtuales`, `/vm`
- `/mvs reload` - Reloads the plugin configuration (useful after editing config.yml)
  - Aliases: `/medallassystem reload`, `/virtualsystem reload`
- `/mvs help` - Shows help with all available commands

## Item ID Guide

### Supported Formats

The plugin supports various ID formats for items:

1. **Vanilla items**: Use the ID in uppercase (e.g.: `DIAMOND`, `EMERALD`, `GOLD_INGOT`)

2. **Mod items with simple namespaces**: Use the format `namespace:id` (e.g.: `cobblemon:boulder_badge`)

3. **Mod items with namespaces containing hyphens**: Write the ID exactly as it appears in the game, including hyphens (e.g.: `cobblemon-progress-items:rain_badge`)

4. **ItemsAdder items**: Use the format `itemsadder:id` (e.g.: `itemsadder:ruby`)

### How to Verify Exact IDs

To find the exact ID of an item:

1. In the game, you can use the command `/data get entity @s SelectedItem` while holding the item to see its complete ID.

2. For mod items with namespaces containing hyphens, make sure to write the ID exactly as it appears, for example: `cobblemon-progress-items:rain_badge`.

3. If you have problems, activate debug mode in config.yml (`debug: true`) to see more information in the console.

## Placeholders

This plugin integrates with PlaceholderAPI to provide dynamic placeholders based on configured items:

- `%mv_ITEM%` - Shows the amount of the item in the virtual inventory (e.g.: `%mv_DIAMOND%`)
- `%mv_has_ITEM%` - Shows "true" or "false" depending on whether the player has the item (e.g.: `%mv_has_DIAMOND%`)
- `%mv_total%` - Shows the total number of different items in the virtual inventory

For mod or ItemsAdder items, replace the colon `:` with an underscore `_`. For example:
- `%mv_cobblemon_boulder_badge%` - To get the amount of "cobblemon:boulder_badge"
- `%mv_itemsadder_ruby%` - To get the amount of "itemsadder:ruby"

For items with namespaces containing hyphens, replace both the colon `:` and hyphens `-` with underscores `_`. For example:
- `%mv_cobblemon_progress_items_rain_badge%` - For "cobblemon-progress-items:rain_badge"

## Permissions

This plugin does not use specific permissions. All players can use the `/medallas` command.

## Troubleshooting

If you have problems with items that are not detected correctly:

1. Set `debug: true` in the config.yml file to see more debugging information in the console.

2. Make sure to write the exact ID of the item in the config.yml file, especially for items with complex namespaces.

3. If you use placeholders, make sure you have replaced all special characters (like `:` and `-`) with underscores `_`.

4. After modifying the configuration, use the `/mvs reload` command to apply the changes without restarting the server.

## Support

If you have any problems or suggestions, please create an issue in the project repository. 