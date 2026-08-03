package com.angolodivino.admin;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class AdminControllerTest {

    private static final String PASSWORD = "test-secret";

    /** Resolved eagerly: @DynamicPropertySource runs before JUnit would inject a @TempDir. */
    private static final Path DATA_DIRECTORY = createTempDataDirectory();

    @DynamicPropertySource
    static void adminProperties(DynamicPropertyRegistry registry) {
        registry.add("app.admin.password", () -> PASSWORD);
        registry.add("app.menu.data-directory", DATA_DIRECTORY::toString);
    }

    private static Path createTempDataDirectory() {
        try {
            return Files.createTempDirectory("admin-controller-test");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void rejectsMenuAccessWithoutToken() throws Exception {
        mockMvc.perform(get("/api/admin/menu/sections"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error", is("unauthorized")));
    }

    @Test
    void rejectsMenuAccessWithBogusToken() throws Exception {
        mockMvc.perform(get("/api/admin/menu/sections")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer not-a-real-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void rejectsWrongPassword() throws Exception {
        mockMvc.perform(post("/api/admin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"password\":\"wrong\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error", is("invalid_password")));
    }

    @Test
    void loginReturnsTokenThatUnlocksTheMenu() throws Exception {
        String token = login();

        mockMvc.perform(get("/api/admin/menu/sections")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is("aperitivo")));
    }

    @Test
    void patchPricesUpdatesThePublicMenu() throws Exception {
        String token = login();

        mockMvc.perform(patch("/api/admin/menu/prices")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"prices\":{\"negroni\":\"11 €\"}}"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/menu/sections"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[1].items[?(@.id == 'negroni')].price.options[0].amount", is(List.of(11))));
    }

    @Test
    void patchPricesRejectsUnknownItem() throws Exception {
        String token = login();

        mockMvc.perform(patch("/api/admin/menu/prices")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"prices\":{\"voce_inesistente\":\"11 €\"}}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("invalid_request")));
    }

    @Test
    void createsUpdatesAndDeletesAnItemWithANumericPrice() throws Exception {
        String token = login();
        String item = "{\"sectionId\":\"aperitivo\",\"name\":\"Piatto prova\",\"subtitle\":\"Specialità\",\"description\":\"Descrizione\",\"notes\":[\"Nota\"],\"price\":{\"options\":[{\"amount\":12.5}]}}";

        mockMvc.perform(post("/api/admin/menu/items").header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(item))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].items[?(@.name == 'Piatto prova')].price.options[0].amount", is(List.of(12.5))));

        mockMvc.perform(put("/api/admin/menu/items/piatto_prova").header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content(item.replace("12.5", "13")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].items[?(@.id == 'piatto_prova')].price.options[0].amount", is(List.of(13))));

        mockMvc.perform(delete("/api/admin/menu/items/piatto_prova").header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].items[?(@.id == 'piatto_prova')]").isEmpty());
    }

    @Test
    void putOneWinePreservesBothPricesAndDoesNotRewriteAnotherItem() throws Exception {
        String token = login();
        String item = "{\"sectionId\":\"vini\",\"name\":\"Tacco Barocco - Negroamaro Bianco Primitivo\","
                + "\"subtitle\":\"Bianchi\",\"description\":\"Aggiornato\",\"notes\":[],"
                + "\"price\":{\"options\":[{\"label\":\"glass\",\"amount\":5.5},"
                + "{\"label\":\"bottle\",\"amount\":23}]}}";

        mockMvc.perform(put("/api/admin/menu/items/tacco_barocco_bianco")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(item))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[2].items[?(@.id == 'tacco_barocco_bianco')].price.options[*].amount",
                        is(List.of(5.5, 23))))
                .andExpect(jsonPath("$[2].items[?(@.id == 'verdeca_salento')].price.options[*].amount",
                        is(List.of(5, 18))));
    }

    @Test
    void publicApiSerializesAbsentAndMultiplePricesWithoutCurrencyStrings() throws Exception {
        mockMvc.perform(get("/api/menu/sections"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[2].items[?(@.id == 'verdeca_salento')].price.options[*].amount",
                        is(List.of(5, 18))))
                .andExpect(jsonPath("$[4].items[?(@.id == 'armagnac')].price",
                        org.hamcrest.Matchers.contains(org.hamcrest.Matchers.nullValue())));
    }

    @Test
    void rejectsNegativeAndAmbiguousPrices() throws Exception {
        String token = login();
        String prefix = "{\"sectionId\":\"aperitivo\",\"name\":\"Non valido\",\"subtitle\":\"\","
                + "\"description\":\"\",\"notes\":[],\"price\":";

        mockMvc.perform(post("/api/admin/menu/items").header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(prefix + "{\"options\":[{\"amount\":-1}]}}"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/admin/menu/items").header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(prefix + "\"5 circa\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void logoutInvalidatesTheToken() throws Exception {
        String token = login();

        mockMvc.perform(post("/api/admin/logout")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/admin/menu/sections")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }

    private String login() throws Exception {
        String body = mockMvc.perform(post("/api/admin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"password\":\"" + PASSWORD + "\"}"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode json = objectMapper.readTree(body);
        return json.get("token").asText();
    }
}
