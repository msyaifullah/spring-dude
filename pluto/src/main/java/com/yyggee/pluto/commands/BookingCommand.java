package com.yyggee.pluto.commands;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.CountRequest;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.DeleteIndexResponse;
import co.elastic.clients.elasticsearch.indices.ExistsRequest;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.yyggee.pluto.model.PassengerDocument;
import com.yyggee.pluto.utils.NameGenerator;
import com.yyggee.pluto.utils.NameNormalizer;
import java.io.StringReader;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Component
@Command(
    name = "booking",
    mixinStandardHelpOptions = true,
    description = "Flight booking search with Asian name support",
    subcommands = {
      BookingCommand.SeedCommand.class,
      BookingCommand.SearchCommand.class,
      BookingCommand.DeleteCommand.class
    })
public class BookingCommand implements Runnable {

  @Override
  public void run() {
    System.out.println("Use a subcommand: seed, search, or delete");
    System.out.println("Run 'pluto booking --help' for more information");
  }

  private static final String INDEX_NAME = "booking-passengers";

  private static final String INDEX_CONFIG =
      """
      {
        "settings": {
          "number_of_shards": 1,
          "number_of_replicas": 0,
          "analysis": {
            "filter": {
              "name_stop": {
                "type": "stop",
                "stopwords": ["encik", "puan", "cik", "tuan", "mr", "mrs", "ms", "miss", "dr", "prof", "bin", "binti", "bt", "b", "a/l", "a/p", "ap", "haji", "hajah", "hj", "datuk", "dato", "tan sri", "tun"]
              },
              "name_shingle": {
                "type": "shingle",
                "min_shingle_size": 2,
                "max_shingle_size": 3
              }
            },
            "analyzer": {
              "name_analyzer": {
                "type": "custom",
                "tokenizer": "standard",
                "filter": ["lowercase", "asciifolding", "name_stop"]
              },
              "name_search_analyzer": {
                "type": "custom",
                "tokenizer": "standard",
                "filter": ["lowercase", "asciifolding", "name_stop", "name_shingle"]
              },
              "no_space_analyzer": {
                "type": "custom",
                "tokenizer": "keyword",
                "filter": ["lowercase"],
                "char_filter": ["space_remove"]
              }
            },
            "char_filter": {
              "space_remove": {
                "type": "pattern_replace",
                "pattern": "\\\\s+",
                "replacement": ""
              }
            }
          }
        },
        "mappings": {
          "properties": {
            "pnr": { "type": "keyword" },
            "full_name": {
              "type": "text",
              "analyzer": "name_analyzer",
              "search_analyzer": "name_search_analyzer",
              "fields": {
                "no_space": {
                  "type": "text",
                  "analyzer": "no_space_analyzer"
                },
                "keyword": {
                  "type": "keyword"
                }
              }
            },
            "first_name": { "type": "text", "analyzer": "name_analyzer" },
            "last_name": { "type": "text", "analyzer": "name_analyzer" },
            "email": { "type": "keyword" },
            "phone": { "type": "keyword" },
            "booking_date": { "type": "date" },
            "flight_number": { "type": "keyword" },
            "seat_number": { "type": "keyword" }
          }
        }
      }
      """;

  @Component
  @Command(
      name = "seed",
      mixinStandardHelpOptions = true,
      description = "Seed sample booking data with Asian names")
  public static class SeedCommand implements Callable<Integer> {

    @Value("${elasticsearch.host:localhost}")
    private String host;

    @Value("${elasticsearch.port:9200}")
    private int port;

    @Option(
        names = {"-c", "--count"},
        description = "Number of bookings to generate",
        defaultValue = "1000")
    private int count;

    @Option(
        names = {"-b", "--batch-size"},
        description = "Batch size for bulk indexing",
        defaultValue = "500")
    private int batchSize;

    @Option(
        names = {"--recreate"},
        description = "Recreate index (delete existing)")
    private boolean recreate;

    private final Random random = new Random();

    @Override
    public Integer call() {
      try (RestClient restClient = RestClient.builder(new HttpHost(host, port, "http")).build()) {
        ElasticsearchTransport transport =
            new RestClientTransport(restClient, new JacksonJsonpMapper());
        ElasticsearchClient client = new ElasticsearchClient(transport);

        System.out.println("Connecting to Elasticsearch at " + host + ":" + port);

        // Check if index exists
        boolean indexExists =
            client.indices().exists(ExistsRequest.of(e -> e.index(INDEX_NAME))).value();

        if (indexExists && recreate) {
          System.out.println("Deleting existing index: " + INDEX_NAME);
          client.indices().delete(d -> d.index(INDEX_NAME));
          indexExists = false;
        }

        // Create index if not exists
        if (!indexExists) {
          System.out.println("Creating index with custom analyzers: " + INDEX_NAME);
          client
              .indices()
              .create(
                  CreateIndexRequest.of(
                      c -> c.index(INDEX_NAME).withJson(new StringReader(INDEX_CONFIG))));
          System.out.println("Index created successfully");
        } else {
          System.out.println("Index already exists: " + INDEX_NAME);
        }

        // Seed bookings in batches
        System.out.println("\nSeeding " + count + " booking records...");
        int totalIndexed = 0;
        int batches = (count + batchSize - 1) / batchSize;

        for (int batch = 0; batch < batches; batch++) {
          BulkRequest.Builder bulkBuilder = new BulkRequest.Builder();
          int start = batch * batchSize;
          int end = Math.min(start + batchSize, count);

          for (int i = start; i < end; i++) {
            PassengerDocument passenger = createRandomPassenger(i + 1);
            String docId = passenger.getPnr() + "-" + UUID.randomUUID();
            bulkBuilder.operations(
                op -> op.index(idx -> idx.index(INDEX_NAME).id(docId).document(passenger)));
          }

          BulkResponse bulkResponse = client.bulk(bulkBuilder.build());

          if (bulkResponse.errors()) {
            System.err.println("Batch " + (batch + 1) + " had errors:");
            for (BulkResponseItem item : bulkResponse.items()) {
              if (item.error() != null) {
                System.err.println("  - " + item.error().reason());
              }
            }
          } else {
            totalIndexed += (end - start);
            System.out.println(
                "Batch "
                    + (batch + 1)
                    + "/"
                    + batches
                    + " completed ("
                    + totalIndexed
                    + "/"
                    + count
                    + " documents)");
          }
        }

        // Show stats
        System.out.println("\nSuccessfully seeded " + totalIndexed + " booking records");
        long docCount = client.count(CountRequest.of(c -> c.index(INDEX_NAME))).count();
        System.out.println("Total documents in index: " + docCount);

        printSamplePnrs();
        return 0;

      } catch (Exception e) {
        System.err.println("Error: " + e.getMessage());
        System.err.println("Make sure Elasticsearch is running at " + host + ":" + port);
        return 1;
      }
    }

    private PassengerDocument createRandomPassenger(int id) {
      String pnr = generatePnr();
      NameGenerator.NameType nameType = NameGenerator.getRandomType();
      String fullName = NameGenerator.generateName(nameType);
      String[] nameParts = fullName.split(" ", 2);
      String firstName = nameParts[0];
      String lastName = nameParts.length > 1 ? nameParts[1] : "";

      return PassengerDocument.builder()
          .pnr(pnr)
          .fullName(fullName)
          .firstName(firstName)
          .lastName(lastName)
          .email(generateEmail(fullName, id))
          .phone(generatePhone(nameType))
          .bookingDate(Instant.now().minus(random.nextInt(365), ChronoUnit.DAYS).toString())
          .flightNumber(generateFlightNumber())
          .seatNumber(generateSeatNumber())
          .build();
    }

    private String generatePnr() {
      String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
      StringBuilder pnr = new StringBuilder();
      for (int i = 0; i < 6; i++) {
        pnr.append(chars.charAt(random.nextInt(chars.length())));
      }
      return pnr.toString();
    }

    private String generateEmail(String fullName, int id) {
      String normalized =
          fullName
              .toLowerCase()
              .replaceAll("[^a-z0-9]", ".")
              .replaceAll("\\.+", ".")
              .replaceAll("^\\.|\\.$", "");
      String[] domains = {"gmail.com", "yahoo.com", "outlook.com", "airasia.com", "mail.com"};
      return normalized + id + "@" + domains[id % domains.length];
    }

    private String generatePhone(NameGenerator.NameType nameType) {
      return switch (nameType) {
        case CHINESE ->
            "+86" + (130 + random.nextInt(70)) + String.format("%08d", random.nextInt(100000000));
        case MALAYSIAN_MALAY, MALAYSIAN_CHINESE, MALAYSIAN_INDIAN ->
            "+60" + (10 + random.nextInt(9)) + String.format("%07d", random.nextInt(10000000));
        case USA ->
            "+1" + (200 + random.nextInt(800)) + String.format("%07d", random.nextInt(10000000));
        case INDONESIAN, INDONESIAN_JAVANESE ->
            "+62" + (81 + random.nextInt(9)) + String.format("%08d", random.nextInt(100000000));
      };
    }

    private String generateFlightNumber() {
      String[] carriers = {"AK", "D7", "QZ", "FD", "Z2"};
      return carriers[random.nextInt(carriers.length)] + (1000 + random.nextInt(9000));
    }

    private String generateSeatNumber() {
      int row = 1 + random.nextInt(40);
      char seat = (char) ('A' + random.nextInt(6));
      return row + "" + seat;
    }

    private void printSamplePnrs() {
      System.out.println("\nSample search commands:");
      System.out.println("  ./run-pluto booking search --pnr=<PNR> --name=\"<name>\"");
      System.out.println("\nName search supports:");
      System.out.println("  - Order-agnostic: \"Khoo Alex\" matches \"Alex Khoo Kah Yee\"");
      System.out.println("  - No-space: \"lethithanh\" matches \"Le Thi Thanh\"");
      System.out.println("  - Partial: \"Chen Alex\" matches \"Chen Kah Yee Alex\"");
      System.out.println("  - Fuzzy: \"Ahmadbin Ali\" matches \"Ahmad bin Ali\"");
    }
  }

  @Component
  @Command(
      name = "search",
      mixinStandardHelpOptions = true,
      description = "Search bookings by PNR and name")
  public static class SearchCommand implements Callable<Integer> {

    @Value("${elasticsearch.host:localhost}")
    private String host;

    @Value("${elasticsearch.port:9200}")
    private int port;

    @Option(
        names = {"-p", "--pnr"},
        description = "PNR (booking reference)",
        required = true)
    private String pnr;

    @Option(
        names = {"-n", "--name"},
        description = "Passenger name to search",
        required = true)
    private String name;

    @Option(
        names = {"-t", "--threshold"},
        description = "Similarity threshold (0.0-1.0)",
        defaultValue = "0.7")
    private double threshold;

    private final NameNormalizer nameNormalizer = new NameNormalizer();

    @Override
    public Integer call() {
      try (RestClient restClient = RestClient.builder(new HttpHost(host, port, "http")).build()) {
        ElasticsearchTransport transport =
            new RestClientTransport(restClient, new JacksonJsonpMapper());
        ElasticsearchClient client = new ElasticsearchClient(transport);

        System.out.println("Searching for:");
        System.out.println("  PNR: " + pnr);
        System.out.println("  Name (original): " + name);
        System.out.println("  Name (normalized): " + nameNormalizer.normalize(name));
        System.out.println("  Threshold: " + threshold);
        System.out.println();

        // Debug: Check index and sample data
        try {
          long docCount = client.count(CountRequest.of(c -> c.index(INDEX_NAME))).count();
          System.out.println(
              "DEBUG: Index '" + INDEX_NAME + "' contains " + docCount + " document(s)");

          // Get some sample PNRs
          SearchRequest sampleRequest =
              SearchRequest.of(s -> s.index(INDEX_NAME).query(q -> q.matchAll(m -> m)).size(10));
          SearchResponse<PassengerDocument> sampleResponse =
              client.search(sampleRequest, PassengerDocument.class);
          if (!sampleResponse.hits().hits().isEmpty()) {
            System.out.println("DEBUG: Sample PNRs in index:");
            for (Hit<PassengerDocument> hit : sampleResponse.hits().hits()) {
              PassengerDocument doc = hit.source();
              if (doc != null) {
                System.out.println(
                    "  - PNR: '"
                        + doc.getPnr()
                        + "' (length: "
                        + doc.getPnr().length()
                        + "), Name: "
                        + doc.getFullName());
              }
            }
          }
        } catch (Exception e) {
          System.out.println("DEBUG: Error checking index: " + e.getMessage());
        }
        System.out.println();

        // Debug: Check PNR matches with different cases
        String pnrUpper = pnr.toUpperCase();
        String pnrLower = pnr.toLowerCase();
        String pnrOriginal = pnr;
        String pnrToUse = pnrUpper; // Default to uppercase

        for (String pnrToTry : new String[] {pnrUpper, pnrLower, pnrOriginal}) {
          SearchRequest pnrOnlyRequest =
              SearchRequest.of(
                  s ->
                      s.index(INDEX_NAME)
                          .query(q -> q.term(t -> t.field("pnr").value(pnrToTry)))
                          .size(5));
          SearchResponse<PassengerDocument> pnrResponse =
              client.search(pnrOnlyRequest, PassengerDocument.class);
          if (!pnrResponse.hits().hits().isEmpty()) {
            System.out.println(
                "DEBUG: Found "
                    + pnrResponse.hits().hits().size()
                    + " document(s) with PNR: '"
                    + pnrToTry
                    + "'");
            for (Hit<PassengerDocument> hit : pnrResponse.hits().hits()) {
              PassengerDocument doc = hit.source();
              if (doc != null) {
                System.out.println("  - PNR: '" + doc.getPnr() + "', Name: " + doc.getFullName());
              }
            }
            pnrToUse = pnrToTry; // Use the case that worked
            break;
          }
        }

        // Debug: Check if name exists (without PNR filter)
        System.out.println("DEBUG: Searching for name '" + name + "' without PNR filter...");
        Query nameQueryDebug = buildNameQuery(name);
        SearchRequest nameOnlyRequest =
            SearchRequest.of(s -> s.index(INDEX_NAME).query(nameQueryDebug).size(10));
        SearchResponse<PassengerDocument> nameResponse =
            client.search(nameOnlyRequest, PassengerDocument.class);
        System.out.println(
            "DEBUG: Found "
                + nameResponse.hits().hits().size()
                + " document(s) with name matching '"
                + name
                + "'");
        if (!nameResponse.hits().hits().isEmpty()) {
          System.out.println("DEBUG: Sample name matches:");
          for (Hit<PassengerDocument> hit : nameResponse.hits().hits()) {
            PassengerDocument doc = hit.source();
            if (doc != null) {
              System.out.println(
                  "  - PNR: '" + doc.getPnr() + "', Name: '" + doc.getFullName() + "'");
            }
          }
        }
        System.out.println();

        // Build multi-strategy query
        Query nameQuery = buildNameQuery(name);

        // Execute search
        final String finalPnr = pnrToUse;
        SearchRequest searchRequest =
            SearchRequest.of(
                s ->
                    s.index(INDEX_NAME)
                        .query(
                            q ->
                                q.bool(
                                    b ->
                                        b.must(m -> m.term(t -> t.field("pnr").value(finalPnr)))
                                            .must(nameQuery)))
                        .size(10));

        SearchResponse<PassengerDocument> response =
            client.search(searchRequest, PassengerDocument.class);

        System.out.println(
            "DEBUG: Combined query (PNR + Name) returned "
                + response.hits().hits().size()
                + " result(s)");
        if (!response.hits().hits().isEmpty()) {
          System.out.println("DEBUG: Raw results before threshold filtering:");
          for (Hit<PassengerDocument> hit : response.hits().hits()) {
            PassengerDocument doc = hit.source();
            if (doc != null) {
              double esScore = hit.score() != null ? hit.score() : 0.0;
              double tokenSimilarity = nameNormalizer.calculateSimilarity(name, doc.getFullName());
              System.out.println(
                  "  - Name: "
                      + doc.getFullName()
                      + ", ES Score: "
                      + String.format("%.2f", esScore)
                      + ", Token Similarity: "
                      + String.format("%.2f", tokenSimilarity));
            }
          }
          System.out.println();
        }
        System.out.println();

        // Display results
        displayResults(response);
        return 0;

      } catch (Exception e) {
        System.err.println("Error: " + e.getMessage());
        e.printStackTrace();
        return 1;
      }
    }

    private Query buildNameQuery(String fullName) {
      String normalizedName = nameNormalizer.normalize(fullName);
      String noSpaceName = nameNormalizer.removeSpaces(fullName);
      List<String> tokens = nameNormalizer.tokenize(fullName);

      return Query.of(
          q ->
              q.bool(
                  b -> {
                    BoolQuery.Builder builder = new BoolQuery.Builder();

                    // Strategy 1: Multi-match on tokens (order-agnostic)
                    builder.should(
                        s ->
                            s.multiMatch(
                                m ->
                                    m.fields("full_name", "full_name.no_space")
                                        .query(normalizedName)
                                        .type(TextQueryType.BestFields)
                                        .boost(3.0f)));

                    // Strategy 2: No-space match
                    builder.should(
                        s ->
                            s.match(
                                m -> m.field("full_name.no_space").query(noSpaceName).boost(2.5f)));

                    // Strategy 3: Individual token matching (middle name omission)
                    for (String token : tokens) {
                      builder.should(
                          s -> s.match(m -> m.field("full_name").query(token).boost(1.5f)));
                    }

                    // Strategy 4: Fuzzy match
                    builder.should(
                        s ->
                            s.match(
                                m ->
                                    m.field("full_name")
                                        .query(normalizedName)
                                        .fuzziness("AUTO")
                                        .boost(1.0f)));

                    builder.minimumShouldMatch("1");
                    return builder;
                  }));
    }

    private void displayResults(SearchResponse<PassengerDocument> response) {
      List<Hit<PassengerDocument>> hits = response.hits().hits();

      if (hits.isEmpty()) {
        System.out.println("No matches found for PNR: " + pnr);
        System.out.println();
        System.out.println(
            "NOTE: The search requires BOTH PNR and name to match. "
                + "If the PNR doesn't exist in the index, no results will be returned "
                + "even if the name exists.");
        return;
      }

      System.out.println("Found " + hits.size() + " match(es):\n");

      int rank = 1;
      for (Hit<PassengerDocument> hit : hits) {
        PassengerDocument doc = hit.source();
        if (doc == null) continue;

        double esScore = hit.score() != null ? hit.score() : 0.0;
        double normalizedScore = esScore / 10.0;

        // Calculate custom similarity
        double tokenSimilarity = nameNormalizer.calculateSimilarity(name, doc.getFullName());
        double finalScore = Math.max(normalizedScore, tokenSimilarity);

        // Filter by threshold
        if (finalScore < threshold) {
          continue;
        }

        // Determine match reason
        String matchReason = determineMatchReason(doc);

        System.out.println("  " + rank + ". Match Score: " + String.format("%.2f", finalScore));
        System.out.println("     PNR: " + doc.getPnr());
        System.out.println("     Full Name: " + doc.getFullName());
        System.out.println("     Email: " + doc.getEmail());
        System.out.println("     Phone: " + doc.getPhone());
        System.out.println(
            "     Flight: " + doc.getFlightNumber() + " (Seat: " + doc.getSeatNumber() + ")");
        System.out.println("     Match Reason: " + matchReason);
        System.out.println("     ES Score: " + String.format("%.2f", esScore));
        System.out.println("     Token Similarity: " + String.format("%.2f", tokenSimilarity));
        System.out.println();

        rank++;
      }

      if (rank == 1) {
        System.out.println("No matches above similarity threshold (" + threshold + ")");
      }
    }

    private String determineMatchReason(PassengerDocument doc) {
      String normalized = nameNormalizer.normalize(name);
      String docNormalized = nameNormalizer.normalize(doc.getFullName());

      if (normalized.equals(docNormalized)) {
        return "EXACT_MATCH";
      }

      String noSpace = nameNormalizer.removeSpaces(name);
      String docNoSpace = nameNormalizer.removeSpaces(doc.getFullName());
      if (noSpace.equals(docNoSpace)) {
        return "NO_SPACE_MATCH";
      }

      Set<String> searchTokens = new HashSet<>(nameNormalizer.tokenize(name));
      Set<String> docTokens = new HashSet<>(nameNormalizer.tokenize(doc.getFullName()));

      if (searchTokens.equals(docTokens)) {
        return "ORDER_AGNOSTIC_MATCH";
      }

      return "PARTIAL_MATCH";
    }
  }

  @Component
  @Command(name = "delete", mixinStandardHelpOptions = true, description = "Delete booking index")
  public static class DeleteCommand implements Callable<Integer> {

    @Value("${elasticsearch.host:localhost}")
    private String host;

    @Value("${elasticsearch.port:9200}")
    private int port;

    @Option(
        names = {"-f", "--force"},
        description = "Force delete without confirmation")
    private boolean force;

    @Override
    public Integer call() {
      if (!force) {
        System.out.println("Are you sure you want to delete index '" + INDEX_NAME + "'?");
        System.out.println("Use --force to confirm deletion");
        return 1;
      }

      try (RestClient restClient = RestClient.builder(new HttpHost(host, port, "http")).build()) {
        ElasticsearchTransport transport =
            new RestClientTransport(restClient, new JacksonJsonpMapper());
        ElasticsearchClient client = new ElasticsearchClient(transport);

        DeleteIndexResponse response = client.indices().delete(d -> d.index(INDEX_NAME));
        if (response.acknowledged()) {
          System.out.println("Successfully deleted index: " + INDEX_NAME);
          return 0;
        } else {
          System.err.println("Failed to delete index: " + INDEX_NAME);
          return 1;
        }

      } catch (Exception e) {
        System.err.println("Error: " + e.getMessage());
        return 1;
      }
    }
  }
}
