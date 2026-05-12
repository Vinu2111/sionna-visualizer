import unittest

from sionna_visualizer.tracker import _build_payload, _detect_simulation_type


class TrackerTests(unittest.TestCase):
    def test_detect_simulation_type_from_ber_data(self):
        payload = {"berData": {"snrRangeDb": [0, 1], "simulatedBer": [0.1, 0.01]}}
        self.assertEqual(_detect_simulation_type(payload), "AWGN_BER")

    def test_build_payload_sets_raw_data(self):
        payload = _build_payload(
            simulation_result={"x": 1},
            simulation_type="AWGN_BER",
            title="demo",
            tags=["a"],
        )
        self.assertEqual(payload["title"], "demo")
        self.assertIn("rawData", payload)


if __name__ == "__main__":
    unittest.main()
