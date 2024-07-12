package com.ks1dotnet.jewelrystore.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ks1dotnet.jewelrystore.repository.IInvalidatedTokenRepository;
import com.ks1dotnet.jewelrystore.service.serviceImp.IInvalidatedTokenService;

@Service
public class InvalidatedTokenService implements IInvalidatedTokenService {
    @Autowired
    IInvalidatedTokenRepository iInvalidatedTokenRepository;

}
