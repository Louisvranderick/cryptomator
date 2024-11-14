## Exécution des Tests avec Différents Flags JVM

Ce workflow exécute les tests avec plusieurs configurations de flags JVM, intégrées dans le fichier [test.yml](https://github.com/Louisvranderick/cryptomator/blob/develop/.github/workflows/test.yml) , pour évaluer leur impact sur les performances et la couverture de code.

### Flags Utilisés et Justification

- `-XX:+OptimizeStringConcat` : Active une concaténation de chaînes optimisée pour des performances accrues.
- `-XX:+AlwaysPreTouch` : Alloue la mémoire physique en avance pour réduire la latence.
- `-XX:+TieredCompilation` : Active une compilation en plusieurs niveaux, optimisant l’équilibre entre le temps de compilation et les performances d’exécution.
- `-XX:+PrintGCDetails` : Affiche des informations détaillées sur le Garbage Collector (GC), utiles pour l’analyse de la gestion mémoire.
- `-XX:MaxInlineLevel=15` : Augmente le niveau d'inlining, améliorant la rapidité d'exécution.

### Résultats et Processus

Les résultats de chaque test et les rapports de couverture de code sont maintenant générés et analysés dans le fichier [test.yml](https://github.com/Louisvranderick/cryptomator/blob/develop/.github/workflows/test.yml). L'exécution des tests avec chaque flag est automatisée grâce à une matrice de flags, et les rapports de couverture sont calculés avec JaCoCo.

Les étapes principales incluent :
1. **Compilation et Tests** : Chaque configuration de flag est utilisée pour exécuter le build et les tests en utilisant `xvfb-run mvn -B verify -Djavafx.platform=linux jacoco:report -Pcoverage`.
2. **Analyse de la Couverture** : Un script Python extrait les données de couverture, et une validation est effectuée pour vérifier que la couverture s'est améliorée (seuil par défaut : 13,43%).
3. **Continuité en cas d'erreur** : Le workflow continue même en cas d'échec de test pour garantir l’obtention des logs.
