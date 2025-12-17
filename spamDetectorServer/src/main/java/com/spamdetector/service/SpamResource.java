package com.spamdetector.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spamdetector.domain.TestFile;
import com.spamdetector.util.SpamDetector;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

import java.io.File;
import java.util.List;

@Path("/spam")
public class SpamResource {

    private SpamDetector detector;
    private ObjectMapper objectMapper;
    private List<TestFile> results;

    public SpamResource(){
        this.detector = new SpamDetector();
        this.objectMapper = new ObjectMapper();
        this.results = this.trainAndTest();
    }

    @GET
    @Produces("application/json")
    public Response getSpamResults() {
        try {
            String resultsJson = objectMapper.writeValueAsString(results);
            return Response.ok(resultsJson)
                    .header("Access-Control-Allow-Origin", "http://localhost:63342")
                    .build();
        } catch (JsonProcessingException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @GET
    @Produces("application/json")
    @Path("/accuracy")
    public Response getAccuracy() {
        int numTruePositives = detector.truePositives(results);
        int numTrueNegatives = detector.trueNegatives(results);
        int numFiles = detector.numberOfFiles(results);

        double accuracy = (double)(numTruePositives + numTrueNegatives) / (double)numFiles;
        return Response.ok("{\"val\":" + accuracy + "}")
                .header("Access-Control-Allow-Origin", "http://localhost:63342")
                .build();
    }

    @GET
    @Produces("application/json")
    @Path("/precision")
    public Response getPrecision() {
        int numTruePositives =  detector.truePositives(results);
        int numFalsePositives = detector.falsePositives(results);
        double precision = (double)numTruePositives / (double)(numTruePositives + numFalsePositives);
        return Response.ok("{\"val\":" + precision + "}")
                .header("Access-Control-Allow-Origin", "http://localhost:63342")
                .build();
    }

    private List<TestFile> trainAndTest() {
        detector.TrainAndTest();
        return detector.testNewEmails();
    }
}