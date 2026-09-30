package factoriaf5.team2.goxu.billing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import factoriaf5.team2.goxu.billing.dtos.BillingReportDTOResponse;
import factoriaf5.team2.goxu.billing.dtos.BillingReportFileDTOResponse;
import factoriaf5.team2.goxu.billing.dtos.InvoiceDTOResponse;

@ExtendWith(MockitoExtension.class)
class BillingControllerTest {

    @Mock
    private BillingService service;

    @InjectMocks
    private BillingController controller;

    private LocalDate startDate;
    private LocalDate endDate;

    @BeforeEach
    void setUp() {
        startDate = LocalDate.of(2026, 9, 1);
        endDate = LocalDate.of(2026, 9, 30);
    }

    @Test
    void getReport_shouldReturnReportFromService() {
        BillingReportDTOResponse response = mock(BillingReportDTOResponse.class);
        when(service.getReport(startDate, endDate)).thenReturn(response);

        ResponseEntity<BillingReportDTOResponse> result = controller.getReport(startDate, endDate);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(response);
        verify(service).getReport(startDate, endDate);
    }

    @Test
    void getReportPdf_shouldReturnPdfInfoFromService() {
        BillingReportFileDTOResponse response = mock(BillingReportFileDTOResponse.class);
        when(service.generateReportPdf(startDate, endDate)).thenReturn(response);

        ResponseEntity<BillingReportFileDTOResponse> result = controller.getReportPdf(startDate, endDate);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(response);
        verify(service).generateReportPdf(startDate, endDate);
    }

    @Test
    void getInvoice_shouldReturnInvoiceFromService() {
        InvoiceDTOResponse response = mock(InvoiceDTOResponse.class);
        when(service.getInvoice(1L)).thenReturn(response);

        ResponseEntity<InvoiceDTOResponse> result = controller.getInvoice(1L);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(response);
        verify(service).getInvoice(1L);
    }

}