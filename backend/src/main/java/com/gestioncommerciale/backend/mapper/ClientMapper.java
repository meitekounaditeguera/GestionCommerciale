package com.gestioncommerciale.backend.mapper;

import com.gestioncommerciale.backend.dto.ClientDTO;
import com.gestioncommerciale.backend.model.Client;

public class ClientMapper {

//C'est une méthode statique qui prend un objet Client en paramètre et retourne un objet ClientDTO.
//Elle vérifie d'abord si le client est null, et si c'est le cas, elle retourne null. Sinon, elle crée un nouvel objet ClientDTO, copie les valeurs des champs du client vers le DTO, puis retourne le DTO.
    public static ClientDTO toDTO(Client client){

        if(client == null){
            return null;
        }
//
        ClientDTO dto = new ClientDTO();

        dto.setId(client.getId());
        dto.setNom(client.getNom());
        dto.setPrenom(client.getPrenom());
        dto.setEmail(client.getEmail());
        dto.setTelephone(client.getTelephone());
        dto.setAdresse(client.getAdresse());

        return dto;
    }

    public static Client toEntity(ClientDTO dto){
//C'est une méthode statique qui prend un objet ClientDTO en paramètre et retourne un objet Client.
//Elle vérifie d'abord si le DTO est null, et si c'est le cas, elle retourne null. 
//Sinon, elle crée un nouvel objet Client, copie les valeurs des champs du DTO vers l'objet Client, puis retourne l'objet Client.
        if(dto == null){
            return null;
        }

        Client client = new Client();

        client.setId(dto.getId());
        client.setNom(dto.getNom());
        client.setPrenom(dto.getPrenom());
        client.setEmail(dto.getEmail());
        client.setTelephone(dto.getTelephone());
        client.setAdresse(dto.getAdresse());

        return client;
    }

}