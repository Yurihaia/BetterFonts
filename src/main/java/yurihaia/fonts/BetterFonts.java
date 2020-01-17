package yurihaia.fonts;

import java.util.List;

import com.google.common.collect.ImmutableList;

import net.fabricmc.api.ModInitializer;
import net.minecraft.client.font.FontManager;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class BetterFonts implements ModInitializer {
	@Override
	public void onInitialize() {
		System.out.println("Font Mod Initialized");
	}
	
	public static List<Text> splitByFont(Text text) {
		ImmutableList.Builder<Text> out = new ImmutableList.Builder<>();
		ImmutableList.Builder<Text> buf = new ImmutableList.Builder<>();
		Identifier lastFont = null;
		for(Text t: (Iterable<Text>) text.stream()::iterator) {
			FontStyle s = (FontStyle) t.getStyle();
			Identifier font = s.getFont();
			if(lastFont == null) {
				lastFont = font;
			}
			if(!lastFont.equals(font)) {
				Text bt = joinText(buf.build());
				((FontStyle) bt.getStyle()).setFont(lastFont);
				out.add(bt);
				buf = new ImmutableList.Builder<>();
				lastFont = font;
			}
			buf.add(t);
		}
		Text bt = joinText(buf.build());
		((FontStyle) bt.getStyle()).setFont(lastFont);
		out.add(bt);
		return out.build();
	}
	
	public static void renderFontedText(Text text, FontManager fontManager, TextRenderFunction fn) {
		List<Text> texts = splitByFont(text);
		int offset = 0;
		for(Text t: texts) {
			fn.render(
				((DefaultedFontManager) fontManager).getTextRendererDefault(
					((FontStyle) t.getStyle()).getFont()
				), 
				t.asFormattedString(),
				offset
			);
			offset += ((DefaultedFontManager) fontManager).getTextRendererDefault(
				((FontStyle) t.getStyle()).getFont()
			).getStringWidth(t.asFormattedString());
		}
	}
	
	public static int fontedTextWidth(Text text, FontManager fontManager) {
		if(text == null) {
			return 0;
		}
		int width = 0;
		for(Text t: splitByFont(text)) {
			width += fontManager.getTextRenderer(((FontStyle) t.getStyle()).getFont())
				.getStringWidth(t.asFormattedString());
		}
		return width;
	}
	
	public static List<Text> wrapString(
		Text text,
		int width,
		FontManager manager,
		boolean bl,
		boolean forceColor
	) {
		ImmutableList.Builder<Text> out = ImmutableList.builder();
		ImmutableList.Builder<Text> buf = ImmutableList.builder();
		int lineWidth = 0;
		for(Text t: (Iterable<Text>) text.stream()::iterator) {
			TextRenderer tr = manager.getTextRenderer(((FontStyle) t.getStyle()).getFont());
			int twidth = tr.getStringWidth(t.getStyle().asString() + t.asString());
			if(lineWidth + twidth > width) {
				int cut = tr.getCharacterCountForWidth(t.getStyle().asString() + t.asString(), width - lineWidth);
				buf.add(new LiteralText(t.asTruncatedString(cut)).setStyle(t.getStyle().deepCopy()));
				out.add(joinText(buf.build()));
				buf = ImmutableList.builder();
				lineWidth = 0;
			} else {
				lineWidth += twidth;
				buf.add(Text.copyWithoutChildren(t));
			}
		}
		out.add(joinText(buf.build()));
		return out.build();
	}
	
	public static Text joinText(List<Text> tList) {
		LiteralText t = new LiteralText("");
		for(Text text: tList) {
			t.append(Text.copyWithoutChildren(text));
		}
		return t;
	}
}
