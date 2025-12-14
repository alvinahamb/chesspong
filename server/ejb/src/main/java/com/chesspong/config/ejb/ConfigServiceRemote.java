package com.chesspong.config.ejb;

import com.chesspong.config.dto.ConfigDTO;
import jakarta.ejb.Remote;
import java.util.List;

@Remote
public interface ConfigServiceRemote {
    ConfigDTO create(ConfigDTO config);
    ConfigDTO getOne(int id);
    List<ConfigDTO> getAll();
    ConfigDTO update(ConfigDTO config);
    boolean delete(int id);
    ConfigDTO getLast();
}