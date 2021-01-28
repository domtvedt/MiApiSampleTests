/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package miApiTest;

/**
 *
 * @author darinomtvedt
 */
import static io.restassured.RestAssured.*;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class locationApiTest {

    // Test that the page is up and returning http status 200 in less than one second
    @Test
    public void isPageUp() {

        RequestSpecification requestSpec = new RequestSpecBuilder().build();
        requestSpec.relaxedHTTPSValidation();
        requestSpec.baseUri("https://www.motionindustries.com");
        requestSpec.basePath("/about-mi/locations");

        Response response = given()
                .spec(requestSpec)
                .get();

        System.out.println(response.statusCode());
        System.out.println(response.time());

        assertAll("response",
                () -> assertTrue(response.statusCode() == 200),
                () -> assertTrue(response.time() < 1000)
        );

    }

    // Test that a page with a bad URL (ie a stray period at the end) 
    //    returns not a 404 error, but a 200 status with the 404 page  
    @Test
    public void badURL() {
        RequestSpecification requestSpec = new RequestSpecBuilder().build();
        requestSpec.relaxedHTTPSValidation();
        requestSpec.baseUri("https://www.motionindustries.com");
        requestSpec.basePath("/about-mi/locations.");

        Response response = given()
                .spec(requestSpec)
                .get();

        System.out.println(response.statusCode());
        System.out.println(response.time());
        System.out.println(response.htmlPath().getString("html.head.title") + "\n");

        assertAll("response",
                () -> assertTrue(response.statusCode() == 200),
                () -> assertTrue(response.time() < 1500),
                () -> assertTrue(response.htmlPath().getString("html.head.title").contentEquals("Page Not Found - Motion"))
        );

    }

    //  Test that the sub get call returns the location information contained within the map 
    //    coords.
    @Test
    public void mapLocationInCoords() {
        RequestSpecification requestSpec = new RequestSpecBuilder().build();
        requestSpec.relaxedHTTPSValidation();
        requestSpec.baseUri("https://www.motionindustries.com");
        requestSpec.basePath("/misvc/mi/services/json/locations.search");
        requestSpec.queryParam("latMax", "65.26938634772425");
        requestSpec.queryParam("latMin", "60.64495971099344");
        requestSpec.queryParam("lonMax", "180");
        requestSpec.queryParam("lonMin", "-153.6730948116455");
        requestSpec.queryParam("siteCode", "MI");

        Response response = given()
                .spec(requestSpec)
                .get();

        System.out.println(response.statusCode());
        System.out.println(response.time());
        System.out.println(response.contentType());
        System.out.println(response.path("locations.label") + "\n");

        assertAll("response",
                () -> assertTrue(response.statusCode() == 200),
                () -> assertTrue(response.time() < 1000),
                () -> assertTrue(response.contentType().contains("application/json")),
                () -> assertTrue(response.path("locations.label").toString().contains("AK84 - MOTION INDUSTRIES"))
        );
    }

    //  Test that the sub GET call to return the MI location information contained within the map 
    //  coords fails with invalid Lattitude values.
    @Test
    public void mapLocationInCoords_BadLat() {
        RequestSpecification requestSpec = new RequestSpecBuilder().build();
        requestSpec.relaxedHTTPSValidation();
        requestSpec.baseUri("https://www.motionindustries.com");
        requestSpec.basePath("/misvc/mi/services/json/locations.search");
        requestSpec.queryParam("latMax", "100");
        requestSpec.queryParam("latMin", "-100");
        requestSpec.queryParam("lonMax", "180");
        requestSpec.queryParam("lonMin", "-153.6730948116455");
        requestSpec.queryParam("siteCode", "MI");

        Response response = given()
                .spec(requestSpec)
                .get();

        System.out.println(response.statusCode());
        System.out.println(response.time());
        System.out.println(response.contentType());
        System.out.println(response.path("success").toString());
        System.out.println(response.path("errorMsg") + "\n");

        assertAll("response",
                () -> assertTrue(response.statusCode() == 200),
                () -> assertTrue(response.time() < 1000),
                () -> assertTrue(response.contentType().contains("application/json")),
                () -> assertTrue(response.path("errorMsg").toString().contains("Error searching for locations"))
        );
    }

    //  THIS TEST FAILS!!!!   It appears that the map accepts out of bounds Longitude.
    //  Test that the sub GET call to return the MI location information contained within the map 
    //  coords fails with invalid Longitude values.
    @Test
    public void mapLocationInCoords_BadLon() {
        RequestSpecification requestSpec = new RequestSpecBuilder().build();
        requestSpec.relaxedHTTPSValidation();
        requestSpec.baseUri("https://www.motionindustries.com");
        requestSpec.basePath("/misvc/mi/services/json/locations.search");
        requestSpec.queryParam("latMax", "65.26938634772425");
        requestSpec.queryParam("latMin", "60.64495971099344");
        requestSpec.queryParam("lonMax", "190");
        requestSpec.queryParam("lonMin", "-190");
        requestSpec.queryParam("siteCode", "MI");

        Response response = given()
                .spec(requestSpec)
                .get();

        System.out.println(response.statusCode());
        System.out.println(response.time());
        System.out.println(response.contentType());
        System.out.println(response.path("success").toString());
        System.out.println(response.path("errorMsg") + "\n");

        assertAll("response",
                () -> assertTrue(response.statusCode() == 200),
                () -> assertTrue(response.time() < 1000),
                () -> assertTrue(response.contentType().contains("application/json")),
                () -> assertTrue(response.path("errorMsg").toString().contains("Error searching for locations"))
        );
    }
}

// PLEASE NOTE: The methods below were an early attempt to build the HEADER and COOKIES needed
//              to successfully call the MI server.  With some research the use of the relaxedHTTPSValidation
//              method made this work not needed.  Kept for reference.
//    public Map<String, Object> buildHeader() {
//        Map<String, Object> headerMap = new HashMap<String, Object>();
//        headerMap.put("Host", "www.motionindustries.com");
//        headerMap.put("Connection", "keep-alive");
//        headerMap.put("Cache-Control", "max-age=0");
//        headerMap.put("sec-ch-ua", "\"Google Chrome\";v=\"87\", \" Not;A Brand\";v=\"99\", \"Chromium\";v=\"87\"");
//        headerMap.put("sec-ch-ua-mobile", "?0");
//        headerMap.put("Upgrade-Insecure-Requests", "1");
//        headerMap.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 11_1_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.141 Safari/537.36");
//        headerMap.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
//        headerMap.put("Sec-Fetch-Site", "same-origin");
//        headerMap.put("Sec-Fetch-Mode", "navigate");
//        headerMap.put("Sec-Fetch-User", "?1");
//        headerMap.put("Sec-Fetch-Dest", "document");
//        headerMap.put("Accept-Encoding", "gzip, deflate, br");
//        headerMap.put("Accept-Language", "en-US,en;q=0.9,es;q=0.8");
//
//        return headerMap;
//    }
//
//    public Map<String, String> buildCookies() {
//        Map<String, String> cookiesMap = new HashMap<String, String>();
//        cookiesMap.put("__cfduid", "db6ec9f0a66572acaa2402dcd233bbdb91611757519");
//        cookiesMap.put("gbi_visitorId", "ckkfita5e00013g9fuxzlda19");
//        cookiesMap.put("__cf_bm", "da29730f6f58528de18df0dd355e0464f4295d69-1611757520-1800-AdK2D78jBblCU+gF8MC0oXYeAC7nEbYxCwL+00tyYgv2rpEc/zUyNf9ZPZtBNqYecPDJuyuwglm+z7DWUN8KR2/ZV7690h+hx/2Rumfdhw3aVriLAcc2G9AzTUTtKEd8ysBMFTg4Tzo7VR9Pr869+jCViBfQeACd1Tfaf/PnPYU6");
//        cookiesMap.put("adobe_pt", "locations");
//        cookiesMap.put("_gcl_au", "1.1.403552421.1611757521");
//        cookiesMap.put("JSESSIONID", "0000tM0s1ADipPZ9bIdgQrUT3A_:svc3");
//        cookiesMap.put("CSRF_AUTH_TOKEN", "2af9f149-a55b-4535-862e-a462e8a9b4cc");
//        cookiesMap.put("DPJSESSIONID", "PBC5YS:2566645863");
//        cookiesMap.put("AMCVS_A9292C1653D60E0F0A490D4B%40AdobeOrg", "1");
//        cookiesMap.put("AMCV_A9292C1653D60E0F0A490D4B%40AdobeOrg", "359503849%7CMCIDTS%7C18655%7CMCMID%7C89796343419613513370620209926387074267%7CMCAAMLH-1612362321%7C9%7CMCAAMB-1612362321%7CRKhpRz8krg2tLO6pguXWp5olkAcUniQYPHaMWWgdJ3xzPWQmdj0y%7CMCOPTOUT-1611764721s%7CNONE%7CvVersion%7C5.0.1");
//        cookiesMap.put("_fbp", "fb.1.1611757521659.1837199090");
//        cookiesMap.put("_ga", "GA1.2.1389799984.1611757522");
//        cookiesMap.put("_gid", "GA1.2.561499308.1611757522");
//        cookiesMap.put("_uetsid", "7cf2c77060ab11ebbb5bf56275a94078");
//        cookiesMap.put("_uetvid", "7cf308c060ab11eb984c73acaa2764c8");
//        cookiesMap.put("s_cc", "true");
//
//        return cookiesMap;
//
//    }

