package de.krieger.personal.contractgenerator.batch;

import de.krieger.personal.contractgenerator.model.Contract;
import de.krieger.personal.contractgenerator.repository.ContractRepository;
import de.krieger.personal.contractgenerator.service.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class TriggerToDelete {
    @Autowired
    private ContractService contractService;

    @Autowired
    private ContractRepository contractRepository;

    private int daysUntilDelete = 180;

    @Scheduled(fixedDelay = 3600000)
    public void triggerDelete(){

        List<Contract> contracts = contractRepository.findAll();
        for (Contract contract:contracts
             ) {
            if (contract.getLastEdit().isBefore(LocalDateTime.now().minusDays(daysUntilDelete))) {
                contractService.deleteById(contract.getId());
            }
        }

    }

}
