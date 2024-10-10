## Documentation des méthodes de test 

### IFT3913 Tâche 2 

#### Louis Vranderick 


1. **Test : shouldSetCheckStateToScheduledWhenBatchExecuted**
  - **Oracle** : Ce test vérifie que l'état des contrôles est correctement mis à "SCHEDULED" lorsque l'exécution d'un lot de contrôles est déclenchée.
  - **Arrange** : Crée deux contrôles fictifs (à l'aide de mocks) ainsi que des contrôles de santé associés. Configure le comportement attendu des mocks.
  - **Act** : Exécute le lot en utilisant les contrôles créés.
  - **Assert** : Vérifie que l'état de chaque contrôle est bien mis à "SCHEDULED" après l'exécution.

2. **Test : shouldTransitionVaultStateOnLockSuccess**
  - **Oracle** : Ce test s'assure que l'état du coffre-fort (« Vault ») passe correctement à "LOCKED" après un verrouillage réussi.
  - **Arrange** : Crée des états de coffre-fort et de verrouillage fictifs (à l'aide de mocks) et configure le comportement attendus.
  - **Act** : Appelle la méthode `lockSucceeded()` pour simuler un verrouillage réussi.
  - **Assert** : Vérifie que l'état du coffre-fort passe à "LOCKED" et s'assure qu'aucune fenêtre d'erreur n'est affichée.

3. **Test : testLockFailureWithGenericException**
  - **Oracle** : Ce test vérifie la bonne gestion d'une exception générique pendant un échec de verrouillage, en particulier que les états et les fenêtres sont correctement gérés.
  - **Arrange** : Crée une exception de type `RuntimeException` à l'aide de la bibliothèque Faker, puis crée un espion (« spy ») sur l'objet `LockWorkflow` pour renvoyer cette exception.
  - **Act** : Appelle la méthode `failed()` sur l'objet `LockWorkflow` pour simuler un échec de verrouillage.
  - **Assert** : Vérifie les transitions d'état et l'affichage de la fenêtre d'erreur.

4. **Test : testLockFailureWithUnmountFailedException**
  - **Oracle** : Ce test vérifie le comportement du système lorsqu'une exception de type `UnmountFailedException` se produit pendant un échec de verrouillage.
  - **Arrange** : Crée une exception de type `UnmountFailedException` à l'aide de la bibliothèque Faker et configure un espion sur `LockWorkflow` pour la renvoyer.
  - **Act** : Appelle la méthode `failed()` pour simuler un échec de verrouillage.
  - **Assert** : Vérifie que l'état du coffre-fort revient à "UNLOCKED", que la scène de verrouillage forcé est affichée, et qu'aucune interaction avec la fenêtre d'application n'a lieu.

5. **Test : shouldRetryLockWhenForceRetryDecisionIsMade**
  - **Oracle** : Ce test vérifie que le système tente de verrouiller à nouveau lorsque la décision de forcer le verrouillage est prise.
  - **Arrange** : Crée un objet `LockWorkflow` avec un décisionnaire de forçage de retry (« force retry decision »). Crée un espion (« spy ») sur l'objet `LockWorkflow`.
  - **Act** : Appelle la méthode `lock()` sur l'objet `LockWorkflow` pour déclencher un verrouillage.
  - **Assert** : Vérifie que la méthode `retry()` est appelée lorsque la décision de forcer le retry est prise.

6. **Test : shouldNotRetryLockWhenNoForceRetryDecision**
  - **Oracle** : Ce test vérifie que le système ne tente pas de verrouiller à nouveau si la décision de forcer le retry n'est pas prise.
  - **Arrange** : Crée un objet `LockWorkflow` avec une décision de retry non forcée. Crée un espion (« spy ») sur l'objet `LockWorkflow`.
  - **Act** : Appelle la méthode `lock()` sur l'objet `LockWorkflow`.
  - **Assert** : Vérifie que la méthode `retry()` n'est pas appelée.

7. **Test : shouldShowErrorWindowOnLockFailure**
  - **Oracle** : Ce test s'assure que la fenêtre d'erreur est affichée lorsque le verrouillage échoue.
  - **Arrange** : Crée un objet `LockWorkflow` avec un état fictif de coffre-fort et une décision de retry forcée. Crée un espion sur `LockWorkflow`.
  - **Act** : Appelle la méthode `failed()` pour simuler un échec de verrouillage.
  - **Assert** : Vérifie que la fenêtre d'erreur est affichée à l'utilisateur.

8. **Test : shouldUnmountSuccessfullyAfterLock**
  - **Oracle** : Ce test vérifie que le démontage (« unmount ») est réalisé avec succès après un verrouillage.
  - **Arrange** : Crée un espion sur `LockWorkflow` pour simuler une tentative de démontage réussie.
  - **Act** : Appelle la méthode `lock()` puis `unmount()` sur l'objet `LockWorkflow`.
  - **Assert** : Vérifie que la méthode `unmount()` est appelée avec succès après le verrouillage.

9. **Test : shouldNotUnmountOnFailedLock**
  - **Oracle** : Ce test s'assure que le démontage n'est pas tenté lorsque le verrouillage échoue.
  - **Arrange** : Crée une exception et un espion sur l'objet `LockWorkflow`.
  - **Act** : Appelle la méthode `failed()` sur l'objet `LockWorkflow`.
  - **Assert** : Vérifie que la méthode `unmount()` n'est pas appelée.

10. **Test : shouldSetErrorStateOnUnhandledException**
  - **Oracle** : Ce test vérifie que l'état d'erreur est mis en place lorsqu'une exception non gérée se produit.
  - **Arrange** : Crée une exception non gérée et un espion sur l'objet `LockWorkflow`.
  - **Act** : Appelle une méthode provoquant l'exception non gérée.
  - **Assert** : Vérifie que l'état de l'objet est mis à "ERROR".

11. **Test : shouldHandleUnmountRetryAfterFailure**
  - **Oracle** : Ce test vérifie que le système gère correctement une nouvelle tentative de démontage après un échec précédent.
  - **Arrange** : Crée une exception simulant un échec de démontage initial et configure un espion sur `LockWorkflow`.
  - **Act** : Appelle la méthode `unmount()` pour simuler un nouvel essai après l'échec.
  - **Assert** : Vérifie que la méthode `retryUnmount()` est correctement appelée.