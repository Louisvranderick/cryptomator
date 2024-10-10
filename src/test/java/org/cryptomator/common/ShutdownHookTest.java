package org.cryptomator.common;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ShutdownHookTest {

	private ShutdownHook shutdownHook;

	@BeforeEach
	void setUp() {
		shutdownHook = new ShutdownHook();
	}

	@Test
	void testTasksExecutedInCorrectOrder() {
		// Arrange
		Runnable task1 = mock(Runnable.class);
		Runnable task2 = mock(Runnable.class);
		Runnable task3 = mock(Runnable.class);

		// Act
		shutdownHook.runOnShutdown(ShutdownHook.PRIO_FIRST, task1);
		shutdownHook.runOnShutdown(ShutdownHook.PRIO_DEFAULT, task2);
		shutdownHook.runOnShutdown(ShutdownHook.PRIO_LAST, task3);
		shutdownHook.run();

		// Assert
		InOrder inOrder = inOrder(task1, task2, task3);
		inOrder.verify(task1).run();
		inOrder.verify(task2).run();
		inOrder.verify(task3).run();
	}


	@Test
	void testTasksExecutedDuringShutdown() {
		// Arrange
		Runnable task = mock(Runnable.class);
		shutdownHook.runOnShutdown(task);

		// Act
		shutdownHook.run();

		// Assert
		verify(task).run();
	}
}