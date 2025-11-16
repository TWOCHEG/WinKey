package twocheg.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import twocheg.mod.ModKt;
import twocheg.mod.events.impl.EventKeyPress;
import twocheg.mod.events.impl.EventKeyRelease;

@Mixin(net.minecraft.client.Keyboard.class)
public class MixinKeyboard {
    @Inject(method = "onKey", at = @At("HEAD"), cancellable = true)
    private void onKey(long windowPointer, int key, int scanCode, int action, int modifiers, CallbackInfo ci) {
        switch (action) {
            case 0 -> {
                EventKeyRelease event = new EventKeyRelease(key, scanCode, modifiers);
                ModKt.getEVENT_BUS().post(event);
                if (event.isCancelled()) ci.cancel();
            }
            case 1 -> {
                EventKeyPress event = new EventKeyPress(key, scanCode, modifiers);
                ModKt.getEVENT_BUS().post(event);
                if (event.isCancelled()) ci.cancel();
            }
        }
    }
}
