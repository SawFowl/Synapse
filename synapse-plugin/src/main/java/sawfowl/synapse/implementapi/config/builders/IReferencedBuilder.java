package sawfowl.synapse.implementapi.config.builders;

import java.io.File;
import java.nio.file.Path;
import java.util.Objects;

import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import com.velocitypowered.api.plugin.PluginContainer;

import sawfowl.synapse.SynapsePlugin;
import sawfowl.synapse.api.config.ConfigTypes;
import sawfowl.synapse.api.config.ReferencedConfig;
import sawfowl.synapse.api.config.builders.ReferencedConfigBuilder;
import sawfowl.synapse.implementapi.config.IReferencedConfig;

public class IReferencedBuilder<T> implements ReferencedConfigBuilder<T> {

	private final Class<T> clazz;
	private final T value;
	private Path configDir;
	private String name;
	private ConfigTypes type;
	private TypeSerializerCollection collection;
	boolean fromFile = false;
	private PluginContainer container;
	public IReferencedBuilder(PluginContainer container, Class<T> type) {
		Objects.requireNonNull(type);
		this.container = container;
		this.clazz = type;
		this.value = null;
		//if(SynapsePlugin.getConfig() != null) this.type = SynapsePlugin.getConfig().getConfigSettings(container).getType();
	}

	@SuppressWarnings("unchecked")
	public IReferencedBuilder(PluginContainer container, T value) {
		Objects.requireNonNull(value);
		this.container = container;
		this.value = value;
		this.clazz = (Class<T>) value.getClass();
		//if(SynapsePlugin.getConfig() != null) this.type = SynapsePlugin.getConfig().getConfigSettings(container).getType();
	}

	@Override
	public ReferencedConfigBuilder<T> fromFile(File file) {
		if(file.exists() && file.toPath().getParent() != null) {
			setPath(file.toPath().getParent());
			ConfigTypes type = ConfigTypes.getTypeByExtension(ConfigTypes.getExtension(file.getName()));
			if(type != null && type != ConfigTypes.UNKNOWN) {
				setType(type);
				setName(file.getName().replace(type.toString(), ""));
				fromFile = true;
			}
		}
		return this;
	}

	@Override
	public ReferencedConfigBuilder<T> setPath(Path configDir) {
		this.configDir = configDir;
		return this;
	}

	@Override
	public ReferencedConfigBuilder<T> setName(String name) {
		if(!fromFile) this.name = name;
		return this;
	}

	@Override
	public ReferencedConfigBuilder<T> setType(ConfigTypes type) {
		if(type != null && !fromFile) this.type = type;
		return this;
	}

	@Override
	public ReferencedConfigBuilder<T> addSerializers(TypeSerializerCollection collection) {
		this.collection = collection;
		return this;
	}

	@Override
	public ReferencedConfig<T> build() {
		Objects.requireNonNull(configDir);
		Objects.requireNonNull(name);
		if(SynapsePlugin.getConfig() != null) {
			if(SynapsePlugin.getConfig().getConfigSettings(container).isForcedUse()) {
				ReferencedConfig<T> updated;
				ReferencedConfig<T> old = null;
				if(value == null) {
					updated = IReferencedConfig.create(configDir, name, SynapsePlugin.getConfig().getConfigSettings(container).getType(), collection, clazz);
					for(File file : configDir.toFile().listFiles()) {
						if(!file.getName().contains(name)) continue;
						type = ConfigTypes.getTypeByExtension(ConfigTypes.getExtension(file.getName()));
						if(type  == ConfigTypes.UNKNOWN || type.comparableType(SynapsePlugin.getConfig().getConfigSettings(container).getType())) continue;
						if(old == null) {
							old = IReferencedConfig.create(configDir, name, type, collection, clazz);
						} else file.delete();
					}
				} else {
					updated = IReferencedConfig.create(configDir, name, SynapsePlugin.getConfig().getConfigSettings(container).getType(), collection, value);
					for(File file : configDir.toFile().listFiles()) {
						if(!file.getName().contains(name)) continue;
						type = ConfigTypes.getTypeByExtension(ConfigTypes.getExtension(file.getName()));
						if(type  == ConfigTypes.UNKNOWN || type == SynapsePlugin.getConfig().getConfigSettings(container).getType()) continue;
						if(old == null) {
							old = IReferencedConfig.create(configDir, name, type, collection, clazz);
						} else file.delete();
					}
				}
				if(old != null) {
					updated.save(old.get());
					try {
						updated.getLoader().save(old.getRootNode());
					} catch (ConfigurateException e) {
						e.printStackTrace();
					}
					old.getPath().toFile().delete();
				}
				updated.load();
				return updated;
			}
		}
		Objects.requireNonNull(type);
		return value == null
			?
			IReferencedConfig.create(configDir, name, type, collection, clazz)
			:
			IReferencedConfig.create(configDir, name, type, collection, value);
	}

}
