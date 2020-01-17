package yurihaia.fonts;

import net.minecraft.text.Style;
import net.minecraft.util.Identifier;

public interface FontStyle {
	public Identifier getFont();
	public Style setFont(Identifier font);
}