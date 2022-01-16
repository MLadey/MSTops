# MSTops - Многофункциональный плагин на топы
____

Плагин имеет достаточно большую и сложную кастомизацию, поэтому перед его использованием стоит прочитать документацию.

Автор плагина - `vk.com/m.ladey` | Вам нужна сборка или плагин? Пишите нам - `vk.com/gotovsborka`
____


## 1. Хранение и управление данными плагина

### 1.1. Подключение плагина к базе данных:

```
mysql:
  hostname: 127.0.0.1
  username: test
  password: "test"
  database: "mstops"
  table: "mstops"
  port: 3306
  options: "?useSSL=false" - в 99% случаев здесь менять ничего не нужно
```
Возможность выбора `table` добавлена для того, чтобы на многосерверных проектах можно было хранить таблицы в одной `database`

### 1.2. Загрузка данных в базу данных:

`Автоматическая` - при заходе на сервер происходит проверка наличия данных игрока, в случае их отсутствия они сразу же записываются. когда игрок выходит с сервера происходит перезапись данных в базу данных.

`Ручная` - если вам нужно загрузить/обновить данные сразу всех игроков, которые когда-либо играли на сервере, то вам необходимо написать команду - **/mstops**

`Промежуточная` - для лучшей оптимизации обновление данных игроков не происходит ежесекундно, как это делается в других плагинах. интервал между обновлениями статистики игроков, которые сейчас онлайн настраивается в конфиге:

```
update: 60
```

**Все конфигурации времени указываются в секундах**

## 2. Основная настройка плагина

### 2.1. Какие существуют виды топов?

`bukkit` - это топы основанные на статистике самого Minecraft. Например: кол-во прыжков, кол-во убитых мобов и т.д.

**Если вы не хотите использовать данный обработчик, то вы можете его выключить в конфиге:**
```
bukkit: true
```

`economy` - это валютные топы, которые работают по **Vault**

**Если вы не хотите использовать данный обработчик, то вы можете его выключить в конфиге:**
```
economy: true
```

`mysql` - это топы, которые работают по данным из других плагинов
```
Постоянного обработчика нет, он включается только при использовании данного типа топов
```

### 2.2. Очень важные настройки, которые нужно сделать в первую очередь:

#### 2.2.1. Список голограмм, которые будут создаваться при запуске сервера:
```
load:
  - deaths
```
**Это нужно, чтобы можно было сделать переключение голограмм**

#### 2.2.2. Список задержек между переключением голограмм:
```
cooldowns:
  - "deaths;economy;duels;3"
```
`deaths;economy;duels` - список голограмм, которые между собой переключаются, их может быть любое количество.

`3` - время между переключениями голограммы, советую ставить от **3 секунд**

`;` - разделитель

**Когда слишком быстро удаляются/создаются голограммы, HolographicDisplays API начинает выдавать ошибки, поэтому была добавлена данная функция**

### 2.3. Одинаковые настройки во всех топах:

**Пример настройки голограммы:**
```
holograms:
  название_топа:
    lines:
      - "Обычный текст"
      - "<clickable_$обработчик$>;Кликабельная строка (любой текст)"
      - "ICON;IRON_SWORD"
    settings:
      positions: 5
      location: "world, -164.49;62.0;148.66, 181.0;4.51"
      type: "тип_топа"
      update: 15
      clickable:
        line1:
          - "change;economy"
          - "message;&c&l MSTops &8| &fВы переключили страницу &8- &ceconomy"       
```

#### 2.3.1. Типы строк:

**Обычная текстовая строка:**
```
- "Обычный текст"
```

**Строка с предметом:**
```
- "ICON;IRON_SWORD"
```
`ICON` - тип строки

`IRON_SWORD` - название предмета

`;` - разделитель

**Кликабельная строка**
```
- "<clickable_$обработчик$>;Кликабельная строка (любой текст)"
settings:
  clickable:
    line1:
      - "change;economy"
      - "message;&c&l MSTops &8| &fВы переключили страницу &8- &ceconomy"
```
`<clickable_$обработчик$>` - Вместо $обработчик$ нужно вставить его название. Например, как в данном случае: **line1**

**Обработчики для строк могут быть одинаковыми**

#### 2.3.2. Типы обработчиков кликабельных голограмм:

```
clickable:
  line1:
    - "change;economy"
    - "message;&c&l MSTops &8| &fВы переключили страницу &8- &ceconomy"
    - "player;spawn"
    - "console;bc $player$ кликнул на голограмму"   
```

`change` - смена голограммы

`message` - отправка сообщения кликнувшему игроку

`player` - исполнение команды от имени игрока

`console` - исполнение команды от имени консоли

`;` - разделитель

**$player$ - ник игрока, который кликнул на голограмму**

**line1 - название обработчика. кол-во и название обработчиков могут быть любыми**

#### 2.3.2. Другие настройки:

`positions: 5` - количество мест в топе **(5 мест)**

`location:` - локация голограммы **(world;x;y;z)**

`type: "economy"` - тип голограммы **(economy;bukkit;mysql)**

`update: 60` - время между обновлениями голограммы **(60 секунд)**


### 2.4. Создание топов с использованием Bukkit статистики:

Для этого нужно указать типы статистик (например: кол-во смертей, кол-во убийств и т.д.), которые будет обрабатывать плагин. Это делается следующим образом:

```
statistics:
  - stat.deaths;deaths
  - stat.mobKills;mobKills
```	

`stat.deaths` - тип статистики, полный список всех статистики Minecraft можно найти здесь - **minecraft.fandom.com/el/wiki/Statistics**

`deaths` - название столбца в таблице, **не советую использовать точки и другие значки, из-за этого база данных скорее всего не будет работать**

`;` - разделитель между данными

**В настройках данного типа топов необходимо указать название столбца из базы данных, который вы указали ранее**

Например, как в данном случае: **deaths**

**Так же настройках голограммы укажите тип топа:**

```
settings:
  type: "bukkit"
  statistic: "deaths"
```

### 2.5. Создание топов с использованием Vault:
  
**Для работы данного типа топов обязательно нужен Vault**

**А так же его исполнитель, например: Essentials**

**В настройках голограммы укажите тип топа:**
```
settings:
  type: "economy"
```  

### 2.6. Создание топов на основе других плагинов:

**В настройках голограммы укажите тип топа:**
```
settings:
  type: "mysql"
```  

**Так же вам нужно указать подробные настройки базы данных в конфиге:**

```
settings:
  mysql:
    hostname: 127.0.0.1
    username: test
    password: "test"
    database: "stats"
    table: "grief1"
    port: 3306
    options: "?useSSL=false"
    column_name: "username"
    column_score: "rwins"  
```
**Настройки такие же, как и у основной базы данных. Добавляются только два новых значения:**

`column_name` - столбец, в котором будут ники игроков

`column_score` - столбец, в котором будут числовые значения

**Обращаю ваше внимание, что тип столбца column_score должен быть числовым (например: int, double и т.д.)**

## 3. Placeholders:

**Вместо # укажите номер места топа, например: 1**
```
<name_#> - имя игрока
```

**Вместо # укажите номер места топа, например: 1**
```
<score_#> - соответствующие числовое значение
```

**В обработчиках кликабельных строк, можно получить ник игрока:**
```
$player$
```

## 4. Информация о плагине и правах на него:

Версия плагина: `beta-1.0`

**В случае каких-то проблем, вы можете написать мне:**

Telegram: `@m_ladey`

VK: `vk.com/m.ladey`

**Если изменяете код плагина, то пожалуйста не убирайте из него авторские данные**

**Если будет хорошая отдача, то возможны обновления и публикация других моих плагинов**




  
