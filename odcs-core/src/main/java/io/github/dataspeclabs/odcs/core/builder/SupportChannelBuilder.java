package io.github.dataspeclabs.odcs.core.builder;

import io.github.dataspeclabs.odcs.core.model.v3.CustomProperty;
import io.github.dataspeclabs.odcs.core.model.v3.SupportChannel;
import io.github.dataspeclabs.odcs.core.model.v3.SupportScope;
import io.github.dataspeclabs.odcs.core.model.v3.SupportTool;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Fluent builder for {@link SupportChannel}.
 */
public final class SupportChannelBuilder {

    private String id;
    private String channel;
    private SupportTool tool;
    private SupportScope scope;
    private String url;
    private String invitationUrl;
    private String description;
    private List<CustomProperty> customProperties;

    public SupportChannelBuilder id(String id) {
        this.id = id;
        return this;
    }

    public SupportChannelBuilder channel(String channel) {
        this.channel = channel;
        return this;
    }

    public SupportChannelBuilder tool(SupportTool tool) {
        this.tool = tool;
        return this;
    }

    public SupportChannelBuilder scope(SupportScope scope) {
        this.scope = scope;
        return this;
    }

    public SupportChannelBuilder url(String url) {
        this.url = url;
        return this;
    }

    public SupportChannelBuilder invitationUrl(String invitationUrl) {
        this.invitationUrl = invitationUrl;
        return this;
    }

    public SupportChannelBuilder description(String description) {
        this.description = description;
        return this;
    }

    public SupportChannelBuilder customProperty(Consumer<CustomPropertyBuilder> config) {
        CustomPropertyBuilder builder = new CustomPropertyBuilder();
        config.accept(builder);
        if (customProperties == null) {
            customProperties = new ArrayList<>();
        }
        customProperties.add(builder.build());
        return this;
    }

    public SupportChannel build() {
        return new SupportChannel(id, channel, tool, scope, url, invitationUrl, description, customProperties);
    }
}
