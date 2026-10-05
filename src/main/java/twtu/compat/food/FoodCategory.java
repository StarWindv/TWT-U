package twtu.compat.food;

/**
 * How wet a dish is, as a fraction of a bottle of clean water.
 *
 * <p>The whole table is measured against one anchor: a bottle of water, which TWT-U's shipped
 * {@code item_settings.json} values at 6 thirst for {@code minecraft:potion}. Every number here is
 * either "some of a bottle" or "more than a bottle", which is a claim a player can check by
 * comparing the tooltip of two items in their own inventory.
 *
 * <p>The key thing this table deliberately does <em>not</em> use is nutrition as a proxy for
 * moisture. Nutrition measures calories, and calories come from starch and sugar, both of which
 * are dry. A 9 nutrition bread and a 3 nutrition custard contain similar amounts of water, and the
 * game's own data says so: hydration tracks the <em>container</em> and how it was <em>cooked</em>,
 * not the calories.
 *
 * <p>So a bread is a bread whether it is 2 or 10 nutrition, and the categories that do scale are
 * the ones where the correlation is real: raw produce, where water and sugar rise together, and
 * drinks, where added solids displace water.
 */
public enum FoodCategory
{
    /**
     * A thin drink: water, milk, coffee, tea. A full bottle, by definition.
     * <p>This is the anchor every other category is measured against.
     */
    THIN_DRINK(6, 8),

    /**
     * A drink with body to it: juice, cider, anything pulped or sweetened.
     * <p>More than a bottle of water, which is what the shipped values give melon juice and apple
     * cider. The pulp is dry but there is still more water than in a thin drink.
     */
    PULPED_DRINK(8, 12),

    /**
     * A drink that has been thickened until it is closer to a shake: honey coffee, custard.
     * <p>Below plain water, because the solids displace it.
     */
    THICK_DRINK(5, 8),

    /**
     * Ice or a frozen confection. Solid, but it melts on the way down, so it drinks like one.
     * <p>Sits between a thin drink and a pulped one: more water than a steak, less than a juice.
     */
    FROZEN(7, 9),

    /**
     * A hot dish served in a bowl: soups, stews, broths, pasta, rice dishes.
     * <p>About two thirds of a bottle. This matches the shipped values, which give beef stew,
     * chicken soup and vegetable soup all 4/5 without caring whether they carry 4 or 14 nutrition.
     * A bowl is a bowl; a bigger pot is not twice the broth.
     */
    BOWL(4, 5),

    /**
     * A cold bowl of raw vegetables and fruit.
     * <p>A little more than a soup, since nothing was cooked off, but it carries no broth of its
     * own, so it stays under a drink.
     */
    SALAD(5, 6),

    /**
     * Raw fruit and vegetables eaten by hand, the watery kind: tomato, cabbage, bell pepper.
     * <p>The one solid category that splits on nutrition, because for raw produce calories and
     * water genuinely rise together. A melon is both the sweetest and the wettest thing in the
     * basket. Dried or pickled produce is not in this category.
     */
    PRODUCE(2, 3),

    /**
     * The wettest of the solids: produce that is nearly all water. Over half a bottle.
     */
    JUICY_PRODUCE(4, 5),

    /**
     * Meat, fish and eggs. Mostly protein and fat, and both are dry.
     * <p>Roughly a sixth of a bottle, which is the price of a dry steak no matter how big it is.
     */
    MEAT(1, 1),

    /**
     * Dough, bread and other starches. Starch holds water but not as free water, so a loaf gives
     * back very little.
     * <p>A flat value on purpose: this is the case that a nutrition-scaled table gets wrong. A
     * 10 nutrition ensaymada is not ten times wetter than a 2 nutrition slice of raw dough, because
     * the difference between them is sugar, and sugar is dry.
     */
    BREAD(1, 1),

    /**
     * Cakes, cookies and pies. Baked dry, so the crust holds onto what water it has.
     */
    DESSERT(1, 1),

    /**
     * Custards, cheesecakes and flans. Mostly milk and egg, which do hold a lot of water, so a
     * custard beats a pastry even at half the calories.
     */
    CUSTARD(3, 4);

    private final int thirst;
    private final int quenched;

    FoodCategory(int thirst, int quenched)
    {
        this.thirst = thirst;
        this.quenched = quenched;
    }

    /** How much thirst this category of dish gives back. */
    public int thirst()
    {
        return this.thirst;
    }

    /** How much quench this category of dish gives back. */
    public int quenched()
    {
        return this.quenched;
    }

    /**
     * Picks the drink sub-category for an item.
     *
     * <p>Only nutrition is consulted, and only to tell a thin drink from a thickened one. Several
     * of Rustic Delight's coffees carry a food component with 0 nutrition purely so they are
     * edible at all, and they are thin drinks like any other.
     */
    public static FoodCategory forDrink(int nutrition)
    {
        if (nutrition <= 1)
        {
            return THIN_DRINK;
        }

        // A drink with enough solids in it to count as food is a shake, not a drink.
        return nutrition >= 4 ? THICK_DRINK : PULPED_DRINK;
    }
}