/* 
 * (C) Copyright 2015-2016 by MSDK Development Team
 *
 * This software is dual-licensed under either
 *
 * (a) the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation
 *
 * or (per the licensee's choosing)
 *
 * (b) the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */

package io.github.msdk.io.chromatof;

/**
 * A pair of two generic objects.
 * 
 * @param <F>
 *            the type of the first element in the pair.
 * @param <S>
 *            the type of the second element in the pair.
 */
public class Pair<F, S> extends
        java.util.AbstractMap.SimpleImmutableEntry<F, S> {

    public Pair(F f, S s) {
        super(f, s);
    }

    public F getFirst() {
        return getKey();
    }

    public S getSecond() {
        return getValue();
    }

    @Override
    public String toString() {
        return "[" + getKey() + "," + getValue() + "]";
    }
}