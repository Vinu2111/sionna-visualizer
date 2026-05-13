package com.sionnavisualizer.service;

import org.apache.commons.math3.special.Erf;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class BerMathEngine {

    private final Random random = new Random();

    public double[] calculateTheoreticalBer(String modulation, double[] snrDbArray) {
        double[] berArray = new double[snrDbArray.length];
        
        for (int i = 0; i < snrDbArray.length; i++) {
            double snrDb = snrDbArray[i];
            double snrLinear = Math.pow(10, snrDb / 10.0);
            
            switch (modulation.toUpperCase()) {
                case "BPSK":
                    berArray[i] = 0.5 * Erf.erfc(Math.sqrt(snrLinear));
                    break;
                case "QPSK":
                    berArray[i] = 0.5 * Erf.erfc(Math.sqrt(snrLinear / 2.0));
                    break;
                case "16QAM":
                    berArray[i] = 0.75 * Erf.erfc(Math.sqrt(snrLinear / 10.0));
                    break;
                case "64QAM":
                    berArray[i] = 0.875 * Erf.erfc(Math.sqrt(snrLinear / 42.0));
                    break;
                default:
                    // Fallback to QPSK
                    berArray[i] = 0.5 * Erf.erfc(Math.sqrt(snrLinear / 2.0));
            }
        }
        
        return berArray;
    }

    public double[] calculateSimulatedBer(String modulation, double[] snrDbArray) {
        double[] theoretical = calculateTheoreticalBer(modulation, snrDbArray);
        double[] simulated = new double[theoretical.length];
        
        for (int i = 0; i < theoretical.length; i++) {
            double noise = -0.08 + (0.16 * random.nextDouble()); // random value between -0.08 and +0.08
            simulated[i] = theoretical[i] * (1.0 + noise);
            
            // Ensure simulated BER doesn't exceed 0.5 (random guessing limit)
            if (simulated[i] > 0.5) {
                simulated[i] = 0.5;
            }
        }
        
        return simulated;
    }

    public double[] generateSnrRange(double snrMin, double snrMax, int steps) {
        double[] snrRange = new double[steps];
        if (steps <= 1) {
            snrRange[0] = snrMin;
            return snrRange;
        }
        
        double stepSize = (snrMax - snrMin) / (steps - 1);
        for (int i = 0; i < steps; i++) {
            snrRange[i] = snrMin + (i * stepSize);
        }
        
        return snrRange;
    }

    public Map<String, Object> calculateBeamPattern(int numAntennas, double frequencyGhz, double steeringAngleDeg) {
        int points = 181; // -90 to 90 degrees
        double[] angles = new double[points];
        double[] patternDb = new double[points];
        
        double steeringRad = Math.toRadians(steeringAngleDeg);
        double d = 0.5; // half wavelength spacing
        
        for (int i = 0; i < points; i++) {
            double thetaDeg = -90 + i;
            angles[i] = thetaDeg;
            double thetaRad = Math.toRadians(thetaDeg);
            
            double psi = 2 * Math.PI * d * (Math.sin(thetaRad) - Math.sin(steeringRad));
            
            // Array Factor = |sum of e^(j*n*psi) for n=0 to N-1|
            // Sum(e^j*n*x) = sin(N*x/2) / sin(x/2)
            double af;
            if (Math.abs(psi) < 1e-10) {
                af = numAntennas;
            } else {
                af = Math.abs(Math.sin(numAntennas * psi / 2.0) / Math.sin(psi / 2.0));
            }
            
            double afNormalized = af / numAntennas;
            patternDb[i] = 20 * Math.log10(afNormalized + 1e-10);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("angles", angles);
        result.put("pattern_db", patternDb);
        return result;
    }

    public double[] calculateChannelCapacity(double[] snrDbArray, double bandwidthMhz) {
        double[] capacityMbps = new double[snrDbArray.length];
        
        for (int i = 0; i < snrDbArray.length; i++) {
            double snrLinear = Math.pow(10, snrDbArray[i] / 10.0);
            capacityMbps[i] = bandwidthMhz * (Math.log(1 + snrLinear) / Math.log(2));
        }
        
        return capacityMbps;
    }

    public double calculatePathLoss(double distanceMeters, double frequencyGhz) {
        double frequencyHz = frequencyGhz * 1e9;
        double c = 3e8;
        
        // FSPL(dB) = 20*log10(d) + 20*log10(f) + 20*log10(4*pi/c)
        double fsplDb = 20 * Math.log10(distanceMeters) + 
                        20 * Math.log10(frequencyHz) + 
                        20 * Math.log10(4 * Math.PI / c);
        
        return fsplDb;
    }
}
