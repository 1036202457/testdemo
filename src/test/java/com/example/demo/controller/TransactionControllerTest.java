package com.example.demo.controller;

import com.example.demo.records.Transaction;
import com.example.demo.records.TransactionCreateRequest;
import com.example.demo.records.TransactionDeleteRequest;
import com.example.demo.records.TransactionUpdateRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;


@SpringBootTest
@AutoConfigureMockMvc
class TransactionControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;

    @Test
    void shouldCreateTransaction() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/api/transactions?page=0&size=10").contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(0));

        TransactionCreateRequest request = new TransactionCreateRequest("A01", BigDecimal.valueOf(100), "Deposit");
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/transactions").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.amount").value(100))
                .andReturn();
        String version = mapper.readValue(mvcResult.getResponse().getContentAsString(),Transaction.class).version();
        request = new TransactionCreateRequest("A01", BigDecimal.valueOf(100), "repeat");
        mockMvc.perform(MockMvcRequestBuilders.post("/api/transactions").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("REQUEST_REPEAT"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Id repeated"));

        TransactionUpdateRequest update = new TransactionUpdateRequest("A01", "1234", BigDecimal.valueOf(100), "Invalid");
        mockMvc.perform(MockMvcRequestBuilders.put("/api/transactions").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(update)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("VERSION_EXPIRED"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Version must be latest"));
        update = new TransactionUpdateRequest("A01", version, BigDecimal.valueOf(100), "Invalid");
        mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/api/transactions").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(update)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        version = mapper.readValue(mvcResult.getResponse().getContentAsString(),Transaction.class).version();


        mockMvc.perform(MockMvcRequestBuilders.get("/api/transactions?page=0&size=10").contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1));

        TransactionDeleteRequest delete = new TransactionDeleteRequest("A01", "1234");
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/transactions").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(delete)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("VERSION_EXPIRED"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Version must be latest"));

        delete = new TransactionDeleteRequest("A01", version);
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/transactions").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(delete)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void validateTest() throws Exception {
        TransactionCreateRequest create = new TransactionCreateRequest("A01", null, "Invalid");
        mockMvc.perform(MockMvcRequestBuilders.post("/api/transactions").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(create)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Amount must not null"));
        create = new TransactionCreateRequest("A01", BigDecimal.valueOf(-10), "Invalid");
        mockMvc.perform(MockMvcRequestBuilders.post("/api/transactions").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(create)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Amount must be positive"));
        create = new TransactionCreateRequest("A01", BigDecimal.valueOf(100), "");
        mockMvc.perform(MockMvcRequestBuilders.post("/api/transactions").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(create)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Desc must not blank"));
        create = new TransactionCreateRequest("", BigDecimal.valueOf(100), "invalid");
        mockMvc.perform(MockMvcRequestBuilders.post("/api/transactions").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(create)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("id must not blank"));

        TransactionUpdateRequest update = new TransactionUpdateRequest("A01", "1234", BigDecimal.valueOf(-10), "Invalid");
        mockMvc.perform(MockMvcRequestBuilders.put("/api/transactions").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(update)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Amount must be positive"));
        update = new TransactionUpdateRequest("A01", "1234", null, "Invalid");
        mockMvc.perform(MockMvcRequestBuilders.put("/api/transactions").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(update)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Amount must not null"));
        update = new TransactionUpdateRequest("A01", "1234", BigDecimal.valueOf(100), "");
        mockMvc.perform(MockMvcRequestBuilders.put("/api/transactions").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(update)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Desc must not blank"));
        update = new TransactionUpdateRequest("", "1234", BigDecimal.valueOf(100), "Invalid");
        mockMvc.perform(MockMvcRequestBuilders.put("/api/transactions").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(update)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("id must not blank"));
        update = new TransactionUpdateRequest("A01", "", BigDecimal.valueOf(100), "Invalid");
        mockMvc.perform(MockMvcRequestBuilders.put("/api/transactions").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(update)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("version must not blank"));
        update = new TransactionUpdateRequest("A01", "1111", BigDecimal.valueOf(100), "Invalid");
        mockMvc.perform(MockMvcRequestBuilders.put("/api/transactions").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(update)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("RESOURCE_NOT_FOUND"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Element must exists"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/transactions?page=1&size=10").contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(0));

        TransactionDeleteRequest delete = new TransactionDeleteRequest("A01", "1111");
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/transactions").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(delete)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("RESOURCE_NOT_FOUND"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Element must exists"));

    }



}
