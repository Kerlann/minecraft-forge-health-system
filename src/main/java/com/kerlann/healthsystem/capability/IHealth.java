package com.kerlann.healthsystem.capability;

/**
 * Interface définissant la capacité de santé personnalisée
 */
public interface IHealth {
    /**
     * Obtient la santé maximale personnalisée
     * @return La valeur de santé maximale
     */
    float getMaxHealth();

    /**
     * Définit la santé maximale personnalisée
     * @param maxHealth La nouvelle valeur de santé maximale
     */
    void setMaxHealth(float maxHealth);

    /**
     * Obtient le taux de régénération de santé
     * @return Le taux de régénération actuel
     */
    float getHealthRegenRate();

    /**
     * Définit le taux de régénération de santé
     * @param rate Le nouveau taux de régénération
     */
    void setHealthRegenRate(float rate);

    /**
     * Obtient le niveau de saignement (0-5)
     * 0 = Pas de saignement
     * 1 = Saignement léger
     * 2 = Saignement modéré
     * 3 = Saignement important
     * 4 = Saignement sévère
     * 5 = Saignement critique
     * @return Le niveau de saignement actuel
     */
    int getBleedingLevel();

    /**
     * Définit le niveau de saignement
     * @param level Le nouveau niveau de saignement (entre 0 et 5)
     */
    void setBleedingLevel(int level);

    /**
     * Obtient le niveau de fracture osseuse (0-3)
     * 0 = Pas de fracture
     * 1 = Fracture légère
     * 2 = Fracture modérée
     * 3 = Fracture grave
     * @return Le niveau de fracture actuel
     */
    int getBoneBrokenLevel();

    /**
     * Définit le niveau de fracture osseuse
     * @param level Le nouveau niveau de fracture (entre 0 et 3)
     */
    void setBoneBrokenLevel(int level);

    /**
     * Obtient le niveau d'empoisonnement (0-3)
     * 0 = Pas d'empoisonnement
     * 1 = Empoisonnement léger
     * 2 = Empoisonnement modéré
     * 3 = Empoisonnement grave
     * @return Le niveau d'empoisonnement actuel
     */
    int getPoisonLevel();

    /**
     * Définit le niveau d'empoisonnement
     * @param level Le nouveau niveau d'empoisonnement (entre 0 et 3)
     */
    void setPoisonLevel(int level);

    /**
     * Vérifie si le joueur est dans un état critique
     * @return true si le joueur est dans un état critique, false sinon
     */
    boolean isCriticalCondition();

    /**
     * Définit si le joueur est dans un état critique
     * @param critical L'état critique
     */
    void setCriticalCondition(boolean critical);
}