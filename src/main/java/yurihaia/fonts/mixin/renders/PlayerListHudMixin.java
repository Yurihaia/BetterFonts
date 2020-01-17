package yurihaia.fonts.mixin.renders;

import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Ordering;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.systems.RenderSystem;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;
import yurihaia.fonts.BetterFonts;

@Mixin(PlayerListHud.class)
public abstract class PlayerListHudMixin extends DrawableHelper {
	@Shadow
	public static Ordering<PlayerListEntry> ENTRY_ORDERING;
	@Shadow
	public MinecraftClient client;
	@Shadow
	public InGameHud inGameHud;
	@Shadow
	public Text footer;
	@Shadow
	public Text header;
	@Shadow
	public long showTime;
	@Shadow
	public boolean visible;
	
	@Shadow
	public abstract Text getPlayerName(PlayerListEntry pl);
	
	@Shadow
	public abstract void renderLatencyIcon(int i, int j, int y, PlayerListEntry playerEntry);
	
	@Shadow
	public abstract void renderScoreboardObjective(
		ScoreboardObjective scoreboardObjective,
		int i,
		String string,
		int j,
		int k,
		PlayerListEntry playerListEntry
	);
	
	@Inject(
		method = "render(ILnet/minecraft/scoreboard/Scoreboard;Lnet/minecraft/scoreboard/ScoreboardObjective;)V",
		at = @At("HEAD"),
		cancellable = true
	)
	private void renderPlayerList(
		int width,
		Scoreboard scoreboard,
		ScoreboardObjective playerListScoreboardObjective,
		CallbackInfo cb
	) {
		ClientPlayNetworkHandler clientPlayNetworkHandler = this.client.player.networkHandler;
		List<PlayerListEntry> list = ENTRY_ORDERING.sortedCopy(clientPlayNetworkHandler.getPlayerList());
		int i = 0;
		int j = 0;
		int n;
		
		for(PlayerListEntry playerListEntry: list) {
			n = BetterFonts.fontedTextWidth(this.getPlayerName(playerListEntry), this.client.getFontManager());
			i = Math.max(i, n);
			if(
				playerListScoreboardObjective != null && playerListScoreboardObjective
					.getRenderType() != ScoreboardCriterion.RenderType.HEARTS
			) {
				n = this.client.textRenderer.getStringWidth(
					" " + scoreboard.getPlayerScore(
						playerListEntry.getProfile().getName(),
						playerListScoreboardObjective
					).getScore()
				);
				j = Math.max(j, n);
			}
		}
		
		list = list.subList(0, Math.min(list.size(), 80));
		int l = list.size();
		int m = l;
		
		for(n = 1; m > 20; m = (l + n - 1) / n) {
			++n;
		}
		
		boolean bl = this.client.isInSingleplayer() || this.client.getNetworkHandler().getConnection().isEncrypted();
		int q;
		if(playerListScoreboardObjective != null) {
			if(playerListScoreboardObjective.getRenderType() == ScoreboardCriterion.RenderType.HEARTS) {
				q = 90;
			} else {
				q = j;
			}
		} else {
			q = 0;
		}
		
		int r = Math.min(n * ((bl? 9: 0) + i + q + 13), width - 50) / n;
		int s = width / 2 - (r * n + (n - 1) * 5) / 2;
		int t = 10;
		int u = r * n + (n - 1) * 5;
		List<Text> list2 = null;
		if(this.header != null) {
			list2 = BetterFonts.wrapString(this.header, width - 50, this.client.getFontManager(), false, false);
			
			Text string;
			for(
				Iterator<Text> var18 = list2.iterator(); var18.hasNext(); u = Math.max(
					u,
					BetterFonts.fontedTextWidth(string, this.client.getFontManager())
				)
			) {
				string = (Text) var18.next();
			}
		}
		
		List<Text> list3 = null;
		Text string3;
		Iterator<Text> var36;
		if(this.footer != null) {
			list3 = BetterFonts.wrapString(this.footer, width - 50, this.client.getFontManager(), false, false);
			
			for(
				var36 = list3.iterator(); var36.hasNext(); u = Math.max(
					u,
					BetterFonts.fontedTextWidth(string3, this.client.getFontManager())
				)
			) {
				string3 = (Text) var36.next();
			}
		}
		
		int var10000;
		int var10001;
		int var10002;
		int var10004;
		int y;
		if(list2 != null) {
			var10000 = width / 2 - u / 2 - 1;
			var10001 = t - 1;
			var10002 = width / 2 + u / 2 + 1;
			var10004 = list2.size();
			this.client.textRenderer.getClass();
			fill(var10000, var10001, var10002, t + var10004 * 9, Integer.MIN_VALUE);
			
			for(var36 = list2.iterator(); var36.hasNext(); t += 9) {
				string3 = (Text) var36.next();
				y = BetterFonts.fontedTextWidth(string3, this.client.getFontManager());
				int y2 = y;
				int t2 = t;
				BetterFonts.renderFontedText(
					string3,
					this.client.getFontManager(),
					(tr, str, xoff) -> tr.drawWithShadow(str, (width / 2 - y2 / 2) + xoff, t2, -1)
				);
			}
			
			++t;
		}
		
		fill(width / 2 - u / 2 - 1, t - 1, width / 2 + u / 2 + 1, t + m * 9, Integer.MIN_VALUE);
		int w = this.client.options.getTextBackgroundColor(553648127);
		
		int ai;
		for(int x = 0; x < l; ++x) {
			y = x / m;
			ai = x % m;
			int aa = s + y * r + y * 5;
			int ab = t + ai * 9;
			fill(aa, ab, aa + r, ab + 8, w);
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			RenderSystem.enableAlphaTest();
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			if(x < list.size()) {
				PlayerListEntry playerListEntry2 = (PlayerListEntry) list.get(x);
				GameProfile gameProfile = playerListEntry2.getProfile();
				int ah;
				if(bl) {
					PlayerEntity playerEntity = this.client.world.getPlayerByUuid(gameProfile.getId());
					boolean bl2 = playerEntity != null && playerEntity.isPartVisible(PlayerModelPart.CAPE) &&
						("Dinnerbone".equals(gameProfile.getName()) || "Grumm".equals(gameProfile.getName()));
					this.client.getTextureManager().bindTexture(playerListEntry2.getSkinTexture());
					ah = 8 + (bl2? 8: 0);
					int ad = 8 * (bl2? -1: 1);
					DrawableHelper.blit(aa, ab, 8, 8, 8.0F, (float) ah, 8, ad, 64, 64);
					if(playerEntity != null && playerEntity.isPartVisible(PlayerModelPart.HAT)) {
						int ae = 8 + (bl2? 8: 0);
						int af = 8 * (bl2? -1: 1);
						DrawableHelper.blit(aa, ab, 8, 8, 40.0F, (float) ae, 8, af, 64, 64);
					}
					
					aa += 9;
				}
				
				Text string4 = this.getPlayerName(playerListEntry2);
				if(playerListEntry2.getGameMode() == GameMode.SPECTATOR) {
					int aa2 = aa;
					BetterFonts.renderFontedText(
						string4.setStyle(string4.getStyle().copy().setItalic(true)),
						this.client.getFontManager(),
						(tr, str, xoff) -> tr.drawWithShadow(str, aa2 + xoff, ab, -1862270977)
					);
				} else {
					int aa2 = aa;
					BetterFonts.renderFontedText(
						string4,
						this.client.getFontManager(),
						(tr, str, xoff) -> tr.drawWithShadow(str, aa2 + xoff, ab, -1)
					);
				}
				
				if(playerListScoreboardObjective != null && playerListEntry2.getGameMode() != GameMode.SPECTATOR) {
					int ag = aa + i + 1;
					ah = ag + q;
					if(ah - ag > 5) {
						this.renderScoreboardObjective(
							playerListScoreboardObjective,
							ab,
							gameProfile.getName(),
							ag,
							ah,
							playerListEntry2
						);
					}
				}
				
				this.renderLatencyIcon(r, aa - (bl? 9: 0), ab, playerListEntry2);
			}
		}
		
		if(list3 != null) {
			t += m * 9 + 1;
			var10000 = width / 2 - u / 2 - 1;
			var10001 = t - 1;
			var10002 = width / 2 + u / 2 + 1;
			var10004 = list3.size();
			this.client.textRenderer.getClass();
			fill(var10000, var10001, var10002, t + var10004 * 9, Integer.MIN_VALUE);
			
			for(Text string5: list3) {
				ai = BetterFonts.fontedTextWidth(string5, this.client.getFontManager());
				int ai2 = ai;
				int t2 = t;
				BetterFonts.renderFontedText(
					string5,
					this.client.getFontManager(),
					(tr, str, xoff) -> tr.drawWithShadow(str, width / 2 - ai2 / 2, t2, -1)
				);
				t += 9;
			}
		}
		cb.cancel();
	}
}