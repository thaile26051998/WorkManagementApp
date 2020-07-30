package com.hcmus.easywork.models;

import androidx.annotation.NonNull;

/**
 * Any model used in an adapter that supports DiffUtil.Callback should implement IComparable interface for object comparison.
 *
 * @param <T> Type that implements IComparable
 */
public interface IComparableModel<T extends IComparableModel> {
    boolean isTheSame(@NonNull T item);
}
