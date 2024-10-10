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
		// Arrange - Initialiser les objets mock et Faker
		mockVault = mock(Vault.class);
		mockMasterkeyRef = mock(AtomicReference.class);
		mockVaultConfigRef = mock(AtomicReference.class);
		mockCsprng = mock(SecureRandom.class);
		faker = new Faker();
		when(mockVault.getPath()).thenReturn(mock(java.nio.file.Path.class));

		// Arrange - Créer une instance de CheckExecutor avec les dépendances mockées
		checkExecutor = new CheckExecutor(mockVault, mockMasterkeyRef, mockVaultConfigRef, mockCsprng);
	}

	@Nested
	class WhenExecutingBatchChecks {

		@Test
		void shouldSetCheckStateToScheduledWhenBatchExecuted() {
			// Arrange - Créer des checks mockés
			Check mockCheck1 = mock(Check.class);
			Check mockCheck2 = mock(Check.class);
			when(mockCheck1.getHealthCheck()).thenReturn(mock(HealthCheck.class));
			when(mockCheck2.getHealthCheck()).thenReturn(mock(HealthCheck.class));

			// Act - Exécuter un batch de checks
			checkExecutor.executeBatch(List.of(mockCheck1, mockCheck2));

			// Assert - Vérifier que l'état des checks est mis à jour à SCHEDULED
			verify(mockCheck1).setState(Check.CheckState.SCHEDULED);
			verify(mockCheck2).setState(Check.CheckState.SCHEDULED);
			assertNotNull(mockCheck1.getHealthCheck(), "HealthCheck ne devrait pas être null après l'exécution du batch");
			assertNotNull(mockCheck2.getHealthCheck(), "HealthCheck ne devrait pas être null après l'exécution du batch");
		}

		@Test
		void shouldUpdateCheckStateToRunning() {
			// Arrange - Créer un nouveau Check
			Check check = new Check(mock(HealthCheck.class));

			// Act - Mettre à jour l'état du Check à RUNNING
			check.setState(Check.CheckState.RUNNING);

			// Assert - Vérifier que l'état est bien mis à jour à RUNNING
			assertEquals(Check.CheckState.RUNNING, check.getState(), "L'état du Check devrait être mis à jour à RUNNING.");
			assertNull(check.getError(), "L'erreur devrait être null lorsque l'état est mis à RUNNING.");
			assertFalse(check.isChosenForExecution(), "Le Check ne devrait pas être choisi pour l'exécution par défaut.");
		}
	}

	@Nested
	class WhenSettingErrorAndProperties {

		@Test
		void shouldReflectErrorWhenSet() {
			// Arrange - Créer un nouveau Check et une erreur test
			Check check = new Check(mock(HealthCheck.class));
			Throwable testError = new RuntimeException("Erreur de test");

			// Act - Définir l'erreur et mettre l'état à ERROR
			check.setError(testError);
			check.setState(Check.CheckState.ERROR);

			// Assert - Vérifier que l'erreur est bien définie et que l'état est mis à jour à ERROR
			assertEquals(testError, check.getError(), "La propriété d'erreur devrait refléter correctement l'erreur définie.");
			assertEquals(Check.CheckState.ERROR, check.getState(), "L'état devrait être ERROR lorsqu'une erreur est définie.");
			assertFalse(check.isChosenForExecution(), "Le Check ne devrait pas être choisi pour l'exécution lorsqu'une erreur est définie.");
		}

		@Test
		void shouldSetChosenForExecutionPropertyToTrue() {
			// Arrange - Créer un nouveau Check
			Check check = new Check(mock(HealthCheck.class));

			// Act - Définir la propriété chosenForExecution à true
			check.chosenForExecutionProperty().set(true);

			// Assert - Vérifier que la propriété chosenForExecution est bien définie à true
			assertTrue(check.isChosenForExecution(), "La propriété chosenForExecution devrait être vraie lorsqu'elle est définie.");
			assertNull(check.getError(), "L'erreur devrait être null lorsque le Check est choisi pour l'exécution.");
		}

		@ParameterizedTest
		@ValueSource(strings = { "Company A", "Company B", "Company C" })
		void shouldMatchHealthCheckNameWithProvidedName(String fakeName) {
			// Arrange - Créer un HealthCheck mocké avec un nom spécifique
			HealthCheck mockHealthCheck = mock(HealthCheck.class);
			when(mockHealthCheck.name()).thenReturn(fakeName);

			// Act - Créer un nouveau Check avec le HealthCheck mocké
			Check newCheck = new Check(mockHealthCheck);

			// Assert - Vérifier que le nom du Check correspond au nom fourni par le HealthCheck
			assertEquals(fakeName, newCheck.getName(), "Le nom du Check devrait correspondre au nom fourni par le HealthCheck.");
			assertNotNull(newCheck.getHealthCheck(), "Le HealthCheck ne devrait pas être null après la création de l'instance Check.");
		}
	}
}
