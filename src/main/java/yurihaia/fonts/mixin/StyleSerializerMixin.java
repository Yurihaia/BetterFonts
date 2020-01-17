package yurihaia.fonts.mixin;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.text.Style;
import net.minecraft.util.Identifier;
import yurihaia.fonts.FontStyle;

@Mixin(Style.Serializer.class)
class StyleSerializerMixin {
	
	@Inject(
		method = "deserialize(Lcom/google/gson/JsonElement;Ljava/lang/reflect/Type;Lcom/google/gson/JsonDeserializationContext;)Lnet/minecraft/text/Style;",
		at = @At(value = "INVOKE", target = "has(Ljava/lang/String;)Z", ordinal = 0),
		locals = LocalCapture.CAPTURE_FAILHARD
	)
	private void deserializeInject(
		JsonElement jsonElement,
		Type type,
		JsonDeserializationContext jsonDeserializationContext,
		CallbackInfoReturnable<Style> cir,
		Style style,
		JsonObject jsonObject
	) {
		if(jsonObject.has("font")) {
			((FontStyle) style).setFont(new Identifier(jsonObject.get("font").getAsString()));
		}
	}
	
	@Inject(
		method = "serialize(Lnet/minecraft/text/Style;Ljava/lang/reflect/Type;Lcom/google/gson/JsonSerializationContext;)Lcom/google/gson/JsonElement;",
		at = @At(value = "TAIL"),
		locals = LocalCapture.CAPTURE_FAILHARD
	)
	private void serializeInject(
		Style style,
		Type arg1,
		JsonSerializationContext jsonSerializationContext,
		CallbackInfoReturnable<JsonElement> cir,
		JsonObject jsonObject
	) {
		System.out.println("Serializing Font");
		if (((FontStyle) style).getFont() != null) {
			System.out.println("Found Font");
			jsonObject.addProperty("font", ((FontStyle) style).getFont().toString());
		}
	}
	
}