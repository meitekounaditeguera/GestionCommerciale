package com.gestioncommerciale.backend.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.gestioncommerciale.backend.dto.FournisseurDTO;
import com.gestioncommerciale.backend.exception.FournisseurNotFoundException;
import com.gestioncommerciale.backend.mapper.FournisseurMapper;
import com.gestioncommerciale.backend.model.Fournisseur;
import com.gestioncommerciale.backend.model.TypeAction;
import com.gestioncommerciale.backend.repository.FournisseurRepository;
import com.gestioncommerciale.backend.service.AuditLogService;
import com.gestioncommerciale.backend.service.FournisseurService;

@Service
public class FournisseurServiceImpl implements FournisseurService {

    private final FournisseurRepository fournisseurRepository;
    private final AuditLogService auditLogService;

    public FournisseurServiceImpl(FournisseurRepository fournisseurRepository, AuditLogService auditLogService) {
        this.fournisseurRepository = fournisseurRepository;
        this.auditLogService = auditLogService;
    }
    
    @Override
    public Page<FournisseurDTO> getAllFournisseurs(Pageable pageable) {
        return fournisseurRepository.findByActifTrue(pageable)
                .map(FournisseurMapper::toDTO);
    }
    
    @Override
    public FournisseurDTO getFournisseurById(Long id) {
        Fournisseur fournisseur = fournisseurRepository.findById(id)
                .orElseThrow(() -> new FournisseurNotFoundException("Fournisseur introuvable"));
        return FournisseurMapper.toDTO(fournisseur);
    }

    @Override
    public FournisseurDTO saveFournisseur(FournisseurDTO fournisseurDTO) {
        Fournisseur fournisseur = FournisseurMapper.toEntity(fournisseurDTO);
        Fournisseur savedFournisseur = fournisseurRepository.save(fournisseur);

        auditLogService.enregistrer(TypeAction.CREATION, "Fournisseur",
                "Le fournisseur " + savedFournisseur.getNom() + " a été créé");

        return FournisseurMapper.toDTO(savedFournisseur);
    }

    @Override
    public FournisseurDTO updateFournisseur(Long id, FournisseurDTO fournisseurDTO) {

        Fournisseur fournisseur = fournisseurRepository.findById(id)
                .orElseThrow(() -> new FournisseurNotFoundException("Fournisseur introuvable"));

        fournisseur.setNom(fournisseurDTO.getNom());
        fournisseur.setEmail(fournisseurDTO.getEmail());
        fournisseur.setTelephone(fournisseurDTO.getTelephone());
        fournisseur.setAdresse(fournisseurDTO.getAdresse());

        Fournisseur updatedFournisseur = fournisseurRepository.save(fournisseur);

        auditLogService.enregistrer(TypeAction.MODIFICATION, "Fournisseur",
                "Le fournisseur " + updatedFournisseur.getNom() + " a été mis à jour");

        return FournisseurMapper.toDTO(updatedFournisseur);
    }

    // Suppression logique : le fournisseur est désactivé, jamais retiré physiquement de la
    // base, pour ne pas perdre l'historique des commandes fournisseur qui le référencent
    // (et éviter toute erreur d'intégrité 409 lors de la suppression).
    @Override
    public void deleteFournisseur(Long id) {
        Fournisseur fournisseur = fournisseurRepository.findById(id)
                .orElseThrow(() -> new FournisseurNotFoundException("Fournisseur introuvable"));

        fournisseur.setActif(false);
        fournisseurRepository.save(fournisseur);

        auditLogService.enregistrer(TypeAction.SUPPRESSION, "Fournisseur",
                "Le fournisseur " + fournisseur.getNom() + " a été supprimé");
    }

}
