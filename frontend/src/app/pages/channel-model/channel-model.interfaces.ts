export interface ChannelModelParams {
  channelModel: string;
  modulation: string;
  snrMin: number;
  snrMax: number;
  snrSteps: number;
  numAntennasTx: number;
  numAntennasRx: number;
  carrierFrequency: number;
  delaySpread: number;
  numTimeSteps: number;
}

export interface DelayProfileTap {
  tap_delay: number;
  power: number;
}

export interface ChannelModelResult {
  channelModel: string;
  modulation: string;
  snrDbRange: number[];
  berValues: number[];
  theoreticalBer: number[];
  delayProfile: DelayProfileTap[];
  simulationTimeSeconds: number;
  numPaths: number;
}
