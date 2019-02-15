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
package org.lenskit.eval.traintest.predict;

import it.unimi.dsi.fastutil.longs.Long2DoubleMap;
import org.lenskit.api.RecommenderEngine;
import org.lenskit.api.Result;
import org.lenskit.api.ResultMap;
import org.lenskit.eval.traintest.AlgorithmInstance;
import org.lenskit.eval.traintest.DataSet;
import org.lenskit.eval.traintest.TestUser;
import org.lenskit.eval.traintest.metrics.MetricColumn;
import org.lenskit.eval.traintest.metrics.MetricResult;
import org.lenskit.eval.traintest.metrics.TypedMetricResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Evaluate a recommender's prediction accuracy with MAE (Mean Absolute Error).
 */
public class MAEPredictMetric extends PredictMetric<MAEPredictMetric.Context> {
    private static final Logger logger = LoggerFactory.getLogger(MAEPredictMetric.class);

    public MAEPredictMetric() {
        super(UserResult.class, AggregateResult.class);
    }

    @Nullable
    @Override
    public Context createContext(AlgorithmInstance algorithm, DataSet dataSet, RecommenderEngine engine) {
        return new Context();
    }

    @Nonnull
    @Override
    public MetricResult measureUser(TestUser user, ResultMap predictions, Context context) {
        Long2DoubleMap ratings = user.getTestRatings();
        double totalError = 0;
        int n = 0;
        for (Result e : predictions) {
            if (!e.hasScore()) {
                continue;
            }

            double err = e.getScore() - ratings.get(e.getId());
            totalError += Math.abs(err);
            n++;
        }
        if (n > 0) {
            double mae = totalError / n;
            context.addUser(n, totalError, mae);
            return new UserResult(mae);
        } else {
            return MetricResult.empty();
        }
    }

    @Nonnull
    @Override
    public MetricResult getAggregateMeasurements(Context context) {
        return context.finish();
    }

    static class UserResult extends TypedMetricResult {
        @MetricColumn("MAE")
        public final double mae;

        public UserResult(double err) {
            mae = err;
        }
    }

    static class AggregateResult extends TypedMetricResult {
        @MetricColumn("MAE.ByUser")
        public final double userMAE;
        @MetricColumn("MAE.ByRating")
        public final double globalMAE;

        public AggregateResult(double uerr, double gerr) {
            userMAE = uerr;
            globalMAE = gerr;
        }
    }

    public class Context {
        private double totalError = 0;
        private double totalMAE = 0;
        private int nratings = 0;
        private int nusers = 0;

        private synchronized void addUser(int n, double err, double mae) {
            totalError += err;
            totalMAE += mae;
            nratings += n;
            nusers += 1;
        }

        public synchronized MetricResult finish() {
            if (nratings > 0) {
                double v = totalError / nratings;
                logger.info("RMSE: {}", v);
                return new AggregateResult(totalMAE / nusers, v);
            } else {
                return MetricResult.empty();
            }
        }
    }
}
