package com.ks1dotnet.jewelrystore.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ks1dotnet.jewelrystore.dto.GemStoneCategoryDTO;
import com.ks1dotnet.jewelrystore.dto.GemStoneOfProductDTO;
import com.ks1dotnet.jewelrystore.dto.GemStoneTypeDTO;
import com.ks1dotnet.jewelrystore.entity.GemStoneOfProduct;
import com.ks1dotnet.jewelrystore.repository.IGemStoneCategoryRepository;
import com.ks1dotnet.jewelrystore.repository.IGemStoneOfProductRepository;
import com.ks1dotnet.jewelrystore.repository.IGemStoneTypeRepository;
import com.ks1dotnet.jewelrystore.repository.IProductRepository;
import com.ks1dotnet.jewelrystore.service.serviceImp.IGemStoneOfProductService;

@Service
public class GemStoneOfProductService implements IGemStoneOfProductService {
    @Autowired
    private IGemStoneOfProductRepository iGemStoneOfProductRepository;
    @Autowired
    private IGemStoneTypeRepository iGemStoneTypeRepository;
    @Autowired
    private IProductRepository iProductRepository;
    @Autowired
    private IGemStoneCategoryRepository iGemStoneCategoryRepository;

    @Override
    public List<GemStoneOfProductDTO> findAll() {
        try {
            List<GemStoneOfProductDTO> listDTO = new ArrayList<>();
            for (GemStoneOfProduct gemStoneOfProduct : iGemStoneOfProductRepository.findAll()) {
                listDTO.add(gemStoneOfProduct.getDTO());
            }
            return listDTO;
        } catch (Exception e) {
            System.out.println("GemStone of product of product find all error: " + e.getMessage());
            return null;
        }
    }

    @Override
    public GemStoneOfProductDTO findById(Integer id) {
        try {
            GemStoneOfProduct gsp = iGemStoneOfProductRepository.findById(id).orElse(null);
            if (gsp == null)
                return null;
            return gsp.getDTO();
        } catch (Exception e) {
            System.out.println("GemStone of product find by id error: " + e.getMessage());
            return null;
        }
    }

    @Override
    public GemStoneOfProductDTO save(GemStoneOfProductDTO t) {
        try {
            iGemStoneOfProductRepository.save(new GemStoneOfProduct(t));
            return t;
        } catch (Exception e) {
            System.out.println("GemStone of product save error: " + e.getMessage());
            return null;
        }
    }

    @Override
    public GemStoneOfProductDTO saveAndFlush(GemStoneOfProductDTO t) {
        try {
            iGemStoneOfProductRepository.saveAndFlush(new GemStoneOfProduct(t));
            return t;
        } catch (Exception e) {
            System.out.println("GemStone of product save error: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<GemStoneOfProductDTO> saveAll(Iterable<GemStoneOfProductDTO> t) {
        List<GemStoneOfProduct> list = new ArrayList<>();
        for (GemStoneOfProductDTO GemStoneOfProductDTO : t) {
            list.add(new GemStoneOfProduct(GemStoneOfProductDTO));
        }
        try {
            List<GemStoneOfProductDTO> listDTO = new ArrayList<>();
            for (GemStoneOfProduct gemStoneCategory : iGemStoneOfProductRepository.saveAll(list)) {
                listDTO.add(gemStoneCategory.getDTO());
            }
            return listDTO;
        } catch (Exception e) {
            System.out.println("GemStones of product saveAll error: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<GemStoneOfProductDTO> saveAllAndFlush(Iterable<GemStoneOfProductDTO> t) {
        List<GemStoneOfProduct> list = new ArrayList<>();
        for (GemStoneOfProductDTO GemStoneOfProductDTO : t) {
            list.add(new GemStoneOfProduct(GemStoneOfProductDTO));
        }
        try {
            List<GemStoneOfProductDTO> listDTO = new ArrayList<>();
            for (GemStoneOfProduct gemStoneCategory : iGemStoneOfProductRepository
                    .saveAllAndFlush(list)) {
                listDTO.add(gemStoneCategory.getDTO());
            }
            return listDTO;
        } catch (Exception e) {
            System.out.println("GemStones of product saveAll error: " + e.getMessage());
            return null;
        }
    }

    @Override
    public boolean existsById(Integer id) {
        return iGemStoneOfProductRepository.existsById(id);
    }

    @Override
    public boolean deleteById(Integer id) {
        try {
            iGemStoneOfProductRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            System.out.println("GemStones of product delete by id error: " + e.getMessage());
            return false;
        }
    }

    @Override
    public GemStoneOfProductDTO update(int id, String color, String clarity, float carat,
            double price, int id_gemStoneType, int id_gemStone_category, String id_product) {
        if (!iGemStoneOfProductRepository.existsById(id)
                && !iGemStoneCategoryRepository.existsById(id_gemStone_category)
                && !iGemStoneTypeRepository.existsById(id_gemStoneType))
            return null;
        GemStoneCategoryDTO gscdto =
                iGemStoneCategoryRepository.findById(id_gemStone_category).orElse(null).getDTO();
        GemStoneTypeDTO gstdto =
                iGemStoneTypeRepository.findById(id_gemStoneType).orElse(null).getDTO();
        try {

        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    @Override
    public GemStoneOfProductDTO insert(String color, String clarity, float carat, double price,
            int id_gemStoneType, int id_gemStone_category, String id_product) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'insert'");
    }

}
