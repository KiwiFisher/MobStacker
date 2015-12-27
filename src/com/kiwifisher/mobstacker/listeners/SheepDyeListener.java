package com.kiwifisher.mobstacker.listeners;

import com.kiwifisher.mobstacker.utils.StackUtils;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.SheepDyeWoolEvent;

public class SheepDyeListener implements Listener {

    @EventHandler
    public void sheepDyeEvent(SheepDyeWoolEvent event) {

        LivingEntity entity = event.getEntity();

        /*
        If mob has valid meta data
         */
        if (StackUtils.hasRequiredData(entity)) {

            /*
            If there is more than one mob in the stack then follow.
             */
            if (StackUtils.getStackSize(entity) > 1) {

                /*
                Peel off the dyed sheep.
                 */
                LivingEntity newEntity = StackUtils.peelOff(entity, true);

            } else {

                /*
                If it was the stack one, just try stack that fucker.
                 */
                StackUtils.attemptToStack(0, (entity), CreatureSpawnEvent.SpawnReason.CUSTOM);
            }

        }

    }

}
