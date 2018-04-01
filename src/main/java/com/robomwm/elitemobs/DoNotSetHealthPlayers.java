package com.robomwm.elitemobs;

import org.bukkit.entity.Damageable;
import org.bukkit.entity.EntityType;

/**
 * Created on 4/1/2018.
 *
 * @author RoboMWM
 *
 * Not a listener cuz
 * @see com.robomwm.elitemobs.events.EliteMobsSetHealthEvent
 */
public class DoNotSetHealthPlayers
{
    /**
     * Technically any action that uses setHealth in place of damage is lazy,
     * bypasses any resistances not accounted for by the "setHealther,"
     * and bypasses **all plugins** that respond to damage.
     *
     * I personally don't care about nonplayer#setHealth though, hence why there's this.
     * @param entity entity to be "setHealthed"
     * @param amount amount of health to be set to
     * @return Whether the setHealth is evil and should NOT be performed.
     */
    public static boolean isEvilAndLazyAction(Damageable entity, double amount)
    {
        if (entity.getType() != EntityType.PLAYER)
            return false;
        if (amount <= 0)
        {
            entity.damage(Short.MAX_VALUE - 1); //Essentials also uses this value, so subtracting 1 to avoid other plugins attempting to detect an essentials /suicide
            return true;
        }
        entity.damage(entity.getHealth() - amount); //I mean obviously the entity is supposed to survive this attack so...
        return true;
    }
}
