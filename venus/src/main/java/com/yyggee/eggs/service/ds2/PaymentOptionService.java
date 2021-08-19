package com.yyggee.eggs.service.ds2;

import com.yyggee.eggs.model.ds2.Currency;
import com.yyggee.eggs.model.ds2.Sequence;
import com.yyggee.eggs.repositories.ds2.SequenceJPARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentOptionService {

    @Autowired
    private SequenceJPARepository sequenceJPARepository;

    public List<Sequence> findAll() {
        return sequenceJPARepository.findAll();
    }

}
