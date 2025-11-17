package twocheg.mod.mixins;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import twocheg.mod.ModKt;
import twocheg.mod.events.impl.EventPostTick;
import twocheg.mod.events.impl.EventTick;
import twocheg.mod.modules.Parent;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient {
    @Inject(method = "tick", at = @At("HEAD"))
    void preTickHook(CallbackInfo ci) {
        if (!Parent.fullNullCheck()) ModKt.getEVENT_BUS().post(new EventTick());
    }

    @Inject(method = "tick", at = @At("RETURN"))
    void postTickHook(CallbackInfo ci) {
        if (!Parent.fullNullCheck()) ModKt.getEVENT_BUS().post(new EventPostTick());
    }
}
