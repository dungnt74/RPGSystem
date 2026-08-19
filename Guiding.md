# RPGSystem — Command Guide

> Tài liệu này mô tả **cấu trúc command hiện tại** của source.
>
> Command quản trị RPG đã được gom về một root duy nhất:
>
> ```text
> /rpg
> ├── item
> ├── mob
> ├── gem
> └── socket
> ```
>
> `dungnt.team` và `dungnt.socket` vẫn là package độc lập với `dungnt.rpg`; `/rpg` chỉ là command entry point để quản trị chúng.

---

## 1. Nguyên tắc command

### Root command

```text
/rpg
```

Khi gõ:

```text
/rpg
```

hoặc:

```text
/rpg help
```

sẽ hiện các nhóm:

```text
item
mob
gem
socket
reload
help
```

Các nhóm quản trị `/rpg item`, `/rpg mob`, `/rpg gem`, `/rpg socket` yêu cầu:

```text
dungntrpg.admin
```

Permission mặc định:

```yaml
dungntrpg.admin:
  default: op
```

---

# 2. `/rpg item`

Quản lý Item đang cầm và registry RPG Item.

## 2.1. Stat

```text
/rpg item stat add <stat> <amount> [flat|percent]
/rpg item stat remove <stat>
/rpg item stat clear
```

Ví dụ:

```text
/rpg item stat add ATTACK 10
/rpg item stat add CRIT_CHANCE 5 percent
/rpg item stat remove ATTACK
/rpg item stat clear
```

### Modifier

Nếu không ghi modifier:

```text
flat
```

được dùng mặc định.

Hai lựa chọn:

```text
flat
percent
```

### Nguồn Stat

Command lấy trực tiếp:

```java
StatType.values()
```

vì vậy TAB completion luôn bám theo `StatType` thật của project.

---

## 2.2. Rename Item

Cầm item bằng tay chính:

```text
/rpg item rename <name>
```

Ví dụ:

```text
/rpg item rename &c&lWarrior Sword
```

`&` được chuyển thành Minecraft color code.

---

## 2.3. Lore Item

### Thêm lore

```text
/rpg item lore add <text>
```

Ví dụ:

```text
/rpg item lore add &7Thanh kiếm của chiến binh
```

### Xóa lore

```text
/rpg item lore remove <text>
```

Command tìm dòng lore tương ứng và xóa nó.

> Lore chỉ là dữ liệu hiển thị. Stat Item vẫn được lưu bằng PDC thông qua `ItemManager`.

---

## 2.4. Equipment Slot

### Thêm slot

```text
/rpg item slot add <slot>
```

Ví dụ:

```text
/rpg item slot add MAIN_HAND
/rpg item slot add OFF_HAND
/rpg item slot add HELMET
/rpg item slot add RING1
/rpg item slot add RING2
```

### Xóa slot

```text
/rpg item slot remove
```

Danh sách slot được TAB completion lấy trực tiếp từ:

```java
EquipmentSlot.values()
```

---

## 2.5. Registry Item

Các chức năng registry cũ vẫn được giữ bên trong `/rpg item`:

```text
/rpg item list
/rpg item give <id> [player] [amount]
/rpg item create <id> <material> <slot> [rarity]
/rpg item delete <id>
```

### List

```text
/rpg item list
```

Liệt kê các RPG Item đã đăng ký.

### Give

```text
/rpg item give <id>
/rpg item give <id> <player>
/rpg item give <id> <player> <amount>
```

### Create

```text
/rpg item create <id> <material> <slot> [rarity]
```

### Delete

```text
/rpg item delete <id>
```

---

# 3. `/rpg mob`

Quản lý RPG Mob Definition.

```text
/rpg mob list
/rpg mob spawn <id> [amount]
/rpg mob egg <id>
/rpg mob create <id> <entityType> <health> <attack> <defense> [magicDefense]
/rpg mob delete <id>
```

## 3.1. Spawn

```text
/rpg mob spawn <id>
```

Hoặc:

```text
/rpg mob spawn <id> <amount>
```

Mob được spawn tại vị trí người chơi.

---

## 3.2. Egg

```text
/rpg mob egg <id>
```

Tạo vanilla Spawn Egg tương ứng với `EntityType` của Mob Definition và gắn PDC:

```text
rpg_mob_id
```

> Hiện tại command chịu trách nhiệm **tạo egg**. Cơ chế click egg để spawn lại RPG Mob cần một listener riêng nếu muốn dùng egg như một vật phẩm triệu hồi hoàn chỉnh.

---

## 3.3. Create Mob

```text
/rpg mob create <id> <entityType> <health> <attack> <defense> [magicDefense]
```

Ví dụ:

```text
/rpg mob create skeleton_knight SKELETON 100 15 10 5
```

---

## 3.4. Delete Mob

```text
/rpg mob delete <id>
```

---

# 4. `/rpg gem`

Gem thuộc package:

```text
dungnt.socket
```

Command được đưa vào `/rpg` để có một entry point thống nhất.

## 4.1. Tạo Gem thường

```text
/rpg gem create <id> <name>
```

Ví dụ:

```text
/rpg gem create ruby &cRuby
```

Gem được tạo ở:

```text
EMERALD
```

---

## 4.2. Tạo Gem đặc biệt

```text
/rpg gem special <id> <name>
```

Gem đặc biệt:

```text
special = true
```

và bị giới hạn level:

```text
Level 1
```

---

## 4.3. Đổi tên Gem

Cầm Gem:

```text
/rpg gem name <name>
```

Ví dụ:

```text
/rpg gem name &cRuby of Power
```

---

# 5. `/rpg gem lore`

Cầm Gem.

### Thêm lore

```text
/rpg gem lore add <text>
```

### Xóa lore

```text
/rpg gem lore remove <text>
```

Lore được lưu trên ItemMeta.

Phần dữ liệu Gem/stat vẫn được lưu trong PDC.

---

# 6. `/rpg gem stat`

## Thêm stat

```text
/rpg gem stat add <stat> <flat|percent> <amount>
```

Ví dụ:

```text
/rpg gem stat add ATTACK flat 10
/rpg gem stat add CRIT_CHANCE percent 5
```

## Xóa stat

```text
/rpg gem stat remove <stat>
```

Ví dụ:

```text
/rpg gem stat remove ATTACK
```

Stat Gem được lưu bằng:

```text
gem_stat
gem_amount
gem_modifier
```

trong PDC của Gem.

---

# 7. `/rpg gem level`

Cầm Gem:

```text
/rpg gem level <1-5>
```

Ví dụ:

```text
/rpg gem level 3
```

Gem thường:

```text
1 → 2 → 3 → 4 → 5
```

Gem đặc biệt:

```text
Level 1
```

---

# 8. `/rpg socket`

## Mở GUI

```text
/rpg socket
```

Lệnh này mở `SocketGUI`.

## Quản lý socket slot

Cầm Item:

```text
/rpg socket slot add
/rpg socket slot remove
```

### Add

```text
/rpg socket slot add
```

- Yêu cầu OP.
- Tối đa 4 socket.
- Socket mới được thêm vào cuối danh sách.

### Remove

```text
/rpg socket slot remove
```

- Yêu cầu OP.
- Chỉ xóa socket cuối.
- Socket cuối phải **không chứa Gem**.
- Không thể giảm dưới 0 socket.

---

# 9. TAB Completion

`/rpg` đã đăng ký:

```java
CommandExecutor
TabCompleter
```

trong `MyRPG`.

## Cấp 1

```text
/rpg <TAB>
```

Hiện:

```text
help
reload
item
mob
gem
socket
```

---

## Item

```text
/rpg item <TAB>
```

Hiện:

```text
stat
rename
lore
slot
list
give
create
delete
```

### Item stat

```text
/rpg item stat <TAB>
```

Hiện:

```text
add
remove
clear
```

### Stat name

```text
/rpg item stat add <TAB>
```

Hiện toàn bộ:

```text
StatType.values()
```

Ví dụ:

```text
ATTACK
MAGIC_ATTACK
DEFENSE
MAGIC_DEFENSE
CRIT_CHANCE
CRIT_DAMAGE
...
```

### Modifier

Sau khi nhập stat và amount:

```text
/rpg item stat add ATTACK 10 <TAB>
```

Hiện:

```text
flat
percent
```

### Equipment Slot

```text
/rpg item slot add <TAB>
```

Hiện toàn bộ:

```text
EquipmentSlot.values()
```

---

# 10. TAB — Mob

```text
/rpg mob <TAB>
```

Hiện:

```text
list
spawn
egg
create
delete
```

### Mob ID

```text
/rpg mob spawn <TAB>
```

TAB lấy trực tiếp:

```java
MobDefinitionManager.getDefinitions().keySet()
```

Tương tự cho:

```text
/rpg mob egg <TAB>
/rpg mob delete <TAB>
```

### Entity Type

```text
/rpg mob create knight <TAB>
```

TAB lấy các `EntityType` có:

```java
EntityType.isAlive()
```

---

# 11. TAB — Gem

```text
/rpg gem <TAB>
```

Hiện:

```text
create
special
name
lore
stat
level
```

### Lore

```text
/rpg gem lore <TAB>
```

Hiện:

```text
add
remove
```

### Stat

```text
/rpg gem stat <TAB>
```

Hiện:

```text
add
remove
```

### Stat name

```text
/rpg gem stat add <TAB>
```

Hiện `StatType.values()`.

### Modifier

```text
/rpg gem stat add ATTACK <TAB>
```

Hiện:

```text
flat
percent
```

### Level

```text
/rpg gem level <TAB>
```

Hiện:

```text
1
2
3
4
5
```

---

# 12. TAB — Socket

```text
/rpg socket <TAB>
```

Hiện:

```text
slot
```

Sau đó:

```text
/rpg socket slot <TAB>
```

Hiện:

```text
add
remove
```

---

# 13. Command registration

`MyRPG.java` chỉ đăng ký `/rpg` một lần:

```java
RPGCommand rpgCommand = new RPGCommand(this);

getCommand("rpg").setExecutor(rpgCommand);
getCommand("rpg").setTabCompleter(rpgCommand);
```

Luồng:

```text
/rpg
   │
   ▼
RPGCommand
   │
   ├── item → RPGItemAdminCommand
   │
   ├── mob  → RPGMobAdminCommand
   │
   ├── gem  → GemManager
   │
   └── socket → SocketGUI / GemManager
```

Điểm quan trọng:

> `MyRPG` không chứa logic xử lý từng sub-command.

---

# 14. Các command cũ

Các command quản trị item/gem cũ:

```text
/itemstat
/itemsocket
```

đã được bỏ khỏi `plugin.yml` và không còn được đăng ký trong `MyRPG`.

Thay thế bằng:

```text
/itemstat add ...
        ↓
/rpg item stat add ...

/itemsocket addstat ...
        ↓
/rpg gem stat add ...
```

Các command player-facing vẫn tồn tại riêng:

```text
/equipment
/class
/team
/socket
/level
/mana
```

Lý do:

- `/equipment` là GUI Equipment.
- `/class` là GUI chọn Class.
- `/socket` là GUI Socket.
- `/team` là hệ thống Team.
- `/level`, `/mana` là command gameplay.

Chúng không phải command admin item/gem nên không bắt buộc phải đưa vào `/rpg`.

---

# 15. Permission

Command `/rpg` admin sử dụng:

```text
dungntrpg.admin
```

Trong `plugin.yml`:

```yaml
permissions:
  dungntrpg.admin:
    description: Quản trị DungNT RPG
    default: op
```

Các command player-facing giữ permission/logic riêng của chúng.

---

# 16. Tóm tắt nhanh

```text
/rpg
│
├── item
│   ├── stat
│   │   ├── add
│   │   ├── remove
│   │   └── clear
│   ├── rename
│   ├── lore
│   │   ├── add
│   │   └── remove
│   ├── slot
│   │   ├── add
│   │   └── remove
│   ├── list
│   ├── give
│   ├── create
│   └── delete
│
├── mob
│   ├── list
│   ├── spawn
│   ├── egg
│   ├── create
│   └── delete
│
├── gem
│   ├── create
│   ├── special
│   ├── name
│   ├── lore
│   │   ├── add
│   │   └── remove
│   ├── stat
│   │   ├── add
│   │   └── remove
│   └── level
│
├── socket
│   └── slot
│       ├── add
│       └── remove
│
├── reload
└── help
```

---

# 17. Files liên quan

## Command root

```text
src/main/java/dungnt/rpg/command/RPGCommand.java
```

Chịu trách nhiệm:

- `/rpg`
- dispatch sub-command
- permission
- help
- TAB completion

## Item

```text
src/main/java/dungnt/rpg/command/RPGItemAdminCommand.java
```

Chịu trách nhiệm:

- `/rpg item ...`

## Mob

```text
src/main/java/dungnt/rpg/command/RPGMobAdminCommand.java
```

Chịu trách nhiệm:

- `/rpg mob ...`

## Gem

```text
src/main/java/dungnt/socket/GemManager.java
```

Chịu trách nhiệm:

- tạo Gem
- đổi tên
- stat
- level
- lore
- socket data

## Plugin registration

```text
src/main/java/dungnt/rpg/MyRPG.java
```

Chịu trách nhiệm đăng ký:

```text
RPGCommand
CommandExecutor
TabCompleter
```

## Bukkit command declaration

```text
src/main/resources/plugin.yml
```

Command admin trung tâm:

```yaml
commands:
  rpg:
```

---

# 18. Quy tắc khi thêm command mới

Nếu sau này cần thêm:

```text
/rpg item rarity
```

thì **không thêm một command mới vào `plugin.yml`**.

Thêm vào:

```text
RPGItemAdminCommand
```

và thêm TAB vào:

```text
RPGCommand.itemTabs()
```

Nếu thêm:

```text
/rpg gem upgrade
```

thì thêm vào:

```text
RPGCommand.handleGem()
RPGCommand.gemTabs()
```

Nguyên tắc:

```text
Một root command
        ↓
Nhiều sub-command
        ↓
Manager xử lý dữ liệu
```

Không tạo lại:

```text
/itemstat2
/itemsocket2
/equipmentadmin
/gemadmin
```

nếu chức năng đó thuộc `/rpg`.
