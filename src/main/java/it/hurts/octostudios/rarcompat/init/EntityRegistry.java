package it.hurts.octostudios.rarcompat.init;

import it.hurts.octostudios.rarcompat.RARCompat;
import it.hurts.octostudios.rarcompat.entity.InvisibilityZoneEntity;
import it.hurts.sskirillss.relics.entities.BlockSimulationEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EntityRegistry {
    private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, RARCompat.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<InvisibilityZoneEntity>> INVISIBILITY_ZONE = ENTITIES.register("invisibility_zone", () ->
            EntityType.Builder.of(InvisibilityZoneEntity::new, MobCategory.MISC)
                    .sized(1F, 1F)
                    .build("invisibility_zone")
    );

    public static void register(IEventBus bus) {
        ENTITIES.register(bus);
    }
}
