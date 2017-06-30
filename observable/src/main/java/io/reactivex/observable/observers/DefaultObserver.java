/**
 * Copyright (c) 2016-present, RxJava Contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See
 * the License for the specific language governing permissions and limitations under the License.
 */

package io.reactivex.observable.observers;

import io.reactivex.common.Disposable;
import io.reactivex.common.annotations.NonNull;
import io.reactivex.common.internal.disposables.DisposableHelper;
import io.reactivex.observable.Observer;
import io.reactivex.observable.internal.utils.EndObserverHelper;

/**
 * Abstract base implementation of an {@link io.reactivex.observable.Observer Observer} with support for cancelling a
 * subscription via {@link #cancel()} (synchronously) and calls {@link #onStart()}
 * when the subscription happens.
 *
 * <p>All pre-implemented final methods are thread-safe.
 *
 * <p>Use the protected {@link #cancel()} to dispose the sequence from within an
 * {@code onNext} implementation.
 *
 * <p>Like all other consumers, {@code DefaultObserver} can be subscribed only once.
 * Any subsequent attempt to subscribe it to a new source will yield an
 * {@link IllegalStateException} with message {@code "It is not allowed to subscribe with a(n) <class name> multiple times."}.
 *
 * <p>Implementation of {@link #onStart()}, {@link #onNext(Object)}, {@link #onError(Throwable)}
 * and {@link #onComplete()} are not allowed to throw any unchecked exceptions.
 * If for some reason this can't be avoided, use {@link io.reactivex.observable.Observable#safeSubscribe(io.reactivex.observable.Observer)}
 * instead of the standard {@code subscribe()} method.
 *
 * <p>Example<code><pre>
 * Observable.range(1, 5)
 *     .subscribeWith(new DefaultObserver&lt;Integer>() {
 *         &#64;Override public void onStart() {
 *             System.out.println("Start!");
 *         }
 *         &#64;Override public void onNext(Integer t) {
 *             if (t == 3) {
 *                 cancel();
 *             }
 *             System.out.println(t);
 *         }
 *         &#64;Override public void onError(Throwable t) {
 *             t.printStackTrace();
 *         }
 *         &#64;Override public void onComplete() {
 *             System.out.println("Done!");
 *         }
 *     });
 * </pre></code>
 *
 * @param <T> the value type
 */
public abstract class DefaultObserver<T> implements Observer<T> {
    private Disposable s;
    @Override
    public final void onSubscribe(@NonNull Disposable s) {
        if (EndObserverHelper.validate(this.s, s, getClass())) {
            this.s = s;
            onStart();
        }
    }

    /**
     * Cancels the upstream's disposable.
     */
    protected final void cancel() {
        Disposable s = this.s;
        this.s = DisposableHelper.DISPOSED;
        s.dispose();
    }
    /**
     * Called once the subscription has been set on this observer; override this
     * to perform initialization.
     */
    protected void onStart() {
    }

}
