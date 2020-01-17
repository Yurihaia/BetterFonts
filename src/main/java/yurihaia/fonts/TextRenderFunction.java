package yurihaia.fonts;

import net.minecraft.client.font.TextRenderer;

@FunctionalInterface
public interface TextRenderFunction {
	public int render(TextRenderer tr, String text, int xoff);
}