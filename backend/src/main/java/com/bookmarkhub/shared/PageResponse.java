package com.bookmarkhub.shared;

import java.util.List;

public record PageResponse<T>(List<T> items) {
}
