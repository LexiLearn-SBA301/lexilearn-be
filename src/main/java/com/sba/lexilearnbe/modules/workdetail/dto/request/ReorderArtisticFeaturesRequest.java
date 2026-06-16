package com.sba.lexilearnbe.modules.workdetail.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class ReorderArtisticFeaturesRequest {

    @NotEmpty(message = "Danh sách đặc điểm nghệ thuật không được để trống")
    private List<@NotNull UUID> featureIds;
}