package com.gestioncommerciale.backend.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.gestioncommerciale.backend.model.Commande;

@Repository
public interface CommandeRepository extends JpaRepository<Commande, Long> {

    @Query("SELECT COALESCE(SUM(c.montantTotal), 0) FROM Commande c")
    BigDecimal sumMontantTotal();

    // Retourne, pour chaque mois depuis la date donnée, le libellé "YYYY-MM" et le total du CA.
    @Query(value = "SELECT TO_CHAR(c.date_commande, 'YYYY-MM') AS mois, SUM(c.montant_total) AS total "
            + "FROM commandes c WHERE c.date_commande >= :depuis "
            + "GROUP BY TO_CHAR(c.date_commande, 'YYYY-MM') "
            + "ORDER BY mois", nativeQuery = true)
    List<Object[]> sumMontantParMoisDepuis(@Param("depuis") LocalDate depuis);

    // Chiffre d'affaires cumulé depuis la date donnée (incluse) : utilisé pour les totaux
    // journalier/hebdomadaire/mensuel/annuel en faisant varier la date de départ.
    @Query("SELECT COALESCE(SUM(c.montantTotal), 0) FROM Commande c WHERE c.dateCommande >= :depuis")
    BigDecimal sumMontantTotalDepuis(@Param("depuis") LocalDate depuis);

    // Classement des clients par chiffre d'affaires cumulé généré (limité via le Pageable fourni
    // par l'appelant) : id, nom, prénom, total dépensé.
    @Query("SELECT c.client.id, c.client.nom, c.client.prenom, SUM(c.montantTotal) AS total "
            + "FROM Commande c "
            + "GROUP BY c.client.id, c.client.nom, c.client.prenom "
            + "ORDER BY total DESC")
    List<Object[]> findMeilleursClients(Pageable pageable);

}