# GregTech 6 Foodworks

GregTech 6 Foodworks（GT6 Food）是 GregTech 6 Community Edition（GT6CE）的食品与饮料附属。它为生存模式加入可种植的原料，并用 GT6CE 的机器把食材加工成饮料、罐头、冰淇淋和各种餐点。 [GT6CE 项目](https://github.com/GregTech6CE/GregTech6)

## 安装

- Minecraft **1.20.1**，Java **17**。
- Forge **47.4.20 至 47.x**，同平台的 GT6CE **1.0.8 或更高**。当前验证组合为 Forge 47.4.20 和 GT6CE 1.0.8。
- 将 GT6CE 和 GT6 Food 的 JAR 放进游戏实例的 `mods/`。多人游戏需要在客户端和服务端都安装。

## 玩法

- **栽培与收获。** 草丛、蕨和部分原版树叶等方块会掉落种子，可在现有存档收集并种在耕地上。
- **加工与酿造。** 可以把苹果切块榨汁，用 GT6CE 的灌装机装瓶，也可以继续发酵成苹果酒和苹果醋。谷物、啤酒花、水果和香料还可用来酿制啤酒、蒸馏酒与利口酒。
- **烹饪与罐装。** 制作烘焙食品、乳制品、冰淇淋和调味料，或用灌装机将肉、鱼、果蔬等装进罐头。附属饮料通常按每瓶 250 mB 装倒，也可用 GT6CE 的杯、罐或保温瓶饮用。
- **叠层三明治。** 手持吐司潜行右键，将它放在有支撑的表面。手持配料右键逐层加入，再用刀右键切成可手持食用的单份。加入罐头或瓶装酱料时会返还空容器。

灌装机、通用流体容器、工业材料和辐射医疗由 GT6CE 提供。

## 构建与测试

使用 JDK 17。将 `gt6ce-1.20.1-forge-1.0.8.jar` 放入 `libs/` 后，在本仓库根目录执行构建和 Forge GameTest。

```sh
./gradlew build runGameTestServer
```

Windows PowerShell 使用 `.\gradlew.bat build runGameTestServer`。也可以追加 `-Pgt6ceDir=<本体仓库路径>`，使用已在本体仓库中通过 `:minecraft:1.20.1-forge:distributionJar` 构建的同版本发行包。构建产物位于 `build/libs/`。

Java 源码按作物、饮料、食物、战利品和叠层三明治分包。机器与合成配方位于数据资源中，GameTest 覆盖加工链、饮料装倒、作物获取、罐头、三明治及战利品迁移。 [源码](src/main/java/gregtechfood) · [配方](src/main/resources/data/gt6food/recipes) · [GameTest](src/test/java)

## 授权与素材

项目代码采用 GNU 宽通用公共许可证第 3 版或任何后续版本（`LGPL-3.0-or-later`）。 [LGPL 3.0 正文](LICENSE) · [GPL 3.0 正文](src/main/resources/META-INF/licenses/GPL-3.0.txt)

源自 GregTech 6 的默认食品和饮料贴图按 **CC0 1.0** 提供。Gradle Wrapper 文件采用 **Apache-2.0**。 [贴图归属说明](src/main/resources/META-INF/licenses/gt6-food-textures.txt) · [Gradle Wrapper 许可声明](gradlew)

种子的两层贴图来自 Crops NH，采用 `LGPL-3.0-or-later`。 [种子贴图归属说明](src/main/resources/META-INF/licenses/CropsNH-NOTICE.txt)
