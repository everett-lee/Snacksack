package com.snacksack.snacksack.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.snacksack.snacksack.api.exceptions.InvalidLocationException;
import com.snacksack.snacksack.api.exceptions.RestaurantNotFoundException;
import com.snacksack.snacksack.dp.Solver;
import com.snacksack.snacksack.model.Restaurant;
import com.snacksack.snacksack.model.answer.Answer;
import com.snacksack.snacksack.model.spoons.SpoonsLocation;
import com.snacksack.snacksack.requesthandler.NandosRequestHandler;
import com.snacksack.snacksack.requesthandler.SpoonsRequestHandler;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class Controller {
    @Value("classpath:spoons-locations.json")
    private Resource locationsFile;
    @Autowired
    private Solver bottomUpSolver;

    @Autowired
    private Solver bottomUpSolverThreaded;

    @Autowired
    private SpoonsRequestHandler spoonsRequestHandler;

    @Autowired
    private NandosRequestHandler nandosRequestHandler;

    @Autowired
    private ObjectMapper objectMapper;

    private List<SpoonsLocation> spoonsLocations;
    private Set<Integer> locationIDs;

    private static final int THREADED_THRESHOLD_MONEY_PENCE = 750_00;
    private static final int MONEY_MAX_VALUE_PENCE = 5_000_00; // £5k
    private static final double MONEY_MAX_VALUE_POUNDS = MONEY_MAX_VALUE_PENCE / 100.0;

    @GetMapping("/snacksack/{restaurant}/")
    public Answer snacksack(
            @PathVariable String restaurant,
            @RequestParam Optional<Integer> locationId,
            @RequestParam float money
    ) {
        final int selectedLocationId = locationId.orElse(-1);
        final String selectedRestaurant = restaurant.toUpperCase();
        final int moneyPence = (int) (money * 100);

        if (money >= MONEY_MAX_VALUE_POUNDS) {
            throw new InvalidLocationException(
                    String.format("Elon doesn't need this app, provide a value below %s", MONEY_MAX_VALUE_POUNDS)
            );
        }
        if (money < 0) {
            throw new InvalidLocationException(
                    "Provide a positive money value"
            );
        }

        try {
            final Restaurant parsedRestaurant = Restaurant.valueOf(selectedRestaurant);
            switch (parsedRestaurant) {
                case SPOONS -> {
                    log.info("Solving for Spoons menu");
                    return spoonsRequestHandler
                            .handleSpoonsRequest(moneyPence, selectedLocationId, THREADED_THRESHOLD_MONEY_PENCE);
                }
                case NANDOS -> {
                    log.info("Solving for Nandos menu");
                    return nandosRequestHandler
                            .handleNandosRequest(moneyPence, THREADED_THRESHOLD_MONEY_PENCE);
                }
                default -> {
                    log.info("Unrecognised restaurant");
                    return new Answer(0, List.of());
                }
            }

        } catch (IllegalArgumentException e) {
            throw new RestaurantNotFoundException(String.format("%s is not a recognised restaurant", restaurant));
        } catch (JsonProcessingException e) {
            log.error("Error processing JSON {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/location/spoons/")
    public List<SpoonsLocation> getPubs() {
        return spoonsLocations;
    }

    @PostConstruct
    public void initLocations() throws IOException {
        this.spoonsLocations = objectMapper
                .readValue(locationsFile.getFile(), new TypeReference<>() {
                });
        this.locationIDs = this.spoonsLocations.stream()
                .map(SpoonsLocation::id).collect(Collectors.toSet());
    }
}
