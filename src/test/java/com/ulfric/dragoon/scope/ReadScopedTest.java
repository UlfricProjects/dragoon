package com.ulfric.dragoon.scope;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.ulfric.verify.Verify;

@RunWith(JUnitPlatform.class)
public class ReadScopedTest {

	@Test
	void testIsRead()
	{
		Verify.that(new ReadScoped<>(Object.class, new Object()).isRead()).isTrue();
	}

	@Test
	void testIsRead_string()
	{
		Verify.that(new ReadScoped<>(Object.class, new Object()).isRead(UUID.randomUUID().toString())).isTrue();
	}

}
