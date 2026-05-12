import webbrowser
from typing import Optional, List

from .client import SionnaVisualizerClient
from .exceptions import ValidationException, SdkException
from .models import SimulationResult, BerData, BeamData

_GLOBAL_API_KEY = None
_GLOBAL_BASE_URL = "https://sionna-visualizer-production-50ae.up.railway.app"

SUPPORTED_SIM_TYPES = {
    "AWGN_BER",
    "BEAM_PATTERN",
    "CHANNEL_CAPACITY",
    "CDL_TDL",
    "PATH_LOSS",
    "THZ_ATMOSPHERIC",
}


def init(
    api_key: str,
    base_url: str = "https://sionna-visualizer-production-50ae.up.railway.app",
):
    # Initializes module-level configuration and validates API key format.
    global _GLOBAL_API_KEY, _GLOBAL_BASE_URL
    if not api_key or not api_key.strip():
        raise ValidationException("API key is required. Call init(api_key='your-key').")
    _GLOBAL_API_KEY = api_key.strip()
    _GLOBAL_BASE_URL = (base_url or "").strip() or _GLOBAL_BASE_URL
    client = SionnaVisualizerClient(_GLOBAL_API_KEY, _GLOBAL_BASE_URL)
    if not client.validate_api_key():
        raise SdkException(
            "API key validation failed. Check your key at sionna-visualizer.vercel.app/api-docs"
        )
    print("Sionna Visualizer SDK initialized successfully.")


def track(
    simulation_result: dict,
    simulation_type: str = "AWGN_BER",
    title: str = None,
    tags: list = None,
    api_key: str = None,
    base_url: str = None,
    open_browser: bool = True,
) -> str:
    # Tracks a simulation result and returns a shareable dashboard URL.
    try:
        if not simulation_result:
            raise ValidationException("simulation_result cannot be empty.")

        detected_type = _detect_simulation_type(simulation_result)
        sim_type = simulation_type or detected_type or "AWGN_BER"
        if sim_type not in SUPPORTED_SIM_TYPES:
            raise ValidationException(
                "Unsupported simulation_type. Use one of: "
                + ", ".join(sorted(SUPPORTED_SIM_TYPES))
            )

        key = _resolve_api_key(api_key)
        url = (base_url or _GLOBAL_BASE_URL).rstrip("/")

        payload = _build_payload(
            simulation_result=simulation_result,
            simulation_type=sim_type,
            title=title,
            tags=tags,
        )
        payload["sdkVersion"] = "1.0.0"
        payload["sdkLanguage"] = "python"

        client = SionnaVisualizerClient(key, url)
        response = client.send_simulation(payload)
        share_url = response["shareableUrl"]

        print("Simulation tracked successfully.")
        print("Shareable URL:", share_url)

        if open_browser:
            webbrowser.open(share_url)
        return share_url
    except Exception as exc:
        # Ensures callers always receive a clear and actionable error.
        if isinstance(exc, SdkException):
            raise
        raise SdkException(f"Tracking failed: {exc}") from exc


def track_ber(
    snr_range: list,
    ber_values: list,
    theoretical_ber: list = None,
    modulation: str = "QPSK",
    frequency_ghz: float = 28.0,
    channel_model: str = "AWGN",
    title: str = None,
    tags: list = None,
    api_key: str = None,
) -> str:
    # Convenience function to track BER results with minimal boilerplate.
    if not snr_range or not ber_values:
        raise ValidationException("snr_range and ber_values are required for track_ber().")
    if len(snr_range) != len(ber_values):
        raise ValidationException("snr_range and ber_values must have the same length.")
    payload = {
        "berData": {
            "snrRangeDb": [float(v) for v in snr_range],
            "simulatedBer": [float(v) for v in ber_values],
            "theoreticalBer": [float(v) for v in theoretical_ber] if theoretical_ber else None,
            "modulation": modulation,
            "channelModel": channel_model,
            "frequencyGhz": float(frequency_ghz),
            "numAntennasTx": 1,
            "numAntennasRx": 1,
        }
    }
    return track(
        simulation_result=payload,
        simulation_type="AWGN_BER",
        title=title,
        tags=tags,
        api_key=api_key,
    )


def track_beam_pattern(
    angles_deg: list,
    pattern_db: list,
    num_antennas: int,
    frequency_ghz: float,
    title: str = None,
    api_key: str = None,
) -> str:
    # Convenience function to track beam pattern outputs.
    if not angles_deg or not pattern_db:
        raise ValidationException("angles_deg and pattern_db are required for track_beam_pattern().")
    if len(angles_deg) != len(pattern_db):
        raise ValidationException("angles_deg and pattern_db must have the same length.")
    payload = {
        "rawData": {
            "anglesDeg": [float(v) for v in angles_deg],
            "patternDb": [float(v) for v in pattern_db],
            "numAntennas": int(num_antennas),
            "frequencyGhz": float(frequency_ghz),
        }
    }
    return track(
        simulation_result=payload,
        simulation_type="BEAM_PATTERN",
        title=title,
        api_key=api_key,
    )


class SionnaVisualizer:
    # Class-style SDK wrapper for users who prefer object-based usage.
    def __init__(self, api_key: str, base_url: str = None):
        # Stores credentials and validates API key input.
        if not api_key or not api_key.strip():
            raise ValidationException("api_key is required for SionnaVisualizer().")
        self.api_key = api_key.strip()
        self.base_url = (base_url or _GLOBAL_BASE_URL).rstrip("/")

    def track(self, simulation_result: dict, **kwargs) -> str:
        # Proxies to module-level track() using instance credentials.
        return track(
            simulation_result=simulation_result,
            api_key=self.api_key,
            base_url=self.base_url,
            **kwargs,
        )

    def track_ber(self, snr_range: list, ber_values: list, **kwargs) -> str:
        # Proxies to module-level track_ber() using instance credentials.
        return track_ber(
            snr_range=snr_range,
            ber_values=ber_values,
            api_key=self.api_key,
            **kwargs,
        )


def _resolve_api_key(api_key: Optional[str]) -> str:
    # Resolves per-call key or falls back to initialized global key.
    key = (api_key or _GLOBAL_API_KEY or "").strip()
    if not key:
        raise ValidationException(
            "API key missing. Call init(api_key='your-key') or pass api_key to track()."
        )
    return key


def _detect_simulation_type(payload: dict) -> Optional[str]:
    # Guesses simulation type from payload shape when caller omits type.
    if isinstance(payload, dict):
        if "berData" in payload:
            return "AWGN_BER"
        if "beamData" in payload:
            return "BEAM_PATTERN"
    return None


def _build_payload(
    simulation_result: dict,
    simulation_type: str,
    title: Optional[str],
    tags: Optional[List[str]],
) -> dict:
    # Normalizes user payload into backend request contract.
    normalized_tags = [str(t) for t in (tags or [])]
    payload = {
        "simulationType": simulation_type,
        "title": title,
        "tags": normalized_tags,
    }
    if "berData" in simulation_result:
        payload["berData"] = simulation_result["berData"]
    elif "beamData" in simulation_result:
        payload["beamData"] = simulation_result["beamData"]
    else:
        payload["rawData"] = simulation_result
    return payload
