package com.ghoomi.modules.trip;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ghoomi.modules.auth.dto.LoginRequest;
import com.ghoomi.modules.auth.dto.RegisterRequest;
import com.ghoomi.modules.trip.dto.CreateTripRequest;
import com.ghoomi.modules.trip.dto.TripPreferencesRequest;
import com.ghoomi.modules.trip.dto.UpdateTripRequest;
import com.ghoomi.modules.user.User;
import com.ghoomi.modules.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TripControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private ItineraryDayRepository itineraryDayRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    private static final String USER_A_EMAIL = "trip_user_a@example.com";
    private static final String USER_B_EMAIL = "trip_user_b@example.com";
    private static final String PASSWORD = "SecurePassword123!";

    @BeforeEach
    void setUp() {
        cleanTestData();
    }

    @AfterEach
    void tearDown() {
        cleanTestData();
    }

    private void cleanTestData() {
        new TransactionTemplate(transactionManager).executeWithoutResult(status -> {
            userRepository.findByEmail(USER_A_EMAIL).ifPresent(user -> {
                tripRepository.findAllByOwnerIdOrderByUpdatedAtDesc(user.getId()).forEach(t -> {
                    itineraryDayRepository.deleteAllByTripId(t.getId());
                    tripRepository.delete(t);
                });
                userRepository.delete(user);
            });
            userRepository.findByEmail(USER_B_EMAIL).ifPresent(user -> {
                tripRepository.findAllByOwnerIdOrderByUpdatedAtDesc(user.getId()).forEach(t -> {
                    itineraryDayRepository.deleteAllByTripId(t.getId());
                    tripRepository.delete(t);
                });
                userRepository.delete(user);
            });
        });
    }

    private MockHttpSession registerAndLogin(String name, String email) throws Exception {
        RegisterRequest register = new RegisterRequest();
        register.setName(name);
        register.setEmail(email);
        register.setPassword(PASSWORD);

        mockMvc.perform(post("/api/v1/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isCreated());

        LoginRequest login = new LoginRequest();
        login.setEmail(email);
        login.setPassword(PASSWORD);

        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn();

        return (MockHttpSession) loginResult.getRequest().getSession(false);
    }

    @Test
    void unauthenticatedRequestsReturn401() throws Exception {
        mockMvc.perform(get("/api/v1/trips"))
                .andExpect(status().isUnauthorized());

        CreateTripRequest create = new CreateTripRequest(
                "Goa Trip", "Mumbai", "Goa",
                LocalDate.now().plusDays(10), LocalDate.now().plusDays(14)
        );

        mockMvc.perform(post("/api/v1/trips")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(create)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createTripAndRetrieveWithDailyScaffolding() throws Exception {
        MockHttpSession sessionA = registerAndLogin("Alice", USER_A_EMAIL);
        assertNotNull(sessionA);

        LocalDate start = LocalDate.now().plusDays(5);
        LocalDate end = LocalDate.now().plusDays(7); // 3 days: start, start+1, start+2

        CreateTripRequest create = new CreateTripRequest("Goa Weekend Getaway", "Mumbai", "North Goa", start, end);
        create.setTravelerCount(2);
        create.setPlanMode(PlanMode.COMFORT);
        create.setTargetBudgetMinor(3500000L); // 35,000 INR
        create.setOwnVehicle(true);
        create.setAlreadyHaveStay(false);
        create.setPreferences("{\"pace\":\"RELAXED\"}");

        MvcResult createResult = mockMvc.perform(post("/api/v1/trips")
                        .session(sessionA)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(create)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("Goa Weekend Getaway"))
                .andExpect(jsonPath("$.data.status").value("DRAFT"))
                .andExpect(jsonPath("$.data.planMode").value("COMFORT"))
                .andExpect(jsonPath("$.data.ownVehicle").value(true))
                .andExpect(jsonPath("$.data.alreadyHaveStay").value(false))
                .andExpect(jsonPath("$.data.days", hasSize(3)))
                .andExpect(jsonPath("$.data.days[0].title").value("Day 1"))
                .andExpect(jsonPath("$.data.days[1].title").value("Day 2"))
                .andExpect(jsonPath("$.data.days[2].title").value("Day 3"))
                .andReturn();

        String tripId = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .path("data").path("id").asText();

        // Retrieve single trip
        mockMvc.perform(get("/api/v1/trips/" + tripId)
                        .session(sessionA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(tripId))
                .andExpect(jsonPath("$.data.days", hasSize(3)));

        // Retrieve user trips list
        mockMvc.perform(get("/api/v1/trips")
                        .session(sessionA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].id").value(tripId));
    }

    @Test
    void userIsolation_UserCannotAccessAnotherUsersTrip() throws Exception {
        MockHttpSession sessionA = registerAndLogin("Alice", USER_A_EMAIL);
        MockHttpSession sessionB = registerAndLogin("Bob", USER_B_EMAIL);

        // User A creates a trip
        CreateTripRequest create = new CreateTripRequest(
                "Alice Private Trip", "Delhi", "Manali",
                LocalDate.now().plusDays(20), LocalDate.now().plusDays(25)
        );

        MvcResult createResult = mockMvc.perform(post("/api/v1/trips")
                        .session(sessionA)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(create)))
                .andExpect(status().isCreated())
                .andReturn();

        String aliceTripId = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .path("data").path("id").asText();

        // User B attempts to access User A's trip -> MUST return 404 Not Found (tenant isolation)
        mockMvc.perform(get("/api/v1/trips/" + aliceTripId)
                        .session(sessionB))
                .andExpect(status().isNotFound());

        // User B attempts to update User A's trip -> MUST return 404
        UpdateTripRequest update = new UpdateTripRequest();
        update.setTitle("Hacked Title");
        mockMvc.perform(patch("/api/v1/trips/" + aliceTripId)
                        .session(sessionB)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isNotFound());

        // User B attempts to delete User A's trip -> MUST return 404
        mockMvc.perform(delete("/api/v1/trips/" + aliceTripId)
                        .session(sessionB)
                        .with(csrf()))
                .andExpect(status().isNotFound());

        // User B's list should be empty
        mockMvc.perform(get("/api/v1/trips")
                        .session(sessionB))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(0)));
    }

    @Test
    void updateTripAndPreferences_PersistsAndAllowsResuming() throws Exception {
        MockHttpSession sessionA = registerAndLogin("Alice", USER_A_EMAIL);

        LocalDate start = LocalDate.now().plusDays(10);
        LocalDate end = LocalDate.now().plusDays(12);

        CreateTripRequest create = new CreateTripRequest("Draft Trip", "Bengaluru", "Ooty", start, end);
        MvcResult createResult = mockMvc.perform(post("/api/v1/trips")
                        .session(sessionA)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(create)))
                .andExpect(status().isCreated())
                .andReturn();

        String tripId = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .path("data").path("id").asText();
        int initialVersion = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .path("data").path("version").asInt();

        // Update preferences
        TripPreferencesRequest prefReq = new TripPreferencesRequest();
        prefReq.setPlanMode(PlanMode.COMFORT);
        prefReq.setOwnVehicle(true);
        prefReq.setAlreadyHaveStay(true);
        prefReq.setTargetBudgetMinor(8000000L);

        mockMvc.perform(put("/api/v1/trips/" + tripId + "/preferences")
                        .session(sessionA)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(prefReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.ownVehicle").value(true))
                .andExpect(jsonPath("$.data.alreadyHaveStay").value(true));

        // Update title and status to PLANNED
        UpdateTripRequest updateReq = new UpdateTripRequest();
        updateReq.setTitle("Updated Ooty Vacation");
        updateReq.setStatus(TripStatus.PLANNED);

        mockMvc.perform(patch("/api/v1/trips/" + tripId)
                        .session(sessionA)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("Updated Ooty Vacation"))
                .andExpect(jsonPath("$.data.status").value("PLANNED"));

        // Verify resuming: fetch trip again
        mockMvc.perform(get("/api/v1/trips/" + tripId)
                        .session(sessionA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("Updated Ooty Vacation"))
                .andExpect(jsonPath("$.data.status").value("PLANNED"))
                .andExpect(jsonPath("$.data.ownVehicle").value(true))
                .andExpect(jsonPath("$.data.alreadyHaveStay").value(true));
    }

    @Test
    void optimisticConcurrencyConflictCheck() throws Exception {
        MockHttpSession sessionA = registerAndLogin("Alice", USER_A_EMAIL);

        CreateTripRequest create = new CreateTripRequest(
                "Versioned Trip", "Pune", "Mahabaleshwar",
                LocalDate.now().plusDays(15), LocalDate.now().plusDays(17)
        );
        MvcResult createResult = mockMvc.perform(post("/api/v1/trips")
                        .session(sessionA)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(create)))
                .andExpect(status().isCreated())
                .andReturn();

        String tripId = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .path("data").path("id").asText();

        // Send update with stale version
        UpdateTripRequest staleUpdate = new UpdateTripRequest();
        staleUpdate.setTitle("Stale Update");
        staleUpdate.setVersion(999); // Invalid version

        mockMvc.perform(patch("/api/v1/trips/" + tripId)
                        .session(sessionA)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(staleUpdate)))
                .andExpect(status().isConflict());
    }

    @Test
    void invalidDateRangeRejected() throws Exception {
        MockHttpSession sessionA = registerAndLogin("Alice", USER_A_EMAIL);

        CreateTripRequest invalidDates = new CreateTripRequest(
                "Backwards Trip", "Delhi", "Agra",
                LocalDate.now().plusDays(10), LocalDate.now().plusDays(5) // end before start!
        );

        mockMvc.perform(post("/api/v1/trips")
                        .session(sessionA)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDates)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteTripSucceeds() throws Exception {
        MockHttpSession sessionA = registerAndLogin("Alice", USER_A_EMAIL);

        CreateTripRequest create = new CreateTripRequest(
                "To Delete", "Jaipur", "Udaipur",
                LocalDate.now().plusDays(30), LocalDate.now().plusDays(32)
        );
        MvcResult createResult = mockMvc.perform(post("/api/v1/trips")
                        .session(sessionA)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(create)))
                .andExpect(status().isCreated())
                .andReturn();

        String tripId = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .path("data").path("id").asText();

        mockMvc.perform(delete("/api/v1/trips/" + tripId)
                        .session(sessionA)
                        .with(csrf()))
                .andExpect(status().isOk());

        // Verify it is gone
        mockMvc.perform(get("/api/v1/trips/" + tripId)
                        .session(sessionA))
                .andExpect(status().isNotFound());
    }
}
