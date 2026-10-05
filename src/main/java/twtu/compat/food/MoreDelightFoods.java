package twtu.compat.food;

import twtu.compat.ModIds;

/**
 * More Delight.
 *
 * <p>Food values come from {@code com.axperty.moredelight.MoreDelight}, built through Delight Lib's
 * {@code FoodBuilder}. All 31 edible items are solid: this mod adds no drinks, so nothing routes
 * through {@link FoodCollector#drink}.
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
                // Hot bowls: soups, rice and pasta dishes. Note that cooked_rice_with_* and
                // creamy_pasta_with_* use milk as a recipe ingredient, but the result is a bowl of
                // food, not a drink.
                "carrot_soup",
                "cooked_rice_with_beef",
                "cooked_rice_with_chicken_cuts",
                "cooked_rice_with_porkchop",
                "creamy_pasta_with_ham",
                "creamy_pasta_with_chicken_cuts"
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

        // Toasts. Fast to eat upstream, which does not change how wet they are.
        for (String id : new String[]{
                "toast",
                "toast_with_egg",
                "toast_with_honey",
                "toast_with_sweet_berries",
                "toast_with_glow_berries",
                "toast_with_chocolate",
                "bread_slice"
        })
        {
            c.food(NS, id, FoodCategory.BREAD);
        }

        // Burgers and sandwiches. Dry bread and cooked meat, so no more than the bread alone.
        for (String id : new String[]{
                "loaded_hamburger",
                "chicken_sandwich_with_egg_and_tomato",
                "egg_with_bacon_sandwich",
                "steak_sandwich",
                "porkchop_sandwich",
                "hamburger_with_egg",
                "simple_hamburger",
                "tomato_sandwich"
        })
        {
            c.food(NS, id, FoodCategory.BREAD);
        }

        // A raw diced potato is watery; the fried versions are not, and go in above as bread.
        c.food(NS, "diced_potatoes", FoodCategory.PRODUCE);
        for (String id : new String[]{
                "mashed_potatoes",
                "diced_potatoes_with_beef",
                "diced_potatoes_with_chicken_cuts",
                "diced_potatoes_with_porkchop",
                "diced_potatoes_with_egg_and_tomato"
        })
        {
            c.food(NS, id, FoodCategory.BOWL);
        }

        c.food(NS, "omelette", FoodCategory.MEAT);
        // A popsicle melts, so it hydrates like More Delight's counterpart in Farmer's Delight.
        c.food(NS, "chocolate_popsicle", FoodCategory.FROZEN);
    }
}