package sawfowl.synapse.configure.localization.commands;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;

import sawfowl.synapse.api.config.locale.Translation;
import sawfowl.synapse.api.economy.Currency;
import sawfowl.synapse.api.utils.TextUtils;
import sawfowl.synapse.configure.localization.Time;

@ConfigSerializable
public class Exceptions implements Translation {

	private static final String TIME = "%time%";
	private static final String CURRENCY = "%currency%";
	private static final String MONEY = "%money%";
	private static final String COMMAND = "%command%";
	public static Exceptions createRu() {
		Exceptions exceptions = new Exceptions();
		if(!exceptions.argumentNotPresent.containsKey("Player")) exceptions.argumentNotPresent.put("Player", exceptions.deserialize("&cНужно указать ник игрока."));
		if(!exceptions.argumentNotPresent.containsKey("Server")) exceptions.argumentNotPresent.put("Server", exceptions.deserialize("&cНужно указать название сервера."));
		if(!exceptions.argumentNotPresent.containsKey("Other")) exceptions.argumentNotPresent.put("Other", exceptions.deserialize("&cАргумент не введен или введен не корректно."));
		exceptions.cooldown = exceptions.deserialize("&cВам нужно подождать %time% прежде чем вы снова сможете использовать эту команду.");
		exceptions.noMoney = exceptions.deserialize("&cЧтобы выполнить команду '/%command%', на вашем балансе должно быть не менее %currency%%money%.");
		exceptions.delayCancel = exceptions.deserialize("&cОтменено выполнение команды '/%command%' так как вы ввели другую команду.");
		return exceptions;
	}

	public static Exceptions createEn() {
		Exceptions exceptions = new Exceptions();
		if(!exceptions.argumentNotPresent.containsKey("Player")) exceptions.argumentNotPresent.put("Player", exceptions.deserialize("&cYou need to specify the player's nickname."));
		if(!exceptions.argumentNotPresent.containsKey("Server")) exceptions.argumentNotPresent.put("Server", exceptions.deserialize("&cYou need to specify the server name."));
		if(!exceptions.argumentNotPresent.containsKey("Other")) exceptions.argumentNotPresent.put("Other", exceptions.deserialize("&cThe argument was not entered or entered incorrectly."));
		return exceptions;
	}

	public Exceptions() {}

	@Setting("ArgumentNotPresent")
	private Map<String, Component> argumentNotPresent = new HashMap<>();
	@Setting("Cooldown")
	private Component cooldown = deserialize("&cYou need to wait %time% before you can use this command again.");
	@Setting("NoMoney")
	private Component noMoney = deserialize("&cTo execute the '/%command%' command, you need to have at least %currency%%money% in your balance.");
	@Setting("DelayCancel")
	private Component delayCancel = deserialize("&cThe execution of the command '/%command%' was canceled because you entered another command.");

	public Component getNotPresent(String key) {
		return argumentNotPresent.containsKey(key) ? argumentNotPresent.get(key) : argumentNotPresent.get("Other");
	}

	public Component getDefaultNotPresent() {
		return argumentNotPresent.get("Other");
	}

	public Component getCooldown(long time, Time timeConfig) {
		return replace(cooldown, TIME, TextUtils.timeFormat(time, timeConfig.getDay(), timeConfig.getHour(), timeConfig.getMinute(), timeConfig.getSecond()));
	}

	public Component getNoMoney(Currency currency, BigDecimal money, String command) {
		return replace(noMoney, new String[] {CURRENCY, MONEY, COMMAND}, currency.getStyledChar(), Component.text(money.doubleValue()), "/" + command);
	}

	public Component getDelayCancel() {
		return delayCancel;
	}

}
