
Bottom up
Threaded for large money param (in pence)

Redis used to cache the menu data fetched from restaurant sites

Get the maximum calories for a money amount of £33.33 at Nandos:
```bash
    curl "http://localhost:8080/snacksack/nandos?money=33.33" | jq
```

Get the location IDs for Greggs at:
```bash
    curl "https://host.com/location/greggs" | jq
```

Get the maximum calories for a money amount of £33.33 at Greggs location with id '874':
```bash
    curl "http://localhost:8080/snacksack/greggs?locationId=874&money=33.33" | jq
```