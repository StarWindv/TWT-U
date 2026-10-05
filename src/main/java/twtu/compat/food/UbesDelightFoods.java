package twtu.compat.food;

import twtu.compat.ModIds;

/**
 * Ube's Delight 1.20.1.
 *
 * <p>Food values come from {@code chefmooon.ubesdelight.common.FoodValues}.
 *
 * <p>This is the mod that shows why calories cannot stand in for water. Its breads run from 2
 * nutrition for a raw dough up to 10 for a finished ensaymada, but none of them are much wetter than
 * the others: the difference is sugar and butter. Every bread here is at the flat
 * {@link FoodCategory#BREAD} value, and the raw doughs are not treated as thirstier than the loaves
 * they become.
 *
 * <p>This mod also registers feast and leaf-feast blocks that hand out a serving item when
 * right-clicked, and blocks that feed the player a bite directly. None of those carry a food
 * component of their own and none are listed here: the serving items they give out
 * ({@code lumpia}, {@code pandesal}, {@code sinangag}, {@code leche_flan} and friends) are, and
 * they are registered like any other food, so eating from a feast hydrates exactly like eating the
 * dish.
 */
class UbesDelightFoods implements ModFoods
{
    private static final String NS = ModIds.UBES_DELIGHT;

    @Override
    public String modId()
    {
        return ModIds.UBES_DELIGHT;
    }

    @Override
    public void register(FoodCollector c)
    {
        registerDrinks(c);
        registerProduce(c);
        registerRiceDishes(c);
        registerMeat(c);
        registerBread(c);
        registerSweets(c);
    }

    /**
     * All four bottled drinks carry a food component with 0 nutrition and a status effect rather
     * than hunger, which is the shape TWT-U expects from a drink, so they are all thin drinks.
     *
     * <p>{@code halo_halo} is a sweet iced dessert drink. Pulled down a little so it does not beat
     * a plain bottle of milk.
     *
     * <p>{@code fish_sauce_bottle} is a condiment with 0 nutrition and a 60 second dolphin's grace.
     * It is a bottle and it is drunk, so it is treated like the thin drinks, but overridden down to
     * keep a seasoning from being the best hydration item in the pack.
     */
    private void registerDrinks(FoodCollector c)
    {
        c.drink(NS, "condensed_milk_bottle");
        c.drink(NS, "milk_tea_ube");
        c.drink(NS, "halo_halo", 6, 9);
        c.drink(NS, "fish_sauce_bottle", 4, 5);
    }

    /**
     * The raw crop items. {@code ube}, {@code garlic} and {@code ginger} are block items of the crop
     * blocks and are edible by hand, unlike their seeds. All of them are watery vegetables, which is
     * what makes them worth more than a cooked cut of the same plant.
     *
     * <p>{@code lemongrass} is edible but is missing from this mod's own {@code c:foods} tag
     * upstream, so it would be missed by anything that trusted that tag.
     */
    private void registerProduce(FoodCollector c)
    {
        for (String id : new String[]{
                "ube",
                "garlic",
                "ginger",
                "lemongrass",
                "garlic_chop",
                "ginger_chop"
        })
        {
            c.food(NS, id, FoodCategory.PRODUCE);
        }
    }

    /** The silog meals, all served in a bowl and all carrying the mod's nourishment effect. */
    private void registerRiceDishes(FoodCollector c)
    {
        for (String id : new String[]{
                "sinangag",
                "kinilaw",
                "chicken_inasal_rice",
                "tosilog",
                "bangsilog",
                "sisig",
                "bulalo",
                "arroz_caldo",
                "mechado"
        })
        {
            c.food(NS, id, FoodCategory.BOWL);
        }
    }

    /** Grilled and battered, so the water has been driven off or absorbed into the wrapper. */
    private void registerMeat(FoodCollector c)
    {
        for (String id : new String[]{
                "lumpia",
                "tocino",
                "chicken_inasal"
        })
        {
            c.food(NS, id, FoodCategory.MEAT);
        }
    }

    /**
     * The breads and the raw dough they are baked from.
     *
     * <p>All twelve are at the same value. The raw doughs give 2 nutrition plus 30 seconds of hunger
     * and the finished loaves give 7 to 10, but baking adds sugar and heat rather than water, so the
     * loaves are if anything the drier of the two. Flat is the honest answer.
     *
     * <p>Note the naming here is a suffix, so {@code pandesal_raw} rather than
     * {@code raw_pandesal}.
     */
    private void registerBread(FoodCollector c)
    {
        for (String id : new String[]{
                "pandesal",
                "pandesal_ube",
                "pandesal_raw",
                "pandesal_ube_raw",
                "ensaymada",
                "ensaymada_ube",
                "ensaymada_raw",
                "ensaymada_ube_raw",
                "hopia_munggo",
                "hopia_ube",
                "hopia_munggo_raw",
                "hopia_ube_raw"
        })
        {
            c.food(NS, id, FoodCategory.BREAD);
        }
    }

    private void registerSweets(FoodCollector c)
    {
        for (String id : new String[]{
                "cookie_ube",
                "cookie_ginger",
                "ube_cake_slice",
                "polvorone",
                "polvorone_pinipig",
                "polvorone_ube",
                "polvorone_cc"
        })
        {
            c.food(NS, id, FoodCategory.DESSERT);
        }

        // Custard: 3 nutrition of milk and egg, and the one sweet in this mod that is not baked dry.
        // This is the item that has to beat a bread, and it does, at 3/4 against 1/1.
        c.food(NS, "leche_flan", FoodCategory.CUSTARD);
    }
}