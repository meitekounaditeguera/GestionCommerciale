package com.gestioncommerciale.backend.service.impl;

import com.gestioncommerciale.backend.dto.ClientDTO;
import com.gestioncommerciale.backend.exception.ClientNotFoundException;
import com.gestioncommerciale.backend.mapper.ClientMapper;
import com.gestioncommerciale.backend.model.Client;
import com.gestioncommerciale.backend.model.TypeAction;
import com.gestioncommerciale.backend.repository.ClientRepository;
import com.gestioncommerciale.backend.service.AuditLogService;
import com.gestioncommerciale.backend.service.ClientService;

import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class ClientServiceImpl implements ClientService {

//permettent à Spring Boot de fournir automatiquement le ClientRepository.
//Tu n'as pas besoin de faire new ClientRepository(). C'est l'un des piliers de Spring.

    private final ClientRepository clientRepository;
    private final AuditLogService auditLogService;

    public ClientServiceImpl(ClientRepository clientRepository, AuditLogService auditLogService) {
        this.clientRepository = clientRepository;
        this.auditLogService = auditLogService;
    }

    //Retourne les clients de la page demandée, triés selon le Pageable fourni par le contrôleur.
    @Override
    public Page<ClientDTO> getAllClients(Pageable pageable) {

    return clientRepository.findByActifTrue(pageable)
            .map(ClientMapper::toDTO);
    }

    //Recherche un client par son id. Si le client n'existe pas, une exception est levée.
    @Override
    public ClientDTO getClientById(Long id) {

        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException("Client introuvable"));

        return ClientMapper.toDTO(client);
    }

    //Ajoute un nouveau client. Le client est converti de DTO à entité, puis sauvegardé dans la base de données. Le client sauvegardé est ensuite converti de nouveau en DTO et retourné.
    @Override
    public ClientDTO saveClient(ClientDTO clientDTO) {

        Client client = ClientMapper.toEntity(clientDTO);

        Client savedClient = clientRepository.save(client);

        auditLogService.enregistrer(TypeAction.CREATION, "Client",
                "Le client " + savedClient.getNom() + " " + savedClient.getPrenom() + " a été créé");

        return ClientMapper.toDTO(savedClient);
    }

    //Modifie un client existant. Le client est d'abord recherché par son id. Si le client n'existe pas, une exception est levée. Sinon, les champs du client sont mis à jour avec les valeurs du DTO, puis le client est sauvegardé dans la base de données. Le client mis à jour est ensuite converti en DTO et retourné.
   @Override
    public ClientDTO updateClient(Long id, ClientDTO clientDTO) {

    Client client = clientRepository.findById(id)
            .orElseThrow(() -> new ClientNotFoundException("Client introuvable"));

    client.setNom(clientDTO.getNom());
    client.setPrenom(clientDTO.getPrenom());
    client.setEmail(clientDTO.getEmail());
    client.setTelephone(clientDTO.getTelephone());
    client.setAdresse(clientDTO.getAdresse());

    Client updatedClient = clientRepository.save(client);

    auditLogService.enregistrer(TypeAction.MODIFICATION, "Client",
            "Le client " + updatedClient.getNom() + " " + updatedClient.getPrenom() + " a été mis à jour");

    return ClientMapper.toDTO(updatedClient);
}

    //Supprime un client par son id. Si le client n'existe pas, une exception est levée.
    //Suppression logique : le client est désactivé, jamais retiré physiquement de la base,
    //pour ne pas perdre l'historique des commandes qui le référencent (et éviter toute
    //erreur d'intégrité 409 lors de la suppression).
    @Override
    public void deleteClient(Long id) {

        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException("Client introuvable"));

        client.setActif(false);
        clientRepository.save(client);

        auditLogService.enregistrer(TypeAction.SUPPRESSION, "Client",
                "Le client " + client.getNom() + " " + client.getPrenom() + " a été supprimé");
    }
}