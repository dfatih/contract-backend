package de.krieger.personal.contractgenerator.service.impl;

import de.krieger.personal.contractgenerator.enums.ContractVersionName;
import de.krieger.personal.contractgenerator.enums.VersionTemplateName;
import de.krieger.personal.contractgenerator.model.ContractVersion;
import de.krieger.personal.contractgenerator.model.OldContractVersion;
import de.krieger.personal.contractgenerator.service.ContractVersionService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ContractVersionServiceImpl implements ContractVersionService {
    @Override
    public List<OldContractVersion> getAll(){
        List<OldContractVersion> contractVersionList = new ArrayList<>();
        for (ContractVersionName contractVersionName:ContractVersionName.values()) {
            contractVersionList.add(new OldContractVersion(contractVersionName.getVersionName(), convertTemplateArrayToList(contractVersionName.getVersionTemplateNames())));
        }
        return contractVersionList;
    }

    private List<String> convertTemplateArrayToList(VersionTemplateName[] versionTemplateNames){
        List<String> templateNameList = new ArrayList<>();
        for (VersionTemplateName versionTemplateName:versionTemplateNames) {
            templateNameList.add(versionTemplateName.getTemplateName());
        }
        return templateNameList;
    }
}
