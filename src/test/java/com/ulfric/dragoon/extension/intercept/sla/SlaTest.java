package com.ulfric.dragoon.extension.intercept.sla;

import java.util.logging.Logger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.ulfric.dragoon.ObjectFactory;
import com.ulfric.dragoon.extension.inject.Inject;

class SlaTest {

	private ObjectFactory factory;

	@Mock
	private Logger logger;

	@BeforeEach
	void setup() {
		factory = new ObjectFactory();
		MockitoAnnotations.initMocks(this);

		factory.bind(Logger.class).toValue(logger);
	}

	@Test
	void testQuickLogsNothing() throws Exception {
		factory.bind(Work.class).to(QuickWork.class);
		factory.request(Example.class).work();
		Mockito.verifyZeroInteractions(logger);
	}

	@Test
	void testLongDoesLog() throws Exception {
		factory.bind(Work.class).to(LongWork.class);
		factory.request(Example.class).work();
		Mockito.verify(logger, Mockito.times(1)).warning(ArgumentMatchers.startsWith(
				"public void com.ulfric.dragoon.extension.intercept.sla.SlaTest$Example.work() violated it's SLA of 3.0 milliseconds by taking "));
	}

	public static class Example {
		@Inject
		private Work work;

		@SLA(3)
		public void work() {
			work.work();
		}
	}

	public interface Work {
		void work();
	}

	public static class QuickWork implements Work {

		@Override
		public void work() {
		}

	}

	public static class LongWork implements Work {

		@Override
		public void work() {
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}

	}
}
