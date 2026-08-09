package sawfowl.synapse.configure.localization;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;

import sawfowl.synapse.api.config.locale.Translation;

@ConfigSerializable
public class Time implements Translation {

	public static Time createRu() {
		Time time = new Time();
		time.milliseconds = time.deserialize("мс");
		time.second = time.deserialize("с");
		time.minute = time.deserialize("м");
		time.hour = time.deserialize("ч");
		time.day = time.deserialize("д");
		time.format = "d.MM.yyyy HH:mm:ss";
		time.timeZone = "Europe/Moscow";
		return time;
	}

	public Time() {}

	@Setting("Milliseconds")
	private Component milliseconds = deserialize("s");
	@Setting("Second")
	private Component second = deserialize("s");
	@Setting("Minute")
	private Component minute = deserialize("m");
	@Setting("Hour")
	private Component hour = deserialize("h");
	@Setting("Day")
	private Component day = deserialize("d");
	@Setting("Format")
	private String format = "MM.dd.yyyy HH:mm:ss";
	@Setting("TimeZone")
	private String timeZone = "UTC";

	public Component getMilliseconds() {
		return milliseconds;
	}

	public Component getSecond() {
		return second;
	}

	public Component getMinute() {
		return minute;
	}

	public Component getHour() {
		return hour;
	}

	public Component getDay() {
		return day;
	}

	public String getFormat() {
		return format;
	}

	public String getTimeZone() {
		return timeZone;
	}

}
