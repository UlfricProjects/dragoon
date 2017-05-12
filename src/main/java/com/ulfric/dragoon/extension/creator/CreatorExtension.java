package com.ulfric.dragoon.extension.creator;

import com.ulfric.dragoon.Factory;
import com.ulfric.dragoon.extension.Extension;
import com.ulfric.dragoon.reflect.FieldProfile;

public class CreatorExtension extends Extension {

	private final FieldProfile fields;

	public CreatorExtension(Factory holder)
	{
		this.fields = FieldProfile.builder()
				.setFactory(new IdentityFactory(holder))
				.setFlagToSearchFor(Creator.class)
				.setFilterForIgnoringFieldsEachInvocation(handle ->
				{
					Class<?> type = handle.getField().getType();
					return type.isAssignableFrom(Factory.class);
				})
				.build();
	}

	@Override
	public <T> T transform(T value)
	{
		this.fields.accept(value);
		return value;
	}

	private static final class IdentityFactory implements Factory
	{
		private final Factory delegate;

		IdentityFactory(Factory delegate)
		{
			this.delegate = delegate;
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T> T request(Class<T> type)
		{
			return (T) this.delegate;
		}
	}

}