package mjt.project.http.models;

import java.util.Map;

public record SymbolsResponse(boolean success, Map<String, String> symbols) {
}
