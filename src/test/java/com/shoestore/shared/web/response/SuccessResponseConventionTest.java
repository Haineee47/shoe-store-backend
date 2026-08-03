package com.shoestore.shared.web.response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SuccessResponseConventionTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new SuccessConventionController())
                .build();
    }

    @Test
    void shouldReturnDtoDirectlyForSingleResource()
            throws Exception {
        mockMvc.perform(get("/test/resources/resource-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value("resource-001"))
                .andExpect(jsonPath("$.name")
                        .value("Running Shoe"))
                .andExpect(jsonPath("$.data")
                        .doesNotExist())
                .andExpect(jsonPath("$.success")
                        .doesNotExist())
                .andExpect(jsonPath("$.message")
                        .doesNotExist());
    }

    @Test
    void shouldReturnCollectionDirectly()
            throws Exception {
        mockMvc.perform(get("/test/resources"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id")
                        .value("resource-001"))
                .andExpect(jsonPath("$[0].name")
                        .value("Running Shoe"))
                .andExpect(jsonPath("$[1].id")
                        .value("resource-002"))
                .andExpect(jsonPath("$[1].name")
                        .value("Casual Shoe"));
    }

    @Test
    void shouldReturnEmptyCollectionAsOk()
            throws Exception {
        mockMvc.perform(get("/test/resources/empty"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void shouldReturnCreatedResourceWithLocationHeader()
            throws Exception {
        mockMvc.perform(
                        post("/test/resources")
                                .contentType("application/json")
                                .content("""
                                        {
                                          "name": "Training Shoe"
                                        }
                                        """)
                )
                .andExpect(status().isCreated())
                .andExpect(header().string(
                        "Location",
                        "/test/resources/resource-003"
                ))
                .andExpect(jsonPath("$.id")
                        .value("resource-003"))
                .andExpect(jsonPath("$.name")
                        .value("Training Shoe"))
                .andExpect(jsonPath("$.data")
                        .doesNotExist());
    }

    @Test
    void shouldReturnNoContentForSuccessfulDelete()
            throws Exception {
        mockMvc.perform(
                        delete("/test/resources/resource-001")
                )
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
    }

    @RestController
    static class SuccessConventionController {

        @GetMapping("/test/resources/{id}")
        ResourceResponse findById(
                @PathVariable String id
        ) {
            return new ResourceResponse(
                    id,
                    "Running Shoe"
            );
        }

        @GetMapping("/test/resources")
        List<ResourceResponse> findAll() {
            return List.of(
                    new ResourceResponse(
                            "resource-001",
                            "Running Shoe"
                    ),
                    new ResourceResponse(
                            "resource-002",
                            "Casual Shoe"
                    )
            );
        }

        @GetMapping("/test/resources/empty")
        List<ResourceResponse> findEmptyCollection() {
            return List.of();
        }

        @PostMapping("/test/resources")
        ResponseEntity<ResourceResponse> create(
                @RequestBody CreateResourceRequest request
        ) {
            ResourceResponse createdResource =
                    new ResourceResponse(
                            "resource-003",
                            request.name()
                    );

            URI location = URI.create(
                    "/test/resources/" + createdResource.id()
            );

            return ResponseEntity
                    .created(location)
                    .body(createdResource);
        }

        @DeleteMapping("/test/resources/{id}")
        ResponseEntity<Void> deleteById(
                @PathVariable String id
        ) {
            return ResponseEntity.noContent().build();
        }
    }

    record CreateResourceRequest(
            String name
    ) {
    }

    record ResourceResponse(
            String id,
            String name
    ) {
    }
}
