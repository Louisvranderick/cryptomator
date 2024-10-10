package org.cryptomator.ui.lock;

import javafx.scene.Scene;
import javafx.stage.Stage;

import dagger.Lazy;
import org.cryptomator.common.vaults.Vault;
import org.cryptomator.integrations.mount.UnmountFailedException;
import org.cryptomator.ui.common.FxmlFile;
import org.cryptomator.ui.common.FxmlScene;
import org.cryptomator.ui.fxapp.FxApplicationWindows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("LockWorkflow Tests")
class LockWorkflowTest {

    @Mock
    private Stage lockWindow;
    @Mock
    private Vault vault;
    @Mock
    private AtomicReference<CompletableFuture<Boolean>> forceRetryDecision;
    @Mock
    @FxmlScene(FxmlFile.LOCK_FORCED)
    private Lazy<Scene> lockForcedScene;
    @Mock
    @FxmlScene(FxmlFile.LOCK_FAILED)
    private Lazy<Scene> lockFailedScene;
    @Mock
    private FxApplicationWindows appWindows;

    @InjectMocks
    private LockWorkflow lockWorkflow;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /***
     * Test lock without force
     *
     * This test verifies the lock method without force by calling the lock method with force set to false.
     * The outcome of this test is the lock method called with force set to false.
     * The vault should still be locked after the call.
     */
    @Test
    @DisplayName("Test lock without force")
    void testLockWithoutForce() throws Exception {
        doNothing().when(vault).lock(false);
        lockWorkflow.call();
        verify(vault).lock(false);
    }

    /***
     * Test lock failure
     *
     * This test verifies the lock method failure by throwing an IllegalStateException.
     * The outcome of this test is the IllegalStateException thrown by the lock method.
     *
     * @throws Exception
     */
    @Test
    @DisplayName("Test lock failure")
    void testLockFailure() throws Exception {
        doThrow(IllegalStateException.class).when(vault).lock(false);
        when(forceRetryDecision.get()).thenReturn(CompletableFuture.completedFuture(false));

        assertThrows(IllegalStateException.class, () -> lockWorkflow.call());
    }
}
