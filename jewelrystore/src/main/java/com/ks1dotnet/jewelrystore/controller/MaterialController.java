package com.ks1dotnet.jewelrystore.controller;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import com.ks1dotnet.jewelrystore.dto.MaterialDTO;
import com.ks1dotnet.jewelrystore.payload.ResponseData;
import com.ks1dotnet.jewelrystore.service.serviceImp.IMaterialService;

@RestController
@RequestMapping("${apiURL}/material")
@CrossOrigin(origins = "${domain}", allowCredentials = "true")
public class MaterialController {
    @Autowired
    private IMaterialService iMaterialService;

    @GetMapping
    public ResponseEntity<?> getById(@RequestParam int id) {
        ResponseData response = iMaterialService.findById(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("all")
    public ResponseEntity<?> getAll() {
        ResponseData response = iMaterialService.Page(0, 10);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("getGoldPrice")
    public ResponseEntity<?> getGoldPrice() {
        ResponseData response = iMaterialService.Page(0, 10);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("nextPage")
    public ResponseEntity<?> getAll(@RequestParam int page, @RequestParam int size) {
        ResponseData response = iMaterialService.Page(page, size);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("goldPriceFromSJC")
    public ResponseEntity<String> getGoldPrices() {
        String url = "https://sjc.com.vn/xml/tygiavang.xml";
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(0,
                new StringHttpMessageConverter(StandardCharsets.UTF_8));

        try {
            String response = restTemplate.getForObject(url, String.class);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_XML);
            headers.set(HttpHeaders.CONTENT_TYPE, "application/xml; charset=UTF-8");
            headers.set("X-Frame-Options", "ALLOWALL");
            headers.set("Content-Security-Policy", "frame-ancestors 'self' *");
            return new ResponseEntity<>(response, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching data");
        }
    }

    @GetMapping("update-gold-prices")
    public ResponseEntity<?> getGoldPricesV2() {
        ResponseData responsedata = new ResponseData();
        List<MaterialDTO> listMaterial = new ArrayList<>();
        String url = "https://sjc.com.vn/xml/tygiavang.xml";
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(0,
                new StringHttpMessageConverter(StandardCharsets.UTF_8));
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            String response = restTemplate.getForObject(url, String.class);

            // Parse dữ liệu XML từ response
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder
                    .parse(new ByteArrayInputStream(response.getBytes(StandardCharsets.UTF_8)));
            document.getDocumentElement().normalize();

            // Lấy thông tin thành phố Hồ Chí Minh
            NodeList cities = document.getElementsByTagName("city");
            for (int i = 0; i < cities.getLength(); i++) {
                Node cityNode = cities.item(i);

                if (cityNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element cityElement = (Element) cityNode;
                    String cityName = cityElement.getAttribute("name");

                    if (cityName.equals("Hồ Chí Minh")) {
                        NodeList items = cityElement.getElementsByTagName("item");

                        for (int j = 0; j < items.getLength(); j++) {
                            Node itemNode = items.item(j);

                            if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
                                Element itemElement = (Element) itemNode;
                                String type = itemElement.getAttribute("type");
                                int idMaterial = convertTypeName(type);
                                if (idMaterial != 0) {
                                    String buy = itemElement.getAttribute("buy");
                                    String sell = itemElement.getAttribute("sell");
                                    MaterialDTO materialDTO = new MaterialDTO();
                                    materialDTO.setId(idMaterial);
                                    materialDTO.setPriceAtTime(
                                            (Double.parseDouble(sell) * 1000) / 37.5);
                                    materialDTO.setPriceBuyAtTime(
                                            (Double.parseDouble(buy) * 1000) / 37.5);
                                    materialDTO.setLastModified((formatter.format(date)));
                                    listMaterial.add(materialDTO);
                                }
                            }
                        }
                    }
                }
            }
            if (listMaterial.size() > 0)
                responsedata = iMaterialService.getGoldPirce(listMaterial);
            return new ResponseEntity<>(responsedata, responsedata.getStatus());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching gold price data");
        }
    }

    private int convertTypeName(String originalType) {
        switch (originalType) {
            case "Vàng nữ trang 99,99%":
                return 5;
            case "Vàng nữ trang 99%":
                return 4;
            case "Vàng nữ trang 75%":
                return 3;
            case "Vàng nữ trang 58,3%":
                return 2;
            case "Vàng nữ trang 41,7%":
                return 1;
            default:
                return 0;
        }
    }
}
