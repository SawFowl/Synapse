package sawfowl.synapse.implementapi;

import java.lang.reflect.Type;

import org.spongepowered.configurate.CommentedConfigurationNodeIntermediary;
import org.spongepowered.configurate.objectmapping.meta.Processor;
import org.spongepowered.configurate.objectmapping.meta.Processor.Factory;

import com.google.inject.Inject;

import sawfowl.synapse.api.Synapse;
import sawfowl.synapse.api.config.LocalisedComment;

public class LocalisedCommentFactory implements Factory<LocalisedComment, Object> {

	@Inject
	private static Synapse synapse;
	public static final LocalisedCommentFactory INSTANCE = new LocalisedCommentFactory();

	@Override
	public Processor<Object> make(LocalisedComment data, Type type) {
		return (value, destination) -> {
			if (destination instanceof CommentedConfigurationNodeIntermediary<?> node) {
				if(node.comment() != null && !node.comment().isEmpty()) return;
				if(data.plugin() == null || data.path() == null || data.path().length == 0) {
					if(!data.def().isEmpty()) node.comment(data.def());
				} else if(synapse.getLocaleService().localesExist(data.plugin()) && synapse.getLocaleService().getLocales(data.plugin()).getSimple(synapse.getLocaleService().getSystemOrDefaultLocale()).contains((Object[]) data.path())) {
					if(node.comment() == null || node.comment().isEmpty()) node.comment(synapse.getLocaleService().getLocales(data.plugin()).getSimple(synapse.getLocaleService().getSystemOrDefaultLocale()).getString((Object[]) data.path()));
				} else if(!data.def().isEmpty()) node.comment(data.def());
			}
		};
	}

}
