package killercreepr.cruxcore.text.tags;

import killercreepr.crux.api.data.Holder;
import killercreepr.crux.api.text.context.TextParserContext;
import killercreepr.crux.api.text.resolver.StringListResolver;
import killercreepr.crux.core.text.format.FormatArgs;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class StringListResolverHolder implements StringListResolver {
    protected final String id;
    protected final Holder<List<String>> value;

    public StringListResolverHolder(String id, Holder<List<String>> value) {
        this.id = id;
        this.value = value;
    }

    @Override
    public @NotNull String identifier() {
        return id;
    }

    @Override
    public @Nullable List<String> resolve(@NotNull FormatArgs formatArgs, @NotNull TextParserContext textParserContext) {
        return value.value();
    }
}
