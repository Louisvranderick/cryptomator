package org.cryptomator.ui.health;

import com.github.javafaker.Faker;
import org.cryptomator.cryptofs.VaultConfig;
import org.cryptomator.cryptofs.health.api.DiagnosticResult;
import org.cryptomator.cryptolib.api.Cryptor;
import org.cryptomator.cryptolib.api.Masterkey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class ResultTest {

	private DiagnosticResult diagnosis;
	private Path vaultPath;
	private VaultConfig config;
	private Masterkey masterkey;
	private Cryptor cryptor;
	private DiagnosticResult.Fix mockFix;
	private Faker faker;

	@BeforeEach
	void setUp() {
		faker = new Faker();

		// Mock dependencies
		diagnosis = mock(DiagnosticResult.class);
		vaultPath = Path.of(faker.file().fileName());
		config = mock(VaultConfig.class);
		masterkey = mock(Masterkey.class);
		cryptor = mock(Cryptor.class);
		mockFix = mock(DiagnosticResult.Fix.class);
	}

	@Test
	void testNonFixableState() {
		// Tester l'état non réparable avec différents chemins aléatoires de coffre-fort
		for (int i = 0; i < 5; i++) {
			// Générer un chemin de fichier aléatoire
			Path randomPath = Path.of(faker.file().fileName());

			// Simuler le diagnostic pour renvoyer une option vide (pas de correction possible)
			when(diagnosis.getFix(randomPath, config, masterkey, cryptor)).thenReturn(Optional.empty());

			// Créer le résultat à partir du diagnostic et vérifier que l'état est NOT_FIXABLE
			Result result = Result.create(diagnosis, randomPath, config, masterkey, cryptor);
			assertEquals(Result.FixState.NOT_FIXABLE, result.getState());
		}
	}



	@Test
	void testFixableStateAndStateChanges() {
		// Tester l'état réparable avec différents chemins aléatoires de coffre-fort
		for (int i = 0; i < 5; i++) {
			// Générer un chemin de fichier aléatoire
			Path randomPath = Path.of(faker.file().fileName());

			// Simuler le diagnostic pour renvoyer une correction possible (mockFix)
			when(diagnosis.getFix(randomPath, config, masterkey, cryptor)).thenReturn(Optional.of(mockFix));

			// Créer le résultat à partir du diagnostic et vérifier que l'état est FIXABLE
			Result result = Result.create(diagnosis, randomPath, config, masterkey, cryptor);
			assertEquals(Result.FixState.FIXABLE, result.getState());

			// Tester l'attribution d'un nouvel état pour chaque instance de résultat
			for (Result.FixState state : Result.FixState.values()) {
				result.setState(state);
				assertEquals(state, result.getState());
			}
		}
	}



	@Test
	void testExtremeCasesAndInvalidPaths() {
		// Tester avec des cas extrêmes pour les chemins, tels que plusieurs noms de fichiers longs
		for (int i = 0; i < 5; i++) {
			// Générer un nom de fichier long aléatoire
			String randomLongFileName = faker.lorem().characters(200, 255);
			Path longPath = Path.of(randomLongFileName);

			// Simuler le diagnostic pour renvoyer une correction possible (mockFix) pour le chemin long
			when(diagnosis.getFix(longPath, config, masterkey, cryptor)).thenReturn(Optional.of(mockFix));

			// Créer le résultat à partir du diagnostic et vérifier qu'il n'est pas nul et que l'état est FIXABLE
			Result result = Result.create(diagnosis, longPath, config, masterkey, cryptor);
			assertNotNull(result);
			assertEquals(Result.FixState.FIXABLE, result.getState());
		}

		// Tester avec un chemin invalide (par exemple, null) pour s'assurer que le traitement est approprié
		when(diagnosis.getFix(null, config, masterkey, cryptor)).thenReturn(Optional.empty());
		Result result = Result.create(diagnosis, null, config, masterkey, cryptor);
		assertEquals(Result.FixState.NOT_FIXABLE, result.getState());
	}

}