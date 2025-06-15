package mjt.project.http.models;

import java.util.Map;

public record LatestRatesResponse(boolean success, long timestamp, String base, String date,
                                  Map<String, Double> rates) {
}
