package twtu.compat.food;

import twtu.compat.ModIds;

/**
 * More Delight 1.20.1.
 *
 * <p>Food values come from {@code com.axperty.moredelight.registry.ItemRegistry}, which builds every
 * item as a Farmer's Delight {@code ConsumableItem} with a {@code FoodProperties} attached.
 *
 * <p>This mod adds no drinks at all: all 35 edible items are solid or served in a bowl, so nothing
 * routes through {@link FoodCollector#drink}. Even {@code chocolate_popsicle} is a plain
 * {@code ConsumableItem} with no remainder, so it is food rather than a drink despite being frozen.
 */
class MoreDelightFoods implements ModFoods
{
    private static final String NS = ModIds.MORE_DELIGHT;

    @Override
    public String modId()
    {
        return ModIds.MORE_DELIGHT;
    }

    @Override
    public void register(FoodCollector c)
    {
        for (String id : new String[]{
                // Hot bowls: soups, rice and pasta dishes. Note that creamy_pasta_with_* uses milk as
                // a recipe ingredient, but the result is a bowl of food, not a drink.
                "carrot_soup",
                "cooked_rice_with_beef",
                "cooked_rice_with_chicken_cuts",
                "cooked_rice_with_porkchop",
                "creamy_pasta_with_ham",
                "creamy_pasta_with_chicken_cuts",
                "mashed_potatoes",
                "diced_potatoes_with_beef",
                "diced_potatoes_with_chicken_cuts",
                "diced_potatoes_with_porkchop",
                "diced_potatoes_with_egg_and_tomato"
        })
        {
            c.food(NS, id, FoodCategory.BOWL);
        }

        // Cold bowls, cut rather than cooked.
        for (String id : new String[]{
                "potato_salad",
                "chicken_salad"
        })
        {
            c.food(NS, id, FoodCategory.SALAD);
        }

        // Toasts and sandwiches. Fast to eat upstream, which does not change how wet they are, and
        // dry bread either way.
        for (String id : new String[]{
                "toast",
                "toast_with_egg",
                "toast_with_honey",
                "toast_with_sweet_berries",
                "toast_with_blueberries",
                "toast_with_glow_berries",
                "toast_with_chocolate",
                "toast_with_cheese",
                "toast_with_peanut_butter",
                "bread_slice",
                "tomato_sandwich",
                "simple_hamburger",
                "hamburger_with_cheese",
                "hamburger_with_egg",
                "steak_sandwich",
                "porkchop_sandwich",
                "chicken_sandwich_with_egg_and_tomato",
                "egg_with_bacon_sandwich",
                "loaded_hamburger"
        })
        {
            c.food(NS, id, FoodCategory.BREAD);
        }

        // A raw diced potato is watery; the cooked versions are in the bowl list above.
        c.food(NS, "diced_potatoes", FoodCategory.PRODUCE);
        c.food(NS, "omelette", FoodCategory.MEAT);
        // Melts on the way down, so it hydrates like a drink and not like its calorie count.
        c.food(NS, "chocolate_popsicle", FoodCategory.FROZEN);
    }
}