package yurihaia.fonts.mixin.renders;

import java.util.Map;
import java.util.UUID;

import com.mojang.blaze3d.systems.RenderSystem;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.client.gui.hud.ClientBossBar;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.util.Identifier;
import yurihaia.fonts.BetterFonts;

@Mixin(BossBarHud.class)
abstract public class BossBarHudMixin {
	
	@Shadow
	private MinecraftClient client;
	
	@Shadow
	private static Identifier BAR_TEX;
	
	@Shadow
	private Map<UUID, ClientBossBar> bossBars;
	
	@Shadow
	public abstract void renderBossBar(int x, int y, BossBar bossBar);
	
	@Inject(method = "render()V", at = @At("HEAD"), cancellable = true)
	private void drawInject(CallbackInfo ci) {
		if(!this.bossBars.isEmpty()) {
			int i = this.client.getWindow().getScaledWidth();
			int j = 12;
			for(ClientBossBar clientBossBar: this.bossBars.values()) {
				int k = i / 2 - 91;
				RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				this.client.getTextureManager().bindTexture(BAR_TEX);
				this.renderBossBar(k, j, clientBossBar);
				int m = BetterFonts.fontedTextWidth(clientBossBar.getName(), this.client.getFontManager());
				int n = i / 2 - m / 2;
				int o = j - 9;
				BetterFonts.renderFontedText(
					clientBossBar.getName(),
					this.client.getFontManager(),
					(tr, str, xoff) -> tr.drawWithShadow(str, n + xoff, o, 16777215)
				);
				j += 19;
				if(j >= this.client.getWindow().getScaledHeight() / 3) {
					break;
				}
			}
			
		}
		ci.cancel();
	}
}