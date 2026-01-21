package com.yyggee.pluto.commands;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import co.elastic.clients.elasticsearch.indices.DeleteIndexResponse;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Component
@Command(
    name = "elastic",
    mixinStandardHelpOptions = true,
    description = "Elasticsearch operations - seed, index, and manage data",
    subcommands = {
      ElasticCommand.SeedCommand.class,
      ElasticCommand.IndexCommand.class,
      ElasticCommand.DeleteCommand.class,
      ElasticCommand.StatusCommand.class
    })
public class ElasticCommand implements Runnable {

  @Override
  public void run() {
    System.out.println("Use a subcommand: seed, index, delete, or status");
    System.out.println("Run 'pluto elastic --help' for more information");
  }

  @Component
  @Command(
      name = "seed",
      mixinStandardHelpOptions = true,
      description =
          "Seed random names from different cultures (Chinese, Malaysian, USA, Indonesian)")
  public static class SeedCommand implements Callable<Integer> {

    @Value("${elasticsearch.host:localhost}")
    private String host;

    @Value("${elasticsearch.port:9200}")
    private int port;

    @Option(
        names = {"-i", "--index"},
        description = "Index name",
        defaultValue = "people")
    private String indexName;

    @Option(
        names = {"-c", "--count"},
        description = "Number of names to generate",
        defaultValue = "1000")
    private int count;

    @Option(
        names = {"-b", "--batch-size"},
        description = "Batch size for bulk indexing",
        defaultValue = "500")
    private int batchSize;

    @Override
    public Integer call() {
      try (RestClient restClient = RestClient.builder(new HttpHost(host, port, "http")).build()) {
        ElasticsearchTransport transport =
            new RestClientTransport(restClient, new JacksonJsonpMapper());
        ElasticsearchClient client = new ElasticsearchClient(transport);

        System.out.println("Connecting to Elasticsearch at " + host + ":" + port);

        // Create index if not exists
        boolean indexExists = client.indices().exists(e -> e.index(indexName)).value();
        if (!indexExists) {
          client.indices().create(c -> c.index(indexName));
          System.out.println("Created index: " + indexName);
        }

        // Seed names in batches
        int totalIndexed = 0;
        int batches = (count + batchSize - 1) / batchSize;

        for (int batch = 0; batch < batches; batch++) {
          BulkRequest.Builder bulkBuilder = new BulkRequest.Builder();
          int start = batch * batchSize;
          int end = Math.min(start + batchSize, count);

          for (int i = start; i < end; i++) {
            Map<String, Object> doc = createPersonDocument(i + 1);
            final int docId = i + 1;
            bulkBuilder.operations(
                op ->
                    op.index(idx -> idx.index(indexName).id(String.valueOf(docId)).document(doc)));
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

        System.out.println(
            "\nSuccessfully seeded " + totalIndexed + " names to index '" + indexName + "'");
        printSummary();
        return 0;

      } catch (Exception e) {
        System.err.println("Error: " + e.getMessage());
        System.err.println("Make sure Elasticsearch is running at " + host + ":" + port);
        return 1;
      }
    }

    private Map<String, Object> createPersonDocument(int id) {
      com.yyggee.pluto.utils.NameGenerator.NameType nameType =
          com.yyggee.pluto.utils.NameGenerator.getRandomType();
      String fullName = com.yyggee.pluto.utils.NameGenerator.generateName(nameType);

      Map<String, Object> person = new HashMap<>();
      person.put("id", id);
      person.put("fullName", fullName);
      person.put("nameType", nameType.name());
      person.put("country", getCountryFromNameType(nameType));
      person.put("email", generateEmail(fullName, id));
      person.put("age", 18 + new java.util.Random().nextInt(60));
      person.put("createdAt", java.time.Instant.now().toString());
      return person;
    }

    private String getCountryFromNameType(com.yyggee.pluto.utils.NameGenerator.NameType nameType) {
      return switch (nameType) {
        case CHINESE -> "China";
        case MALAYSIAN_MALAY, MALAYSIAN_CHINESE, MALAYSIAN_INDIAN -> "Malaysia";
        case USA -> "United States";
        case INDONESIAN, INDONESIAN_JAVANESE -> "Indonesia";
      };
    }

    private String generateEmail(String fullName, int id) {
      String normalized =
          fullName
              .toLowerCase()
              .replaceAll("[^a-z0-9]", ".")
              .replaceAll("\\.+", ".")
              .replaceAll("^\\.|\\.$", "");
      String[] domains = {"gmail.com", "yahoo.com", "outlook.com", "email.com", "mail.com"};
      return normalized + id + "@" + domains[id % domains.length];
    }

    private void printSummary() {
      System.out.println("\nName types included:");
      System.out.println("  - CHINESE: Chinese 2-3 character names (e.g., Wang Wei, Li Mingfeng)");
      System.out.println(
          "  - MALAYSIAN_MALAY: Malay names with bin/binti (e.g., Ahmad bin Abdullah)");
      System.out.println("  - MALAYSIAN_CHINESE: Malaysian Chinese names (e.g., Tan Wei Ming)");
      System.out.println(
          "  - MALAYSIAN_INDIAN: Malaysian Indian names with a/l or a/p (e.g., Rajesh a/l Kumar)");
      System.out.println("  - USA: American names (e.g., John Smith, Emily R. Johnson)");
      System.out.println("  - INDONESIAN: Indonesian names (e.g., Budi Santoso, Dewi)");
      System.out.println("  - INDONESIAN_JAVANESE: Javanese single names (e.g., Suharto)");
    }
  }

  @Component
  @Command(name = "index", mixinStandardHelpOptions = true, description = "Index a single document")
  public static class IndexCommand implements Callable<Integer> {

    @Value("${elasticsearch.host:localhost}")
    private String host;

    @Value("${elasticsearch.port:9200}")
    private int port;

    @Option(
        names = {"-i", "--index"},
        description = "Index name",
        required = true)
    private String indexName;

    @Option(
        names = {"--id"},
        description = "Document ID")
    private String docId;

    @Option(
        names = {"-d", "--data"},
        description = "JSON data to index",
        required = true)
    private String jsonData;

    @Override
    public Integer call() {
      try (RestClient restClient = RestClient.builder(new HttpHost(host, port, "http")).build()) {
        ElasticsearchTransport transport =
            new RestClientTransport(restClient, new JacksonJsonpMapper());
        ElasticsearchClient client = new ElasticsearchClient(transport);

        com.fasterxml.jackson.databind.ObjectMapper mapper =
            new com.fasterxml.jackson.databind.ObjectMapper();
        @SuppressWarnings("unchecked")
        Map<String, Object> document = mapper.readValue(jsonData, Map.class);

        IndexResponse response;
        if (docId != null) {
          response = client.index(i -> i.index(indexName).id(docId).document(document));
        } else {
          response = client.index(i -> i.index(indexName).document(document));
        }

        System.out.println("Indexed document with ID: " + response.id());
        System.out.println("Result: " + response.result().jsonValue());
        return 0;

      } catch (Exception e) {
        System.err.println("Error: " + e.getMessage());
        return 1;
      }
    }
  }

  @Component
  @Command(name = "delete", mixinStandardHelpOptions = true, description = "Delete an index")
  public static class DeleteCommand implements Callable<Integer> {

    @Value("${elasticsearch.host:localhost}")
    private String host;

    @Value("${elasticsearch.port:9200}")
    private int port;

    @Option(
        names = {"-i", "--index"},
        description = "Index name to delete",
        required = true)
    private String indexName;

    @Option(
        names = {"-f", "--force"},
        description = "Force delete without confirmation")
    private boolean force;

    @Override
    public Integer call() {
      if (!force) {
        System.out.println("Are you sure you want to delete index '" + indexName + "'?");
        System.out.println("Use --force to confirm deletion");
        return 1;
      }

      try (RestClient restClient = RestClient.builder(new HttpHost(host, port, "http")).build()) {
        ElasticsearchTransport transport =
            new RestClientTransport(restClient, new JacksonJsonpMapper());
        ElasticsearchClient client = new ElasticsearchClient(transport);

        DeleteIndexResponse response = client.indices().delete(d -> d.index(indexName));
        if (response.acknowledged()) {
          System.out.println("Successfully deleted index: " + indexName);
          return 0;
        } else {
          System.err.println("Failed to delete index: " + indexName);
          return 1;
        }

      } catch (Exception e) {
        System.err.println("Error: " + e.getMessage());
        return 1;
      }
    }
  }

  @Component
  @Command(
      name = "status",
      mixinStandardHelpOptions = true,
      description = "Check Elasticsearch cluster status")
  public static class StatusCommand implements Callable<Integer> {

    @Value("${elasticsearch.host:localhost}")
    private String host;

    @Value("${elasticsearch.port:9200}")
    private int port;

    @Override
    public Integer call() {
      try (RestClient restClient = RestClient.builder(new HttpHost(host, port, "http")).build()) {
        ElasticsearchTransport transport =
            new RestClientTransport(restClient, new JacksonJsonpMapper());
        ElasticsearchClient client = new ElasticsearchClient(transport);

        var health = client.cluster().health();
        var info = client.info();

        System.out.println("Elasticsearch Cluster Status");
        System.out.println("============================");
        System.out.println("Cluster name: " + health.clusterName());
        System.out.println("Status: " + health.status().jsonValue());
        System.out.println("Number of nodes: " + health.numberOfNodes());
        System.out.println("Active shards: " + health.activeShards());
        System.out.println("Version: " + info.version().number());
        return 0;

      } catch (Exception e) {
        System.err.println("Error connecting to Elasticsearch: " + e.getMessage());
        System.err.println("Make sure Elasticsearch is running at " + host + ":" + port);
        return 1;
      }
    }
  }
}
