package com.yyggee.eggs.service.ds1;


import com.yyggee.eggs.model.ds1.Auditor;
import com.yyggee.eggs.repositories.ds1.AuditorJPARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuditorService {

    @Autowired
    private AuditorJPARepository auditorJPARepository;

    public List<Auditor> findAll() {
        return auditorJPARepository.findAll();
    }

    public Optional<Auditor> findById(long id) {
        return auditorJPARepository.findById(id);
    }

    public Auditor createOrUpdate(Auditor audit) {
        return auditorJPARepository.save(audit);
    }

    public void deleteById(long id) {
        auditorJPARepository.deleteById(id);
    }
}
