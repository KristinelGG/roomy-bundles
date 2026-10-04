package dev.kris.roomybundles.mixin;

import com.mojang.serialization.DataResult;
import dev.kris.roomybundles.RoomyBundles;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.component.BundleContents;
import org.apache.commons.lang3.math.Fraction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/** Weight used while inserting, transferring and recalculating bundle contents. */
@Mixin(targets = "net.minecraft.world.item.component.BundleContents$Mutable", remap = false)
public abstract class BundleContentsMutableMixin {
    private static final String GET_WEIGHT =
            "Lnet/minecraft/world/item/component/BundleContents;getWeight(Lnet/minecraft/world/item/ItemInstance;)Lcom/mojang/serialization/DataResult;";

    @Redirect(method = {"tryInsert", "tryTransfer"},
            at = @At(value = "INVOKE", target = GET_WEIGHT, remap = false),
            require = 2, remap = false)
    private DataResult<Fraction> roomybundles$insertWeight(ItemInstance item) {
        return RoomyBundles.adjustWeight(item, BundleContents.getWeight(item));
    }

    @Redirect(method = {"getStackedWeight(Lnet/minecraft/world/item/ItemStack;)Lorg/apache/commons/lang3/math/Fraction;", "getWeightWithAddedItems"},
            at = @At(value = "INVOKE", target = GET_WEIGHT, remap = false),
            require = 2, remap = false)
    private static DataResult<Fraction> roomybundles$stackedWeight(ItemInstance item) {
        return RoomyBundles.adjustWeight(item, BundleContents.getWeight(item));
    }
}
