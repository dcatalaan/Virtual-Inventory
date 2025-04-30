# Inventario Virtual de Medallas

Plugin para Minecraft que crea un inventario virtual donde se almacenan automáticamente ítems especiales como medallas o coleccionables.

## Características

- Los ítems especificados en la configuración se añaden automáticamente a un cofre virtual cuando el jugador los recoge.
- Los ítems nunca ocupan espacio en el inventario normal del jugador.
- Compatible con ítems vanilla y de mods (como Cobblemon, incluyendo namespaces con guiones).
- Compatible con ItemsAdder para ítems personalizados y fondos de GUI.
- Configuración personalizable mediante YAML.
- Comando `/medallas` para acceder al inventario virtual.
- Capacidad para ver el inventario virtual de otros jugadores.
- Integración con PlaceholderAPI: cada ítem configurado genera automáticamente su propio placeholder.
- Persistencia de datos (los ítems se guardan incluso cuando el jugador se desconecta).
- Sistema de depuración avanzado para solucionar problemas.

## Instalación

1. Descarga el archivo `.jar` de la última versión.
2. Coloca el archivo en la carpeta `plugins/` de tu servidor.
3. Asegúrate de tener PlaceholderAPI instalado si quieres usar los placeholders.
4. (Opcional) Instala ItemsAdder si quieres usar ítems personalizados y GUI con texturas personalizadas.
5. Reinicia el servidor.

## Configuración

El archivo de configuración `config.yml` se crea automáticamente cuando se inicia el plugin por primera vez. Puedes editar los siguientes parámetros:

### Ejemplo de configuración

```yaml
# Configuración del Inventario Virtual
inventario:
  nombre: "Medallas Virtuales"  # Nombre del inventario
  tamaño: 9  # Tamaño del inventario (9, 27, o 54 slots)
  
  # Configuración de apariencia (ItemsAdder)
  itemsadder:
    habilitado: false
    fondo_menu: "custom_gui_background"
    slot_vacio: "custom_gui_empty_slot"

# Lista de ítems que serán capturados por el inventario virtual
items:
  # Ítems de Minecraft vanilla
  - DIAMOND
  - EMERALD
  - GOLD_INGOT
  
  # Ítems de mods (ejemplos, incluyendo namespaces con guiones)
  - cobblemon:boulder_badge
  - cobblemon-progress-items:rain_badge
  
  # Ítems de ItemsAdder (ejemplos)
  - itemsadder:ruby
  - itemsadder:sapphire
  
mensajes:
  item_añadido: "&a¡Has conseguido un nuevo ítem para tu colección de medallas!"
  inventario_abierto: "&6Abriendo tu colección de medallas virtuales..."

# Configuración avanzada
debug: false  # Establece a 'true' para ver información de depuración en la consola
```

## Comandos

- `/medallas` - Abre tu propio inventario virtual de medallas.
- `/medallas <jugador>` - Abre el inventario virtual de medallas de otro jugador.
  - Alias: `/medallasvirtuales`, `/vm`
- `/mvs reload` - Recarga la configuración del plugin (útil después de editar config.yml).
  - Alias: `/mvsr`, `/medallasreload`

## Guía de uso paso a paso

### 1. Configuración básica

1. Después de instalar el plugin y reiniciar el servidor, el archivo `config.yml` se generará en la carpeta `plugins/virtualInventory/`.
2. Edita el archivo `config.yml` para personalizar:
   - El nombre del inventario virtual (`inventario.nombre`).
   - El tamaño del inventario (`inventario.tamaño`) - puede ser 9, 27 o 54 slots.
   - Los mensajes que se muestran a los jugadores.

### 2. Configuración de ítems

1. En la sección `items` del archivo `config.yml`, añade los ítems que quieres que se guarden en el inventario virtual.
2. Puedes añadir:
   - Ítems de Minecraft vanilla usando su ID (ej: `DIAMOND`, `EMERALD`, etc.).
   - Ítems de mods usando el formato `namespace:id` (ej: `cobblemon:boulder_badge`, `cobblemon-progress-items:rain_badge`).
   - Ítems de ItemsAdder usando el formato `itemsadder:id` (ej: `itemsadder:ruby`).

### 3. Uso del inventario virtual

1. Cuando un jugador recoge un ítem que está en la lista configurada, automáticamente:
   - El ítem no entrará en su inventario normal.
   - El ítem se añadirá a su inventario virtual.
   - El jugador recibirá un mensaje de confirmación.

2. Para ver el inventario virtual:
   - El jugador puede usar el comando `/medallas`.
   - Para ver el inventario de otro jugador, puede usar `/medallas <nombre_jugador>`.

### 4. Integración con ItemsAdder (opcional)

1. Asegúrate de que ItemsAdder esté instalado en tu servidor.
2. En el archivo `config.yml`, establece `inventario.itemsadder.habilitado` a `true`.
3. Configura las IDs de los ítems personalizados que quieres usar como fondo y slots vacíos.
4. Añade ítems personalizados de ItemsAdder a la lista de ítems usando el formato `itemsadder:id`.

**Formato correcto de IDs de ItemsAdder:**  
Los IDs de ItemsAdder suelen tener el formato `namespace:item_id`.  
Por ejemplo: `kryuk:panel_negro`, `kryuk:slot_vacio`.

**Ejemplo práctico:**
```yaml
inventario:
  nombre: "Medallas Virtuales"
  tamaño: 27
  itemsadder:
    habilitado: true
    fondo_menu: "kryuk:panel_negro"
    slot_vacio: "kryuk:slot_vacio"
```
Después de guardar estos cambios y ejecutar `/mvs reload`, cuando los jugadores abran el inventario virtual con `/medallas`, verán el fondo personalizado y los slots vacíos con los ítems configurados.

### 5. Integración con PlaceholderAPI (opcional)

1. Asegúrate de que PlaceholderAPI está instalado en tu servidor.
2. El plugin registrará automáticamente los siguientes placeholders:
   - `%mv_ITEM%` - Muestra la cantidad del ítem en el inventario virtual (ej: `%mv_DIAMOND%`).
   - `%mv_has_ITEM%` - Muestra "true" o "false" dependiendo de si el jugador tiene el ítem.
   - `%mv_total%` - Muestra el número total de ítems diferentes en el inventario virtual.

3. Para ítems de mods o ItemsAdder, reemplaza los dos puntos `:` con un guión bajo `_`. Por ejemplo:
   - `%mv_cobblemon_boulder_badge%` - Para obtener la cantidad de "cobblemon:boulder_badge".
   - `%mv_cobblemon-progress-items_rain_badge%` - Para obtener la cantidad de "cobblemon-progress-items:rain_badge".
   - `%mv_itemsadder_ruby%` - Para obtener la cantidad de "itemsadder:ruby".

### 6. Recarga de configuración

1. Después de hacer cambios en el archivo `config.yml`, puedes recargar la configuración sin reiniciar el servidor usando el comando `/mvs reload`.
2. Esto aplicará inmediatamente los cambios realizados, como nuevos ítems añadidos o cambios en la apariencia.

## Solución de problemas

1. **Los ítems no se añaden al inventario virtual:**
   - Asegúrate de que los IDs de los ítems están escritos correctamente en el archivo config.yml.
   - Para ítems vanilla, usa los nombres exactos (ej: `DIAMOND`, `EMERALD`).
   - Para ítems de mods, usa el formato correcto (ej: `cobblemon:boulder_badge`, `cobblemon-progress-items:rain_badge`).
   - Habilita el modo de depuración estableciendo `debug: true` en config.yml para ver información detallada en la consola.

2. **Problemas con ItemsAdder:**
   - Asegúrate de que ItemsAdder está instalado y funcionando correctamente.
   - Verifica que las IDs de los ítems personalizados son correctas.
   - Si el plugin no detecta ItemsAdder, revisa los logs del servidor para más información.

3. **Placeholders no funcionan:**
   - Asegúrate de que PlaceholderAPI está instalado.
   - Verifica que estás usando el formato correcto para los placeholders.
   - Recuerda reemplazar los dos puntos `:` con guiones bajos `_` en los placeholders.

## Permisos

Este plugin no utiliza permisos específicos. Todos los jugadores pueden usar el comando `/medallas`.