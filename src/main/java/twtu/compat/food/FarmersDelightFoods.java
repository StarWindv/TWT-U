package twtu.compat.food;

import twtu.compat.ModIds;

/**
 * Farmer's Delight.
 *
 * <p>Food values come from {@code vectorwing.farmersdelight.common.FoodValues}. Only the category
 * is declared here; the hydration numbers live in {@link FoodCategory}, measured against a bottle
 * of water.
 *
 * <p>Nutrition is deliberately not consulted for anything but the drink path. The container
 * upstream returns says far more about moisture than the calorie count does.
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
     * The bottle drinks. Milk, hot cocoa and melon juice carry no food component at all, so they
     * read as thin drinks and hydrate like a full bottle. Cider and melon juice are pulped, which
     * is worth more than plain water.
     *
     * <p>{@code glow_berry_custard} is a drink by its glass bottle remainder but is a custard by its
     * 7 nutrition, and it is missing from Farmer's Delight's own {@code item/drinks} tag, so it has
     * to be listed by hand either way.
     */
    private void registerDrinks(FoodCollector c)
    {
        c.drink(NS, "milk_bottle");
        c.drink(NS, "hot_cocoa");
        c.drink(NS, "melon_juice");
        c.drink(NS, "apple_cider");
        c.drink(NS, "glow_berry_custard");
    }

    /**
     * Everything served in a bowl. Note that {@code onion_soup} is bound upstream to the same
     * values as a shepherd's pie, which is an upstream copy-paste slip but harmless here: it is in a
     * bowl either way, and nutrition is not read.
     *
     * <p>{@code dog_food} is genuinely player-edible upstream. Kept, at soup value.
     */
    private void registerSoups(FoodCollector c)
    {
        for (String id : new String[]{
                "tomato_sauce",
                "stuffed_potato",
                "cabbage_rolls",
                "salmon_roll",
                "cod_roll",
                "kelp_roll",
                "kelp_roll_slice",
                "cooked_rice",
                "bone_broth",
                "beef_stew",
                "chicken_soup",
                "vegetable_soup",
                "fish_stew",
                "fried_rice",
                "pumpkin_soup",
                "baked_cod_stew",
                "noodle_soup",
                "onion_soup",
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
                "dog_food"
        })
        {
            c.food(NS, id, FoodCategory.BOWL);
        }
    }

    /**
     * Cold bowls of cut fruit and vegetables.
     *
     * <p>{@code gleaming_salad} is a bowl upstream but a salad by name and by content, so it goes
     * here rather than into {@link #registerSoups}.
     */
    private void registerSalads(FoodCollector c)
    {
        for (String id : new String[]{
                "fruit_salad",
                "mixed_salad",
                "nether_salad",
                "gleaming_salad"
        })
        {
            c.food(NS, id, FoodCategory.SALAD);
        }
    }

    private void registerProduce(FoodCollector c)
    {
        c.food(NS, "cabbage", FoodCategory.PRODUCE);
        c.food(NS, "tomato", FoodCategory.PRODUCE);
        c.food(NS, "onion", FoodCategory.PRODUCE);
        c.food(NS, "cabbage_leaf", FoodCategory.PRODUCE);
        c.food(NS, "pumpkin_slice", FoodCategory.PRODUCE);
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

    private void registerSweets(FoodCollector c)
    {
        for (String id : new String[]{
                "cake_slice",
                "apple_pie_slice",
                "chocolate_pie_slice",
                "pumpkin_pie_slice",
                "sweet_berry_cookie",
                "honey_cookie"
        })
        {
            c.food(NS, id, FoodCategory.DESSERT);
        }

        // Custard, not pastry: the slice is set in a milk and egg base.
        c.food(NS, "sweet_berry_cheesecake_slice", FoodCategory.CUSTARD);

        // A popsicle is solid but it melts, so it drinks like one and not like its calorie count
        // would suggest.
        c.food(NS, "melon_popsicle", FoodCategory.FROZEN);
    }
}