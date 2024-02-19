package com.ldsystems.api.rest.springbootapirest.repository;

import com.ldsystems.api.rest.springbootapirest.model.ConfigGeral;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigGeralRepository extends JpaRepository<ConfigGeral, Long> {

    /**
     * Método para retorna um único registro de configGeral, vai ser alimentado por fora (apenas 1 registro)!
     *
     * @return ConfigGeral
     */
    @Query(value = "select cg from ConfigGeral cg")
    public ConfigGeral findUniqueConfigGeral();

}
