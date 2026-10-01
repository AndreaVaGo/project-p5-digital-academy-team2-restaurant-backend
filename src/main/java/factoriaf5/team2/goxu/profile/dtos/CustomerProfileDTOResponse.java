package factoriaf5.team2.goxu.profile.dtos;

import java.math.BigDecimal;
import java.util.List;

import factoriaf5.team2.goxu.dashboard.dtos.TopProductDTO;
import factoriaf5.team2.goxu.orders.dtos.OrderDTOResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CustomerProfileDTOResponse {

    private Long userId;
    private String name;
    private String email;

    private String surname;
    private String phone;
    private String address;
    private String postalCode;
    private String city;
    private String avatar;

    private long totalOrders;
    private BigDecimal totalSpent;
    private List<TopProductDTO> favoriteProducts;
    private List<OrderDTOResponse> recentOrders;
}
