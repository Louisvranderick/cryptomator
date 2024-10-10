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
	
	/***
	 * Test tasks executed in correct order
	 *
	 * This test verifies that tasks are executed in the correct order
	 * by adding tasks with different priorities to the shutdown hook.
	 * 
	 * The outcome of this test is the tasks being executed in the
	 * correct order.
	 */
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

	/***
	 * Test tasks executed during shutdown
	 *
	 * This test verifies that tasks are executed during shutdown
	 * by adding a task to the shutdown hook and calling the run method.
	 *
	 * The outcome of this test is the task being executed.
	 */
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
