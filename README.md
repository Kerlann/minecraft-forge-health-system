# Health System Mod

Un mod Minecraft Forge (1.12.2) qui implémente un système de santé avancé avec des mécaniques de régénération, blessures et guérison personnalisées.

## Caractéristiques

### Système de santé étendu
- Saignements (niveaux 1-5) : dommages continus et réduction des soins
- Fractures osseuses (niveaux 1-3) : réduction de la vitesse et difficultés de mouvement
- Empoisonnement (niveaux 1-3) : effets négatifs progressifs
- État critique : augmentation des dégâts reçus lorsque plusieurs conditions graves sont présentes

### Items médicaux
- **Bandage** : Traite les saignements (efficacité modérée)
- **Attelle** : Traite les fractures osseuses (réduit d'un niveau)
- **Antidote** : Neutralise les empoisonnements (efficacité complète)
- **Kit de premiers soins** : Soins complets pour toutes les afflictions et régénère la santé
- **Injection d'adrénaline** : Soins d'urgence rapides et boost temporaire en situation critique

### Interface utilisateur
- Affichage HUD avec icônes indiquant les afflictions actives
- Notification textuelle lors de changements d'état de santé
- Système de messages pour informer le joueur de son état

## Installation

1. Installez Minecraft Forge pour Minecraft 1.12.2
2. Téléchargez la dernière version du mod (.jar) depuis la page des releases
3. Placez le fichier .jar dans le dossier `mods` de votre installation Minecraft
4. Lancez Minecraft avec le profil Forge

## Recettes

### Bandage
```
 S 
PWP
 S 
```
- S = Ficelle
- P = Papier
- W = Laine

### Attelle
```
S S
SSS
S S
```
- S = Bâton

### Antidote
```
 B 
SFS
 G 
```
- B = Bouteille en verre
- S = Œil d'araignée
- F = Œil d'araignée fermenté
- G = Poudre de redstone brillante

### Kit de premiers soins
```
BBB
BAB
BBB
```
- B = Bandage
- A = Pomme dorée

### Injection d'adrénaline
```
 N 
GBG
 R 
```
- N = Verrue du Nether
- G = Poudre de redstone brillante
- B = Bouteille en verre
- R = Poudre de redstone

## Développement

Ce projet utilise Gradle pour la gestion des dépendances et la compilation.

### Prérequis
- Java JDK 8
- Git

### Configuration
1. Clonez le dépôt : `git clone https://github.com/Kerlann/minecraft-forge-health-system.git`
2. Naviguez dans le dossier : `cd minecraft-forge-health-system`
3. Configurez l'environnement de développement :
   - Pour Windows : `gradlew setupDecompWorkspace`
   - Pour Linux/Mac : `./gradlew setupDecompWorkspace`

### Compilation
```bash
# Windows
gradlew build

# Linux/Mac
./gradlew build
```

Le fichier JAR du mod sera généré dans `build/libs/`.

### Pour les développeurs
Si vous souhaitez contribuer au projet, voici les zones principales à explorer :
- `src/main/java/com/kerlann/healthsystem/capability/` - Système de capacités personnalisées
- `src/main/java/com/kerlann/healthsystem/event/` - Gestionnaires d'événements
- `src/main/java/com/kerlann/healthsystem/item/` - Items médicaux

## Licence

Ce projet est sous licence MIT. Voir le fichier LICENSE pour plus de détails.

## Remerciements

- Minecraft Forge pour l'API de modding
- La communauté Minecraft pour le support et les idées