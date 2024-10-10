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
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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
import java.util.stream.Stream;

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
		// Arrange - Initialiser Mockito et Faker
		MockitoAnnotations.openMocks(this);
		faker = new Faker();

		// Arrange - Mock des paramètres nécessaires pour le constructeur de ReportWriter
		mockVault = mock(Vault.class);
		mockVaultConfigRef = new AtomicReference<>(mock(VaultConfig.class));
		mockApplication = mock(Application.class);
		mockEnvironment = mock(Environment.class);

		// Arrange - Création de l'instance ReportWriter avec des dépendances mockées
		reportWriter = new ReportWriter(mockVault, mockVaultConfigRef, mockApplication, mockEnvironment);
	}

	static Stream<Arguments> provideRandomPaths() {
		Faker faker = new Faker();
		return Stream.of(
				Arguments.of(faker.file().fileName(), faker.file().fileName()),
				Arguments.of(faker.file().fileName(), faker.file().fileName()),
				Arguments.of(faker.file().fileName(), faker.file().fileName())
		);
	}

	@Nested
	class WhenTestingReportWritingWithVariousPaths {

		@ParameterizedTest
		@MethodSource("org.cryptomator.ui.health.ReportWriterTest#provideRandomPaths")
		void shouldHandleRandomPaths(String path1, String path2) {
			// Arrange - Créer des chemins aléatoires
			Path randomPath1 = Path.of(path1);
			Path randomPath2 = Path.of(path2);

			// Act & Assert - Tester le premier chemin aléatoire
			assertThrows(IOException.class, () -> {
				try (FileWriter writer = new FileWriter(randomPath1.toFile())) {
					writer.write(faker.lorem().paragraph());
				}
			}, "Une IOException devrait être levée pour un chemin de fichier invalide");

			// Act & Assert - Tester le deuxième chemin aléatoire
			assertThrows(IOException.class, () -> {
				try (FileWriter writer = new FileWriter(randomPath2.toFile())) {
					writer.write(faker.lorem().paragraph());
				}
			}, "Une IOException devrait être levée pour un chemin de fichier invalide");
		}
	}

	@Nested
	class WhenTestingSimultaneousWrites {

		@Test
		@Timeout(value = 5, unit = TimeUnit.SECONDS)
		void testSimultaneousWrites() throws IOException {
			// Arrange - Créer un répertoire temporaire pour éviter les problèmes de chemin
			Path tempDir = Files.createTempDirectory("testSimultaneousReports");

			// Arrange - Créer un contenu de rapport fictif avec Faker
			String fakeReportContent = faker.lorem().paragraph();

			// Act - Simuler plusieurs écritures simultanées
			int numberOfConcurrentWrites = 5;
			Thread[] threads = new Thread[numberOfConcurrentWrites];
			for (int i = 0; i < numberOfConcurrentWrites; i++) {
				threads[i] = new Thread(() -> {
					try {
						// Arrange - Créer un fichier fictif avec un nom aléatoire
						File mockFile = new File(tempDir.toFile(), faker.file().fileName());

						// Act - Assurer la création de tous les répertoires parents
						mockFile.getParentFile().mkdirs();

						// Act - Simuler l'écriture du rapport
						try (FileWriter writer = new FileWriter(mockFile)) {
							writer.write(fakeReportContent);
						}

						// Assert - Vérifier que le contenu du rapport a été écrit avec succès
						assertTrue(mockFile.exists(), "Le fichier devrait exister après l'écriture");

					} catch (IOException e) {
						e.printStackTrace();
					}
				});
				threads[i].start();
			}

			// Act - Attendre la fin de tous les threads
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
