import {NativeModules, NativeEventEmitter, EmitterSubscription} from 'react-native';

const pingEventType = '@BST/PING';
const networkInfoEventType = '@BST/NETWORK_INFO';

const BSTNetworkLoggingModule = NativeModules.BSTNetworkLoggingModule;
const eventEmitter = new NativeEventEmitter(BSTNetworkLoggingModule);

export type PingEvents = 'ICMP_PACKET' | 'STATISTIC' | 'SUMMARY' | 'UNKNOWN';
export type AllEvents = PingEvents | ('E_RUNTIME_EXCEPTION' | 'E_INVALID_PARAM');

export type ICMPPacket = {
  icmpSeq: number;
  ttl: number;
  time: number;
};

export type PingStatistic = {
  min: number;
  avg: number;
  max: number;
  mdev: number;
};

export type PingSummary = {
  transmitted: number;
  received: number;
  lostPercentage: number;
};

export type PingEvent = {
  type: AllEvents;
  raw?: string;
  icmpPacket?: ICMPPacket;
  statistic?: PingStatistic;
  summary?: PingSummary;
};

export type CellIdentityLte = {
  bands: number[];
  bandwidth: number;
  ci: number;
  earfcn: number;
  mccString: string;
  mncString: string;
  pci: number;
  tac: number;
};

export type CellSignalStrengthLte = {
  cqi: number;
  rsrp: number;
  rsrq: number;
  rssi: number;
  rssnr: number;
  timingAdvance: number;
};

export type CellInfoLte = {
  cellIdentity: CellIdentityLte;
  cellSignalStrength: CellSignalStrengthLte;
};

export type CellIdentityNr = {
  mccString: string;
  mncString: string;
  nci: number;
  nrarfcn: number;
  pci: number;
  tac: number;
};

export type CellSignalStrengthNr = {
  csiRsrp: number;
  csiRsrq: number;
  csiSinr: number;
  ssRsrp: number;
  ssRsrq: number;
  ssSinr: number;
};

export type CellInfoNr = {
  cellIdentity: CellIdentityNr;
  cellSignalStrength: CellSignalStrengthNr;
};

export type NetworkInfoEvent = {
  lte: CellInfoLte[];
  nr: CellInfoNr[];
};

export type PhoneInfo = {
  imsi: String;
  imei: String;
  model: String;
};

export type RequestPermissionOptions = {
  requestPermission: boolean;
};

export type PingParams = {
  durationSeconds?: number;
  reportIntervalSeconds?: number;
};

export type Coordination = {
  latitude: number;
  longitude: number;
};

export function registerPingListener(listener: (event: PingEvent) => any): EmitterSubscription {
  return eventEmitter.addListener(pingEventType, listener);
}

export function registerNetworkInfoListener(listener: (event: NetworkInfoEvent) => any): EmitterSubscription {
  return eventEmitter.addListener(networkInfoEventType, listener);
}

export async function startPingAsync(
  domainName: string,
  params?: PingParams,
  options?: {[name: string]: string},
): Promise<void> {
  await BSTNetworkLoggingModule.startPingAsync(domainName, params, options);
}

export async function stopPingAsync(): Promise<void> {
  await BSTNetworkLoggingModule.stopPingAsync();
}

export async function hasTelephonyFeatureAsync(): Promise<boolean> {
  return await BSTNetworkLoggingModule.hasTelephonyFeatureAsync();
}

export async function startNetworkLoggingAsync(): Promise<string> {
  return await BSTNetworkLoggingModule.startNetworkLoggingAsync();
}

export async function stopNetworkLoggingAsync(): Promise<void> {
  return await BSTNetworkLoggingModule.stopNetworkLoggingAsync();
}

export async function getPhoneInfoAsync(): Promise<PhoneInfo> {
  return await BSTNetworkLoggingModule.getPhoneInfoAsync();
}

export async function requestPermissionsAsync(options?: RequestPermissionOptions): Promise<boolean> {
  return await BSTNetworkLoggingModule.requestPermissionsAsync(options);
}

export async function initializeAsync(): Promise<void> {
  await BSTNetworkLoggingModule.initializeAsync();
}

export async function feedLocationAsync(location: Coordination): Promise<void> {
  await BSTNetworkLoggingModule.feedLocationAsync(location);
}
