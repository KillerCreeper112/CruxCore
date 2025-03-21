package killercreepr.cruxcore.text.tags;

import killercreepr.crux.api.data.Holder;
import killercreepr.crux.api.text.context.TextParserContext;
import killercreepr.crux.api.text.resolver.StringResolver;
import killercreepr.crux.core.text.format.FormatArgs;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StringResolverHolder implements StringResolver {
    protected final String id;
    protected final Holder<String> value;

    public StringResolverHolder(String id, Holder<String> value) {
        this.id = id;
        this.value = value;
    }

    @Override
    public @NotNull String identifier() {
        return id;
    }

    @Override
    public @Nullable String resolve(@NotNull FormatArgs formatArgs, @NotNull TextParserContext textParserContext) {
        return value.value();
    }
}
