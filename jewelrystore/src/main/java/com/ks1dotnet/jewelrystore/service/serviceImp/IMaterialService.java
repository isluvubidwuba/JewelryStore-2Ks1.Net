package com.ks1dotnet.jewelrystore.service.serviceImp;

import java.util.List;

import com.ks1dotnet.jewelrystore.dto.MaterialDTO;
import com.ks1dotnet.jewelrystore.payload.ResponseData;

public interface IMaterialService extends ICRUDService<MaterialDTO, Integer> {
    public ResponseData getGoldPirce(List<MaterialDTO> t);
}
