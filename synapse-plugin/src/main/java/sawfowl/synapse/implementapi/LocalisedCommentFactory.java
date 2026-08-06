package sawfowl.synapse.implementapi;

import java.lang.reflect.Type;

import org.spongepowered.configurate.CommentedConfigurationNodeIntermediary;
import org.spongepowered.configurate.objectmapping.meta.Processor;
import org.spongepowered.configurate.objectmapping.meta.Processor.Factory;

import sawfowl.synapse.api.config.LocalisedComment;
import sawfowl.synapse.api.services.LocaleService;

public class LocalisedCommentFactory implements Factory<LocalisedComment, Object> {

	public static final LocalisedCommentFactory INSTANCE = new LocalisedCommentFactory();

	@Override
	public Processor<Object> make(LocalisedComment data, Type type) {
		return (_, destination) -> {
			if (destination instanceof CommentedConfigurationNodeIntermediary<?> node) {
				if(node.comment() != null && !node.comment().isEmpty()) return;
				if(data.plugin() == null || data.path() == null || data.path().length == 0) {
					if(!data.def().isEmpty()) node.comment(data.def());
				} else if(LocaleService.get().localesExist(data.plugin()) && LocaleService.get().getLocales(data.plugin()).getSimple(LocaleService.get().getSystemOrDefaultLocale()).contains((Object[]) data.path())) {
					if(node.comment() == null || node.comment().isEmpty()) node.comment(LocaleService.get().getLocales(data.plugin()).getSimple(LocaleService.get().getSystemOrDefaultLocale()).getString((Object[]) data.path()));
				} else if(!data.def().isEmpty()) node.comment(data.def());
			}
		};
	}

}
