package factoriaf5.team2.goxu.storage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import com.sun.net.httpserver.HttpServer;

class SupabaseStorageServiceTest {

    private HttpServer server;
    private int port;

    @BeforeEach
    void setUp() throws IOException {
        server = HttpServer.create(new InetSocketAddress(0), 0);
        port = server.getAddress().getPort();
    }

    @AfterEach
    void tearDown() {
        server.stop(0);
    }

    @Test
    void uploadFile_shouldReturnPublicUrl_whenUploadSucceeds() throws IOException {
        server.createContext("/storage/v1/object/informes-ventas/report.pdf", exchange -> {
            exchange.sendResponseHeaders(200, -1);
            exchange.close();
        });
        server.start();

        SupabaseStorageService service = new SupabaseStorageService(
                "http://localhost:" + port, "fake-key", "informes-ventas");

        String result = service.uploadFile("report.pdf", "contenido".getBytes(), "application/pdf");

        assertThat(result).isEqualTo(
                "http://localhost:" + port + "/storage/v1/object/public/informes-ventas/report.pdf");
    }

    @Test
    void uploadFile_shouldThrow_whenSupabaseRespondsWithError() throws IOException {
        server.createContext("/storage/v1/object/informes-ventas/report.pdf", exchange -> {
            exchange.sendResponseHeaders(403, -1);
            exchange.close();
        });
        server.start();

        SupabaseStorageService service = new SupabaseStorageService(
                "http://localhost:" + port, "fake-key", "informes-ventas");

        assertThatThrownBy(() -> service.uploadFile("report.pdf", "contenido".getBytes(), "application/pdf"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Error al subir el archivo a Supabase Storage");
    }

}