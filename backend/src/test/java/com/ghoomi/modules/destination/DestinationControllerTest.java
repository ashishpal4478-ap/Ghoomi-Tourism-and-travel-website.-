package com.ghoomi.modules.destination;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DestinationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DestinationRepository destinationRepository;

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    private static final String TEST_SLUG = "test-destination-jaipur";
    private String testDestinationId;
    private String testAttractionId;
    private String testRestaurantId;

    @BeforeEach
    void setUp() {
        cleanTestData();

        new TransactionTemplate(transactionManager).executeWithoutResult(status -> {
            Destination dest = new Destination(TEST_SLUG, "Test Destination Jaipur");
            dest.setStateRegion("Rajasthan");
            dest.setCountryCode("IN");
            dest.setLatitude(new BigDecimal("26.9124"));
            dest.setLongitude(new BigDecimal("75.7873"));
            dest.setSummary("Historic pink city of royal palaces and rich heritage.");
            dest.setActive(true);
            Destination savedDest = destinationRepository.save(dest);
            testDestinationId = savedDest.getId();

            Place attraction = new Place(savedDest, "Hawa Mahal", PlaceCategory.ATTRACTION);
            attraction.setDescription("Palace of Winds with honeycomb windows");
            attraction.setEstimatedCostMinor(20000L); // 200 INR
            attraction.setCurrency("INR");
            attraction.setActive(true);
            testAttractionId = placeRepository.save(attraction).getId();

            Place restaurant = new Place(savedDest, "Laxmi Misthan Bhandar", PlaceCategory.RESTAURANT);
            restaurant.setDescription("Famous Rajasthani thali and traditional sweets");
            restaurant.setEstimatedCostMinor(60000L); // 600 INR
            restaurant.setCurrency("INR");
            restaurant.setActive(true);
            testRestaurantId = placeRepository.save(restaurant).getId();
        });
    }

    @AfterEach
    void tearDown() {
        cleanTestData();
    }

    private void cleanTestData() {
        new TransactionTemplate(transactionManager).executeWithoutResult(status -> {
            destinationRepository.findBySlug(TEST_SLUG).ifPresent(dest -> {
                placeRepository.findAllByDestinationIdAndActiveTrue(dest.getId()).forEach(placeRepository::delete);
                destinationRepository.delete(dest);
            });
        });
    }

    @Test
    void getDestinations_isPublicAndReturnsList() throws Exception {
        mockMvc.perform(get("/api/v1/destinations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    void getDestinations_withQueryFilter_returnsMatching() throws Exception {
        mockMvc.perform(get("/api/v1/destinations").param("query", "Jaipur"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].slug").value(TEST_SLUG))
                .andExpect(jsonPath("$.data[0].name").value("Test Destination Jaipur"));
    }

    @Test
    void getDestinationBySlug_succeeds() throws Exception {
        mockMvc.perform(get("/api/v1/destinations/" + TEST_SLUG))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(testDestinationId))
                .andExpect(jsonPath("$.data.name").value("Test Destination Jaipur"))
                .andExpect(jsonPath("$.data.stateRegion").value("Rajasthan"));
    }

    @Test
    void getDestinationBySlug_notFound_returns404() throws Exception {
        mockMvc.perform(get("/api/v1/destinations/non-existent-slug-xyz"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void getPlacesByDestination_returnsPlaces() throws Exception {
        mockMvc.perform(get("/api/v1/destinations/" + testDestinationId + "/places"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(2))));
    }

    @Test
    void getPlaces_filteredByCategory_returnsOnlyMatchingCategory() throws Exception {
        // Query restaurant
        mockMvc.perform(get("/api/v1/places")
                        .param("destinationId", testDestinationId)
                        .param("category", "RESTAURANT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].name").value("Laxmi Misthan Bhandar"))
                .andExpect(jsonPath("$.data[0].category").value("RESTAURANT"));

        // Query attraction
        mockMvc.perform(get("/api/v1/places")
                        .param("destinationId", testDestinationId)
                        .param("category", "ATTRACTION"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].name").value("Hawa Mahal"))
                .andExpect(jsonPath("$.data[0].category").value("ATTRACTION"));
    }

    @Test
    void getPlaceById_succeeds() throws Exception {
        mockMvc.perform(get("/api/v1/places/" + testAttractionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(testAttractionId))
                .andExpect(jsonPath("$.data.name").value("Hawa Mahal"))
                .andExpect(jsonPath("$.data.category").value("ATTRACTION"));
    }

    @Test
    void getPlaceById_notFound_returns404() throws Exception {
        mockMvc.perform(get("/api/v1/places/non-existent-place-id"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }
}
