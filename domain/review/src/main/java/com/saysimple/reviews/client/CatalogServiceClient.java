//package com.saysimple.products.client;
//
//import com.saysimple.users.error.FeignErrorDecoder2;
//import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.web.bind.annotation.GetMapping;
//
//import java.util.List;
//
//@FeignClient(name = "catalogs", configuration = FeignErrorDecoder2.class)
//public interface CatalogServiceClient {
//
//    @GetMapping("/catalogs/getCatalogs_wrong")
//    List<ResponseCatalog> getCatalogs();
//
//}
