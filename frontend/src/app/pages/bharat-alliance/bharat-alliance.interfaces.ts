export interface AllianceOrganization {
  orgId: number;
  orgName: string;
  memberType: string;
  allianceTrack: string;
  memberId: string;
  userId: number;
}

export interface Poc {
  pocId: number;
  title: string;
  description: string;
  targetUseCase: string;
  allianceTrack: string;
  currentTrl: number;
  expectedCompletionTrl: number;
  status: string;
  targetCompletionDate: string;
  linkedSimulationCount: number;
  updatedAt: string;
}

export interface TrlAdvancement {
  trl: number;
  achievedAt: string;
  linkedSimulationId: number;
  evidenceDescription: string;
}

export interface LinkedSimulation {
  simulationId: number;
  trlEvidenceFor: number;
  linkedAt: string;
  modulation: string;
  berAt20db: number;
}

export interface AllianceKpi {
  kpiId: number;
  kpiName: string;
  targetValue: number;
  actualValue: number;
  unit: string;
  allianceTrack: string;
  status: string;
}

export interface QuarterlyStatus {
  id: number;
  quarter: string;
  year: number;
  status: string;
  dueDate: string;
  submittedAt: string;
}

export interface PocDetail extends Poc {
  trlHistory: TrlAdvancement[];
  linkedSimulations: LinkedSimulation[];
  kpis: AllianceKpi[];
  quarterlyStatus: QuarterlyStatus[];
}

export interface KpiTemplate {
  kpiName: string;
  unit: string;
  suggestedTarget: number;
  allianceTrack: string;
}

export interface RegisterPocData {
  title: string;
  description: string;
  targetUseCase: string;
  allianceTrack: string;
  currentTrl: number;
  expectedCompletionTrl: number;
  targetCompletionDate: string;
  kpis: { kpiName: string; targetValue: number; unit: string }[];
}

export interface AllianceReportOptions {
  reportType: string;
  pocId: number;
  quarter: string;
  year: number;
}

export const ALLIANCE_TRACKS = [
  { value: 'AIR_INTERFACE', label: 'Air Interface Technology' },
  { value: 'NETWORK_ARCHITECTURE', label: 'Network Architecture and Slicing' },
  { value: 'SPECTRUM_MANAGEMENT', label: 'Spectrum Management' },
  { value: 'SECURITY', label: 'Security and Privacy' },
  { value: 'RURAL_CONNECTIVITY', label: 'Rural and Remote Connectivity' },
  { value: 'ENERGY_EFFICIENCY', label: 'Energy Efficiency' },
  { value: 'DEVICE_TECHNOLOGY', label: 'Device Technology' }
];

export const TRACK_COLORS: Record<string, string> = {
  AIR_INTERFACE: '#1976d2',
  NETWORK_ARCHITECTURE: '#7b1fa2',
  SPECTRUM_MANAGEMENT: '#0097a7',
  SECURITY: '#d32f2f',
  RURAL_CONNECTIVITY: '#388e3c',
  ENERGY_EFFICIENCY: '#f57c00',
  DEVICE_TECHNOLOGY: '#5d4037'
};
