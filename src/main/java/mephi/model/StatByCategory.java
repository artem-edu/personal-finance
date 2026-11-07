package mephi.model;

import java.util.List;

public record StatByCategory(Category category, Stat stat, List<Transaction> transactions) {
}
