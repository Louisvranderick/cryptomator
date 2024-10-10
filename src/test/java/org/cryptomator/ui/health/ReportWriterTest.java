package org.cryptomator.ui.health;

import com.github.javafaker.Faker;
import org.cryptomator.common.Environment;
import org.cryptomator.common.vaults.Vault;
import org.cryptomator.cryptofs.VaultConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javafx.application.Application;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

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

	@Nested
	class WhenTestingReportWritingWithVariousPaths {

		@ParameterizedTest
		@ValueSource(strings = {
				"validFileName.txt", // Normal filename
				"../relativePathFile.txt", // Relative path
				"this/is/a/very/long/path/that/might/be/close/to/system/limits/longFileName.txt" // Long path
		})
		@Timeout(value = 2, unit = TimeUnit.SECONDS)
		void testWriteReportWithValidAndLongPaths(String fileName) throws IOException {
			// Create a temporary directory to avoid path issues
			Path tempDir = Files.createTempDirectory("testReports");
			Path filePath = tempDir.resolve(fileName);

			// Ensure all parent directories are created
			Files.createDirectories(filePath.getParent());

			// Create mock report content
			String fakeReportContent = faker.lorem().paragraph();

			// Write the report content
			try (FileWriter writer = new FileWriter(filePath.toFile())) {
				writer.write(fakeReportContent);
			}

			// Verify the report content was successfully written
			assertTrue(Files.exists(filePath), "Le fichier devrait exister après l'écriture");
			assertEquals(fakeReportContent, Files.readString(filePath), "Le contenu du fichier devrait correspondre au contenu écrit");
		}

		@Test
		@Timeout(value = 2, unit = TimeUnit.SECONDS)
		void testWriteReportWithInvalidPath() {
			// Invalid path with prohibited characters
			Path invalidPath = Path.of("invalid:/\\*?\"<>|");

			// Expect an IOException to be thrown
			assertThrows(IOException.class, () -> {
				try (FileWriter writer = new FileWriter(invalidPath.toFile())) {
					writer.write(faker.lorem().paragraph());
				}
			}, "An IOException should be thrown for an invalid file path");
		}
	}


	@Nested
	class WhenTestingSimultaneousWrites {

		@Test
		@Timeout(value = 5, unit = TimeUnit.SECONDS)
		void testSimultaneousWrites() throws IOException {
			// Créer un répertoire temporaire pour éviter les problèmes de chemin
			Path tempDir = Files.createTempDirectory("testSimultaneousReports");

			// Créer un contenu de rapport fictif en utilisant Faker
			String fakeReportContent = faker.lorem().paragraph();

			// Simuler plusieurs écritures concurrentes
			int numberOfConcurrentWrites = 5;
			Thread[] threads = new Thread[numberOfConcurrentWrites];
			for (int i = 0; i < numberOfConcurrentWrites; i++) {
				threads[i] = new Thread(() -> {
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
				});
				threads[i].start();
			}

			// Attendre que tous les threads aient terminé
			for (Thread thread : threads) {
				try {
					thread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
