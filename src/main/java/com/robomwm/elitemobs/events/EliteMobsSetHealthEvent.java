package com.robomwm.elitemobs.events;

import org.bukkit.entity.Damageable;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created on 4/1/2018.
 *
 * @author RoboMWM
 *
 * Unused cuz
 * a) Hopefully MagmaGuy stops setHealth(0) on players (and just in general stops using setHealth, ESPECIALLY ON PLAYERS, to get the results he wants...)
 * b) MagmaGuy will add his own event for this
 * c) MagmaGuy won't do anything and so I'll end up using a fork anyways
 *
 * So there's really no point in me firing an event when I could just modify this anyways.
 *
 * But I'll keep it here cuz I typed it up so yea.
 */
public class EliteMobsSetHealthEvent extends Event implements Cancellable
{
    // Custom Event Requirements
    private static final HandlerList handlers = new HandlerList();
    public static HandlerList getHandlerList() {
        return handlers;
    }
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    private boolean canceled = false;
    private Damageable entity;
    private double amount;


    public EliteMobsSetHealthEvent(Damageable entity, double amount)
    {
        this.entity = entity;
        this.amount = amount;
    }

    @Override
    public boolean isCancelled()
    {
        return canceled;
    }

    @Override
    public void setCancelled(boolean cancel)
    {
        this.canceled = cancel;
    }
}
