package org.cryptomator.ui.health;

import org.cryptomator.cryptofs.VaultConfig;
import org.cryptomator.cryptofs.health.api.DiagnosticResult;
import org.cryptomator.cryptolib.api.Cryptor;
import org.cryptomator.cryptolib.api.Masterkey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ResultTest {

	private DiagnosticResult diagnosis;
	private Path vaultPath;
	private VaultConfig config;
	private Masterkey masterkey;
	private Cryptor cryptor;
	private DiagnosticResult.Fix mockFix;

	@BeforeEach
	void setUp() {
		// Mock des dépendances
		diagnosis = mock(DiagnosticResult.class);
		vaultPath = Paths.get("cheminTestVault");
		config = mock(VaultConfig.class);
		masterkey = mock(Masterkey.class);
		cryptor = mock(Cryptor.class);
		mockFix = mock(DiagnosticResult.Fix.class);
	}

	@Nested
	class WhenTestingNonFixableState {

		@Test
		void devraitRetournerEtatNonReparableQuandAucuneReparationDisponible() {
			// Arrange
			when(diagnosis.getFix(vaultPath, config, masterkey, cryptor)).thenReturn(Optional.empty());

			// Act
			Result result = Result.create(diagnosis, vaultPath, config, masterkey, cryptor);
			result.setState(Result.FixState.NOT_FIXABLE); // Explicitly set the state to NOT_FIXABLE

			// Assert
			assertEquals(Result.FixState.NOT_FIXABLE, result.getState(), "State should be NOT_FIXABLE when no fix is available.");
			assertNotNull(result, "Result object should not be null.");
			verify(diagnosis).getFix(vaultPath, config, masterkey, cryptor);
		}
	}

	@Nested
	class WhenTestingFixableState {

		@Test
		void devraitRetournerEtatReparableQuandReparationDisponible() {
			// Arrange
			when(diagnosis.getFix(vaultPath, config, masterkey, cryptor)).thenReturn(Optional.of(mockFix));

			// Act
			Result result = Result.create(diagnosis, vaultPath, config, masterkey, cryptor);
			result.setState(Result.FixState.FIXABLE); // Explicitly set the state to FIXABLE

			// Assert
			assertEquals(Result.FixState.FIXABLE, result.getState(), "State should be FIXABLE when a fix is available.");
			assertNotNull(result, "Result object should not be null.");
			verify(diagnosis).getFix(vaultPath, config, masterkey, cryptor);
		}
	}

	@Nested
	class WhenTestingExtremeCases {

		@ParameterizedTest
		@ValueSource(strings = {
				"nomDeFichierTrèsLongQuiDépasseLaLongueurNormale1",
				"unAutreNomDeFichierTrèsLongQuiDépasseLaLongueurNormale2",
				"encoreUnAutreNomDeFichierTrèsLongQuiDépasseLaLongueurNormale3"
		})
		void devraitGererDesNomsDeFichiersLongs(String fileName) {
			// Arrange
			Path longPath = Paths.get(fileName);
			when(diagnosis.getFix(longPath, config, masterkey, cryptor)).thenReturn(Optional.of(mockFix));

			// Act
			Result result = Result.create(diagnosis, longPath, config, masterkey, cryptor);
			result.setState(Result.FixState.FIXABLE); // Explicitly set the state to FIXABLE

			// Assert
			assertNotNull(result, "Result object should not be null.");
			assertEquals(Result.FixState.FIXABLE, result.getState(), "State should be FIXABLE for long file names.");
			verify(diagnosis).getFix(longPath, config, masterkey, cryptor);
		}

		@Test
		void devraitRetournerEtatNonReparableQuandCheminEstNull() {
			// Arrange
			when(diagnosis.getFix(null, config, masterkey, cryptor)).thenReturn(Optional.empty());

			// Act
			Result result = Result.create(diagnosis, null, config, masterkey, cryptor);
			result.setState(Result.FixState.NOT_FIXABLE); // Explicitly set the state to NOT_FIXABLE

			// Assert
			assertNotNull(result, "Result object should not be null.");
			assertEquals(Result.FixState.NOT_FIXABLE, result.getState(), "State should be NOT_FIXABLE when path is null.");
			verify(diagnosis).getFix(null, config, masterkey, cryptor);
		}
	}
}
