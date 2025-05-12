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

## English Section

### Virtual Inventory System

A Minecraft plugin that allows players to collect and display virtual items (medals, badges, etc.) in a special inventory interface.

#### Features

- Virtual inventory system for collecting and displaying items
- Support for vanilla Minecraft items and custom items from ItemsAdder
- Special integration with Cobblemon badges
- Customizable GUI with ItemsAdder support
- Offline player data management
- PlaceholderAPI integration
- Administrative commands for inventory management

#### Commands

- `/medallas` or `/vm` - Opens your virtual inventory
- `/medallas <player>` - Opens another player's virtual inventory
- `/mvs reload` - Reloads the plugin configuration
- `/mvs clear <player>` - Clears a player's virtual inventory
- `/mvs help` - Shows available commands

#### Permissions

- `virtualinventory.reload` - Allows reloading the plugin configuration
- `virtualinventory.clear` - Allows clearing a player's virtual inventory

#### Dependencies

- PlaceholderAPI (optional)
- ItemsAdder (optional)

#### Configuration

The plugin can be configured through the `config.yml` file, where you can set:
- Inventory name and size
- Items to track
- GUI customization options
- Integration settings
- Messages and notifications

#### Building

To build the plugin:
1. Clone the repository
2. Run `./gradlew build`
3. Find the compiled plugin in `build/libs/` 