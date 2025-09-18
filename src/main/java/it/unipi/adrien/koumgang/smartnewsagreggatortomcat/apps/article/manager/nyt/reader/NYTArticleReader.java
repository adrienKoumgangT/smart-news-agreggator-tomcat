package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.article.manager.nyt.reader;

import com.google.gson.GsonBuilder;
import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.article.manager.base.reader.ArticleReader;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.article.manager.nyt.model.NYTArticle;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.log.MineLog;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class NYTArticleReader implements ArticleReader<NYTArticle> {


    public static NYTArticleReader getInstance() {
        return new NYTArticleReader();
    }


    public List<NYTArticle> readArticles(InputStream fileStream) {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter("[READER] [ARTICLE] [NYT] ");

        try (CSVReader reader = new CSVReader(new InputStreamReader(fileStream, StandardCharsets.UTF_8))) {

            HeaderColumnNameMappingStrategy<NYTArticle> strategy =
                    new HeaderColumnNameMappingStrategy<>();
            strategy.setType(NYTArticle.class);

            CsvToBean<NYTArticle> csvToBean = new CsvToBeanBuilder<NYTArticle>(reader)
                    .withMappingStrategy(strategy)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withIgnoreEmptyLine(true)
                    .withThrowExceptions(false)
                    // .withOrderedResults(true) // ensures in-order results; avoids parallel reordering
                    .build();

            List<NYTArticle> articles = csvToBean.parse();

            // Print summary of articles
            System.out.println("Successfully parsed " + articles.size() + " articles:");
            System.out.println("=" + "=".repeat(80));

            System.out.println("Example article:");
            if(!articles.isEmpty()) System.out.println(new GsonBuilder().serializeNulls().create().toJson(articles.getFirst()));
            System.out.println("=" + "=".repeat(80));


            /*for (NYTArticle article : articles) {
                System.out.printf("%s | %s | %s | %d words%n",
                        article.getPublicationDate(),
                        article.getMaterialType(),
                        article.getHeadline().substring(0, Math.min(40, article.getHeadline().length())),
                        article.getWordCount());
            }*/

            // Generate some statistics
            generateStatistics(articles);

            timePrinter.log();

            return articles;

        } catch (Exception e) {
            timePrinter.error(e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }

    private void generateStatistics(List<NYTArticle> articles) {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("ARTICLE STATISTICS:");
        System.out.println("=".repeat(80));

        // Total word count
        long totalWords = articles.stream()
                .mapToInt(NYTArticle::getWordCount)
                .sum();
        System.out.printf("Total words across all articles: %,d%n", totalWords);

        // Average word count
        double avgWords = articles.stream()
                .mapToInt(NYTArticle::getWordCount)
                .average()
                .orElse(0);
        System.out.printf("Average word count per article: %.1f%n", avgWords);

        // Count by material type
        System.out.println("\nArticles by type:");
        articles.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        NYTArticle::getMaterialType,
                        java.util.stream.Collectors.counting()))
                .forEach((type, count) ->
                        System.out.printf("  %-12s: %d articles%n", type, count));

        // Count by news desk
        System.out.println("\nArticles by news desk:");
        articles.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        NYTArticle::getNewsDesk,
                        java.util.stream.Collectors.counting()))
                .forEach((desk, count) ->
                        System.out.printf("  %-12s: %d articles%n", desk, count));
    }
}
