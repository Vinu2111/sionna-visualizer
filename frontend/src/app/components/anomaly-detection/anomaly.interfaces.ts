export interface AnomalyReport {
  reportId: number;
  simulationId: number;
  analyzedAt: string;
  totalAnomalies: number;
  hasCritical: boolean;
  overallStatus: 'CLEAR' | 'WARNING' | 'CRITICAL';
  anomalies: Anomaly[];
}

export interface Anomaly {
  anomalyId: number;
  anomalyType: string;
  severity: 'CRITICAL' | 'HIGH' | 'MEDIUM' | 'INFO';
  title: string;
  description: string;
  affectedSnrPoint: number | null;
  affectedBerValue: number | null;
  likelyCause: string;
  suggestedFix: string;
  aiExplanation: string | null;
}

export interface AiExplanation {
  anomalyId: number;
  fullExplanation: string;
  generatedAt: string;
}

// Maps severity level to display colours
export const SEVERITY_CONFIG: Record<string, { border: string; bg: string; icon: string; iconColor: string; label: string }> = {
  CRITICAL: { border: '#f44336', bg: '#fff5f5', icon: 'dangerous',     iconColor: '#f44336', label: 'CRITICAL' },
  HIGH:     { border: '#ff9800', bg: '#fff8f0', icon: 'warning',        iconColor: '#ff9800', label: 'HIGH'     },
  MEDIUM:   { border: '#ffc107', bg: '#fffbf0', icon: 'info',           iconColor: '#f9a825', label: 'MEDIUM'   },
  INFO:     { border: '#4caf50', bg: '#f6fff6', icon: 'check_circle',   iconColor: '#4caf50', label: 'ALL CLEAR'}
};
