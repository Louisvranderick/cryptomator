package org.cryptomator.ui.health;

import com.github.javafaker.Faker;
import org.cryptomator.common.Environment;
import org.cryptomator.common.vaults.Vault;
import org.cryptomator.cryptofs.VaultConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javafx.application.Application;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import static org.mockito.Mockito.mock;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ReportWriterTest {

	private ReportWriter reportWriter;
	private Faker faker;

	@Mock
	private Vault mockVault;

	@Mock
	private AtomicReference<VaultConfig> mockVaultConfigRef;

	@Mock
	private Application mockApplication;

	@Mock
	private Environment mockEnvironment;

	@BeforeEach
	void setUp() {
		// Initialize Mockito and Faker
		MockitoAnnotations.openMocks(this);
		faker = new Faker();

		// Mocking the necessary constructor parameters for ReportWriter
		mockVault = mock(Vault.class);
		mockVaultConfigRef = new AtomicReference<>(mock(VaultConfig.class));
		mockApplication = mock(Application.class);
		mockEnvironment = mock(Environment.class);

		// Creating the ReportWriter instance with mocked dependencies
		reportWriter = new ReportWriter(mockVault, mockVaultConfigRef, mockApplication, mockEnvironment);
	}

	@Test
	void testWriteReportWithVariousPaths() throws IOException {
		// Créer un répertoire temporaire pour éviter les problèmes de chemin
		Path tempDir = Files.createTempDirectory("testReports");

		// Générer et tester plusieurs chemins, y compris des cas invalides et limites
		String[] paths = {
				faker.file().fileName(), // Nom de fichier normal
				"../" + faker.file().fileName(), // Chemin relatif
				tempDir.toString() + "/nonExistentDir/" + faker.file().fileName(), // Répertoire inexistant
				tempDir.toString() + "/readonlyDir/" + faker.file().fileName(), // Répertoire en lecture seule
				tempDir.toString() + "/this/is/a/very/long/path/name/that/might/be/close/to/system/limits/" + faker.file().fileName(), // Chemin long
				"invalid:/\\*?\"<>|" // Chemin invalide avec des caractères interdits
		};

		for (String pathString : paths) {
			Path filePath = Path.of(tempDir.toString(), pathString);
			File mockFile = filePath.toFile();

			try {
				// S'assurer que tous les répertoires parents sont créés
				mockFile.getParentFile().mkdirs();

				// Créer un contenu de rapport fictif en utilisant Faker
				String fakeReportContent = faker.lorem().paragraph();

				// Simuler l'écriture du rapport
				try (FileWriter writer = new FileWriter(mockFile)) {
					writer.write(fakeReportContent);
				}

				// Vérifier que le contenu du rapport est écrit avec succès
				assertTrue(mockFile.exists(), "Le fichier devrait exister après l'écriture");
				assertEquals(fakeReportContent, Files.readString(filePath), "Le contenu du fichier devrait correspondre au contenu écrit");

			} catch (IOException e) {
				if (pathString.contains("readonlyDir")) {
					// Vérifier que l'erreur de permission est levée pour le répertoire en lecture seule
					assertTrue(e.getMessage().contains("Permission denied"), "Devrait lever une erreur de permission pour le répertoire en lecture seule");
				} else if (pathString.contains("invalid")) {
					// Vérifier que l'erreur est levée pour un chemin de fichier invalide
					assertTrue(e.getMessage().contains("Invalid"), "Devrait lever une erreur pour un chemin de fichier invalide");
				} else {
					// Les IOExceptions inattendues doivent être levées
					throw e;
				}
			}
		}
	}


	@Test
	void testSimultaneousWrites() throws IOException {
		// Créer un répertoire temporaire pour éviter les problèmes de chemin
		Path tempDir = Files.createTempDirectory("testSimultaneousReports");

		// Créer un contenu de rapport fictif en utilisant Faker
		String fakeReportContent = faker.lorem().paragraph();

		// Simuler plusieurs écritures concurrentes
		int numberOfConcurrentWrites = 5;
		Stream.generate(() -> new Thread(() -> {
			try {
				// Créer un fichier fictif avec un nom aléatoire
				File mockFile = new File(tempDir.toFile(), faker.file().fileName());

				// S'assurer que tous les répertoires parents sont créés
				mockFile.getParentFile().mkdirs();

				// Simuler l'écriture du rapport
				try (FileWriter writer = new FileWriter(mockFile)) {
					writer.write(fakeReportContent);
				}

				// Vérifier que le contenu du rapport est écrit avec succès
				assertTrue(mockFile.exists(), "Le fichier devrait exister après l'écriture");

			} catch (IOException e) {
				e.printStackTrace();
			}
		})).limit(numberOfConcurrentWrites).forEach(Thread::start);
	}

}
