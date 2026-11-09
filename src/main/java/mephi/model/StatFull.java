package mephi.model;

import java.util.List;

public record StatFull(List<StatFullByCategory> stats, Stat total) {
}
