package com.chesspong.config.ejb;

import com.chesspong.config.dto.ConfigDTO;
import com.chesspong.config.entity.Config;
import com.chesspong.config.repository.ConfigRepository;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
@Remote(ConfigServiceRemote.class)
public class ConfigService implements ConfigServiceRemote {

    @Inject
    private ConfigRepository repo;

    // Méthode pour convertir Entity vers DTO
    private ConfigDTO toDTO(Config config) {
        if (config == null) {
            return null;
        }
        ConfigDTO dto = new ConfigDTO();
        dto.setId(config.getId());
        dto.setRoi(config.getRoi());
        dto.setDame(config.getDame());
        dto.setTour(config.getTour());
        dto.setFou(config.getFou());
        dto.setCavalier(config.getCavalier());
        dto.setPion(config.getPion());
        dto.setBallDegats(config.getBallDegats());
        dto.setPouvoirBall(config.getPouvoirBall());
        dto.setAtteintePouvoir(config.getAtteintePouvoir());
        dto.setPieceNumber(config.getPieceNumber());
        return dto;
    }

    // Méthode pour convertir DTO vers Entity
    private Config toEntity(ConfigDTO dto) {
        if (dto == null) {
            return null;
        }
        Config config = new Config();
        // Only set ID if it's a positive value (existing entity)
        // For new entities with IDENTITY strategy, don't set the ID
        if (dto.getId() > 0) {
            config.setId(dto.getId());
        }
        config.setRoi(dto.getRoi());
        config.setDame(dto.getDame());
        config.setTour(dto.getTour());
        config.setFou(dto.getFou());
        config.setCavalier(dto.getCavalier());
        config.setPion(dto.getPion());
        config.setBallDegats(dto.getBallDegats());
        config.setPouvoirBall(dto.getPouvoirBall());
        config.setAtteintePouvoir(dto.getAtteintePouvoir());
        config.setPieceNumber(dto.getPieceNumber());
        return config;
    }

    public ConfigDTO create(ConfigDTO config) {
        Config entity = toEntity(config);
        repo.save(entity);
        return toDTO(entity);
    }

    public ConfigDTO getOne(int id) {
        return toDTO(repo.find(id));
    }

    public List<ConfigDTO> getAll() {
        return repo.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public ConfigDTO update(ConfigDTO config) {
        Config entity = toEntity(config);
        Config updated = repo.update(entity);
        return toDTO(updated);
    }

    public boolean delete(int id) {
        return repo.delete(id);
    }

    public ConfigDTO getLast() {
        return toDTO(repo.findLast());
    }
}