package yurihaia.fonts;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.util.Identifier;

public interface DefaultedFontManager {
	public TextRenderer getTextRendererDefault(Identifier id);
}