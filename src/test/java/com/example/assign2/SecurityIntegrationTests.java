package com.example.assign2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@SpringBootTest
public class SecurityIntegrationTests {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @BeforeEach
    void setup() {
        this.mvc = webAppContextSetup(context).apply(springSecurity()).build();
    }

    @Test
    void unauthenticatedGetsUnauthorized() throws Exception {
        mvc.perform(get("/api/depts")).andExpect(status().isUnauthorized());
    }

    @Test
    void studentCanReadDepts() throws Exception {
        String token = loginAndGetToken("alice", "password");
        mvc.perform(get("/api/depts")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void studentCannotCreateDept() throws Exception {
        String token = loginAndGetToken("alice", "password");
        String json = "{\"name\":\"Physics\"}";
        mvc.perform(post("/api/depts")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isForbidden());
    }

    @Test
    void teacherCanCreateDept() throws Exception {
        String token = loginAndGetToken("bob", "password");
        String json = "{\"name\":\"Mathematics\"}";
        mvc.perform(post("/api/depts")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }

    private String loginAndGetToken(String username, String password) throws Exception {
        String body = "{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}";
        String response = mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        int tokenIdx = response.indexOf("\"token\":\"");
        if (tokenIdx < 0) {
            throw new IllegalStateException("Token not found in response: " + response);
        }
        int start = tokenIdx + "\"token\":\"".length();
        int end = response.indexOf('"', start);
        return response.substring(start, end);
    }
}
