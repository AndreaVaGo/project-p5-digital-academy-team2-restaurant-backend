package factoriaf5.team2.goxu.orders;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import factoriaf5.team2.goxu.orders.dtos.OrderDTORequest;
import factoriaf5.team2.goxu.orders.dtos.OrderDTOResponse;
import factoriaf5.team2.goxu.orders.dtos.OrderStatusHistoryDTOResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping(path = "${api-endpoint}/orders")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @GetMapping("")
    public ResponseEntity<List<OrderDTOResponse>> getOrders(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) Long userId) {

        if (status != null) {
            return ResponseEntity.ok(service.getByStatus(status));
        }

        if (userId != null) {
            return ResponseEntity.ok(service.getByUser(userId));
        }

        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTOResponse> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/{id}/tracking")
    public ResponseEntity<List<OrderStatusHistoryDTOResponse>> getTracking(@PathVariable Long id) {
        return ResponseEntity.ok(service.getTracking(id));
    }

    @PostMapping("")
    public ResponseEntity<OrderDTOResponse> createOrder(@Valid @RequestBody OrderDTORequest dto) {
        OrderDTOResponse response = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<OrderDTOResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status) {

        OrderDTOResponse response = service.updateStatus(id, status);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/pay")
    public ResponseEntity<OrderDTOResponse> markAsPaid(@PathVariable Long id) {
        OrderDTOResponse response = service.markAsPaid(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

}