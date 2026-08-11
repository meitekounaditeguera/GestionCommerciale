package com.gestioncommerciale.backend.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gestioncommerciale.backend.dto.CommandeFournisseurDTO;
import com.gestioncommerciale.backend.dto.LigneCommandeFournisseurDTO;
import com.gestioncommerciale.backend.exception.CommandeFournisseurNotFoundException;
import com.gestioncommerciale.backend.exception.FournisseurNotFoundException;
import com.gestioncommerciale.backend.exception.ProduitNotFoundException;
import com.gestioncommerciale.backend.exception.StatutCommandeFournisseurInvalideException;
import com.gestioncommerciale.backend.mapper.CommandeFournisseurMapper;
import com.gestioncommerciale.backend.model.CommandeFournisseur;
import com.gestioncommerciale.backend.model.Fournisseur;
import com.gestioncommerciale.backend.model.LigneCommandeFournisseur;
import com.gestioncommerciale.backend.model.Produit;
import com.gestioncommerciale.backend.model.StatutCommandeFournisseur;
import com.gestioncommerciale.backend.repository.CommandeFournisseurRepository;
import com.gestioncommerciale.backend.repository.FournisseurRepository;
import com.gestioncommerciale.backend.repository.ProduitRepository;
import com.gestioncommerciale.backend.service.CommandeFournisseurService;
import com.gestioncommerciale.backend.service.MouvementStockService;

@Service
public class CommandeFournisseurServiceImpl implements CommandeFournisseurService {

    private final CommandeFournisseurRepository commandeFournisseurRepository;
    private final FournisseurRepository fournisseurRepository;
    private final ProduitRepository produitRepository;
    private final MouvementStockService mouvementStockService;

    public CommandeFournisseurServiceImpl(
            CommandeFournisseurRepository commandeFournisseurRepository,
            FournisseurRepository fournisseurRepository,
            ProduitRepository produitRepository,
            MouvementStockService mouvementStockService) {

        this.commandeFournisseurRepository = commandeFournisseurRepository;
        this.fournisseurRepository = fournisseurRepository;
        this.produitRepository = produitRepository;
        this.mouvementStockService = mouvementStockService;
    }

    @Override
    public Page<CommandeFournisseurDTO> getAllCommandesFournisseurs(Pageable pageable) {
        return commandeFournisseurRepository.findAll(pageable)
                .map(CommandeFournisseurMapper::toDTO);
    }

    @Override
    public CommandeFournisseurDTO getCommandeFournisseurById(Long id) {
        return commandeFournisseurRepository.findById(id)
                .map(CommandeFournisseurMapper::toDTO)
                .orElseThrow(() -> new CommandeFournisseurNotFoundException(
                        "Commande fournisseur introuvable avec l'id : " + id));
    }

    @Override
    @Transactional
    public CommandeFournisseurDTO creerCommandeFournisseur(CommandeFournisseurDTO commandeFournisseurDTO) {

        Fournisseur fournisseur = fournisseurRepository.findById(commandeFournisseurDTO.getFournisseurId())
                .orElseThrow(() -> new FournisseurNotFoundException("Fournisseur introuvable"));

        CommandeFournisseur commande = new CommandeFournisseur();

        commande.setReference(genererReference());
        commande.setFournisseur(fournisseur);
        commande.setDateCommande(commandeFournisseurDTO.getDateCommande());
        commande.setStatut(StatutCommandeFournisseur.BROUILLON);
        commande.setMontantTotal(BigDecimal.ZERO);

        BigDecimal montantTotal = BigDecimal.ZERO;
        List<LigneCommandeFournisseur> lignes = new ArrayList<>();

        for (LigneCommandeFournisseurDTO ligneDTO : commandeFournisseurDTO.getLignes()) {

            Produit produit = produitRepository.findById(ligneDTO.getProduitId())
                    .orElseThrow(() -> new ProduitNotFoundException("Produit introuvable"));

            LigneCommandeFournisseur ligne = new LigneCommandeFournisseur();
            ligne.setCommandeFournisseur(commande);
            ligne.setProduit(produit);
            ligne.setQuantite(ligneDTO.getQuantite());
            ligne.setPrixAchatUnitaire(ligneDTO.getPrixAchatUnitaire());

            lignes.add(ligne);

            BigDecimal sousTotal = ligneDTO.getPrixAchatUnitaire().multiply(BigDecimal.valueOf(ligneDTO.getQuantite()));
            montantTotal = montantTotal.add(sousTotal);
        }

        commande.setLignes(lignes);
        commande.setMontantTotal(montantTotal);

        CommandeFournisseur commandeSauvegardee = commandeFournisseurRepository.save(commande);

        return CommandeFournisseurMapper.toDTO(commandeSauvegardee);
    }

    @Override
    @Transactional
    public CommandeFournisseurDTO validerCommandeFournisseur(Long id) {

        CommandeFournisseur commande = trouverParId(id);

        if (commande.getStatut() != StatutCommandeFournisseur.BROUILLON) {
            throw new StatutCommandeFournisseurInvalideException(
                    "Seule une commande en brouillon peut être validée.");
        }

        commande.setStatut(StatutCommandeFournisseur.VALIDEE);

        return CommandeFournisseurMapper.toDTO(commandeFournisseurRepository.save(commande));
    }

    @Override
    @Transactional
    public CommandeFournisseurDTO annulerCommandeFournisseur(Long id) {

        CommandeFournisseur commande = trouverParId(id);

        if (commande.getStatut() == StatutCommandeFournisseur.LIVREE) {
            throw new StatutCommandeFournisseurInvalideException(
                    "Impossible d'annuler une commande déjà livrée.");
        }

        if (commande.getStatut() == StatutCommandeFournisseur.ANNULEE) {
            throw new StatutCommandeFournisseurInvalideException(
                    "Cette commande est déjà annulée.");
        }

        commande.setStatut(StatutCommandeFournisseur.ANNULEE);

        return CommandeFournisseurMapper.toDTO(commandeFournisseurRepository.save(commande));
    }

    @Override
    @Transactional
    public CommandeFournisseurDTO recevoirCommandeFournisseur(Long id) {

        CommandeFournisseur commande = trouverParId(id);

        if (commande.getStatut() == StatutCommandeFournisseur.LIVREE) {
            throw new StatutCommandeFournisseurInvalideException(
                    "Cette commande a déjà été réceptionnée.");
        }

        if (commande.getStatut() == StatutCommandeFournisseur.ANNULEE) {
            throw new StatutCommandeFournisseurInvalideException(
                    "Impossible de réceptionner une commande annulée.");
        }

        // Mise à jour du stock : chaque ligne augmente la quantité du produit
        // et journalise une entrée de stock traçable jusqu'à cette commande.
        for (LigneCommandeFournisseur ligne : commande.getLignes()) {

            Produit produit = ligne.getProduit();
            produit.setQuantite(produit.getQuantite() + ligne.getQuantite());
            produitRepository.save(produit);

            mouvementStockService.enregistrerEntreeAutomatique(
                    produit,
                    ligne.getQuantite(),
                    "Réception commande fournisseur #" + commande.getReference());
        }

        commande.setStatut(StatutCommandeFournisseur.LIVREE);

        CommandeFournisseur commandeLivree = commandeFournisseurRepository.save(commande);

        return CommandeFournisseurMapper.toDTO(commandeLivree);
    }

    private CommandeFournisseur trouverParId(Long id) {
        return commandeFournisseurRepository.findById(id)
                .orElseThrow(() -> new CommandeFournisseurNotFoundException(
                        "Commande fournisseur introuvable avec l'id : " + id));
    }

    // Numérotation séquentielle simple (ex: CF-2026-0001), dans le même esprit que la
    // numérotation des factures de vente.
    private String genererReference() {
        long sequence = commandeFournisseurRepository.count() + 1;
        return String.format("CF-%d-%04d", LocalDate.now().getYear(), sequence);
    }

}
