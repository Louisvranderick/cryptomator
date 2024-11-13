# Exécution des Tests avec Différents Flags JVM

Ce workflow exécute les tests avec cinq configurations de flags pour évaluer leur impact.

## Flags Utilisés et Justification
- `-XX:+UseG1GC` : Sélectionne le Garbage Collector G1, optimisé pour les applications avec des pauses prévisibles.
- `-XX:+AlwaysPreTouch` : Alloue la mémoire physique en avance pour optimiser les performances.
- `-XX:+TieredCompilation` : Active la compilation en plusieurs niveaux pour un meilleur équilibrage des performances.
- `-XX:+PrintGCDetails` : Donne des informations détaillées sur le GC, utiles pour analyser la gestion mémoire.
- `-XX:MaxInlineLevel=15` : Augmente le niveau d'inlining pour améliorer les performances.

## Résultats
Les résultats de chaque test et les rapports de couverture sont sauvegardés dans `test-results` et `coverage-report`.
