package net.njw.justendportal.client.model;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public record GeneratorBobProperty() implements RangeSelectItemModelProperty {
    public static final MapCodec<GeneratorBobProperty> MAP_CODEC = MapCodec.unit(new GeneratorBobProperty());

    @Override
    public float get(ItemStack stack, @Nullable ClientLevel level, @Nullable ItemOwner owner, int seed) {
        double seconds = System.nanoTime() / 1_000_000_000.0;
        return 0.5F + 0.5F * Mth.sin((float) (seconds * 3.0));
    }

    @Override
    public MapCodec<GeneratorBobProperty> type() {
        return MAP_CODEC;
    }
}
