package dev.kris.roomybundles.mixin;

import com.mojang.serialization.DataResult;
import dev.kris.roomybundles.RoomyBundles;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.component.BundleContents;
import org.apache.commons.lang3.math.Fraction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/** Weight of the items already stored in a bundle (tooltip bar, saved contents). */
@Mixin(targets = "net.minecraft.world.item.component.BundleContents", remap = false)
public abstract class BundleContentsMixin {
    @Redirect(method = "computeContentWeight",
            at = @At(value = "INVOKE", remap = false,
                    target = "Lnet/minecraft/world/item/component/BundleContents;getWeight(Lnet/minecraft/world/item/ItemInstance;)Lcom/mojang/serialization/DataResult;"),
            require = 1, remap = false)
    private static DataResult<Fraction> roomybundles$contentWeight(ItemInstance item) {
        return RoomyBundles.adjustWeight(item, BundleContents.getWeight(item));
    }
}
