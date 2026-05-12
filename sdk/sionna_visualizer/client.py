import requests

from .exceptions import (
    ConnectionException,
    InvalidApiKeyException,
    RateLimitException,
    SdkException,
)


class SionnaVisualizerClient:
    DEFAULT_BASE_URL = "https://sionna-backend.onrender.com"
    SDK_ENDPOINT = "/api/sdk/track"
    VALIDATE_ENDPOINT = "/api/sdk/validate"
    TIMEOUT_SECONDS = 30

    def __init__(self, api_key: str, base_url: str = None):
        # Store API key and base URL for all SDK network calls.
        self.api_key = (api_key or "").strip()
        self.base_url = (base_url or self.DEFAULT_BASE_URL).rstrip("/")

    def send_simulation(self, payload: dict) -> dict:
        # Sends simulation payload to tracking endpoint and returns parsed JSON response.
        url = self.base_url + self.SDK_ENDPOINT
        headers = self._build_headers()
        try:
            response = requests.post(url, json=payload, headers=headers, timeout=self.TIMEOUT_SECONDS)
        except requests.Timeout as exc:
            raise ConnectionException(
                "Cannot connect to Sionna Visualizer. Check your internet connection."
            ) from exc
        except requests.RequestException as exc:
            raise ConnectionException(
                "Cannot connect to Sionna Visualizer. Check your internet connection."
            ) from exc

        if response.status_code == 401:
            raise InvalidApiKeyException(
                "Invalid API key. Get yours at sionna-visualizer.vercel.app/api-docs"
            )
        if response.status_code == 429:
            raise RateLimitException(
                "Rate limit exceeded. 100 requests per day per API key."
            )
        if response.status_code >= 400:
            detail = _extract_error_message(response)
            raise SdkException(f"Sionna Visualizer API error ({response.status_code}): {detail}")

        try:
            data = response.json()
        except ValueError as exc:
            raise SdkException("Sionna Visualizer API returned invalid JSON response.") from exc

        if "shareableUrl" not in data:
            raise SdkException("Sionna Visualizer API response did not include shareableUrl.")
        return data

    def validate_api_key(self) -> bool:
        # Checks whether API key is valid via dedicated validation endpoint.
        url = self.base_url + self.VALIDATE_ENDPOINT
        headers = self._build_headers()
        try:
            response = requests.get(url, headers=headers, timeout=self.TIMEOUT_SECONDS)
        except requests.RequestException:
            return False
        return response.status_code == 200

    def _build_headers(self) -> dict:
        # Builds standard SDK headers used for all requests.
        return {
            "Content-Type": "application/json",
            "X-API-Key": self.api_key,
            "X-SDK-Version": "1.0.0",
            "X-SDK-Language": "python",
        }


def _extract_error_message(response: requests.Response) -> str:
    # Attempts to parse backend error payload for better user-facing messages.
    try:
        body = response.json()
        if isinstance(body, dict) and "error" in body:
            return str(body["error"])
    except ValueError:
        pass
    return response.text or "Unknown error"
