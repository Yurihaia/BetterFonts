package yurihaia.fonts.mixin.renders;

import com.mojang.blaze3d.systems.RenderSystem;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.util.math.Matrix4f;
import yurihaia.fonts.BetterFonts;

@Mixin(ChatHud.class)
public class ChatHudMixin {
	
	@Shadow
	private MinecraftClient client;
	
	@Inject(
		method = "render(I)V",
		at = @At(value = "INVOKE", target = "net/minecraft/text/Text.asFormattedString()Ljava/lang/String;"),
		locals = LocalCapture.CAPTURE_FAILHARD
	)
	private void drawInject(
		int ticks,
		CallbackInfo ci,
		int _i,
		int _j,
		boolean _bl,
		double _d,
		int _k,
		double _e,
		double _f,
		int _l,
		Matrix4f _matrix4f,
		int _m,
		ChatHudLine chatHudLine,
		double _g,
		int o,
		int _p,
		int _q,
		int r
	) {
		RenderSystem.enableBlend();
		BetterFonts.renderFontedText(
			chatHudLine.getText(),
			this.client.getFontManager(),
			(tr, text, xoff) -> tr.drawWithShadow(text, (float) xoff, (float) (r - 8), 16777215 + (o << 24))
		);
		RenderSystem.disableAlphaTest();
		RenderSystem.disableBlend();
	}
	
	@Redirect(
		method = "render(I)V",
		at = @At(
			value = "INVOKE",
			target = "net/minecraft/client/font/TextRenderer.drawWithShadow(Ljava/lang/String;FFI)I"
		)
	)
	private int removeRegularDraw(TextRenderer t, String s, float f1, float f2, int i) {
		return 0;
	}
}