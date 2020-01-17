package yurihaia.fonts.mixin.renders;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import yurihaia.fonts.BetterFonts;

@Mixin(InGameHud.class)
public class InGameHudMixin extends DrawableHelper {
	
	@Shadow
	MinecraftClient client;
	@Shadow
	ItemStack currentStack;
	@Shadow
	int heldItemTooltipFade;
	@Shadow
	int scaledWidth;
	@Shadow
	int scaledHeight;
	
	@Inject(method = "renderHeldItemTooltip()V", at = @At("HEAD"), cancellable = true)
	private void drawItemTooltip(CallbackInfo cb) {
		this.client.getProfiler().push("selectedItemName");
		if(this.heldItemTooltipFade > 0 && !this.currentStack.isEmpty()) {
			Text text = (new LiteralText("")).append(this.currentStack.getName())
				.formatted(this.currentStack.getRarity().formatting);
			if(this.currentStack.hasCustomName()) {
				text.formatted(Formatting.ITALIC);
			}
			
			int i = (this.scaledWidth - BetterFonts.fontedTextWidth(text, this.client.getFontManager())) / 2;
			int j = this.scaledHeight - 59;
			if(!this.client.interactionManager.hasStatusBars()) {
				j += 14;
			}
			
			int k = (int) ((float) this.heldItemTooltipFade * 256.0F / 10.0F);
			if(k > 255) {
				k = 255;
			}
			
			if(k > 0) {
				RenderSystem.pushMatrix();
				RenderSystem.enableBlend();
				RenderSystem.defaultBlendFunc();
				int var10000 = i - 2;
				int var10001 = j - 2;
				int var10002 = i + BetterFonts.fontedTextWidth(text, this.client.getFontManager()) + 2;
				fill(var10000, var10001, var10002, j + 9 + 2, this.client.options.getTextBackgroundColor(0));
				int yoff = j;
				int k2 = k;
				BetterFonts.renderFontedText(
					text,
					this.client.getFontManager(),
					(tr, str, off) -> tr.drawWithShadow(str, i + off, yoff, 16777215 + (k2 << 24))
				);
				RenderSystem.disableBlend();
				RenderSystem.popMatrix();
			}
		}
		
		this.client.getProfiler().pop();
		cb.cancel();
	}
	
	@Inject(
		method = "renderScoreboardSidebar(Lnet/minecraft/scoreboard/ScoreboardObjective;)V",
		at = @At("HEAD"),
		cancellable = true
	)
	private void drawScoreboardSidebar(ScoreboardObjective scoreboardObjective, CallbackInfo cb) {
		Scoreboard scoreboard = scoreboardObjective.getScoreboard();
		Collection<ScoreboardPlayerScore> collection = scoreboard.getAllPlayerScores(scoreboardObjective);
		List<ScoreboardPlayerScore> list = collection.stream().filter((scoreboardPlayerScorex) -> {
			return scoreboardPlayerScorex.getPlayerName() != null && !scoreboardPlayerScorex.getPlayerName()
				.startsWith("#");
		}).collect(Collectors.toList());
		if(list.size() > 15) {
			collection = Lists.newArrayList(Iterables.skip(list, collection.size() - 15));
		} else {
			collection = list;
		}
		
		int i = BetterFonts.fontedTextWidth(scoreboardObjective.getDisplayName(), this.client.getFontManager());
		int j = i;
		
		Text text2;
		for(
			Iterator<ScoreboardPlayerScore> var8 = collection.iterator(); var8.hasNext(); j = Math.max(
				j,
				BetterFonts.fontedTextWidth(text2, this.client.getFontManager())
			)
		) {
			ScoreboardPlayerScore scoreboardPlayerScore = (ScoreboardPlayerScore) var8.next();
			Team team = scoreboard.getPlayerTeam(scoreboardPlayerScore.getPlayerName());
			text2 = new LiteralText("").append(
				Team.modifyText(team, new LiteralText(scoreboardPlayerScore.getPlayerName()))
			)
				.append(": ")
				.append(new LiteralText(Integer.toString(scoreboardPlayerScore.getScore())).formatted(Formatting.RED));
		}
		
		int var10000 = collection.size();
		int k = var10000 * 9;
		int l = this.scaledHeight / 2 + k / 3;
		int n = this.scaledWidth - j - 3;
		int o = 0;
		int p = this.client.options.getTextBackgroundColor(0.3F);
		int q = this.client.options.getTextBackgroundColor(0.4F);
		
		for(ScoreboardPlayerScore scoreboardPlayerScore2: (Iterable<ScoreboardPlayerScore>) collection) {
			++o;
			Team team2 = scoreboard.getPlayerTeam(scoreboardPlayerScore2.getPlayerName());
			String string4 = Formatting.RED + "" + scoreboardPlayerScore2.getScore();
			int s = l - o * 9;
			int t = this.scaledWidth - 3 + 2;
			var10000 = n - 2;
			fill(var10000, s, t, s + 9, p);
			BetterFonts.renderFontedText(
				Team.modifyText(team2, new LiteralText(scoreboardPlayerScore2.getPlayerName())),
				this.client.getFontManager(),
				(tr, str, xoff) -> tr.draw(str, n + xoff, s, -1)
			);
			TextRenderer defaultTextRenderer = this.client.getFontManager()
				.getTextRenderer(MinecraftClient.DEFAULT_TEXT_RENDERER_ID);
			defaultTextRenderer.draw(string4, t - defaultTextRenderer.getStringWidth(string4), s, -1);
			if(o == collection.size()) {
				var10000 = n - 2;
				fill(var10000, s - 9 - 1, t, s - 1, q);
				fill(n - 2, s - 1, t, s, p);
				float var10002 = (float) (n + j / 2 - i / 2);
				BetterFonts.renderFontedText(
					scoreboardObjective.getDisplayName(),
					this.client.getFontManager(),
					(tr, tx, xoff) -> tr.draw(tx, var10002 + xoff, s - 9, -1)
				);
			}
		}

		cb.cancel();
	}
}