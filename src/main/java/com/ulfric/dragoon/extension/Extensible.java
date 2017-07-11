package com.ulfric.dragoon.extension;

import com.ulfric.dragoon.value.Result;

public interface Extensible<T> {

	Result install(T extension);

}
