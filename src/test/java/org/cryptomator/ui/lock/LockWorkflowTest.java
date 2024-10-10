package org.cryptomator.ui.lock;

import com.github.javafaker.Faker;
import javafx.scene.Scene;
import org.cryptomator.common.vaults.Vault;
import org.cryptomator.common.vaults.VaultState;
import org.cryptomator.integrations.mount.UnmountFailedException;
import org.cryptomator.ui.fxapp.FxApplicationWindows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javafx.stage.Stage;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import dagger.Lazy;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class LockWorkflowTest {

    private Faker faker;

    @Mock
    private Vault mockVault;
    @Mock
    private VaultState mockVaultState;
    @Mock
    private Stage mockLockWindow;
    @Mock
    private FxApplicationWindows mockAppWindows;
    @Mock
    private Lazy<Scene> mockLockFailedScene;
    @Mock
    private Lazy<Scene> mockLockForcedScene;
    private AtomicReference<CompletableFuture<Boolean>> forceRetryDecision;

    @BeforeEach
    void setUp() {
        // Arrange - Initialiser les mocks
        MockitoAnnotations.openMocks(this);
        faker = new Faker();
        forceRetryDecision = new AtomicReference<>(new CompletableFuture<>());

        // Arrange - Configurer le Vault mocké
        when(mockVault.getDisplayName()).thenReturn(faker.file().fileName());
        when(mockVault.stateProperty()).thenReturn(mockVaultState);

        // Arrange - Configurer les scènes mockées
        when(mockLockFailedScene.get()).thenReturn(mock(Scene.class));
        when(mockLockForcedScene.get()).thenReturn(mock(Scene.class));
    }

    @Test
    void testLockWithoutForce() {
        // Arrange - Créer une instance de LockWorkflow
        LockWorkflow lockWorkflow = new LockWorkflow(
                mockLockWindow,
                mockVault,
                forceRetryDecision,
                mockLockForcedScene,
                mockLockFailedScene,
                mockAppWindows
        );

        // Act - Appeler la méthode succeeded()
        lockWorkflow.succeeded();

        // Assert - Vérifier la transition d'état du Vault et l'absence de fenêtre d'erreur
        verify(mockVaultState).transition(VaultState.Value.PROCESSING, VaultState.Value.LOCKED);
        verify(mockAppWindows, never()).showErrorWindow(any(), any(), any());
    }

    @Test
    void testLockFailureWithGenericException() {
        // Arrange - Créer une exception générique de test
        Exception testException = new RuntimeException(faker.lorem().sentence());

        // Arrange - Créer un spy de LockWorkflow
        LockWorkflow lockWorkflow = spy(new LockWorkflow(
                mockLockWindow,
                mockVault,
                forceRetryDecision,
                mockLockForcedScene,
                mockLockFailedScene,
                mockAppWindows
        ));

        // Arrange - Mock de la méthode getException() pour retourner l'exception de test
        doReturn(testException).when(lockWorkflow).getException();

        // Act - Appeler la méthode failed()
        lockWorkflow.failed();

        // Assert - Vérifier la transition d'état du Vault et l'affichage de la fenêtre d'erreur
        verify(mockVaultState).transition(VaultState.Value.PROCESSING, VaultState.Value.UNLOCKED);
        verify(mockAppWindows).showErrorWindow(eq(testException), eq(mockLockWindow), isNull());
    }

    @Test
    void testLockFailureWithUnmountFailedException() {
        // Arrange - Créer une exception de type UnmountFailedException
        UnmountFailedException testException = new UnmountFailedException(faker.lorem().sentence());

        // Arrange - Créer un spy de LockWorkflow
        LockWorkflow lockWorkflow = spy(new LockWorkflow(
                mockLockWindow,
                mockVault,
                forceRetryDecision,
                mockLockForcedScene,
                mockLockFailedScene,
                mockAppWindows
        ));

        // Arrange - Mock de la méthode getException() pour retourner l'exception de test
        doReturn(testException).when(lockWorkflow).getException();

        // Act - Appeler la méthode failed()
        lockWorkflow.failed();

        // Assert - Vérifier la transition d'état du Vault, la configuration et l'affichage de la scène appropriée
        verify(mockVaultState).transition(VaultState.Value.PROCESSING, VaultState.Value.UNLOCKED);
        verify(mockLockWindow).setScene(any());
        verify(mockLockWindow).show();
        verifyNoInteractions(mockAppWindows);
    }
}
