package com.gestioncommerciale.backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.gestioncommerciale.backend.dto.ClientDTO;

public interface ClientService {

    Page<ClientDTO> getAllClients(Pageable pageable);

    ClientDTO getClientById(Long id);

    ClientDTO saveClient(ClientDTO clientDTO);

    ClientDTO updateClient(Long id, ClientDTO clientDTO);

    void deleteClient(Long id);

}