package com.snacksack.snacksack.requesthandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.snacksack.snacksack.dp.Solver;
import com.snacksack.snacksack.jedisclient.JedisClient;
import com.snacksack.snacksack.model.NormalisedProduct;
import com.snacksack.snacksack.model.answer.Answer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

@Slf4j
public class BaseRequestHandler {
    @Autowired
    protected Solver bottomUpSolver;

    @Autowired
    protected Solver bottomUpSolverThreaded;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected JedisClient jedisClient;

    protected Answer getAnswer(int moneyPence, int threadedThreshold, Set<NormalisedProduct> normalisedProducts) {
        if (moneyPence >= threadedThreshold) {
            log.info("Large money parameter, using threaded version");
            return bottomUpSolverThreaded.solve(moneyPence, normalisedProducts);
        } else {
            return bottomUpSolver.solve(moneyPence, normalisedProducts);
        }
    }

}
