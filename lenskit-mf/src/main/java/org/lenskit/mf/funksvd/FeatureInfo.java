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
package org.lenskit.mf.funksvd;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleLists;

import java.io.Serializable;

/**
 * Information about a feature.
 *
 * @author <a href="http://www.grouplens.org">GroupLens Research</a>
 * @since 1.1
 */
public class FeatureInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int feature;
    private final double userAverage;
    private final double itemAverage;
    private final double singularValue;
    private final DoubleList trainingErrors;

    private FeatureInfo(int f, double uavg, double iavg, double sval,
                        DoubleList errors) {
        feature = f;
        userAverage = uavg;
        itemAverage = iavg;
        singularValue = sval;
        trainingErrors = new DoubleArrayList(errors);
    }

    //region Getters
    /**
     * Get the feature number.
     *
     * @return The feature number.
     */
    public int getFeature() {
        return feature;
    }

    /**
     * Get the iteration count for the feature.
     *
     * @return The number of iterations used to train the feature.
     */
    public int getIterCount() {
        return trainingErrors.size();
    }

    /**
     * Get the training error for each iteration.
     * @return The training error for each iteration.
     */
    public DoubleList getTrainingErrors() {
        return DoubleLists.unmodifiable(trainingErrors);
    }

    /**
     * Get the last training RMSE of the feature.
     *
     * @return The RMSE of the last iteration training this feature.
     */
    public double getLastRMSE() {
        return trainingErrors.getDouble(trainingErrors.size() - 1);
    }

    /**
     * Get the last delta RMSE of the feature.
     *
     * @return The RMSE improvement in the last training round of this feature.
     */
    public double getLastDeltaRMSE() {
        int n = trainingErrors.size();
        return trainingErrors.getDouble(n-2) - trainingErrors.getDouble(n-1);
    }

    /**
     * Get the user average value of this feature.
     *
     * @return The user average value.
     */
    public double getUserAverage() {
        return userAverage;
    }

    /**
     * Get the item average value of this feature.
     *
     * @return The item average value.
     */
    public double getItemAverage() {
        return itemAverage;
    }

    /**
     * Get the singular value of this feature.
     *
     * @return The singular value (weight) of the feature.
     */
    public double getSingularValue() {
        return singularValue;
    }
    //endregion

    /**
     * Helper class to build feature info.
     */
    public static class Builder implements org.apache.commons.lang3.builder.Builder<FeatureInfo> {
        private final int feature;
        private double userAverage;
        private double itemAverage;
        private double singularValue;
        private DoubleList trainingError = new DoubleArrayList();

        /**
         * Construct a new builder.
         * @param f The feature number.
         */
        public Builder(int f) {
            feature = f;
        }

        /**
         * Get the feature's number.
         * @return The feature's number.
         */
        public int getFeature() {
            return feature;
        }

        @Override
        public FeatureInfo build() {
            return new FeatureInfo(feature, userAverage, itemAverage, singularValue, trainingError);
        }

        public double getUserAverage() {
            return userAverage;
        }

        public Builder setUserAverage(double userAverage) {
            this.userAverage = userAverage;
            return this;
        }

        public double getItemAverage() {
            return itemAverage;
        }

        public Builder setItemAverage(double itemAverage) {
            this.itemAverage = itemAverage;
            return this;
        }

        public double getSingularValue() {
            return singularValue;
        }

        /**
         * Set the singular value for this feature.
         * @param singularValue The feature's singular value.
         * @return The builder (for chaining).
         */
        public Builder setSingularValue(double singularValue) {
            this.singularValue = singularValue;
            return this;
        }

        /**
         * Add the error for a training round.
         * @param err The error for the training round.
         * @return The builder (for chaining).
         */
        public Builder addTrainingRound(double err) {
            trainingError.add(err);
            return this;
        }
    }
}
