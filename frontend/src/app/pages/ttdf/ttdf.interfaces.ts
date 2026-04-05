export interface TtdfProject {
  projectId: number;
  title: string;
  ttdfGrantId: string;
  piName: string;
  institution: string;
  grantAmountLakhs: number;
  startDate: string;
  endDate: string;
  currentTrl: number;
  userId: number;
}

export interface KpiTarget {
  kpiId: number;
  kpiName: string;
  targetValue: number;
  actualValue: number;
  unit: string;
  metricType: string;
  status: string;
}

export interface Milestone {
  milestoneId: number;
  title: string;
  description: string;
  monthNumber: number;
  dueDate: string;
  status: string;
  linkedSimulationId: number;
  kpis: KpiTarget[];
}

export interface CreateMilestoneData {
  title: string;
  description: string;
  monthNumber: number;
  dueDate: string;
  kpis: {
      kpiName: string;
      targetValue: number;
      unit: string;
      metricType: string;
  }[];
}

export interface ReportOptions {
  reportType: string;
  fromDate: string;
  toDate: string;
  includeSections: string[];
}
