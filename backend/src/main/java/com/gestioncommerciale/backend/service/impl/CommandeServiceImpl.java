package com.gestioncommerciale.backend.service.impl;

import com.gestioncommerciale.backend.mapper.CommandeMapper;
import com.gestioncommerciale.backend.model.Client;
import com.gestioncommerciale.backend.model.Commande;
import com.gestioncommerciale.backend.model.LigneCommande;
import com.gestioncommerciale.backend.model.Produit;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gestioncommerciale.backend.dto.CommandeDTO;
import com.gestioncommerciale.backend.dto.LigneCommandeDTO;
import com.gestioncommerciale.backend.exception.ClientNotFoundException;
import com.gestioncommerciale.backend.exception.CommandeNotFoundException;
import com.gestioncommerciale.backend.exception.ProduitNotFoundException;
import com.gestioncommerciale.backend.exception.StockInsuffisantException;
import com.gestioncommerciale.backend.repository.ClientRepository;
import com.gestioncommerciale.backend.repository.CommandeRepository;
import com.gestioncommerciale.backend.repository.ProduitRepository;
import com.gestioncommerciale.backend.service.CommandeService;


@Service
public class CommandeServiceImpl implements CommandeService {

    private final CommandeRepository commandeRepository;
    private final ClientRepository clientRepository;
    private final ProduitRepository produitRepository;

    public CommandeServiceImpl(
            CommandeRepository commandeRepository,
            ClientRepository clientRepository,
            ProduitRepository produitRepository) {

        this.commandeRepository = commandeRepository;
        this.clientRepository = clientRepository;
        this.produitRepository = produitRepository;
    }

    @Override
    public List<CommandeDTO> getAllCommandes() {

    return commandeRepository.findAll()
            .stream()
            .map(CommandeMapper::toDTO)
            .collect(Collectors.toList());

    }

   @Override
    public CommandeDTO getCommandeById(Long id) {

    return commandeRepository.findById(id)
            .map(CommandeMapper::toDTO)
            .orElseThrow(() ->
                new CommandeNotFoundException("Commande introuvable avec l'id : " + id));

    }

   @Override
   // Cette annotation garantit que toutes les opérations de la méthode sont exécutées dans une seule transaction.
   @Transactional
    public CommandeDTO saveCommande(CommandeDTO commandeDTO) {

    // Vérifier que le client existe
    Client client = clientRepository.findById(commandeDTO.getClientId())
            .orElseThrow(() ->
                new ClientNotFoundException("Client introuvable"));

    // Création de la commande
    Commande commande = new Commande();

    commande.setClient(client);
    commande.setDateCommande(commandeDTO.getDateCommande());

    // Au début le montant est à 0.
    // Nous le calculerons après avoir traité les lignes.
    commande.setMontantTotal(BigDecimal.ZERO);

    // Variable qui servira à calculer le montant total
    BigDecimal montantTotal = BigDecimal.ZERO;

    // Lignes de la commande, rattachées à la commande avant sauvegarde
    // pour qu'elles soient bien renvoyées dans la réponse et persistées par cascade.
    List<LigneCommande> lignesCommande = new ArrayList<>();

    // Parcours de toutes les lignes de la commande
    for (LigneCommandeDTO ligneDTO : commandeDTO.getLignes()) {

        // Recherche du produit
        Produit produit = produitRepository.findById(ligneDTO.getProduitId())
                .orElseThrow(() ->
                    new ProduitNotFoundException("Produit introuvable"));

        // Vérification : empêcher un stock négatif
        int nouveauStock = produit.getQuantite() - ligneDTO.getQuantite();

        if (nouveauStock < 0) {
            throw new StockInsuffisantException(
                "Stock insuffisant pour le produit : " + produit.getNom()
            );
        }

        // Création d'une ligne de commande
        LigneCommande ligneCommande = new LigneCommande();

        ligneCommande.setCommande(commande);
        ligneCommande.setProduit(produit);
        ligneCommande.setQuantite(ligneDTO.getQuantite());

        // Prix unitaire récupéré automatiquement depuis le produit
        ligneCommande.setPrixUnitaire(produit.getPrix());

        lignesCommande.add(ligneCommande);

        // Calcul du sous-total
        BigDecimal sousTotal = produit.getPrix().multiply(BigDecimal.valueOf(ligneDTO.getQuantite()));

        // Ajout au montant total
        montantTotal = montantTotal.add(sousTotal);

        // Mise à jour du stock
        produit.setQuantite(nouveauStock);

        // Sauvegarde du produit
        produitRepository.save(produit);

    }

    // Rattachement des lignes à la commande : la cascade (CascadeType.ALL)
    // définie sur Commande.lignesCommande se charge de les persister.
    commande.setLignesCommande(lignesCommande);

    // Mise à jour du montant total
    commande.setMontantTotal(montantTotal);

    // Sauvegarde de la commande avec ses lignes et son montant calculé
    Commande commandeSauvegardee = commandeRepository.save(commande);

    // Retour du DTO
    return CommandeMapper.toDTO(commandeSauvegardee);

    }


    @Override
    @Transactional
    public void deleteCommande(Long id) {

        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() ->
                    new CommandeNotFoundException("Commande introuvable avec l'id : " + id));

        // Restitution du stock des produits avant de supprimer la commande,
        // pour ne pas perdre définitivement la quantité qui avait été décrémentée.
        if (commande.getLignesCommande() != null) {
            for (LigneCommande ligne : commande.getLignesCommande()) {
                Produit produit = ligne.getProduit();
                produit.setQuantite(produit.getQuantite() + ligne.getQuantite());
                produitRepository.save(produit);
            }
        }

        commandeRepository.delete(commande);
    }


}
