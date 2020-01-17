package yurihaia.fonts.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Style;
import net.minecraft.util.Identifier;
import yurihaia.fonts.FontStyle;

@Mixin(Style.class)
public abstract class StyleMixin implements FontStyle {
	private Identifier font;

	@Shadow
	public Style parent;
	
	@Override
	public Identifier getFont() {
		if(this.parent == null && this.font == null) {
			return MinecraftClient.DEFAULT_TEXT_RENDERER_ID;
		}
		return this.font == null? ((FontStyle) this.parent).getFont(): this.font;
	}
	
	@Override
	public Style setFont(Identifier font) {
		this.font = font;
		return (Style) (Object) this;
	}

	@Inject(
		method = "net/minecraft/text/Style.isEmpty()Z",
		at = @At("HEAD"),
		cancellable = true
	)
	private void isEmptyInject(CallbackInfoReturnable<Boolean> info) {
		if(this.font != null) {
			info.setReturnValue(false);
			info.cancel();
		}
	}
	
	@Inject(
		method = "deepCopy()Lnet/minecraft/text/Style;",
		at = @At("RETURN"),
		locals = LocalCapture.CAPTURE_FAILHARD
	)
	private void deepCopyInject(CallbackInfoReturnable<Style> info, Style style) {
		((FontStyle) style).setFont(this.font);
	}
	
	@Inject(
		method = "copy()Lnet/minecraft/text/Style;",
		at = @At("RETURN"),
		locals = LocalCapture.CAPTURE_FAILHARD
	)
	private void copyInject(CallbackInfoReturnable<Style> info, Style style) {
		((FontStyle) style).setFont(this.getFont());
	}

	@Inject(
		method = "equals(Ljava/lang/Object;)Z",
		at = @At(
			value = "INVOKE",
			target = "getInsertion()Ljava/lang/String;",
			ordinal = 0
		),
		cancellable = true
	)
	private void equalsInject(Object obj, CallbackInfoReturnable<Boolean> info) {
		// The point at which this is invoked obj is already known to be `Style`
		FontStyle fs = (FontStyle) obj;
		if(this.font != null) {
			if(!this.font.equals(fs.getFont())) {
				info.setReturnValue(false);
				info.cancel();
			}
		} else if(fs.getFont() == null) {
			info.setReturnValue(false);
			info.cancel();
		}
	}
}