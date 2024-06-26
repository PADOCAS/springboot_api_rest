package com.ldsystems.api.rest.springbootapirest.service;

import jakarta.servlet.ServletContext;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService implements Serializable {

    private static final long serialVersionUID = 1L;

    public byte[] getReportPdf(List<?> listDataBean, String nomeRelatorio, Map<String, Object> paramRel, ServletContext context) throws Exception {
        //Obter conexão com o banco de dados:
        if (nomeRelatorio != null
                && listDataBean != null) {
            String caminhoJasper = context.getRealPath("report") + File.separator + nomeRelatorio + ".jasper";

            //Cria a lista de CollectionDataSource de beans que carregam os dados para relatório
            JRBeanCollectionDataSource jrBeanColDs = new JRBeanCollectionDataSource(listDataBean);

            //Parâmetros:
            if(paramRel == null) {
                paramRel = new HashMap<>();
            }

            //Caminho para Imagem - Logo que vai buscar lá nos Relatórios:
            paramRel.put("REPORT_PARAMETERS_IMG", context.getRealPath("report") + File.separator);

//            JasperPrint print = JasperFillManager.fillReport(caminhoJasper, paramRel, connection); //Pode trabalhar direto com conection também fazendo select dentro do Ireport lá.. (mas vamos mandar direto a lista pronta)
            JasperPrint print = JasperFillManager.fillReport(caminhoJasper, paramRel, jrBeanColDs);

            return JasperExportManager.exportReportToPdf(print);
        }

        return null;
    }
}
