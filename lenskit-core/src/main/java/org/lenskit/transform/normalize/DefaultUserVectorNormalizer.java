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
package org.lenskit.transform.normalize;

import it.unimi.dsi.fastutil.longs.Long2DoubleMap;
import org.lenskit.inject.Shareable;
import org.lenskit.util.InvertibleFunction;

import javax.inject.Inject;
import java.io.Serializable;

/**
 * Default user vector normalizer that delegates to a generic {@link VectorNormalizer}.
 *
 * @author <a href="http://www.grouplens.org">GroupLens Research</a>
 * @since 0.11
 */
@Shareable
public class DefaultUserVectorNormalizer implements UserVectorNormalizer, Serializable {
    private static final long serialVersionUID = 1L;
    protected final VectorNormalizer delegate;

    /**
     * Construct a new user vector normalizer that uses the identity normalization.
     */
    public DefaultUserVectorNormalizer() {
        this(new IdentityVectorNormalizer());
    }

    /**
     * Construct a new user vector normalizer wrapping a generic vector normalizer.
     *
     * @param norm The generic normalizer to use.
     */
    @Inject
    public DefaultUserVectorNormalizer(VectorNormalizer norm) {
        delegate = norm;
    }

    /**
     * Get the delegate vector normalizer.
     * @return The vector normalizer used by this UVN.
     */
    public VectorNormalizer getVectorNormalizer() {
        return delegate;
    }

    @Override
    public InvertibleFunction<Long2DoubleMap, Long2DoubleMap> makeTransformation(long user, Long2DoubleMap vector) {
        return delegate.makeTransformation(vector);
    }
}
