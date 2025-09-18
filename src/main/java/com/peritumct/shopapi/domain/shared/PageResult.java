package com.peritumct.shopapi.domain.shared;

import java.util.List;

public record PageResult<T>(List<T> items, long totalElements, int page, int size) {
}
