package com.gestioncommerciale.backend.service.impl;

import com.gestioncommerciale.backend.dto.ClientDTO;
import com.gestioncommerciale.backend.exception.ClientNotFoundException;
import com.gestioncommerciale.backend.mapper.ClientMapper;
import com.gestioncommerciale.backend.model.Client;
import com.gestioncommerciale.backend.repository.ClientRepository;
import com.gestioncommerciale.backend.service.ClientService;

import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;

import java.util.List;

@Service
public class ClientServiceImpl implements ClientService {

//permettent à Spring Boot de fournir automatiquement le ClientRepository. 
//Tu n'as pas besoin de faire new ClientRepository(). C'est l'un des piliers de Spring.

    private final ClientRepository clientRepository;

    public ClientServiceImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    //Retourne tous les clients, triés par id croissant.
    @Override
    public List<ClientDTO> getAllClients() {

    return clientRepository.findAll(
            Sort.by(Sort.Direction.ASC, "id"))
            .stream()
            .map(ClientMapper::toDTO)
            .toList();
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

    Client updatedClient = clientRepository.save(client);

    return ClientMapper.toDTO(updatedClient);
}

    //Supprime un client par son id. Si le client n'existe pas, une exception est levée.
    @Override
    public void deleteClient(Long id) {

        if (!clientRepository.existsById(id)) {
            throw new ClientNotFoundException("Client introuvable");
        }

        clientRepository.deleteById(id);
    }
}