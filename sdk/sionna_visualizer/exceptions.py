class SdkException(Exception):
    # Base exception for all SDK errors
    pass


class InvalidApiKeyException(SdkException):
    # Raised when API key is invalid or missing
    # Message: "Invalid API key. Get yours at sionna-visualizer.vercel.app/api-docs"
    pass


class RateLimitException(SdkException):
    # Raised when rate limit exceeded
    # Message: "Rate limit exceeded. 100 requests per day per API key."
    pass


class ConnectionException(SdkException):
    # Raised when cannot reach API
    # Message: "Cannot connect to Sionna Visualizer. Check your internet connection."
    pass


class ValidationException(SdkException):
    # Raised when simulation data is invalid
    # Message: descriptive validation error
    pass
