package com.snacksack.snacksack.api;

import com.snacksack.snacksack.client.SpoonsClient;
import com.snacksack.snacksack.dp.Solver;
import com.snacksack.snacksack.model.NormalisedProduct;
import com.snacksack.snacksack.model.Restaurant;
import com.snacksack.snacksack.model.answer.Answer;
import com.snacksack.snacksack.model.spoons.SpoonsApiMenuData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@Slf4j
public class Controller {

    @Autowired
    private Solver bottomUpSolver;

    @Autowired
    private Solver bottomUpSolverThreaded;

    @Autowired
    private SpoonsClient spoonsClient;


    @GetMapping("/snacksack/{restaurant}/")
    public Answer snacksack(
            @PathVariable String restaurant,
            @RequestParam Optional<String> location,
            @RequestParam float money
    ) {
        final String selectedLocation = location.orElse("");
        final String selectedRestaurant = restaurant.toUpperCase();
        final int intMoney = (int) (money * 100);

        try {
            final Restaurant parsedRestaurant = Restaurant.valueOf(selectedRestaurant);
            switch (parsedRestaurant) {
                case SPOONS -> {
                    log.info("Solving for Spoons menu");
                    return this.handleSpoonsRequest(intMoney);
                }
                case NANDOS -> {
                    log.info("Solving for Nandos menu");
                }
            }

        } catch (IllegalArgumentException e) {
            log.error("Exception {}", e.getMessage());
        }
        return new Answer(0, List.of());
    }

    private Answer handleSpoonsRequest(int money) {
        final URI uri = spoonsClient.constructURI(43);
        final SpoonsApiMenuData menuResponse = spoonsClient.getMenuResponse(uri);
        final Set<NormalisedProduct> normalisedProducts = spoonsClient.getProducts(menuResponse);
        // check response size for threaded option
        return bottomUpSolver.solve(money, normalisedProducts);
    }
}
