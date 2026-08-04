package io.github.dataspeclabs.odcs.core.builder;

import io.github.dataspeclabs.odcs.core.model.v3.CustomProperty;
import io.github.dataspeclabs.odcs.core.model.v3.DataQuality;
import io.github.dataspeclabs.odcs.core.model.v3.DataQualityDimension;
import io.github.dataspeclabs.odcs.core.model.v3.DataQualityMetric;
import io.github.dataspeclabs.odcs.core.model.v3.DataQualityType;
import io.github.dataspeclabs.odcs.core.model.v3.DataQualityUnit;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Fluent builder for {@link DataQuality}.
 */
public final class DataQualityBuilder {

    private DataQualityType type;
    private String description;
    private DataQualityMetric metric;
    private String query;
    private String engine;
    private Object implementation;
    private Object mustBe;
    private Object mustNotBe;
    private Object mustBeGreaterThan;
    private Object mustBeGreaterOrEqualTo;
    private Object mustBeLessThan;
    private Object mustBeLessOrEqualTo;
    private List<Object> mustBeBetween;
    private List<Object> mustNotBeBetween;
    private DataQualityUnit unit;
    private DataQualityDimension dimension;
    private String severity;
    private String businessImpact;
    private String method;
    private String scheduler;
    private String schedule;
    @Deprecated
    private String rule;
    private List<CustomProperty> customProperties;

    public DataQualityBuilder type(DataQualityType type) {
        this.type = type;
        return this;
    }

    public DataQualityBuilder description(String description) {
        this.description = description;
        return this;
    }

    public DataQualityBuilder metric(DataQualityMetric metric) {
        this.metric = metric;
        return this;
    }

    public DataQualityBuilder query(String query) {
        this.query = query;
        return this;
    }

    public DataQualityBuilder engine(String engine) {
        this.engine = engine;
        return this;
    }

    public DataQualityBuilder implementation(Object implementation) {
        this.implementation = implementation;
        return this;
    }

    public DataQualityBuilder mustBe(Object mustBe) {
        this.mustBe = mustBe;
        return this;
    }

    public DataQualityBuilder mustNotBe(Object mustNotBe) {
        this.mustNotBe = mustNotBe;
        return this;
    }

    public DataQualityBuilder mustBeGreaterThan(Object mustBeGreaterThan) {
        this.mustBeGreaterThan = mustBeGreaterThan;
        return this;
    }

    public DataQualityBuilder mustBeGreaterOrEqualTo(Object mustBeGreaterOrEqualTo) {
        this.mustBeGreaterOrEqualTo = mustBeGreaterOrEqualTo;
        return this;
    }

    public DataQualityBuilder mustBeLessThan(Object mustBeLessThan) {
        this.mustBeLessThan = mustBeLessThan;
        return this;
    }

    public DataQualityBuilder mustBeLessOrEqualTo(Object mustBeLessOrEqualTo) {
        this.mustBeLessOrEqualTo = mustBeLessOrEqualTo;
        return this;
    }

    public DataQualityBuilder mustBeBetween(List<Object> mustBeBetween) {
        this.mustBeBetween = mustBeBetween == null ? null : new ArrayList<>(mustBeBetween);
        return this;
    }

    public DataQualityBuilder mustNotBeBetween(List<Object> mustNotBeBetween) {
        this.mustNotBeBetween = mustNotBeBetween == null ? null : new ArrayList<>(mustNotBeBetween);
        return this;
    }

    public DataQualityBuilder unit(DataQualityUnit unit) {
        this.unit = unit;
        return this;
    }

    public DataQualityBuilder dimension(DataQualityDimension dimension) {
        this.dimension = dimension;
        return this;
    }

    public DataQualityBuilder severity(String severity) {
        this.severity = severity;
        return this;
    }

    public DataQualityBuilder businessImpact(String businessImpact) {
        this.businessImpact = businessImpact;
        return this;
    }

    public DataQualityBuilder method(String method) {
        this.method = method;
        return this;
    }

    public DataQualityBuilder scheduler(String scheduler) {
        this.scheduler = scheduler;
        return this;
    }

    public DataQualityBuilder schedule(String schedule) {
        this.schedule = schedule;
        return this;
    }

    @Deprecated
    public DataQualityBuilder rule(String rule) {
        this.rule = rule;
        return this;
    }

    public DataQualityBuilder customProperty(Consumer<CustomPropertyBuilder> config) {
        CustomPropertyBuilder builder = new CustomPropertyBuilder();
        config.accept(builder);
        if (customProperties == null) {
            customProperties = new ArrayList<>();
        }
        customProperties.add(builder.build());
        return this;
    }

    public DataQuality build() {
        return new DataQuality(
                type, description, metric, query, engine, implementation,
                mustBe, mustNotBe, mustBeGreaterThan, mustBeGreaterOrEqualTo,
                mustBeLessThan, mustBeLessOrEqualTo, mustBeBetween, mustNotBeBetween,
                unit, dimension, severity, businessImpact, method, scheduler, schedule,
                rule, customProperties);
    }
}
