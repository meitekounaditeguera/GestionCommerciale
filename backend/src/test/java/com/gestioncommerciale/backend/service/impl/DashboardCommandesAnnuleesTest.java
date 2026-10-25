package com.gestioncommerciale.backend.service.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.gestioncommerciale.backend.dto.CaMensuelDTO;
import com.gestioncommerciale.backend.dto.CommandeDTO;
import com.gestioncommerciale.backend.dto.LigneCommandeDTO;
import com.gestioncommerciale.backend.model.Client;
import com.gestioncommerciale.backend.model.Commande;
import com.gestioncommerciale.backend.model.Produit;
import com.gestioncommerciale.backend.repository.ClientRepository;
import com.gestioncommerciale.backend.repository.CommandeRepository;
import com.gestioncommerciale.backend.repository.ProduitRepository;
import com.gestioncommerciale.backend.service.CommandeService;

// Test d'intégration (H2) : passe par les vrais services et les vraies requêtes JPQL/SQL, pour
// vérifier qu'une commande annulée n'est comptée ni dans le nombre de commandes, ni dans le
// chiffre d'affaires, ni dans la vélocité de vente. @Transactional : chaque test est annulé en fin d'exécution.
@SpringBootTest
@Transactional
class DashboardCommandesAnnuleesTest {

    @Autowired
    private CommandeService commandeService;

    @Autowired
    private DashboardServiceImpl dashboardService;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ProduitRepository produitRepository;

    @Autowired
    private CommandeRepository commandeRepository;

    private Client client;
    private Produit produit;

    @BeforeEach
    void setUp() {
        client = new Client();
        client.setNom("Kouassi");
        client.setPrenom("Awa");
        client.setEmail("awa.kouassi@example.com");
        client = clientRepository.save(client);

        produit = new Produit();
        produit.setNom("Riz 5 kg");
        produit.setPrix(new BigDecimal("1000"));
        produit.setQuantite(100);
        produit.setCategorie("Alimentation");
        produit = produitRepository.save(produit);
    }

    @Test
    void uneCommandeAnnuleeNeComptePasDansLeChiffreAffaires() {
        commanderPuisRetournerId(2);
        Long idAnnulee = commanderPuisRetournerId(3);
        commandeService.annulerCommande(idAnnulee);

        // Commande antérieure à l'introduction du statut (colonne vide) : comptée comme VALIDE.
        Commande sansStatut = commandeRepository.findById(commanderPuisRetournerId(1)).orElseThrow();
        sansStatut.setStatut(null);
        commandeRepository.saveAndFlush(sansStatut);

        // 2 + 1 unités à 1 000 FCFA ; les 3 unités de la commande annulée sont ignorées.
        BigDecimal attendu = new BigDecimal("3000");

        assertThat(dashboardService.getStats().getChiffreAffaires()).isEqualByComparingTo(attendu);
        assertThat(dashboardService.getChiffreAffaires().getJournalier()).isEqualByComparingTo(attendu);
        assertThat(dashboardService.getChiffreAffaires().getAnnuel()).isEqualByComparingTo(attendu);

        List<CaMensuelDTO> caMensuel = dashboardService.getCaMensuel();
        assertThat(caMensuel.get(caMensuel.size() - 1).getChiffreAffaires()).isEqualByComparingTo(attendu);

        assertThat(dashboardService.getMeilleurClient().getTotalDepense()).isEqualByComparingTo(attendu);
        assertThat(dashboardService.getTopProduits().get(0).getQuantiteVendue()).isEqualTo(3L);
        assertThat(dashboardService.getTopProduits().get(0).getChiffreAffaires()).isEqualByComparingTo(attendu);
        assertThat(dashboardService.getVentesParCategorie().get(0).getTotalVentes()).isEqualByComparingTo(attendu);
        assertThat(dashboardService.getCategoriePopulaire().getQuantiteVendue()).isEqualTo(3L);

        // Le compteur ne retient que les commandes valides : celle de 2 unités et celle sans
        // statut, pas la commande annulée.
        assertThat(dashboardService.getStats().getTotalCommandes()).isEqualTo(2L);
    }

    @Test
    void uneVenteAnnuleeNeChangePasLaVelocite() {
        commanderPuisRetournerId(2);
        double velociteAvant = dashboardService.calculerVelociteVente(produit.getId());

        Long idAnnulee = commanderPuisRetournerId(5);
        commandeService.annulerCommande(idAnnulee);

        assertThat(velociteAvant).isGreaterThan(0.0);
        assertThat(dashboardService.calculerVelociteVente(produit.getId())).isEqualTo(velociteAvant);
    }

    private Long commanderPuisRetournerId(int quantite) {
        LigneCommandeDTO ligne = new LigneCommandeDTO();
        ligne.setProduitId(produit.getId());
        ligne.setQuantite(quantite);

        CommandeDTO commande = new CommandeDTO();
        commande.setClientId(client.getId());
        commande.setDateCommande(LocalDate.now());
        commande.setLignes(List.of(ligne));

        return commandeService.saveCommande(commande).getId();
    }
}
