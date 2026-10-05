package twtu.compat.food;

import twtu.compat.ModIds;

/**
 * Farmer's Delight 1.20.1.
 *
 * <p>Food values come from {@code vectorwing.farmersdelight.common.FoodValues}. Only the category is
 * declared here; the hydration numbers live in {@link FoodCategory}, measured against a bottle of
 * water.
 *
 * <p>Nutrition is deliberately not consulted for anything but the drink path. The container the
 * item returns says far more about moisture than the calorie count does.
 */
class FarmersDelightFoods implements ModFoods
{
    private static final String NS = ModIds.FARMERS_DELIGHT;

    @Override
    public String modId()
    {
        return ModIds.FARMERS_DELIGHT;
    }

    @Override
    public void register(FoodCollector c)
    {
        registerDrinks(c);
        registerSoups(c);
        registerSalads(c);
        registerProduce(c);
        registerMeatAndBread(c);
        registerSweets(c);
    }

    /**
     * The bottle drinks.
     *
     * <p>Three of the five have no food component at all on 1.20.1, so they read back as 0
     * nutrition and are correctly treated as thin drinks that hydrate like a full bottle. Apple cider
     * has a food component that is present but zero-valued, the same trap.
     *
     * <p>{@code glow_berry_custard} is a custard by its 7 nutrition, so it comes out as a thick
     * drink rather than a thin one.
     *
     * <p>{@code bone_broth} is built with the drink animation but returns a bowl, so it is food and
     * is listed with the soups below.
     */
    private void registerDrinks(FoodCollector c)
    {
        c.drink(NS, "milk_bottle");
        c.drink(NS, "hot_cocoa");
        c.drink(NS, "melon_juice");
        c.drink(NS, "apple_cider");
        c.drink(NS, "glow_berry_custard");
    }

    /** Everything served in a bowl, plus the fried and dried items that hold no water either. */
    private void registerSoups(FoodCollector c)
    {
        for (String id : new String[]{
                "tomato_sauce",
                "bone_broth",
                "cooked_rice",
                "beef_stew",
                "chicken_soup",
                "vegetable_soup",
                "fish_stew",
                "fried_rice",
                "pumpkin_soup",
                "baked_cod_stew",
                "noodle_soup",
                "bacon_and_eggs",
                "pasta_with_meatballs",
                "pasta_with_mutton_chop",
                "mushroom_rice",
                "roasted_mutton_chops",
                "vegetable_noodles",
                "steak_and_potatoes",
                "ratatouille",
                "squid_ink_pasta",
                "grilled_salmon",
                "roast_chicken",
                "stuffed_pumpkin",
                "honey_glazed_ham",
                "shepherds_pie",
                // Genuinely player-edible upstream. Kept, at soup value.
                "dog_food",
                // Rolls and fried items are in a bowl but have had their water driven off, so they
                // read as bread rather than broth.
                "stuffed_potato",
                "cabbage_rolls",
                "salmon_roll",
                "cod_roll",
                "kelp_roll",
                "kelp_roll_slice"
        })
        {
            c.food(NS, id, FoodCategory.BOWL);
        }
    }

    /** Cold bowls of cut fruit and vegetables. */
    private void registerSalads(FoodCollector c)
    {
        for (String id : new String[]{
                "fruit_salad",
                "mixed_salad",
                "nether_salad"
        })
        {
            c.food(NS, id, FoodCategory.SALAD);
        }
    }

    private void registerProduce(FoodCollector c)
    {
        for (String id : new String[]{
                "cabbage",
                "tomato",
                "onion",
                "cabbage_leaf",
                "pumpkin_slice"
        })
        {
            c.food(NS, id, FoodCategory.PRODUCE);
        }
    }

    /**
     * Raw and cooked cuts alike, and the sandwiches built out of them. Both categories are flat, so
     * a smoked ham and a rasher of bacon hydrate the same, which is correct: both are dry protein
     * and the difference between them is salt and smoke, not water.
     */
    private void registerMeatAndBread(FoodCollector c)
    {
        for (String id : new String[]{
                "fried_egg",
                "minced_beef",
                "beef_patty",
                "chicken_cuts",
                "cooked_chicken_cuts",
                "bacon",
                "cooked_bacon",
                "cod_slice",
                "cooked_cod_slice",
                "salmon_slice",
                "cooked_salmon_slice",
                "mutton_chops",
                "cooked_mutton_chops",
                "ham",
                "smoked_ham"
        })
        {
            c.food(NS, id, FoodCategory.MEAT);
        }

        for (String id : new String[]{
                "wheat_dough",
                "raw_pasta",
                "pie_crust",
                "barbecue_stick",
                "egg_sandwich",
                "chicken_sandwich",
                "hamburger",
                "bacon_sandwich",
                "mutton_wrap",
                "dumplings"
        })
        {
            c.food(NS, id, FoodCategory.BREAD);
        }
    }

    /**
     * Cakes, cookies and pie slices. Baked dry, except the cheesecake, which is set in a milk and
     * egg base and does hold water.
     *
     * <p>The whole pies themselves ({@code apple_pie}, {@code chocolate_pie},
     * {@code sweet_berry_cheesecake}) carry no food component: right-clicking the placed block feeds
     * the player a bite directly, bypassing the item. They are deliberately absent here. Feast
     * blocks are not food either, since they hand out a serving item, and those serving items are
     * all listed above.
     */
    private void registerSweets(FoodCollector c)
    {
        for (String id : new String[]{
                "cake_slice",
                "apple_pie_slice",
                "chocolate_pie_slice",
                "sweet_berry_cookie",
                "honey_cookie"
        })
        {
            c.food(NS, id, FoodCategory.DESSERT);
        }

        c.food(NS, "sweet_berry_cheesecake_slice", FoodCategory.CUSTARD);

        // A popsicle is solid but it melts, so it drinks like one and not like its calorie count
        // would suggest.
        c.food(NS, "melon_popsicle", FoodCategory.FROZEN);
    }
}