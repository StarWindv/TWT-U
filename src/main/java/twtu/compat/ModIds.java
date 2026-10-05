package twtu.compat;

/**
 * Ids of the mods TWT-U carries compat data for.
 *
 * <p>Every id here is optional. Nothing in the compat layer touches another namespace's registry
 * entry until it has been found in the item registry, so a pack without any of these runs the
 * exact same code path and simply ends up with fewer hydration values.
 */
@SuppressWarnings("SpellCheckingInspection")
public final class ModIds
{
    public static final String FARMERS_DELIGHT = "farmersdelight";
    public static final String RUSTIC_DELIGHT = "rusticdelight";
    public static final String MORE_DELIGHT = "moredelight";
    public static final String UBES_DELIGHT = "ubesdelight";
    public static final String EX_DEORUM = "exdeorum";

    private ModIds()
    {
    }
}