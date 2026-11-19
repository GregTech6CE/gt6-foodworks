# GT6 Food 迁移进度

更新于 2026-11-19。验证基线为 Minecraft 1.20.1、Forge 47.4.20 和 GT6CE 1.0.8，附属版本为 0.1.0。

GT6CE v1.1 将食物、饮料、罐头、可放置三明治和扩展农业迁至附属。本体负责灌装机、通用流体容器、工业材料和辐射医疗。 [本体 v1.1 路线图](https://github.com/GregTech6CE/GregTech6/blob/master/docs/roadmap/v1.1.md)

## 当前覆盖

| 内容 | 实现 |
| --- | --- |
| 食物 | 本体 251 条食物目录中，附属有 188 个同名 ID，另有 2 个已移除的面团 ID。普通食物可以从原版材料或附属的采集、种植、加工链取得。 |
| 饮料 | 194 种可装倒的瓶装食品。现行本体 131 条饮料记录中对应 114 条，涵盖 42 条原 Binnie 酒类记录。 |
| 罐头 | 57 个通用 ID 全部注册，72 种有专名的果蔬、坚果、烘焙、肉和鱼罐头。常见肉、鱼、面包和空气可灌装。 |
| 三明治 | 6 种汉堡、8 种手持三明治和可放置叠层三明治。放置版接受吐司、肉、菜、罐头和瓶装酱料，配料和份数随存档保留。 |
| 作物与调味 | 60 种可耕种作物。葡萄、醋栗、苹果变种、辣椒、椰子、榛子、樱桃、猕猴桃、茶和咖啡等均有生存来源。已接通葡萄干、腌黄瓜、辣味薯片、烧烤酱、魔改型番茄。 |
| 冰淇淋 | 37 种口味都有机器产出配方。鲜奶、奶油、果汁、坚果、香草、咖啡、薄荷等已接入。 |

## 饮料与生存来源

- **茶、咖啡与乳饮。** 茶叶在混合机中冲泡，咖啡豆经蒸馏机萃取。可继续制作甜茶、冰茶、奶咖、巧克力牛奶、巧克力奶油和榛子酱。鲜奶可在满饱食时饮用并解除牛奶可解除的效果。
- **汽水、果汁和酒。** 注入机以二氧化碳制作汽水和矿泉汽水。新增葡萄、醋栗、樱桃、猕猴桃与椰子加工，及伏特加、朗姆、蜂蜜酒、柠檬酒、葡萄酒、果酒等后续配方。
- **特殊饮品。** 金苹果、附魔金苹果与金胡萝卜可切分后压榨。紫饮料采用蓝莓汁、紫葡萄汁和糖，给杂碎肉提供独立的生产来源。蜂露和蜂王浆有以原版蜂蜜、蜂脾和甘蔗汁为原料的替代配方。枫树液和彩虹树汁通过本体取汁后，在灌装机用选择电路 1 灌装成附属饮料。
- **喝法。** 本体灌装机每次装倒 250 mB。手持杯、罐和保温瓶可直接饮用附属流体。已放置的容器可右键饮用，水罐从顶部饮用。

作物种子由草丛、蕨、橡树和白桦树叶、丛林树叶等原版方块掉落，可在现有存档取得。成熟作物常规收获 2 份产物和 2 粒种子，提前收获返回 1 粒种子。HarvestCraft 2 可通过可选标签接入相应加工配方。

从支持面上潜行右键放一份吐司开始制作叠层三明治。继续右键添加配料，用刀切分或拆下后可手持食用。加入罐头或瓶装酱料会返还空容器。

### 独立酿造

42 种酒参考 Extra Trees 的谷物酿造、果酒蒸馏和风味调配，用附属作物和 GT6CE 的现有机器实现。基础游戏环境为 Forge、GT6CE 和 GT6 Food。 [Extra Trees 上游说明](https://github.com/ForestryMC/Binnie/blob/master-MC1.12/README.md)

| 成品 | 配方链 |
| --- | --- |
| 6 种啤酒 | 混合机将谷物、啤酒花和水制成麦汁，发酵机加入对应酵母。艾尔与拉格共用大麦麦汁，通过酵母区分。世涛使用烘烤大麦芽。 |
| 9 种白兰地和 6 种果味烈酒 | 果汁先发酵为果酒，再蒸馏成白兰地。对应白兰地用选择电路 1 再蒸馏，得到果味烈酒。 |
| 4 种其他蒸馏酒 | 甘蔗基底依次制成甘蔗烈酒和中性烈酒。中性烈酒与杜松子蒸馏成金酒，龙舌兰汁发酵后可蒸馏成龙舌兰酒。 |
| 17 种利口酒 | 在混合机中用中性烈酒、糖和果汁或香料调配，覆盖坚果、果味、草本、咖啡、巧克力等口味。 |

新增的谷物、啤酒花和茴芹种子来自草丛与蕨，杏、接骨木莓、桃、梨、李、扁桃与杜松种子来自橡树与白桦树叶，橙种子来自丛林树叶，龙舌兰种子来自仙人掌。

## 迁移边界与后续验证

- 榨汁和研磨残渣已由附属产出。旧本体面团、冰淇淋和肉类的转换按本体物品是否存在加载。普通战利品表中的已迁移本体食品会转换到附属，原版地牢可直接抽到附属饮品和食物。
- GT6CE 避难所的酒杯、工坊酒桶和瓶箱优先使用附属饮品，储藏室原饮品槽也可生成附属的圣水和紫饮料。未安装附属时，生成器使用本体仍有的饮品，缺少流体时用水，缺少瓶装物品时用空瓶。
- **GT6CE v1.1 切换。** 本体移除注册后，需复测加工链、动物掉落和避难所生成。当前转换覆盖生成的战利品、生物掉落和两个已移除的附属面团 ID。旧存档中的 `gt6ce` 食品、流体和叠层三明治仍需跨命名空间迁移。旧三明治把批量存在底层配料的数量里，附属使用单独的 `Servings` 字段，方块实体迁移也要转换这部分数据。
- **客户端与容器。** GameTest 覆盖三明治的存档数据、客户端同步数据和手持杯饮用。叠层的实际渲染、手持水罐和保温瓶、放置容器的交互仍待实测。
- **HarvestCraft 2 联动。** 可选标签已经接入。待装入 Trees 和 Crops 后验证对应的材料能进入 GT6CE 机器配方。
- **通用罐头的生存来源。** 57 个通用 ID 中，有 13 个由附属机器配方直接产出，其中一种也出现在附属地牢战利品表里。其余规格应按用途核对来源，常见食材也有对应的专名罐头配方。

## 旧目录补齐与宿主

| 内容 | 成品和来源 |
| --- | --- |
| 28 种果饮 | 13 种果汁各有瓶装和冰沙，另有混合果汁和混合冰沙。杏、橙、接骨木莓、桃、梨、李保留原有酿酒产出，新增青柠、西柚、柿子、杨桃、无花果、芒果、木瓜的作物和榨汁。七种新水果的种子可从丛林树叶取得。 |
| 6 种酿造、乳饮和调味成品 | 酒花浆用啤酒花和水制作，再发酵成深色啤酒和龙血啤酒。米浆发酵成清酒，大豆可压榨为豆奶，食用油和醋加盐可做沙拉酱。大豆和稻米的种子来自草丛或蕨。 |
| 5 种特殊饮品和 4 种药丸 | 变质饮品、圣水、BAWLS、恢复药水、泻药都有独立生产和装瓶配方。薄荷丸、蓝丸、红丸、解毒剂使用本体空蜡丸制作。香料叶的种子来自草丛或蕨。辐射药品由 GT6CE 提供。 |
| 34 种本地专名罐头 | 为原版或附属的肉、鱼、杂碎肉和绿宝石肉接通 37 个原料入口。罐头支持空罐回收、对应的宠物喂食和三明治叠料。 |
| 蘑菇煲 | 原版红、棕蘑菇在混合机产出本体蘑菇煲流体，灌装机可以按每碗 1,000 mB 装倒成原版蘑菇煲。 |

圣水在注入机用荧石粉和水制作，右键虚弱的僵尸村民可启动原版转化，并返还空玻璃瓶。BAWLS 保留跳跃提升，用原版缓慢表现旧版的黏滞效果。

旧瓶装目录中缺生存链的 31 个饮品 ID 已补齐。另外恢复了更早的 8 个瓶装果汁。旧瓶装目录仍有 12 个油类或材料瓶由 GT6CE 提供，原版蜂蜜瓶由 GT6CE 的灌装连接承接。 [旧瓶装目录](https://github.com/GregTech6CE/GregTech6/blob/master/core/src/main/java/gregtech/content/data/generated/GTBottlesGen.java)

果树原料标签可接受 Pam’s HarvestCraft 2 – Trees 的 12 种对应水果。大豆、稻米、香料叶标签可接受 Pam’s HarvestCraft 2 – Crops。当前测试覆盖附属可独立供料的基线。 [HC2 Trees 发布页](https://www.curseforge.com/minecraft/mc-mods/pams-harvestcraft-2-trees) · [HC2 Crops 发布页](https://www.curseforge.com/minecraft/mc-mods/pams-harvestcraft-2-crops)

历史外模专名罐头已退出迁移范围。深渊面团和深渊曲奇面团已移除，旧存档中的两个附属物品 ID 分别映射到普通面团和普通曲奇面团。

### 现行本体饮料目录中附属未另注册的 17 条

| 本体目录键 | 当前宿主与用途 |
| --- | --- |
| 食用油（7）<br>`binnie.juiceolive`、`fishoil`、`hempoil`、`linoil`、`nutoil`、`seedoil`、`sunfloweroil` | GT6CE 流体及灌装体系。本体可灌装橄榄油、鱼油、麻油、亚麻油、坚果油、种子油和葵花籽油。 |
| 材料液体（4）<br>`glue`、`lubricant`、`mercury`、`slime` | GT6CE 工业与材料流体，保有对应旧瓶。 |
| 机器、环境流体及占位（4）<br>`ic2distilledwater`、`watergeothermal`、`sluicejuice`、`error` | GT6CE 供应蒸馏水、地热水、洗矿浆和隐藏错误占位流体。蒸馏水沿用历史名称，现由本体注册。 |
| 蜂蜜（1）<br>`honey` | GT6CE 提供流体和灌装配方，瓶装物品为原版 `minecraft:honey_bottle`。 |
| 蘑菇煲（1）<br>`mushroomsoup` | GT6CE 提供流体，碗装成品 `minecraft:mushroom_stew` 由原版提供。GT6 Food 提供原版蘑菇的混合配方及碗的装倒配方。 [旧 GT6 碗装映射](https://github.com/GregTech6/gregtech6/blob/master/src/main/java/gregtech/loaders/a/Loader_Fluids.java#L670) |

[现行饮料属性目录](https://github.com/GregTech6CE/GregTech6/blob/master/core/src/main/java/gregtech/content/data/generated/GTDrinksGen.java) · [GT6CE 流体注册目录](https://github.com/GregTech6CE/GregTech6/blob/master/core/src/main/java/gregtech/content/data/FluidCatalog.java)

### 由 GT6CE 提供的 61 个食物目录 ID

| 条目 | 当前宿主与来源 |
| --- | --- |
| 肉渣（1）<br>`scrap_meat` | GT6CE 的动物掉落和肉类加工副产物。附属杂碎肉配方可选接受本体肉渣，也接受附属生火腿片。 [杂碎肉原料标签](../src/main/resources/data/gt6food/tags/items/ingredients/chum_scraps.json) |
| 草料（4）<br>`grass_2`、`dry_grass`、`moldy_grass`、`rotten_grass` | GT6CE 植物收集、干燥或工业生物质加工。 |
| 魔法作物（2）<br>`cerublossom`、`desert_nova` | GT6CE 当前有物品条目。旧 GT6 将作物映射到 **Ars Magica 2**，另有旧 **IndustrialCraft 2** 的农业联动。 [旧 GT6 作物映射](https://github.com/GregTech6/gregtech6/blob/master/src/main/java/gregapi/GT_API_Post.java#L418) · [旧 IC2 联动](https://github.com/GregTech6/gregtech6/blob/master/src/main/java/gregtech/compat/Compat_Recipes_IndustrialCraft.java#L647) |
| 树脂、黏性材料和树皮（4）<br>`rubber_resin`、`sticky_goo`、`slimeball`、`dry_bark` | GT6CE 注册，含本体橡胶树树脂、黏性材料和倒木加工。此处的 `slimeball` 是 GT6 的硼砂胶球。 |
| 泥与黏土（6）<br>`mud_2`、`brown_clay`、`red_clay`、`yellow_clay`、`blue_clay`、`white_clay` | GT6CE 材料与方块加工。 |
| 通用动物部位（4）<br>`tusk`、`hoof`、`horn`、`antler` | GT6CE 动物部位通用化与材料加工。 |
| 原版动物提供的部位（6）<br>`hoglin_tusk`、`cow_hoof`、`horse_hoof`、`mule_hoof`、`donkey_hoof`、`cow_horn` | GT6CE 注册，掉落源是原版疣猪兽、牛、马、骡和驴。 [本体生物掉落规则](https://github.com/GregTech6CE/GregTech6/blob/master/core/src/main/java/gregtech/content/loot/MobDropRules.java#L92) |
| 历史联动动物部位（5）<br>`boar_tusk`、`elephant_tusk`、`deer_hoof`、`sheep_horn`、`deer_antler` | GT6CE 当前有物品条目。旧来源包括 **Twilight Forest（暮色森林）** 的野猪、鹿和羊类，以及旧 **Mo’ Creatures** 的野猪、大象和鹿。基线生存掉落待逐项验证。 [旧 GT6 生物掉落](https://github.com/GregTech6/gregtech6/blob/master/src/main/java/gregtech/entities/Override_Drops.java#L535) |
| 蜂窝（20）<br>`honey_comb`、`water_comb`、`magic_comb`、`nether_comb`、`end_comb`、`rock_comb`、`jungle_comb`、`frozen_comb`、`shroomy_comb`、`sandy_comb`、`clay_comb`、`sticky_comb`、`royal_comb`、`soul_comb`、`amnesic_comb`、`military_comb`、`pyro_comb`、`cryo_comb`、`aero_comb`、`tera_comb` | GT6CE 自己的养蜂系统。本体路线图计划在补齐工业蜡来源后移除这套养蜂条目。 [本体路线图](https://github.com/GregTech6CE/GregTech6/blob/master/docs/roadmap/v1.1.md) |
| 辐射医疗与胶囊（3）<br>`radaway`、`cure_all`、`empty_wax_pill` | 本体明确负责两种辐射医疗物品。空蜡丸由本体提供，也是 Radaway 的配方输入。 |
| 压制条（6）<br>`raw_meat_bar`、`cooked_meat_bar`、`chocolate_bar`、`cheese_bar`、`raw_fish_bar`、`cooked_fish_bar` | GT6CE 材料统一承接这些生熟肉、巧克力、奶酪和鱼肉的锭形。 |

[本体食物属性目录](https://github.com/GregTech6CE/GregTech6/blob/master/core/src/main/java/gregtech/content/data/generated/GTFoodItemsGen.java) · [本体物品注册目录](https://github.com/GregTech6CE/GregTech6/blob/master/core/src/main/java/gregtech/content/registry/GTMultiItemsGen.java)

## 验证

Forge `build` 与 `runGameTestServer` 均已通过。42 项必需 GameTest 全部通过，覆盖 194 种附属瓶装液体的装倒循环、新作物获取和加工、专名罐头、蘑菇煲装倒、药丸效果和圣水返瓶。机器配方覆盖层追加 1,023 条，拒绝 0 条。

使用 JDK 17，并把 `gt6ce-1.20.1-forge-1.0.8.jar` 放入 `libs/`，或用 `-Pgt6ceDir` 指向已经构建 Forge 发行包的本体检出。

```sh
./gradlew build runGameTestServer
```

Windows 可使用 `gradlew.bat`。构建产物位于 `build/libs/`。 [饮料迁移测试](../src/test/java/gregtechfood/drink/DrinkMigrationGameTests.java) · [特殊饮品与药丸测试](../src/test/java/gregtechfood/drink/SpecialFoodGameTests.java)
