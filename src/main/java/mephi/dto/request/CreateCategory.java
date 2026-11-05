package mephi.dto.request;

import java.util.UUID;

public record CreateCategory(String name, UUID userId, boolean isDepositOnly) {
}
