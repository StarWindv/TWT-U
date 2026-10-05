package twtu.compat.food;

import twtu.compat.ModIds;

/**
 * Ex Deorum.
 *
 * <p>A single item: the cooked silkworm. The raw silkworm is not edible and the porcelain milk
 * bucket is a container item with no food component, so neither is listed.
 */
class ExDeorumFoods implements ModFoods
{
    private static final String NS = ModIds.EX_DEORUM;

    @Override
    public String modId()
    {
        return ModIds.EX_DEORUM;
    }

    @Override
    public void register(FoodCollector c)
    {
        c.food(NS, "cooked_silkworm", FoodCategory.MEAT);
    }
}