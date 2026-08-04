package io.github.dataspeclabs.odcs.core.builder;

import io.github.dataspeclabs.odcs.core.model.v3.CustomProperty;
import io.github.dataspeclabs.odcs.core.model.v3.SlaProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Fluent builder for {@link SlaProperty}.
 */
public final class SlaPropertyBuilder {

    private String property;
    private Object value;
    private Object valueExt;
    private String unit;
    private String element;
    private String driver;
    private String scheduler;
    private String schedule;
    private List<CustomProperty> customProperties;

    public SlaPropertyBuilder property(String property) {
        this.property = property;
        return this;
    }

    public SlaPropertyBuilder value(Object value) {
        this.value = value;
        return this;
    }

    public SlaPropertyBuilder valueExt(Object valueExt) {
        this.valueExt = valueExt;
        return this;
    }

    public SlaPropertyBuilder unit(String unit) {
        this.unit = unit;
        return this;
    }

    public SlaPropertyBuilder element(String element) {
        this.element = element;
        return this;
    }

    public SlaPropertyBuilder driver(String driver) {
        this.driver = driver;
        return this;
    }

    public SlaPropertyBuilder scheduler(String scheduler) {
        this.scheduler = scheduler;
        return this;
    }

    public SlaPropertyBuilder schedule(String schedule) {
        this.schedule = schedule;
        return this;
    }

    public SlaPropertyBuilder customProperty(Consumer<CustomPropertyBuilder> config) {
        CustomPropertyBuilder builder = new CustomPropertyBuilder();
        config.accept(builder);
        if (customProperties == null) {
            customProperties = new ArrayList<>();
        }
        customProperties.add(builder.build());
        return this;
    }

    public SlaProperty build() {
        return new SlaProperty(
                property, value, valueExt, unit, element, driver, scheduler, schedule, customProperties);
    }
}
