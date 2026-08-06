package sawfowl.synapse.implementapi;

import java.lang.reflect.Type;

import org.spongepowered.configurate.CommentedConfigurationNodeIntermediary;
import org.spongepowered.configurate.objectmapping.meta.Processor;
import org.spongepowered.configurate.objectmapping.meta.Processor.Factory;

import sawfowl.synapse.api.config.LocalisedComment;
import sawfowl.synapse.implementapi.services.ILocaleService;

public class LocalisedCommentFactory implements Factory<LocalisedComment, Object> {

	public static final LocalisedCommentFactory INSTANCE = new LocalisedCommentFactory();

	@Override
	public Processor<Object> make(LocalisedComment data, Type type) {
		return (_, destination) -> {
			if (destination instanceof CommentedConfigurationNodeIntermediary<?> node) {
				if(node.comment() != null && !node.comment().isEmpty()) return;
				if(data.plugin() == null || data.path() == null || data.path().length == 0) {
					if(!data.def().isEmpty()) node.comment(data.def());
				} else if(ILocaleService.getInstance().localesExist(data.plugin()) && ILocaleService.getInstance().getLocales(data.plugin()).getSimple(ILocaleService.getInstance().getSystemOrDefaultLocale()).contains((Object[]) data.path())) {
					if(node.comment() == null || node.comment().isEmpty()) node.comment(ILocaleService.getInstance().getLocales(data.plugin()).getSimple(ILocaleService.getInstance().getSystemOrDefaultLocale()).getString((Object[]) data.path()));
				} else if(!data.def().isEmpty()) node.comment(data.def());
			}
		};
	}

}
