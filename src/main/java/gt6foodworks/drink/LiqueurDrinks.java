package gt6foodworks.drink;

import gt6foodworks.drink.DrinkRegistry.DrinkEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;

final class LiqueurDrinks {
    private LiqueurDrinks() {}

    static void register(DeferredRegister<Item> items) {
        liqueur(items, "almond_liqueur", 0xFFDABE97);
        liqueur(items, "anise_liqueur", 0xFFC8A86D);
        liqueur(items, "banana_liqueur", 0xFFEDCA54);
        liqueur(items, "blackberry_liqueur", 0xFF60304C);
        liqueur(items, "blackcurrant_liqueur", 0xFF6A3B78);
        liqueur(items, "cherry_liqueur", 0xFFC33145);
        liqueur(items, "chocolate_liqueur", 0xFF805035);
        liqueur(items, "cinnamon_liqueur", 0xFFBF6742);
        liqueur(items, "coffee_liqueur", 0xFF60402D);
        liqueur(items, "hazelnut_liqueur", 0xFFAD8A54);
        liqueur(items, "herbal_liqueur", 0xFF657448);
        liqueur(items, "lemon_liqueur", 0xFFE9DE68);
        liqueur(items, "melon_liqueur", 0xFFDB6874);
        liqueur(items, "mint_liqueur", 0xFF6AB891);
        liqueur(items, "orange_liqueur", 0xFFE98539);
        liqueur(items, "peach_liqueur", 0xFFEEA582);
        liqueur(items, "raspberry_liqueur", 0xFFB64A83);
    }

    private static void liqueur(DeferredRegister<Item> items, String id, int tint) {
        DrinkRegistry.drink(items, id, tint, 4, 0.2F,
                new DrinkEffect(MobEffects.DAMAGE_RESISTANCE, 400, 1, 0.6F));
    }
}
