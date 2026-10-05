package twtu.compat;

/**
 * Ids of the mods TWT-U carries compat data for.
 *
 * <p>Every id here is optional. Nothing in the compat layer loads a class or touches a registry
 * entry from another namespace before the mod behind that namespace has been found in the item
 * registry, so a pack without any of these runs the exact same code path and simply ends up with
 * fewer hydration values.
 */
@SuppressWarnings("SpellCheckingInspection")
public final class ModIds
{
    public static final String FARMERS_DELIGHT = "farmersdelight";
    public static final String RUSTIC_DELIGHT = "rusticdelight";
    public static final String MORE_DELIGHT = "moredelight";
    public static final String UBES_DELIGHT = "ubesdelight";
    public static final String EX_DEORUM = "exdeorum";

    /** {@code delightlib} is Farmer's Delight's build helper. It registers no items of its own. */
    public static final String DELIGHT_LIB = "delightlib";

    private ModIds()
    {
    }
}