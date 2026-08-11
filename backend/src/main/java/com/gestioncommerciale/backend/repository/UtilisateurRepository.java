package com.gestioncommerciale.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.gestioncommerciale.backend.model.Utilisateur;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    Optional<Utilisateur> findByUsername(String username);

    // Suppression en SQL natif (sans charger l'entité) : utilisée pour retirer les anciens
    // comptes de démo dont le rôle stocké en base ne correspond plus à une valeur de l'enum Role
    // (ex: "MANAGER"/"USER" après le renommage vers GESTIONNAIRE/CAISSIER), ce qui empêcherait
    // toute désérialisation JPA classique de ces lignes.
    @Modifying
    @Query(value = "DELETE FROM utilisateurs WHERE username IN (:usernames)", nativeQuery = true)
    void supprimerParUsernames(@Param("usernames") List<String> usernames);

    // Le ddl-auto=update de Hibernate avait généré une contrainte CHECK sur la colonne "role"
    // listant les valeurs de l'enum Role au moment de la création de la table (ADMIN/MANAGER/USER).
    // Cette contrainte n'est jamais mise à jour automatiquement lors d'un renommage de l'enum :
    // il faut la retirer explicitement, sous peine de rejeter les nouvelles valeurs.
    @Modifying
    @Query(value = "ALTER TABLE utilisateurs DROP CONSTRAINT IF EXISTS utilisateurs_role_check", nativeQuery = true)
    void supprimerContrainteRoleObsolete();

}
