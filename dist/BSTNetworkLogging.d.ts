import { EmitterSubscription } from 'react-native';
export declare type PingEvents = 'ICMP_PACKET' | 'STATISTIC' | 'SUMMARY' | 'UNKNOWN';
export declare type AllEvents = PingEvents | ('E_RUNTIME_EXCEPTION' | 'E_INVALID_PARAM');
export declare type ICMPPacket = {
    icmpSeq: number;
    ttl: number;
    time: number;
};
export declare type PingStatistic = {
    min: number;
    avg: number;
    max: number;
    mdev: number;
};
export declare type PingSummary = {
    transmitted: number;
    received: number;
    lostPercentage: number;
};
export declare type PingEvent = {
    type: AllEvents;
    raw?: string;
    icmpPacket?: ICMPPacket;
    statistic?: PingStatistic;
    summary?: PingSummary;
};
export declare type CellIdentityLte = {
    bands: number[];
    bandwidth: number;
    ci: number;
    earfcn: number;
    mccString: string;
    mncString: string;
    pci: number;
    tac: number;
};
export declare type CellSignalStrengthLte = {
    cqi: number;
    rsrp: number;
    rsrq: number;
    rssi: number;
    rssnr: number;
    timingAdvance: number;
};
export declare type CellInfoLte = {
    cellIdentity: CellIdentityLte;
    cellSignalStrength: CellSignalStrengthLte;
};
export declare type CellIdentityNr = {
    mccString: string;
    mncString: string;
    nci: number;
    nrarfcn: number;
    pci: number;
    tac: number;
};
export declare type CellSignalStrengthNr = {
    csiRsrp: number;
    csiRsrq: number;
    csiSinr: number;
    ssRsrp: number;
    ssRsrq: number;
    ssSinr: number;
};
export declare type CellInfoNr = {
    cellIdentity: CellIdentityNr;
    cellSignalStrength: CellSignalStrengthNr;
};
export declare type NetworkInfoEvent = {
    lte: CellInfoLte[];
    nr: CellInfoNr[];
};
export declare type PhoneInfo = {
    imsi: String;
    imei: String;
    model: String;
};
export declare type RequestPermissionOptions = {
    requestPermission: boolean;
};
export declare type PingParams = {
    durationSeconds?: number;
    reportIntervalSeconds?: number;
};
export declare function registerPingListener(listener: (event: PingEvent) => any): EmitterSubscription;
export declare function registerNetworkInfoListener(listener: (event: NetworkInfoEvent) => any): EmitterSubscription;
export declare function startPingAsync(domainName: string, params?: PingParams, options?: {
    [name: string]: string;
}): Promise<void>;
export declare function stopPingAsync(): Promise<void>;
export declare function startNetworkLoggingAsync(): Promise<void>;
export declare function stopNetworkLoggingAsync(): Promise<void>;
export declare function getPhoneInfoAsync(): Promise<PhoneInfo>;
export declare function requestPermissions(options: RequestPermissionOptions): Promise<void>;
