package org.cryptomator.ui.health;

import org.cryptomator.common.vaults.Vault;
import org.cryptomator.cryptofs.VaultConfig;
import org.cryptomator.cryptofs.health.api.HealthCheck;
import org.cryptomator.cryptolib.api.Masterkey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import com.github.javafaker.Faker;
import org.mockito.Mockito;

import java.security.SecureRandom;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CheckTest {

	private CheckExecutor checkExecutor;
	private Vault mockVault;
	private AtomicReference<Masterkey> mockMasterkeyRef;
	private AtomicReference<VaultConfig> mockVaultConfigRef;
	private SecureRandom mockCsprng;
	private Faker faker;

	@BeforeEach
	public void setUp() {
		mockVault = mock(Vault.class);
		mockMasterkeyRef = mock(AtomicReference.class);
		mockVaultConfigRef = mock(AtomicReference.class);
		mockCsprng = mock(SecureRandom.class);
		faker = new Faker();
		when(mockVault.getPath()).thenReturn(mock(java.nio.file.Path.class));

		checkExecutor = new CheckExecutor(mockVault, mockMasterkeyRef, mockVaultConfigRef, mockCsprng);
	}

	@Nested
	class WhenExecutingBatchChecks {

		@Test
		void shouldSetCheckStateToScheduledWhenBatchExecuted() {
			// Arrange
			Check mockCheck1 = mock(Check.class);
			Check mockCheck2 = mock(Check.class);
			when(mockCheck1.getHealthCheck()).thenReturn(mock(HealthCheck.class));
			when(mockCheck2.getHealthCheck()).thenReturn(mock(HealthCheck.class));

			// Act
			checkExecutor.executeBatch(List.of(mockCheck1, mockCheck2));

			// Assert
			verify(mockCheck1).setState(Check.CheckState.SCHEDULED);
			verify(mockCheck2).setState(Check.CheckState.SCHEDULED);
			assertNotNull(mockCheck1.getHealthCheck(), "HealthCheck should not be null after batch execution");
			assertNotNull(mockCheck2.getHealthCheck(), "HealthCheck should not be null after batch execution");
		}

		@Test
		void shouldUpdateCheckStateToRunning() {
			// Arrange
			Check check = new Check(mock(HealthCheck.class));

			// Act
			check.setState(Check.CheckState.RUNNING);

			// Assert
			assertEquals(Check.CheckState.RUNNING, check.getState(), "L'etat du Check devrait etre mis a jour a RUNNING.");
			assertNull(check.getError(), "Error should be null when state is set to RUNNING.");
			assertFalse(check.isChosenForExecution(), "Check should not be chosen for execution by default.");
		}
	}

	@Nested
	class WhenSettingErrorAndProperties {

		@Test
		void shouldReflectErrorWhenSet() {
			// Arrange
			Check check = new Check(mock(HealthCheck.class));
			Throwable testError = new RuntimeException("Test error");

			// Act
			check.setError(testError);
			check.setState(Check.CheckState.ERROR); // Explicitly set the state to ERROR

			// Assert
			assertEquals(testError, check.getError(), "La propriete d'erreur devrait refléter correctement l'erreur definie.");
			assertEquals(Check.CheckState.ERROR, check.getState(), "State should be ERROR when an error is set.");
			assertFalse(check.isChosenForExecution(), "Check should not be chosen for execution when an error is set.");
		}

		@Test
		void shouldSetChosenForExecutionPropertyToTrue() {
			// Arrange
			Check check = new Check(mock(HealthCheck.class));

			// Act
			check.chosenForExecutionProperty().set(true);

			// Assert
			assertTrue(check.isChosenForExecution(), "La propriete chosenForExecution devrait etre vraie lorsqu'elle est definie.");
			assertNull(check.getError(), "Error should be null when check is chosen for execution.");
		}

		@ParameterizedTest
		@ValueSource(strings = { "Company A", "Company B", "Company C" })
		void shouldMatchHealthCheckNameWithProvidedName(String fakeName) {
			// Arrange
			HealthCheck mockHealthCheck = mock(HealthCheck.class);
			when(mockHealthCheck.name()).thenReturn(fakeName);

			// Act
			Check newCheck = new Check(mockHealthCheck);

			// Assert
			assertEquals(fakeName, newCheck.getName(), "Le nom du Check devrait correspondre au nom fourni par le HealthCheck.");
			assertNotNull(newCheck.getHealthCheck(), "HealthCheck should not be null after creating Check instance.");
		}
	}
}
