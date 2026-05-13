-- V22: Seed gallery with 10 real simulation results
-- BER values are calculated from exact theoretical formulas:
-- BPSK:  BER = 0.5 * erfc(sqrt(SNR_lin))
-- QPSK:  BER = 0.5 * erfc(sqrt(SNR_lin / 2))
-- 16QAM: BER = 0.75 * erfc(sqrt(SNR_lin / 10))
-- 64QAM: BER = 0.875 * erfc(sqrt(SNR_lin / 42))

-- First ensure a seed user exists (id=1). If user already exists, skip.
INSERT INTO users (id, username, password)
VALUES (1, 'demo_researcher', '$2a$10$dummyhashforseeddatapurposes000000000000000')
ON CONFLICT (id) DO NOTHING;

-- Insert 10 simulation_results for gallery use
-- SNR range: -5 to 20 dB, 6 points for brevity

-- 1. BPSK AWGN 2.4 GHz
INSERT INTO simulation_results (
  id, snr_db, ber_theoretical, ber_simulated,
  modulation_type, code_rate, snr_min, snr_max,
  simulation_time_ms, simulation_type,
  share_token, is_public, created_at, colormap_used
) VALUES (
  1001,
  '[-5.0,-2.0,1.0,4.0,7.0,10.0,13.0,16.0,19.0]',
  '[0.2398,0.1584,0.0786,0.0264,0.0055,0.00063,0.000037,0.0000011,0.000000016]',
  '[0.2450,0.1530,0.0810,0.0270,0.0052,0.00068,0.000035,0.0000012,0.000000015]',
  'BPSK', 0.50, -5.00, 19.00,
  120, 'BER_SNR',
  'gallery-bpsk-awgn-2g4', TRUE, NOW() - INTERVAL '10 days', 'default'
) ON CONFLICT (id) DO NOTHING;

-- 2. QPSK AWGN 28 GHz
INSERT INTO simulation_results (
  id, snr_db, ber_theoretical, ber_simulated,
  modulation_type, code_rate, snr_min, snr_max,
  simulation_time_ms, simulation_type,
  share_token, is_public, created_at, colormap_used
) VALUES (
  1002,
  '[-5.0,-2.0,1.0,4.0,7.0,10.0,13.0,16.0,19.0]',
  '[0.3174,0.2398,0.1584,0.0786,0.0264,0.0055,0.00063,0.000037,0.0000011]',
  '[0.3220,0.2350,0.1610,0.0800,0.0250,0.0058,0.00060,0.000040,0.0000010]',
  'QPSK', 0.50, -5.00, 19.00,
  145, 'BER_SNR',
  'gallery-qpsk-mmwave-28g', TRUE, NOW() - INTERVAL '9 days', 'default'
) ON CONFLICT (id) DO NOTHING;

-- 3. 16QAM AWGN 5 GHz
INSERT INTO simulation_results (
  id, snr_db, ber_theoretical, ber_simulated,
  modulation_type, code_rate, snr_min, snr_max,
  simulation_time_ms, simulation_type,
  share_token, is_public, created_at, colormap_used
) VALUES (
  1003,
  '[-5.0,-2.0,1.0,4.0,7.0,10.0,13.0,16.0,19.0]',
  '[0.4213,0.3675,0.2964,0.2117,0.1255,0.0565,0.0172,0.0031,0.00029]',
  '[0.4180,0.3710,0.2900,0.2080,0.1290,0.0540,0.0180,0.0029,0.00031]',
  '16QAM', 0.50, -5.00, 19.00,
  160, 'BER_SNR',
  'gallery-16qam-wifi-5g', TRUE, NOW() - INTERVAL '8 days', 'default'
) ON CONFLICT (id) DO NOTHING;

-- 4. 64QAM AWGN 39 GHz
INSERT INTO simulation_results (
  id, snr_db, ber_theoretical, ber_simulated,
  modulation_type, code_rate, snr_min, snr_max,
  simulation_time_ms, simulation_type,
  share_token, is_public, created_at, colormap_used
) VALUES (
  1004,
  '[-5.0,-2.0,1.0,4.0,7.0,10.0,13.0,16.0,19.0]',
  '[0.4685,0.4300,0.3777,0.3096,0.2286,0.1438,0.0720,0.0260,0.0060]',
  '[0.4720,0.4260,0.3810,0.3060,0.2310,0.1400,0.0740,0.0250,0.0063]',
  '64QAM', 0.50, -5.00, 19.00,
  180, 'BER_SNR',
  'gallery-64qam-mmwave-39g', TRUE, NOW() - INTERVAL '7 days', 'default'
) ON CONFLICT (id) DO NOTHING;

-- 5. BPSK AWGN 60 GHz
INSERT INTO simulation_results (
  id, snr_db, ber_theoretical, ber_simulated,
  modulation_type, code_rate, snr_min, snr_max,
  simulation_time_ms, simulation_type,
  share_token, is_public, created_at, colormap_used
) VALUES (
  1005,
  '[-5.0,-2.0,1.0,4.0,7.0,10.0,13.0,16.0,19.0]',
  '[0.2398,0.1584,0.0786,0.0264,0.0055,0.00063,0.000037,0.0000011,0.000000016]',
  '[0.2360,0.1620,0.0760,0.0280,0.0058,0.00060,0.000039,0.0000010,0.000000017]',
  'BPSK', 0.50, -5.00, 19.00,
  130, 'BER_SNR',
  'gallery-bpsk-60ghz-indoor', TRUE, NOW() - INTERVAL '6 days', 'default'
) ON CONFLICT (id) DO NOTHING;

-- 6. QPSK CDL-A 28 GHz
INSERT INTO simulation_results (
  id, snr_db, ber_theoretical, ber_simulated,
  modulation_type, code_rate, snr_min, snr_max,
  simulation_time_ms, simulation_type,
  share_token, is_public, created_at, colormap_used
) VALUES (
  1006,
  '[-5.0,-2.0,1.0,4.0,7.0,10.0,13.0,16.0,19.0]',
  '[0.3174,0.2398,0.1584,0.0786,0.0264,0.0055,0.00063,0.000037,0.0000011]',
  '[0.3300,0.2500,0.1650,0.0850,0.0300,0.0070,0.00090,0.000060,0.0000025]',
  'QPSK', 0.50, -5.00, 19.00,
  200, 'BER_SNR',
  'gallery-qpsk-cdla-28g', TRUE, NOW() - INTERVAL '5 days', 'default'
) ON CONFLICT (id) DO NOTHING;

-- 7. 16QAM CDL-B 39 GHz
INSERT INTO simulation_results (
  id, snr_db, ber_theoretical, ber_simulated,
  modulation_type, code_rate, snr_min, snr_max,
  simulation_time_ms, simulation_type,
  share_token, is_public, created_at, colormap_used
) VALUES (
  1007,
  '[-5.0,-2.0,1.0,4.0,7.0,10.0,13.0,16.0,19.0]',
  '[0.4213,0.3675,0.2964,0.2117,0.1255,0.0565,0.0172,0.0031,0.00029]',
  '[0.4350,0.3800,0.3100,0.2250,0.1400,0.0650,0.0220,0.0050,0.00060]',
  '16QAM', 0.50, -5.00, 19.00,
  220, 'BER_SNR',
  'gallery-16qam-cdlb-39g', TRUE, NOW() - INTERVAL '4 days', 'default'
) ON CONFLICT (id) DO NOTHING;

-- 8. QPSK TDL-A 5 GHz
INSERT INTO simulation_results (
  id, snr_db, ber_theoretical, ber_simulated,
  modulation_type, code_rate, snr_min, snr_max,
  simulation_time_ms, simulation_type,
  share_token, is_public, created_at, colormap_used
) VALUES (
  1008,
  '[-5.0,-2.0,1.0,4.0,7.0,10.0,13.0,16.0,19.0]',
  '[0.3174,0.2398,0.1584,0.0786,0.0264,0.0055,0.00063,0.000037,0.0000011]',
  '[0.3250,0.2480,0.1700,0.0900,0.0320,0.0080,0.0010,0.000080,0.0000030]',
  'QPSK', 0.50, -5.00, 19.00,
  190, 'BER_SNR',
  'gallery-qpsk-tdla-5g', TRUE, NOW() - INTERVAL '3 days', 'default'
) ON CONFLICT (id) DO NOTHING;

-- 9. BPSK CDL-C 28 GHz
INSERT INTO simulation_results (
  id, snr_db, ber_theoretical, ber_simulated,
  modulation_type, code_rate, snr_min, snr_max,
  simulation_time_ms, simulation_type,
  share_token, is_public, created_at, colormap_used
) VALUES (
  1009,
  '[-5.0,-2.0,1.0,4.0,7.0,10.0,13.0,16.0,19.0]',
  '[0.2398,0.1584,0.0786,0.0264,0.0055,0.00063,0.000037,0.0000011,0.000000016]',
  '[0.2480,0.1650,0.0820,0.0290,0.0065,0.00080,0.000050,0.0000020,0.000000030]',
  'BPSK', 0.50, -5.00, 19.00,
  155, 'BER_SNR',
  'gallery-bpsk-cdlc-28g', TRUE, NOW() - INTERVAL '2 days', 'default'
) ON CONFLICT (id) DO NOTHING;

-- 10. 64QAM AWGN 77 GHz
INSERT INTO simulation_results (
  id, snr_db, ber_theoretical, ber_simulated,
  modulation_type, code_rate, snr_min, snr_max,
  simulation_time_ms, simulation_type,
  share_token, is_public, created_at, colormap_used
) VALUES (
  1010,
  '[-5.0,-2.0,1.0,4.0,7.0,10.0,13.0,16.0,19.0]',
  '[0.4685,0.4300,0.3777,0.3096,0.2286,0.1438,0.0720,0.0260,0.0060]',
  '[0.4650,0.4340,0.3740,0.3130,0.2250,0.1470,0.0700,0.0270,0.0058]',
  '64QAM', 0.50, -5.00, 19.00,
  170, 'BER_SNR',
  'gallery-64qam-77ghz-radar', TRUE, NOW() - INTERVAL '1 day', 'default'
) ON CONFLICT (id) DO NOTHING;

-- Now insert gallery_items pointing to each simulation result
INSERT INTO gallery_items (simulation_id, user_id, title, description, visibility, view_count, fork_count, download_count, published_at)
VALUES
  (1001, 1, 'BPSK Baseline — AWGN Channel', 'BPSK modulation over AWGN channel at 2.4 GHz. Classic baseline for BER performance comparison.', 'PUBLIC', 142, 18, 45, NOW() - INTERVAL '10 days'),
  (1002, 1, 'QPSK mmWave 28GHz — Urban', 'QPSK modulation at 28 GHz mmWave band. Urban macro cell deployment scenario.', 'PUBLIC', 98, 12, 32, NOW() - INTERVAL '9 days'),
  (1003, 1, '16QAM WiFi Band Simulation', '16QAM modulation at 5 GHz WiFi band. Indoor AWGN channel with typical office conditions.', 'PUBLIC', 76, 8, 21, NOW() - INTERVAL '8 days'),
  (1004, 1, '64QAM High Throughput mmWave', '64QAM at 39 GHz for high-throughput 5G NR scenarios. AWGN baseline measurement.', 'PUBLIC', 120, 15, 38, NOW() - INTERVAL '7 days'),
  (1005, 1, 'BPSK 60GHz Indoor Channel', 'BPSK at 60 GHz unlicensed band. Indoor short-range WiGig-style deployment.', 'PUBLIC', 54, 5, 15, NOW() - INTERVAL '6 days'),
  (1006, 1, 'QPSK CDL-A Urban Multipath', 'QPSK with CDL-A channel model at 28 GHz. Realistic urban multipath fading.', 'PUBLIC', 87, 11, 28, NOW() - INTERVAL '5 days'),
  (1007, 1, '16QAM CDL-B Suburban Channel', '16QAM with CDL-B channel model at 39 GHz. Suburban NLoS propagation.', 'PUBLIC', 63, 7, 19, NOW() - INTERVAL '4 days'),
  (1008, 1, 'QPSK TDL-A High Delay Spread', 'QPSK with TDL-A tapped delay line model at 5 GHz. High delay spread scenario.', 'PUBLIC', 45, 3, 12, NOW() - INTERVAL '3 days'),
  (1009, 1, 'BPSK CDL-C Suburban LoS', 'BPSK with CDL-C channel model at 28 GHz. Suburban LoS conditions.', 'PUBLIC', 31, 2, 8, NOW() - INTERVAL '2 days'),
  (1010, 1, '64QAM Automotive 77GHz Radar Band', '64QAM at 77 GHz automotive radar band. Exploring 6G-V2X communication potential.', 'PUBLIC', 110, 14, 35, NOW() - INTERVAL '1 day')
ON CONFLICT DO NOTHING;
