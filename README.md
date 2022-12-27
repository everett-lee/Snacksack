<h2> Snacksack App </h2>

App for the commonplace problem of finding the maximum pound-per-calorie menu 
options. Fine dining institutions Nandos and Greggs are currently supported.

<h3> Usage </h3>

Get the maximum calories for a money amount of £33.33 at Nandos:
```bash
    curl "https://snacksack-app.herokuapp.com/snacksack/nandos?money=33.33" | jq
```

Get JSON containing all location IDs for Greggs at:
```bash
    curl "https://snacksack-app.herokuapp.com/location/greggs" | jq
```

Get the maximum calories for a money amount of £33.33 at Greggs location with id '874':
```bash
    curl "https://snacksack-app.herokuapp.com/snacksack/greggs?locationId=874&money=33.33" | jq
```

<h3> Implementation </h3>

The app is deployed with Spring Boot running as a Docker image with an attached Redis cache. 

The app treats finding maximum calories as a version of the '0 1 knapsack problem' - so assumes 
each item is purchased only once - and uses the bottom up 'dynamic programming' approach to finding
an answer.

The money parameter (weight in the standard knapsack problem) is converted to pence, resulting
in many columns in the solution matrix for large values. A multithreaded approach to the 
problem is used in such cases.

The menu data is fetched from the restaurant API. This is converted into a normalised set of products
as input to the solver. To avoid re-fetching data these normamlised products are stored in the 
Redis cache. Answers for a given restaurant_locationId_money triplet are also stored in the cache.