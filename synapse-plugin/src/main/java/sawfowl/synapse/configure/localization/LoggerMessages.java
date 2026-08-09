package sawfowl.synapse.configure.localization;

import java.util.Locale;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import com.velocitypowered.api.plugin.PluginContainer;

import sawfowl.synapse.api.config.ConfigTypes;

@ConfigSerializable
public class LoggerMessages {

	private static final String COMMAND = "%command%'";
	public static LoggerMessages createRu() {
		LoggerMessages loggerMessages = new LoggerMessages();
		loggerMessages.startWatch = "Запущено отслеживание файлов локализаций.";
		loggerMessages.remove = "Локализация \"%locale%\" для плагина \"%plugin%\" удалена!";
		loggerMessages.add = "Добавлен новый файл локализации \"%file%\" для плагина \"%plugin%\"! Загрузка...";
		loggerMessages.reload = "Файл локализации \"%file%\" для плагина \"%plugin%\" был изменен! Перезагрузка...";
		loggerMessages.saveAsset = "Был сохранен конфиг локализации \"%file%\" для плагина \"%plugin%\".";
		loggerMessages.executorNotAssigned = "Для команды '/%command%' не назначен исполнитель или дочерние команды.";
		return loggerMessages;
	}

	public LoggerMessages(){}

	@Setting("StartWatch")
	private String startWatch = "Localization file tracking has been started.";
	@Setting("Remove")
	private String remove = "The \"%locale%\" localization for the \"%plugin%\" has been removed!";
	@Setting("Add")
	private String add = "Added a new localization file \"%file%\" for plugin \"%plugin%\"! Loading...";
	@Setting("Reload")
	private String reload = "Locale file \"%file%\" for plugin \"%plugin%\" has been changed! Reloading...";
	@Setting("SaveAsset")
	private String saveAsset = "Locale config \"%file%\" for plugin \"%plugin%\" has been saved.";
	@Setting("ExecutorNotAssigned")
	private String executorNotAssigned = "An executor or subcommands have not been assigned to the '/%command%' command.";

	public String getStartWatch() {
		return startWatch;
	}

	public String getRemove(Locale locale, PluginContainer container) {
		return remove.replace("%locale%", locale.toLanguageTag()).replace("%plugin%", container.getDescription().getId());
	}

	public String getAdd(Locale locale, ConfigTypes type, PluginContainer container) {
		return add.replace("%file%", locale.toLanguageTag() + type.toString()).replace("%plugin%", container.getDescription().getId());
	}

	public String getReload(Locale locale, ConfigTypes type, PluginContainer container) {
		return reload.replace("%file%", locale.toLanguageTag() + type.toString()).replace("%plugin%", container.getDescription().getId());
	}

	public String getSaveAsset(Locale locale, ConfigTypes type, String plugin) {
		return saveAsset.replace("%file%", locale.toLanguageTag() + type.toString()).replace("%plugin%", plugin);
	}

	public String getExecutorNotAssigned(String command) {
		return executorNotAssigned.replace(COMMAND, command);
	}

}
