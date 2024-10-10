package org.cryptomator.ui.health;

import org.cryptomator.common.vaults.Vault;
import org.cryptomator.cryptofs.VaultConfig;
import org.cryptomator.cryptofs.health.api.HealthCheck;
import org.cryptomator.cryptolib.api.Masterkey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.github.javafaker.Faker;
import org.mockito.Mockito;

import java.security.SecureRandom;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

	@Test
	public void testExecuteBatchAndSetState() {
		// Créer un objet fictif de type Check
		Check mockCheck = mock(Check.class);

		// Simuler la méthode getHealthCheck() pour renvoyer un objet HealthCheck fictif
		when(mockCheck.getHealthCheck()).thenReturn(mock(HealthCheck.class));

		// Exécuter un batch de vérifications en utilisant checkExecutor
		checkExecutor.executeBatch(List.of(mockCheck));

		// Vérifier que l'état du Check a été mis à jour à SCHEDULED
		verify(mockCheck).setState(Check.CheckState.SCHEDULED);

		// Créer un nouvel objet Check avec un HealthCheck fictif
		Check check = new Check(mock(HealthCheck.class));

		// Mettre à jour l'état à RUNNING
		check.setState(Check.CheckState.RUNNING);

		// Vérifier que l'état est bien mis à jour à RUNNING
		assertEquals(Check.CheckState.RUNNING, check.getState(), "L'état du Check devrait être mis à jour à RUNNING.");
	}


	@Test
	public void testSetErrorAndProperties() {
		// Créer un objet Check avec un HealthCheck fictif
		Check check = new Check(mock(HealthCheck.class));

		// Créer une exception pour simuler une erreur et l'attribuer au Check
		Throwable testError = new RuntimeException("Test error");
		check.setError(testError);

		// Vérifier que la propriété d'erreur reflète correctement l'erreur définie
		assertEquals(testError, check.getError(), "La propriété d'erreur devrait refléter correctement l'erreur définie.");

		// Définir la propriété chosenForExecution à true
		check.chosenForExecutionProperty().set(true);

		// Vérifier que la propriété chosenForExecution est vraie lorsque définie
		assertEquals(true, check.isChosenForExecution(), "La propriété chosenForExecution devrait être vraie lorsqu'elle est définie.");

		// Créer un HealthCheck fictif et définir un nom simulé avec Faker
		HealthCheck mockHealthCheck = mock(HealthCheck.class);
		String fakeName = faker.company().name();
		when(mockHealthCheck.name()).thenReturn(fakeName);

		// Créer un nouvel objet Check avec le HealthCheck fictif
		Check newCheck = new Check(mockHealthCheck);

		// Vérifier que le nom du Check correspond au nom fourni par le HealthCheck
		assertEquals(fakeName, newCheck.getName(), "Le nom du Check devrait correspondre au nom fourni par le HealthCheck.");
	}

}
