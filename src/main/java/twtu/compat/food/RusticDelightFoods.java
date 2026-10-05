package twtu.compat.food;

import twtu.compat.ModIds;

/**
 * Rustic Delight.
 *
 * <p>Hard-depends on Farmer's Delight and borrows some of its {@code FoodValues} constants, so a
 * few of the items below share a calorie count with their Farmer's Delight equivalent. That does not
 * affect them, since hydration is decided by the container rather than the calories.
 *
 * <p>Food values come from {@code com.phantomwing.rusticdelight.food.FoodValues}.
 */
class RusticDelightFoods implements ModFoods
{
    private static final String NS = ModIds.RUSTIC_DELIGHT;

    /**
     * The nine bell pepper colours. Every pepper variant in the mod is one of these plus a prefix,
     * so they are generated rather than spelled out six times over.
     */
    private static final String[] COLORS = {
            "green", "yellow", "red", "orange", "white", "pink", "blue", "purple", "black"
    };

    @Override
    public String modId()
    {
        return ModIds.RUSTIC_DELIGHT;
    }

    @Override
    public void register(FoodCollector c)
    {
        registerDrinks(c);
        registerBellPeppers(c);
        registerSoupsAndSalads(c);
        registerSeafood(c);
        registerProduce(c);
        registerBread(c);
        registerSweets(c);
    }

    /**
     * Seven of the eight coffees are a food component with 0 nutrition and {@code canAlwaysEat},
     * which exists only so they are edible at all, so they are thin drinks like any other. Only
     * honey coffee is thick enough to count as a shake.
     *
     * <p>{@code cooking_oil} and {@code syrup} come in a bottle but are ingredients, not drinks:
     * a bottle of cooking oil should not be the best hydration source in the game, and cooking oil
     * also inflicts nausea upstream. Both are overridden down.
     */
    private void registerDrinks(FoodCollector c)
    {
        for (String id : new String[]{
                "coffee",
                "dark_coffee",
                "milk_coffee",
                "chocolate_coffee",
                "syrup_coffee",
                "pumpkin_coffee",
                "cherry_blossom_coffee"
        })
        {
            c.drink(NS, id);
        }

        c.drink(NS, "honey_coffee");
        c.drink(NS, "syrup", 5, 7);
        c.food(NS, "cooking_oil", FoodCategory.MEAT, 1, 1);
    }

    /**
     * Raw, roasted, sliced, stuffed and rolled peppers, across all nine colours.
     *
     * <p>Raw pepper is a genuinely watery vegetable. Roasting drives water off, so a roasted pepper
     * is drier than the raw one and a roasted slice is drier than a raw slice, even though roasting
     * makes it more calorically dense. This is the one place where reading calories as moisture
     * would give exactly the wrong answer, which is why it is spelled out here.
     */
    private void registerBellPeppers(FoodCollector c)
    {
        for (String color : COLORS)
        {
            c.food(NS, "bell_pepper_" + color, FoodCategory.PRODUCE);
            c.food(NS, "roasted_bell_pepper_" + color, FoodCategory.MEAT);
            c.food(NS, "bell_pepper_slice_" + color, FoodCategory.PRODUCE);
            c.food(NS, "roasted_bell_pepper_slice_" + color, FoodCategory.MEAT);
            c.food(NS, "stuffed_bell_pepper_" + color, FoodCategory.BREAD);
            c.food(NS, "bell_pepper_roll_" + color, FoodCategory.BREAD);
        }
    }

    /**
     * Fried and boiled dishes, which are served in a bowl but have had their water driven off or
     * absorbed into the batter. Drier than a soup of the same size.
     */
    private void registerSoupsAndSalads(FoodCollector c)
    {
        for (String id : new String[]{
                "bell_pepper_soup",
                "calamari_soup",
                "bell_pepper_pasta"
        })
        {
            c.food(NS, id, FoodCategory.BOWL);
        }

        for (String id : new String[]{
                "fried_calamari",
                "fried_chicken",
                "fried_mushrooms",
                "coffee_braised_beef"
        })
        {
            c.food(NS, id, FoodCategory.MEAT);
        }

        // Cold bowls, cut rather than cooked.
        for (String id : new String[]{
                "potato_salad",
                "sweet_salad"
        })
        {
            c.food(NS, id, FoodCategory.SALAD);
        }

        // A bowl of batter that upstream consumes with the drink animation. Treated as food so that
        // ENABLE_DRINKS_NUTRITION does not strip its 2 calories for no reason, and as bread so that
        // it reads the way it is eaten.
        c.food(NS, "batter", FoodCategory.BREAD);
    }

    private void registerSeafood(FoodCollector c)
    {
        c.food(NS, "calamari", FoodCategory.MEAT);
        c.food(NS, "cooked_calamari", FoodCategory.MEAT);
        c.food(NS, "calamari_slice", FoodCategory.MEAT);
        c.food(NS, "cooked_calamari_slice", FoodCategory.MEAT);
        c.food(NS, "calamari_roll", FoodCategory.BREAD);
    }

    private void registerProduce(FoodCollector c)
    {
        // A raw potato slice is watery.
        c.food(NS, "potato_slices", FoodCategory.PRODUCE);
        // Baked, so the water is gone.
        c.food(NS, "baked_potato_slices", FoodCategory.BREAD);
        // Roasted and golden beans are dry, whatever they are worth in calories.
        c.food(NS, "roasted_coffee_beans", FoodCategory.BREAD);
        c.food(NS, "golden_coffee_beans", FoodCategory.DESSERT);
    }

    private void registerBread(FoodCollector c)
    {
        for (String id : new String[]{
                "syrup_sandwich",
                "fruit_beignet",
                "pancake",
                "honey_pancake",
                "chocolate_pancake",
                "cherry_blossom_pancake",
                "vegetable_pancake",
                "pumpkin_pancake",
                "coffee_pancake",
                "fried_dough",
                "fried_dumplings",
                "spring_rolls",
                "fried_fish",
                "cherry_blossom_roll"
        })
        {
            c.food(NS, id, FoodCategory.BREAD);
        }
    }

    /**
     * The cheesecakes and cookies take their calories from Farmer's Delight's {@code PIE_SLICE} and
     * {@code COOKIES} constants rather than Rustic Delight's own, which is irrelevant here. What
     * matters is that a cheesecake is a custard and a cookie is a baked dry biscuit, which upstream
     * agrees with.
     */
    private void registerSweets(FoodCollector c)
    {
        for (String id : new String[]{
                "syrup_cheesecake_slice",
                "cherry_blossom_cheesecake_slice",
                "coffee_cheesecake_slice"
        })
        {
            c.food(NS, id, FoodCategory.CUSTARD);
        }

        for (String id : new String[]{
                "syrup_cookie",
                "cherry_blossom_cookie",
                "coffee_cookie"
        })
        {
            c.food(NS, id, FoodCategory.DESSERT);
        }
    }
}