package com.codesoom.sejongdeveloper.application;

import com.codesoom.sejongdeveloper.domain.Item;
import com.codesoom.sejongdeveloper.domain.ObtainOrder;
import com.codesoom.sejongdeveloper.domain.ObtainOrderDetail;
import com.codesoom.sejongdeveloper.dto.ItemResponse;
import com.codesoom.sejongdeveloper.dto.ObtainOrderDetailResponse;
import com.codesoom.sejongdeveloper.dto.ObtainOrderResponse;
import com.codesoom.sejongdeveloper.errors.ObtainOrderDetailNotFoundException;
import com.codesoom.sejongdeveloper.repository.ObtainOrderDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ObtainOrderDetailService {

    private final ObtainOrderDetailRepository obtainOrderDetailRepository;

    @Transactional
    public void createObtainOrderDetails(List<ObtainOrderDetail> obtainOrderDetails) {
        obtainOrderDetailRepository.saveAll(obtainOrderDetails);
    }

    @Transactional
    public void updateObtainOrderDetails(List<ObtainOrderDetail> obtainOrderDetails) {
        obtainOrderDetails.forEach(source -> getObtainOrderDetail(source.getId())
                .update(
                        source.getItem(),
                        source.getQuantity()
                )
        );
    }

    private ObtainOrderDetail getObtainOrderDetail(Long id) {
        return obtainOrderDetailRepository.findById(id)
                .orElseThrow(() -> new ObtainOrderDetailNotFoundException(id));
    }

    public List<ObtainOrderDetailResponse> getObtainOrderDetails(Long obtainOrderId) {
        return obtainOrderDetailRepository.findAllByObtainOrderId(obtainOrderId).stream()
                .map(source -> ObtainOrderDetailResponse.builder()
                        .id(source.getId())
                        .item(getItemResponse(source.getItem()))
                        .quantity(source.getQuantity())
                        .build())
                .collect(Collectors.toList());
    }

    private ItemResponse getItemResponse(Item source) {
        return ItemResponse.builder()
                .id(source.getId())
                .code(source.getCode())
                .name(source.getName())
                .quantity(source.getQuantity())
                .build();
    }

    private ObtainOrderResponse getObtainOrderResponse(ObtainOrder source) {
        return ObtainOrderResponse.builder()
                .id(source.getId())
                .name(source.getName())
                .date(source.getDate())
                .build();
    }
}
