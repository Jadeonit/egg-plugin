# Egg

Неприметный плагин для **Paper / Leaf 1.21.11**.

Одна команда, один залп: снаряд падает с неба на выбранного игрока.
Снарядом может быть любая спавнящаяся сущность: TNT, яйца, вардены, зомби и т.д.

[Скачать последний релиз](https://github.com/Jadeonit/egg-plugin/releases) · [Лицензия MIT](LICENSE)

- Любой снаряд: TNT, яйца, вардены, зомби и другие сущности.
- Мобы падают плавно (Slow Falling) и не получают урон от падения.
- Любое количество, включая `unlimited`.
- Запуск командой или уже настроенной удочкой.

## Команда

```
/egg <снаряд|orbital|stab> <кол-во> <цель> <fire|rod>
```

| Аргумент | Значения |
|---|---|
| `снаряд` | `tnt`, `egg`, `warden`, `zombie`, `creeper`, `falling_block`, ... |
| `режим` | `orbital` — орбитальный залп; `stab` — скоростной удар вниз; можно совмещать с типом: `orbital:warden`, `stab:creeper` |
| `кол-во` | число или `unlimited` |
| `цель` | ник игрока, `me` / `я` |
| `активация` | `fire` / `запуск` / `команда` — залп сразу; `rod` / `удочка` — выдать уже настроенную удочку |

Примеры:

```
/egg tnt 100 Steve fire
/egg warden 300 Steve rod
/egg egg unlimited Alex удочка
/egg creeper 50 me запуск
/egg orbital 100 Jadeonit rod
/egg stab 100 Jadeonit fire
/egg orbital:warden 50 Steve rod
```

## Режимы

Режим указывается вместо типа снаряда (или через двоеточие вместе с типом).

| Режим | Что делает |
|---|---|
| `orbital` | спавнит снаряды на `+300` блоков над целью (с ограничением по высоте мира) и сбрасывает вниз — «орбитальный» залп. |
| `stab` | спавнит там же, но с высокой начальной скоростью вниз (`-8.0`), чтобы заряд вбивался в землю и взрывался у поверхности. |

Если указать только режим, используется `modes.<режим>.projectile` из конфига (по умолчанию `TNT`).
Можно выбрать сущность явно: `/egg orbital:warden 100 Steve rod`.

Высота, разброс, радиус, скорость и фитиль TNT настраиваются отдельно для каждого режима в секции `modes`.

## Маскировка

- Команда называется `/egg` (алиас `/eg`), описание — `Egg helper`.
- Плагин в `/plugins` отображается как **Egg**.
- Tab-подсказки отключены — команда не светится в автодополнении.
- Сообщения по умолчанию без префикса (`messages.prefix: ""`).
- Удочка по умолчанию выглядит как обычная: без кастомного имени и без лора
  (`disguise.rod-name: ""`, `disguise.rod-lore: false`). Снаряд, количество и цель
  хранятся в `PersistentDataContainer` и игроку не видны.
- Права `egg.*` по умолчанию только у операторов; без права команда отвечает
  `Unknown command.`

Если для отладки нужно видеть параметры удочки — включите `disguise.rod-lore: true`.

## AI disclosure

Проект создан с помощью ИИ и проверен вручную на Paper 1.21.11.

## Сборка

Нужен **JDK 21** и **Maven**.

```bash
mvn clean package
```

Готовый jar: `target/Egg.jar`.

В этом проекте уже собранный jar лежит по этому пути. Если Maven/JDK нет в PATH,
они лежат локально в `C:\Users\user\.tools`:

```powershell
$env:JAVA_HOME="$env:USERPROFILE\.tools\jdk-21.0.13+9"
$env:Path="$env:JAVA_HOME\bin;$env:USERPROFILE\.tools\apache-maven-3.9.9\bin;$env:Path"
mvn -f C:\Users\user\egg-plugin\pom.xml clean package
```

## Установка

1. Положите `Egg.jar` в папку `plugins/` сервера.
2. Перезапустите сервер.
3. Конфиг: `plugins/Egg/config.yml`.

Перезагрузка конфига без рестарта: `/egg reload` (право `egg.admin`).

## Права

| Право | По умолчанию | Что даёт |
|---|---|---|
| `egg.use` | op | Доступ к `/egg` |
| `egg.rod` | op | Выдача удочки |
| `egg.unlimited` | op | Режим `unlimited` |
| `egg.bypasscap` | op | Игнорировать `safety.max-entities-per-fire` |
| `egg.bypasscooldown` | op | Игнорировать кулдаун |
| `egg.admin` | op | `/egg reload` |

## Конфиг

- `disguise.rod-name` — имя удочки (`""` = как обычная).
- `disguise.rod-lore` — показывать снаряд/цель в описании.
- `messages.prefix` — префикс сообщений (`""` = без префикса).
- `defaults.projectile`, `defaults.count` — значения по умолчанию.
- `spawn.height`, `spawn.radius`, `spawn.velocity` — геометрия падения.
- `spawn.per-tick` — сколько сущностей спавнить за тик (антилаг).
- `spawn.tnt-fuse-extra` — доп. тики к фитилю TNT (фитиль считается от высоты).
- `spawn.disable-ai` — отключить AI у мобов.
- `modes.orbital.projectile`, `modes.orbital.height`, `modes.orbital.height-jitter`, `modes.orbital.radius`, `modes.orbital.velocity`, `modes.orbital.tnt-fuse-extra` — параметры орбитального режима.
- `modes.stab.projectile`, `modes.stab.height`, `modes.stab.height-jitter`, `modes.stab.radius`, `modes.stab.velocity`, `modes.stab.tnt-fuse-extra` — параметры режима `stab`.
- `mobs.slow-falling` — плавное падение мобов (Slow Falling).
- `mobs.slow-falling-seconds` — длительность эффекта.
- `mobs.fall-speed` — начальная скорость падения мобов (отрицательная — вниз).
- `safety.max-entities-per-fire` — жёсткий лимит на залп (`-1` = без лимита).
- `safety.unlimited-cap` — сколько реально спавнить при `unlimited` (`-1` = без лимита).
- `safety.cleanup-seconds` — автоудаление снарядов (`0` = не удалять).
- `safety.blocked-types` — запрещённые типы сущностей.
- `cooldown-seconds` — кулдаун между залпами.
- `effects.launch-sound` — звук запуска.

### Про «безлимит»

`unlimited` спавнит столько, сколько указано в `safety.unlimited-cap`
(по умолчанию 500), но не больше `safety.max-entities-per-fire` (по умолчанию 2000).
Чтобы снять лимиты, поставьте оба значения в `-1` и выдайте право `egg.unlimited`.
Внутренний предохранитель — 100 000 сущностей за залп.
