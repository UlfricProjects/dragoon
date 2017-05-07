package com.ulfric.dragoon.extension.creator;

import com.ulfric.dragoon.Factory;
import com.ulfric.dragoon.extension.Extension;
import com.ulfric.dragoon.reflect.FieldProfile;

public class CreatorExtension extends Extension {

	private final FieldProfile fields;

	public CreatorExtension(Factory holder)
	{
		this.fields = FieldProfile.builder()
				.setFactory(new SelfThenDelegateFactory(holder))
				.setFlagToSearchFor(Creator.class)
				.setFilterForIgnoringFieldsEachInvocation(handle ->
				{
					Class<?> type = handle.getField().getType();
					return Factory.class.isAssignableFrom(type);
				})
				.build();
	}

	@Override
	public <T> T transform(T value)
	{
		this.fields.accept(value);
		return value;
	}

	private static final class SelfThenDelegateFactory implements Factory
	{
		private final Factory delegate;

		SelfThenDelegateFactory(Factory delegate)
		{
			this.delegate = delegate;
		}

		@Override
		public <T> T request(Class<T> type)
		{
			if (type.isInstance(this.delegate))
			{
				@SuppressWarnings("unchecked")
				T casted = (T) this.delegate;
				return casted;
			}
			return this.delegate.request(type);
		}
	}

}