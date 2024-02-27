package com.ldsystems.api.rest.springbootapirest.service;

import jakarta.servlet.ServletContext;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.Serializable;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

@Service
public class ReportService implements Serializable {

    private static final long serialVersionUID = 1L;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public byte[] getReportPdf(String nomeRelatorio, ServletContext context) throws Exception {
        //Obter conexão com o banco de dados:
        if (nomeRelatorio != null
                && jdbcTemplate != null
                && jdbcTemplate.getDataSource() != null) {
            Connection connection = jdbcTemplate.getDataSource().getConnection();
            String caminhoJasper = context.getRealPath("report") + File.separator + nomeRelatorio + ".jasper";

            //Parâmetros:
            Map<String, Object> param = new HashMap<>();
            JasperPrint print = JasperFillManager.fillReport(caminhoJasper, param, connection);

            return JasperExportManager.exportReportToPdf(print);
        }

        return null;
    }
}
