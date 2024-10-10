# Documentation pour les 10 tests

## IFT3913
Louis Vranderick 

## Description des Tests de ReportWriter

### 1. `testWriteReportWithVariousPaths`
Cette méthode de test se concentre sur la vérification du comportement du `ReportWriter` dans différentes conditions de chemin de fichier, incluant des scénarios valides et invalides. L'objectif de ce test est de s'assurer que le `ReportWriter` peut gérer correctement différents chemins de fichier, et que des erreurs appropriées sont levées lorsque des problèmes surviennent.

- **Oracle de Test** : Le résultat attendu est que le `ReportWriter` réussisse à écrire le rapport ou lève des exceptions appropriées lorsqu'il tente d'écrire sur des chemins problématiques. L'oracle est utilisé pour valider si le comportement réel correspond à cette attente.

- **Modèle Arrange-Act-Assert (AAA)** :
    - **Arrange** : Configurer l'environnement nécessaire, y compris la génération de différents chemins de fichier (par exemple, valides, relatifs, en lecture seule, longs, et invalides). Utiliser `Faker` pour générer des données et créer des répertoires temporaires pour les fichiers de test.
    - **Act** : Tenter d'écrire des rapports sur les chemins spécifiés. S'assurer que les répertoires nécessaires sont créés lorsque cela est requis.
    - **Assert** : Vérifier que les fichiers sont correctement créés pour les chemins valides, et que des exceptions appropriées sont levées pour les chemins invalides (par exemple, erreurs de permission, erreurs de chemin invalide).

- **Scénarios Couverts** :
    - Écriture sur un **chemin de fichier normal** pour assurer la réussite des opérations d'écriture.
    - Écriture sur un **chemin relatif** pour vérifier la gestion des traversées de répertoire.
    - Écriture sur un **répertoire inexistant**, nécessitant la création à la volée des répertoires parents.
    - Écriture sur un **répertoire en lecture seule**, testant la capacité de l'application à reconnaître et gérer les erreurs de permission.
    - Écriture sur un **chemin très long**, proche des limites du système, pour tester la robustesse de la logique de gestion des chemins.
    - Écriture sur un **chemin invalide** contenant des caractères interdits, pour assurer une gestion appropriée des erreurs.

### 2. `testSimultaneousWrites`
Cette méthode de test évalue le comportement du `ReportWriter` lors d'une utilisation concurrente. Elle simule plusieurs threads écrivant des rapports simultanément pour vérifier que la classe gère les opérations concurrentes sans erreurs telles que la corruption des données ou les conditions de course.

- **Oracle de Test** : Le résultat attendu est que chaque thread soit capable de créer et d'écrire son propre fichier sans provoquer de corruption des données ou d'erreurs. L'oracle est utilisé pour valider que chaque opération d'écriture est indépendante et exécutée correctement.

- **Modèle Arrange-Act-Assert (AAA)** :
    - **Arrange** : Configurer l'environnement nécessaire, y compris la création d'un répertoire temporaire pour les rapports. Utiliser `Faker` pour générer un contenu de rapport fictif.
    - **Act** : Lancer plusieurs threads, chacun tentant d'écrire son propre rapport simultanément.
    - **Assert** : Vérifier que chaque thread crée et écrit correctement son propre fichier, en s'assurant que tous les fichiers attendus sont présents et contiennent le bon contenu.

- **Scénarios Couverts** :
    - **Écritures Simultanées** : Plusieurs threads tentent d'écrire des rapports en parallèle dans différents fichiers au sein d'un répertoire temporaire.
    - **Sécurité des Threads** : Ce scénario teste si le `ReportWriter` peut gérer les opérations d'écriture de fichiers concurrentes sans problème.

## Description des Tests de Result

### 3. `testNonFixableState`
Ce test vérifie le comportement de `Result` lorsqu'un diagnostic ne peut pas être corrigé.

- **Oracle de Test** : Le résultat attendu est que l'état de `Result` soit défini sur `NOT_FIXABLE` lorsqu'aucune correction n'est disponible. L'oracle est utilisé pour valider que l'état est correctement défini en fonction des informations de diagnostic.

- **Modèle Arrange-Act-Assert (AAA)** :
    - **Arrange** : Configurer un diagnostic non corrigeable avec différents chemins de coffre générés aléatoirement.
    - **Act** : Créer des instances de `Result` en utilisant ces diagnostics.
    - **Assert** : Vérifier que l'état est bien `NOT_FIXABLE` pour chaque instance de `Result`.

- **Scénarios Couverts** :
    - Création de `Result` avec des chemins de fichiers aléatoires et vérification de l'état non corrigeable.

### 4. `testFixableStateAndStateChanges`
Ce test vérifie le comportement de `Result` lorsqu'un diagnostic est corrigeable, et teste également les transitions entre différents états.

- **Oracle de Test** : Le résultat attendu est que l'état initial de `Result` soit `FIXABLE` lorsque la correction est disponible, et que les changements d'état ultérieurs soient correctement pris en compte. L'oracle est utilisé pour valider que chaque transition d'état fonctionne comme prévu.

- **Modèle Arrange-Act-Assert (AAA)** :
    - **Arrange** : Configurer un diagnostic corrigeable avec des chemins de coffre générés aléatoirement et une correction simulée.
    - **Act** : Créer des instances de `Result` et changer leur état à travers toutes les valeurs possibles.
    - **Assert** : Vérifier que l'état initial est `FIXABLE` et que chaque transition d'état est correctement appliquée.

- **Scénarios Couverts** :
    - Création de `Result` avec des chemins de fichiers aléatoires et vérification de l'état corrigeable.
    - Changement de l'état de `Result` et vérification que chaque nouvelle valeur est correctement définie.

### 5. `testExtremeCasesAndInvalidPaths`
Ce test couvre les cas extrêmes, y compris des chemins de fichiers très longs et des chemins invalides, pour vérifier la robustesse de la création de `Result`.

- **Oracle de Test** : Le résultat attendu est que `Result` soit correctement créé pour des chemins très longs et que des états appropriés soient définis pour les chemins invalides. L'oracle est utilisé pour valider la gestion correcte de ces cas.

- **Modèle Arrange-Act-Assert (AAA)** :
    - **Arrange** : Générer des chemins très longs et des chemins invalides (par exemple, `null`). Utiliser `Faker` pour créer des noms de fichiers de longueur extrême.
    - **Act** : Créer des instances de `Result` avec ces chemins extrêmes et vérifier leur état.
    - **Assert** : Vérifier que `Result` est créé correctement pour les chemins très longs et que l'état est `NOT_FIXABLE` pour les chemins invalides.

- **Scénarios Couverts** :
    - Création de `Result` avec des noms de fichiers très longs pour tester la robustesse.
    - Utilisation d'un chemin invalide (`null`) pour vérifier la gestion appropriée des erreurs.

## Description des Tests de Check

### 6. `testExecuteBatchAndSetState`
Ce test vérifie l'exécution d'un lot de vérifications ainsi que la mise à jour de l'état de `Check`.

- **Oracle de Test** : Le résultat attendu est que l'état de `Check` soit mis à jour en `SCHEDULED` après l'exécution du lot, puis en `RUNNING` lors de la mise à jour manuelle. L'oracle est utilisé pour valider que chaque transition d'état se produit correctement.

- **Modèle Arrange-Act-Assert (AAA)** :
    - **Arrange** : Configurer un `CheckExecutor` avec des dépendances simulées et préparer un objet `Check` simulé.
    - **Act** : Exécuter le lot de vérifications et mettre à jour l'état de `Check` manuellement.
    - **Assert** : Vérifier que l'état de `Check` passe à `SCHEDULED` puis à `RUNNING` selon les attentes.

- **Scénarios Couverts** :
    - Exécution d'un lot de vérifications avec un `Check` simulé et vérification des transitions d'état.

### 7. `testSetErrorAndProperties`
Ce test vérifie le comportement de `Check` lorsqu'une erreur est définie et que certaines propriétés sont modifiées.

- **Oracle de Test** : Le résultat attendu est que l'erreur définie soit correctement reflétée par la propriété `error`, et que les autres propriétés (`chosenForExecution`, `name`) soient définies correctement. L'oracle est utilisé pour valider la précision des propriétés après leur modification.

- **Modèle Arrange-Act-Assert (AAA)** :
    - **Arrange** : Configurer une instance de `Check` avec des dépendances simulées, y compris une erreur fictive et un `HealthCheck` fictif.
    - **Act** : Définir l'erreur et les propriétés (`chosenForExecution`, `name`).
    - **Assert** : Vérifier que l'erreur, l'état `chosenForExecution`, et le nom sont correctement définis.

- **Scénarios Couverts** :
    - Définition d'une erreur sur une instance de `Check` et vérification que la propriété `error` reflète correctement l'erreur définie.
    - Modification de la propriété `chosenForExecution` et vérification de son état.
    - Vérification du nom d'un `Check` basé sur un objet `HealthCheck` simulé.


## Améliorations Futures
- **Système de Fichiers Simulé** : Envisagez d'utiliser une bibliothèque comme Jimfs (système de fichiers en mémoire de Google) pour simplifier le test des différentes opérations de fichiers sans dépendre du système de fichiers local.
- **Tests de Concurrence Plus Importants** : Étendre le test de concurrence pour inclure plus de threads ou des scénarios de stress-test pour valider les performances sous une charge élevée.

