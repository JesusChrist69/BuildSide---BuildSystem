package cz.jesuschrist69.buildsystem.component;

import java.util.List;

public interface Role {

    /**
     * Returns the key that is used as role identifier in settings.yml
     *
     * @return {@link String} role key
     */
    String getRoleKey();

    /**
     * Returns the permission required to use this role.
     *
     * @return {@link String} permission
     */
    String getRequiredPermission();

    /**
     * Returns a list of all the world types that are allowed to be used by user.
     *
     * @return {@link List<WorldType>} A list of all the world types that are allowed.
     */
    List<WorldType> getAllowedWorldTypes();

    /**
     * Returns true if the user is allowed to search for a specific world.
     *
     * @return {@link Boolean}
     */
    boolean isSearchAllowed();

    /**
     * Returns true if the player is allowed to teleport into the worlds.
     *
     * @return {@link Boolean}
     */
    boolean isTeleportAllowed();

    /**
     * Returns true if the player can see all worlds.
     *
     * @return {@link Boolean}
     */
    boolean canSeeAllWorlds();

    /**
     * Returns true if the player can delete worlds.
     *
     * @return {@link Boolean}
     */
    boolean canDeleteWorlds();

    /**
     * Returns true if the player can lock and unlock worlds
     *
     * @return {@link Boolean}
     */
    boolean canLockWorlds();

    /**
     * Returns whether the player can hide/show worlds.
     *
     * @return {@link Boolean}
     */
    boolean canHideWorlds();


}
