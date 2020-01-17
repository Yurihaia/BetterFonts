package yurihaia.fonts.mixin;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.FontManager;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.util.Identifier;
import yurihaia.fonts.DefaultedFontManager;

@Mixin(FontManager.class)
public abstract class FontManagerMixin implements DefaultedFontManager {
	@Shadow
	public Map<Identifier, TextRenderer> textRenderers;

	@Shadow
	public abstract TextRenderer getTextRenderer(Identifier id);
	
	@Override
	public TextRenderer getTextRendererDefault(Identifier id) {
		if(this.textRenderers.containsKey(id)) {
			return this.textRenderers.get(id);
		} else {
			return this.getTextRenderer(MinecraftClient.DEFAULT_TEXT_RENDERER_ID);
		}
	}
}