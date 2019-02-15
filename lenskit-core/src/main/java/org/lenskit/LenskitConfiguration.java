/*
 * LensKit, an open-source toolkit for recommender systems.
 * Copyright 2014-2017 LensKit contributors (see CONTRIBUTORS.md)
 * Copyright 2010-2014 Regents of the University of Minnesota
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.lenskit;

import com.google.common.collect.ImmutableSet;
import org.grouplens.grapht.BindingFunctionBuilder;
import org.grouplens.grapht.Component;
import org.grouplens.grapht.Dependency;
import org.grouplens.grapht.ResolutionException;
import org.grouplens.grapht.context.ContextPattern;
import org.grouplens.grapht.graph.DAGNode;
import org.lenskit.api.*;
import org.lenskit.data.dao.DataAccessObject;
import org.lenskit.inject.AbstractConfigContext;
import org.lenskit.inject.RecommenderGraphBuilder;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * A LensKit algorithm configuration.  Once you have a configuration, you can pass it to
 * {@link LenskitRecommenderEngine#build(LenskitConfiguration)}
 * to build a recommender engine, or {@link LenskitRecommender#build(LenskitConfiguration)}
 * to skip the engine and just build a recommender.
 *
 * @since 1.2
 * @compat Public
 */
public class LenskitConfiguration extends AbstractConfigContext {
    private static final Class<?>[] INITIAL_ROOTS = {
            RatingPredictor.class,
            ItemScorer.class,
            ItemRecommender.class,
            ItemBasedItemScorer.class,
            ItemBasedItemRecommender.class,
            DataAccessObject.class
    };

    private final BindingFunctionBuilder bindings;
    private final Set<Class<?>> roots;

    /**
     * Create a new LensKit configuration.
     */
    public LenskitConfiguration() {
        bindings = new BindingFunctionBuilder(true);
        roots = new HashSet<>();
        Collections.addAll(roots, INITIAL_ROOTS);
    }

    /**
     * Create a new copy of a LensKit configuration.
     * @param other The configuration to copy.
     */
    public LenskitConfiguration(LenskitConfiguration other) {
        bindings = other.bindings.clone();
        roots = new HashSet<>(other.roots);
    }

    /**
     * Convenience method to copy a LensKit configuration.
     * @return An independent copy of this configuration.
     */
    public LenskitConfiguration copy() {
        return new LenskitConfiguration(this);
    }

    /**
     * Add the specified component type as a root component. This forces it (and its
     * dependencies) to be resolved, and makes it available from the resulting
     * recommenders.
     *
     * @param componentType The type of component to add as a root (typically an interface).
     * @see LenskitRecommender#get(Class)
     */
    public void addRoot(Class<?> componentType) {
        roots.add(componentType);
    }

    /**
     * Clear the set of roots, removing all configured <em>and default</em> roots.  This is almost
     * never desired in production, but is useful for testing.
     */
    public void clearRoots() {
        roots.clear();
    }

    @Override
    public <T> LenskitBinding<T> bind(Class<T> type) {
        return wrapContext(bindings.getRootContext()).bind(type);
    }

    @Override
    public LenskitConfigContext within(Class<?> type) {
        return wrapContext(bindings.getRootContext().within(type));
    }

    @Override
    public LenskitConfigContext within(Class<? extends Annotation> qualifier, Class<?> type) {
        return wrapContext(bindings.getRootContext().within(qualifier, type));
    }

    @Override
    public LenskitConfigContext within(Annotation qualifier, Class<?> type) {
        return wrapContext(bindings.getRootContext().within(qualifier, type));
    }

    @Override
    public LenskitConfigContext matching(ContextPattern pattern) {
        return wrapContext(bindings.getRootContext().matching(pattern));
    }

    @Override
    public LenskitConfigContext at(Class<?> type) {
        return wrapContext(bindings.getRootContext().at(type));
    }

    @Override
    public LenskitConfigContext at(Class<? extends Annotation> qualifier, Class<?> type) {
        return wrapContext(bindings.getRootContext().at(qualifier, type));
    }

    @Override
    public LenskitConfigContext at(Annotation qualifier, Class<?> type) {
        return wrapContext(bindings.getRootContext().at(qualifier, type));
    }

    public BindingFunctionBuilder getBindings() {
        return bindings;
    }

    public Set<Class<?>> getRoots() {
        return ImmutableSet.copyOf(roots);
    }

    /**
     * Get a mockup of the full recommender graph. This fully resolves the graph so that
     * it can be analyzed, but does not create any objects.
     *
     * @return The full graph.
     * @deprecated This shouldn't be used anymore.
     */
    @Deprecated
    public DAGNode<Component,Dependency> buildGraph() throws RecommenderConfigurationException {
        RecommenderGraphBuilder rgb = new RecommenderGraphBuilder();
        rgb.addBindings(bindings);
        rgb.addRoots(roots);
        try {
            return rgb.buildGraph();
        } catch (ResolutionException e) {
            throw new RecommenderConfigurationException("Cannot resolve configuration graph", e);
        }
    }
}
