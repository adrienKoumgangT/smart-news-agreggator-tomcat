package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.article.manager.nyt.reader;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.article.manager.nyt.model.NYTComment;

import java.io.FileReader;
import java.io.Reader;
import java.util.List;
import java.util.stream.Collectors;

public class NYTCommentReader {

    public static void main(String[] args) {
        String csvFile = "nyt_comments.csv";
        List<NYTComment> comments = readComments(csvFile);

        if (!comments.isEmpty()) {
            analyzeComments(comments);
            findTopCommenters(comments);
            findMostDiscussedArticles(comments);
        }
    }

    public static List<NYTComment> readComments(String filename) {
        try (Reader reader = new FileReader(filename)) {

            HeaderColumnNameMappingStrategy<NYTComment> strategy =
                    new HeaderColumnNameMappingStrategy<>();
            strategy.setType(NYTComment.class);

            CsvToBean<NYTComment> csvToBean = new CsvToBeanBuilder<NYTComment>(reader)
                    .withMappingStrategy(strategy)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withIgnoreEmptyLine(true)
                    .withThrowExceptions(false)
                    .build();

            List<NYTComment> comments = csvToBean.parse();

            System.out.println("Successfully parsed " + comments.size() + " comments");
            return comments;

        } catch (Exception e) {
            System.err.println("Error reading CSV: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }

    private static void analyzeComments(List<NYTComment> comments) {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("COMMENT ANALYSIS:");
        System.out.println("=".repeat(80));

        // Basic statistics
        long totalComments = comments.size();
        long editorSelections = comments.stream()
                .filter(comment -> Boolean.TRUE.equals(comment.getEditorsSelection()))
                .count();

        double totalReplies = comments.stream()
                .mapToDouble(NYTComment::getReplyCount)
                .sum();

        double totalRecommendations = comments.stream()
                .mapToDouble(NYTComment::getRecommendations)
                .sum();

        System.out.printf("Total comments: %,d%n", totalComments);
        System.out.printf("Editor's selections: %,d (%.1f%%)%n",
                editorSelections, (editorSelections * 100.0 / totalComments));
        System.out.printf("Total replies: %,d%n", (long) totalReplies);
        System.out.printf("Total recommendations: %,d%n", (long) totalRecommendations);
        System.out.printf("Average recommendations per comment: %.1f%n",
                totalRecommendations / totalComments);

        // Comments by status
        System.out.println("\nComments by status:");
        comments.stream()
                .collect(Collectors.groupingBy(
                        NYTComment::getStatus,
                        Collectors.counting()))
                .forEach((status, count) ->
                        System.out.printf("  %-10s: %,d comments%n", status, count));
    }

    private static void findTopCommenters(List<NYTComment> comments) {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("TOP COMMENTERS:");
        System.out.println("=".repeat(80));

        comments.stream()
                .filter(comment -> comment.getUserDisplayName() != null && !comment.getUserDisplayName().isEmpty())
                .collect(Collectors.groupingBy(
                        NYTComment::getUserDisplayName,
                        Collectors.summingDouble(comment ->
                                comment.getRecommendations() != null ? comment.getRecommendations() : 0)))
                .entrySet().stream()
                .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
                .limit(10)
                .forEach(entry ->
                        System.out.printf("  %-20s: %.0f recommendations%n",
                                entry.getKey(), entry.getValue()));
    }

    private static void findMostDiscussedArticles(List<NYTComment> comments) {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("MOST DISCUSSED ARTICLES:");
        System.out.println("=".repeat(80));

        comments.stream()
                .collect(Collectors.groupingBy(
                        NYTComment::getArticleId,
                        Collectors.counting()))
                .entrySet().stream()
                .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
                .limit(5)
                .forEach(entry ->
                        System.out.printf("  Article %s: %,d comments%n",
                                entry.getKey(), entry.getValue()));
    }
}
