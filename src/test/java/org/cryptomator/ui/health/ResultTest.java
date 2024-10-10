package org.cryptomator.ui.health;

import com.github.javafaker.Faker;
import org.cryptomator.cryptofs.VaultConfig;
import org.cryptomator.cryptofs.health.api.DiagnosticResult;
import org.cryptomator.cryptolib.api.Cryptor;
import org.cryptomator.cryptolib.api.Masterkey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ResultTest {

	private DiagnosticResult diagnosis;
	private VaultConfig config;
	private Masterkey masterkey;
	private Cryptor cryptor;
	private DiagnosticResult.Fix mockFix;
	private Faker faker;

	@BeforeEach
	void setUp() {
		// Arrange - Mock des dépendances
		diagnosis = mock(DiagnosticResult.class);
		config = mock(VaultConfig.class);
		masterkey = mock(Masterkey.class);
		cryptor = mock(Cryptor.class);
		mockFix = mock(DiagnosticResult.Fix.class);
		faker = new Faker();
	}

	static Stream<Arguments> provideRandomPathsAndFixStates() {
		Faker faker = new Faker();
		return Stream.of(
				Arguments.of(Path.of(faker.file().fileName()), true),
				Arguments.of(Path.of(faker.file().fileName()), false),
				Arguments.of(Path.of(faker.file().fileName()), true),
				Arguments.of(null, false)
		);
	}

	@ParameterizedTest
	@MethodSource("provideRandomPathsAndFixStates")
	void shouldHandleRandomPathsAndFixStates(Path path, boolean fixable) {
		// Arrange - Préparer l'objet DiagnosticResult et déterminer si une correction est possible
		Optional<DiagnosticResult.Fix> fix = fixable ? Optional.of(mockFix) : Optional.empty();
		when(diagnosis.getFix(path, config, masterkey, cryptor)).thenReturn(fix);

		// Act - Créer un objet Result et définir son état
		Result result = Result.create(diagnosis, path, config, masterkey, cryptor);
		Result.FixState expectedState = fixable ? Result.FixState.FIXABLE : Result.FixState.NOT_FIXABLE;
		result.setState(expectedState);

		// Assert - Vérifier que l'objet Result n'est pas null et que l'état est celui attendu
		assertNotNull(result, "L'objet Result ne devrait pas être null pour le chemin: " + path);
		assertEquals(expectedState, result.getState(), "État inattendu pour le chemin: " + path);
		verify(diagnosis).getFix(path, config, masterkey, cryptor);
	}

	@Test
	void shouldHandleLargeNumberOfIterationsWithRandomPaths() {
		// Arrange - Test extrême avec un grand nombre d'itérations
		for (int i = 0; i < 100; i++) {
			Path dynamicPath = Path.of(faker.file().fileName());
			when(diagnosis.getFix(dynamicPath, config, masterkey, cryptor)).thenReturn(Optional.of(mockFix));

			// Act - Créer un objet Result pour chaque itération et définir son état
			Result result = Result.create(diagnosis, dynamicPath, config, masterkey, cryptor);
			result.setState(Result.FixState.FIXABLE);

			// Assert - Vérifier que l'objet Result n'est pas null et que l'état est FIXABLE pour chaque itération
			assertNotNull(result, "L'objet Result ne devrait pas être null pour l'itération " + i);
			assertEquals(Result.FixState.FIXABLE, result.getState(), "L'état devrait être FIXABLE pour l'itération " + i);
			verify(diagnosis).getFix(dynamicPath, config, masterkey, cryptor);
		}
	}
}
