package twtu.compat.food;

/**
 * One food mod's contribution to TWT-U's hydration table: what it adds and how much water each of
 * those items gives back.
 *
 * <p>Implementations are only consulted when their mod is loaded, and every item lookup is
 * optional, so a provider for an absent mod, or for a mod that renamed an item in a newer version,
 * contributes nothing instead of breaking the game.
 *
 * <p>Implementations describe food in categories rather than in numbers. The numeric bands live in
 * {@link FoodCategory}, so retuning the whole table is a one file change.
 */
public interface ModFoods
{
    /**
     * The mod that has to be present for this provider to apply.
     */
    String modId();

    /** Adds this mod's food to the hydration table. */
    void register(FoodCollector collector);
}