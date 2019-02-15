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
package org.grouplens.lenskit.transform.truncate;

import it.unimi.dsi.fastutil.longs.Long2DoubleMap;
import it.unimi.dsi.fastutil.longs.Long2DoubleOpenHashMap;
import org.grouplens.lenskit.transform.threshold.Threshold;
import org.lenskit.inject.Shareable;
import org.lenskit.util.math.Vectors;

import javax.inject.Inject;
import java.io.Serializable;

/**
 * A {@code VectorTruncator} that will retain entries with values
 * that are accepted by some {@code Threshold}.
 */
@Shareable
public class ThresholdTruncator implements VectorTruncator, Serializable {

    private static final long serialVersionUID = 1L;

    private final Threshold threshold;

    @Inject
    public ThresholdTruncator(Threshold threshold) {
        this.threshold = threshold;
    }

    @Override
    public Long2DoubleMap truncate(Long2DoubleMap v) {
        Long2DoubleMap res = new Long2DoubleOpenHashMap(v.size());
        for (Long2DoubleMap.Entry e: Vectors.fastEntries(v)) {
            if (threshold.retain(e.getDoubleValue())) {
                res.put(e.getLongKey(), e.getDoubleValue());
            }
        }
        return res;
    }
}
