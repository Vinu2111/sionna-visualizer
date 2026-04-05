package com.sionnavisualizer.service;

import com.sionnavisualizer.dto.*;
import com.sionnavisualizer.model.ThzScenario;
import com.sionnavisualizer.repository.ThzScenarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ThzService {

    @Autowired
    private ThzScenarioRepository scenarioRepository;

    @Value("${python-bridge.url}")
    private String pythonBridgeUrl;

    private WebClient webClient;

    /**
     * THz OVERVIEW (for Java Developers):
     * THz (Terahertz) refers to extremely high-frequency radio waves (100 GHz to 10 THz).
     * Unlike typical Wi-Fi or even 5G mmWave, THz frequencies are so high that they 
     * interact directly with the molecules in the air. 
     * 
     * Why THz is special compared to mmWave:
     * mmWave (e.g., 28 GHz) passes through air mostly fine but struggles with walls. 
     * THz is so high-frequency that even the air itself (specifically humidity/water and oxygen) 
     * absorbs the signal aggressively. This fundamentally restrains THz to short-range 
     * "micro-links" (under 100 meters).
     *
     * ITU-R P.676 is the global standard model defining how atmospheric gases absorb signal power.
     * ITU-R P.838 is the standard model defining how rain scatters and absorbs signal power.
     *
     * In this application, we accept environmental sliders via UI, validate them, 
     * and send them to the Python bridge where Numpy computes these mathematically intense models.
     */
    public ThzService() {
        this.webClient = WebClient.builder().build();
    }

    /**
     * Validates THz atmospheric inputs to ensure the physics simulation does not receive 
     * mathematically invalid values, which could cause the python bridge to crash.
     * Passes the data seamlessly via POST request to FastAPI where NumPy performs the 
     * heavy calculations. 
     * 
     * There are three primary forces of signal loss we track:
     * 1. Free Space Path Loss (FSPL): Natural geometric spreading of the wave as it travels.
     * 2. Molecular Absorption: Energy lost directly into heating up H2O and O2 molecules.
     * 3. Rain Attenuation: Energy scattered violently by physical water droplets in the air.
     */
    public ThzResponse calculate(ThzRequest request, Long userId) {
        // Step 1: Validate ranges
        requireRange(request.getHumidity_percent(), 0, 100, "Humidity");
        requireRange(request.getTemperature_celsius(), -20, 50, "Temperature");
        requireRange(request.getPressure_hpa(), 800, 1100, "Pressure");
        requireRange(request.getRain_rate_mm_per_hr(), 0, 150, "Rain Rate");
        requireRange(request.getLink_distance_meters(), 1, 1000, "Link Distance");
        requireRange(request.getFrequency_ghz(), 50, 1000, "Frequency");
        requireRange(request.getTx_power_dbm(), 0, 30, "TX Power");

        // Step 2: Send directly to the fast API bridge
        // pythonBridgeUrl usually points to /simulate/demo. So we strip the path and use the host.
        String bridgeBaseUrl = pythonBridgeUrl.substring(0, pythonBridgeUrl.indexOf("/", 8));

        return webClient.post()
                .uri(bridgeBaseUrl + "/calculate/thz-atmospheric")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ThzResponse.class)
                .block();
    }

    /**
     * Saves a snapshot of atmospheric sliders as a distinct scenario that can be used later.
     */
    public ThzScenarioResponse saveScenario(SaveScenarioRequest request, Long userId) {
        ThzScenario scenario = new ThzScenario();
        scenario.setUserId(userId);
        scenario.setName(request.getName());
        
        ThzRequest params = request.getParams();
        scenario.setFrequencyGhz(params.getFrequency_ghz());
        scenario.setHumidityPercent(params.getHumidity_percent());
        scenario.setTemperatureCelsius(params.getTemperature_celsius());
        scenario.setPressureHpa(params.getPressure_hpa());
        scenario.setRainRateMmPerHr(params.getRain_rate_mm_per_hr());
        scenario.setLinkDistanceMeters(params.getLink_distance_meters());
        scenario.setTxPowerDbm(params.getTx_power_dbm());

        ThzScenario saved = scenarioRepository.save(scenario);
        return mapToDto(saved);
    }

    /**
     * Returns a list of previously configured atmospheric scenarios.
     */
    public List<ThzScenarioResponse> getUserScenarios(Long userId) {
        return scenarioRepository.findByUserId(userId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public void deleteScenario(Long id, Long userId) {
        scenarioRepository.findById(id).ifPresent(scenario -> {
            if (scenario.getUserId().equals(userId)) {
                scenarioRepository.delete(scenario);
            }
        });
    }

    private ThzScenarioResponse mapToDto(ThzScenario scenario) {
        ThzScenarioResponse dto = new ThzScenarioResponse();
        dto.setScenarioId(scenario.getId());
        dto.setName(scenario.getName());
        dto.setCreatedAt(scenario.getCreatedAt().toString());

        ThzRequest params = new ThzRequest();
        params.setFrequency_ghz(scenario.getFrequencyGhz());
        params.setHumidity_percent(scenario.getHumidityPercent());
        params.setTemperature_celsius(scenario.getTemperatureCelsius());
        params.setPressure_hpa(scenario.getPressureHpa());
        params.setRain_rate_mm_per_hr(scenario.getRainRateMmPerHr());
        params.setLink_distance_meters(scenario.getLinkDistanceMeters());
        params.setTx_power_dbm(scenario.getTxPowerDbm());
        dto.setParams(params);

        return dto;
    }

    private void requireRange(Double value, double min, double max, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
        if (value < min || value > max) {
            throw new IllegalArgumentException(fieldName + " must be between " + min + " and " + max + ".");
        }
    }
}
