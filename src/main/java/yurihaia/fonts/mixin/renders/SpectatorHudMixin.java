package yurihaia.fonts.mixin.renders;

import com.mojang.blaze3d.systems.RenderSystem;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.SpectatorHud;
import net.minecraft.client.gui.hud.spectator.SpectatorMenu;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuCommand;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import yurihaia.fonts.BetterFonts;

@Mixin(SpectatorHud.class)
public abstract class SpectatorHudMixin {
	
	@Shadow
	private static Identifier WIDGETS_TEX;
	@Shadow
	public static Identifier SPECTATOR_TEX;
	@Shadow
	private MinecraftClient client;
	@Shadow
	private long lastInteractionTime;
	@Shadow
	private SpectatorMenu spectatorMenu;
	
	@Shadow
	public abstract int getSpectatorMenuHeight();
	
	@Inject(method = "render()V", at = @At("HEAD"), cancellable = true)
	public void render() {
		int i = (int) (this.getSpectatorMenuHeight() * 255.0F);
		if(i > 3 && this.spectatorMenu != null) {
			SpectatorMenuCommand spectatorMenuCommand = this.spectatorMenu.getSelectedCommand();
			Text string = spectatorMenuCommand == SpectatorMenu.BLANK_COMMAND? this.spectatorMenu.getCurrentGroup()
				.getPrompt(): spectatorMenuCommand.getName();
			if(string != null) {
				int j = (this.client.getWindow().getScaledWidth() - BetterFonts.fontedTextWidth(
					string,
					this.client.getFontManager()
				)) / 2;
				int k = this.client.getWindow().getScaledHeight() - 35;
				RenderSystem.pushMatrix();
				RenderSystem.enableBlend();
				RenderSystem.defaultBlendFunc();
				BetterFonts.renderFontedText(
					string,
					this.client.getFontManager(),
					(tr, str, xoff) -> tr.drawWithShadow(str, j + xoff, k, 16777215 + (i << 24))
				);
				RenderSystem.disableBlend();
				RenderSystem.popMatrix();
			}
		}
		
	}
}