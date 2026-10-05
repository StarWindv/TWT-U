package twtu.compat.food;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import twtu.TWTU;
import twtu.api.ThirstHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

import java.util.HashSet;
import java.util.Set;

/**
 * Gathers the hydration values one food mod contributes, and hands them to TWT-U.
 *
 * <p>Every lookup goes through {@link BuiltInRegistries#ITEM}, so an item that the target mod
 * renamed or dropped in a newer version is simply skipped. A provider therefore only has to
 * describe what its mod adds; it never has to know whether the mod is installed.
 *
 * <p>Values are registered with {@code putIfAbsent} semantics, so a player who wrote their own
 * number into {@code item_settings.json} keeps it.
 */
public final class FoodCollector
{
    private final String owner;
    private final Set<Item> addedFoods = new HashSet<>();
    private final Set<Item> addedDrinks = new HashSet<>();

    FoodCollector(String owner)
    {
        this.owner = owner;
    }

    /** @see #food(String, String, FoodCategory, int, int) */
    public void food(String namespace, String path, FoodCategory category)
    {
        food(namespace, path, category, -1, -1);
    }

    /**
     * Registers a food at the value its category carries.
     *
     * @param overrideThirst a value to use instead of the category's, or {@code -1} to use the
     *                       category's
     * @param overrideQuenched a value to use instead of the category's, or {@code -1} to use the
     *                         category's
     * @return whether the item exists and was registered
     */
    public boolean food(String namespace, String path, FoodCategory category, int overrideThirst, int overrideQuenched)
    {
        Item item = item(namespace, path);

        if (item == null || this.addedFoods.contains(item) || this.addedDrinks.contains(item))
        {
            return false;
        }

        int thirst = overrideThirst >= 0 ? overrideThirst : category.thirst();
        int quenched = overrideQuenched >= 0 ? overrideQuenched : category.quenched();

        // Every category carries a value of at least 1, so this is unreachable for the built-in
        // table. It guards against a provider passing a category that models nothing.
        if (thirst <= 0)
        {
            return false;
        }

        this.addedFoods.add(item);
        ThirstHelper.addFood(item, thirst, quenched);
        return true;
    }

    /**
     * Registers a drink. Same as {@link #food} but routes through
     * {@link ThirstHelper#addDrink}, so that {@code ENABLE_DRINKS_NUTRITION = false} strips its
     * hunger instead of leaving the player with a full bar and a full hunger bar.
     */
    public boolean drink(String namespace, String path)
    {
        return drink(namespace, path, -1, -1);
    }

    /** @see #food(String, String, FoodCategory, int, int) for the override semantics */
    public boolean drink(String namespace, String path, int overrideThirst, int overrideQuenched)
    {
        Item item = item(namespace, path);

        if (item == null || this.addedDrinks.contains(item) || this.addedFoods.contains(item))
        {
            return false;
        }

        // Which kind of drink this is, decided by how much is dissolved in it.
        FoodCategory category = FoodCategory.forDrink(nutritionOf(item));
        int thirst = overrideThirst >= 0 ? overrideThirst : category.thirst();
        int quenched = overrideQuenched >= 0 ? overrideQuenched : category.quenched();

        if (thirst <= 0)
        {
            return false;
        }

        this.addedDrinks.add(item);
        ThirstHelper.addDrink(item, thirst, quenched);
        return true;
    }

    /** How many distinct items this mod contributed, foods and drinks together. */
    public int size()
    {
        return this.addedFoods.size() + this.addedDrinks.size();
    }

    /**
     * @return the item, or {@code null} when the namespace is not loaded or the id is unknown.
     * Never throws, so a renamed item in one mod cannot stop the game from starting.
     */
    private Item item(String namespace, String path)
    {
        try
        {
            return BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(namespace, path));
        }
        catch (Exception e)
        {
            TWTU.LOGGER.warn("Food compat: {}:{} could not be resolved ({}), skipping", namespace, path, e.toString());
            return null;
        }
    }

    /**
     * The item's declared nutrition, or {@code 0} when it has no food component at all.
     * <p>Only the drink path reads this, to tell a thin drink from a thickened one. Farmer's
     * Delight's milk bottle, hot cocoa and melon juice are drinkable but carry no hunger, and are
     * correctly treated as thin.
     */
    private static int nutritionOf(Item item)
    {
        FoodProperties food = new ItemStack(item).get(DataComponents.FOOD);

        return food == null ? 0 : food.nutrition();
    }
}