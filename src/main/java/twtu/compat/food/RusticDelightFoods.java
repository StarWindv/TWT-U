package twtu.compat.food;

import twtu.compat.ModIds;

/**
 * Rustic Delight 1.20.1.
 *
 * <p>Hard-depends on Farmer's Delight and borrows its {@code FoodValues} constants and its
 * {@code PieBlock}, so a few of the items below share a calorie count with their Farmer's Delight
 * equivalent. That does not affect them here, since hydration is decided by the container rather
 * than the calories.
 *
 * <p>Food values come from {@code com.phantomwing.rusticdelight.food.FoodValues}.
 *
 * <p>Note this version only has three bell pepper colours, not the nine the 26.2 build has.
 */
class RusticDelightFoods implements ModFoods
{
    private static final String NS = ModIds.RUSTIC_DELIGHT;

    /** The only bell pepper colours in this version. */
    private static final String[] COLORS = {"green", "yellow", "red"};

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
     * Five of the six bottle items have a food component whose nutrition is 0, which exists only so
     * they are edible at all, so they are thin drinks like any other. Only honey coffee is thick
     * enough to count as a shake.
     *
     * <p>{@code cooking_oil} comes in a glass bottle but is an ingredient, not a drink: it inflicts
     * nausea upstream, and a bottle of it should not be the best hydration source in the game. It
     * is registered as food and overridden down.
     *
     * <p>This version has no syrup, unlike the 26.2 build.
     */
    private void registerDrinks(FoodCollector c)
    {
        for (String id : new String[]{
                "coffee",
                "dark_coffee",
                "milk_coffee",
                "chocolate_coffee"
        })
        {
            c.drink(NS, id);
        }

        c.drink(NS, "honey_coffee");
        c.food(NS, "cooking_oil", FoodCategory.MEAT, 1, 1);
    }

    /**
     * Raw, roasted, stuffed and sliced peppers, across the three colours this version has.
     *
     * <p>Raw pepper is a genuinely watery vegetable. Roasting drives water off, so a roasted pepper
     * is drier than the raw one even though roasting makes it more calorically dense. This is the
     * one place where reading calories as moisture would give exactly the wrong answer, which is why
     * it is spelled out here.
     */
    private void registerBellPeppers(FoodCollector c)
    {
        for (String color : COLORS)
        {
            c.food(NS, "bell_pepper_" + color, FoodCategory.PRODUCE);
            c.food(NS, "roasted_bell_pepper_" + color, FoodCategory.MEAT);
            c.food(NS, "stuffed_bell_pepper_" + color, FoodCategory.BREAD);
        }
    }

    /**
     * Soups and pasta, which are served in a bowl and are mostly broth.
     *
     * <p>The fried items are in a bowl too, but frying has driven their water off or absorbed it
     * into the batter, so they read as dry food instead. {@code batter} is a bowl of raw batter
     * that upstream consumes with the drink animation; it is treated as food so that
     * ENABLE_DRINKS_NUTRITION does not strip its 2 calories for no reason, and as bread because
     * that is what it is.
     */
    private void registerSoupsAndSalads(FoodCollector c)
    {
        for (String id : new String[]{
                "bell_pepper_soup",
                "bell_pepper_pasta",
                "coffee_braised_beef"
        })
        {
            c.food(NS, id, FoodCategory.BOWL);
        }

        for (String id : new String[]{
                "fried_calamari",
                "fried_chicken",
                "fried_mushrooms"
        })
        {
            c.food(NS, id, FoodCategory.MEAT);
        }

        // A cold bowl, cut rather than cooked.
        c.food(NS, "potato_salad", FoodCategory.SALAD);
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
                "fruit_beignet",
                "cherry_blossom_roll",
                "spring_rolls"
        })
        {
            c.food(NS, id, FoodCategory.BREAD);
        }
    }

    /**
     * The cheesecake slice takes its calories from Farmer's Delight's {@code PIE_SLICE} constant
     * rather than Rustic Delight's own, which is irrelevant here. What matters is that a cheesecake
     * is a custard and a cookie is a baked dry biscuit, which upstream agrees with.
     *
     * <p>The whole cheesecake block itself carries no food component: right-clicking it feeds the
     * player a bite directly, bypassing the item, so it is deliberately absent. The pancake blocks
     * work the same way and are absent too; their servings are listed above.
     */
    private void registerSweets(FoodCollector c)
    {
        c.food(NS, "cherry_blossom_cheesecake_slice", FoodCategory.CUSTARD);
        c.food(NS, "cherry_blossom_cookie", FoodCategory.DESSERT);
    }
}