/*
 * SPDX-FileCopyrightText: 2025 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.settings.esimswitcher

import android.content.Context
import android.os.ServiceManager
import android.os.SystemProperties
import android.se.omapi.SEService
import android.telephony.TelephonyManager
import android.telephony.UiccSlotMapping
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import vendor.oplus.hardware.esim.IOplusEsim
import vendor.oplus.hardware.subsys_interface.subsys.SubsysResponseInfo
import vendor.oplus.hardware.subsys_interface.subsys_radio.*
import java.util.concurrent.atomic.AtomicInteger

private class EsimSubsysRadioResponse : ISubsysRadioResponse.Stub() {
    override fun backupNvBackupResponse(info: SubsysResponseInfo?) {}
    override fun configGsmTimingDataResponse(info: SubsysResponseInfo?) {}
    override fun configPaIcqDataResponse(info: SubsysResponseInfo?) {}
    override fun connectSarSensorResponse(info: SubsysResponseInfo?) {}
    override fun deleteEfsItemResponse(info: SubsysResponseInfo?) {}
    override fun deprioritizeNrResponse(info: SubsysResponseInfo?) {}
    override fun disconnectSarSensorResponse(info: SubsysResponseInfo?) {}
    override fun enableEndcResponse(info: SubsysResponseInfo?) {}
    override fun fetchOlogResponse(info: SubsysResponseInfo?) {}
    override fun getAntForceStateByRatResponse(info: SubsysResponseInfo?, enable: Int) {}
    override fun getAntIdResponse(info: SubsysResponseInfo?, result: Int) {}
    override fun getAsdivFixPositionResponse(info: SubsysResponseInfo?, position: Int) {}
    override fun getAsdivStatesResponse(info: SubsysResponseInfo?, data: AsdivState?) {}
    override fun getAvailableBandModesResponse(info: SubsysResponseInfo?, bandModes: IntArray?) {}
    override fun getBandResponse(info: SubsysResponseInfo?, result: Long) {}
    override fun getBandsTxNumResponse(info: SubsysResponseInfo?, result: Int) {}
    override fun getCABandComboResponse(info: SubsysResponseInfo?, caInfo: ByteArray?) {}
    override fun getCallInfoResponse(info: SubsysResponseInfo?, result: Int) {}
    override fun getCapabilityResponse(info: SubsysResponseInfo?, value: Byte) {}
    override fun getDeviceLockStatusResponse(info: SubsysResponseInfo?, result: Byte) {}
    override fun getDeviceLockinfoResponse(info: SubsysResponseInfo?, result: ByteArray?) {}
    override fun getDiagPktVersionMismatchDbResponse(info: SubsysResponseInfo?, mismatchDb: DiagPacketVersionMismatchDb?) {}
    override fun getEchoLocateDlCarrierLogResponse(info: SubsysResponseInfo?, respInfo: ElDlCellInfo?) {}
    override fun getEchoLocateUlCarrierLogResponse(info: SubsysResponseInfo?, respInfo: ElUlCellInfo?) {}
    override fun getFiveGSaNsaModeResponse(info: SubsysResponseInfo?, result: Int) {}
    override fun getGpioStatusResponse(info: SubsysResponseInfo?, gpioInfo: Byte) {}
    override fun getGsmPclPwrResponse(info: SubsysResponseInfo?, pcl: Int) {}
    override fun getImsPolMgrApnResponse(info: SubsysResponseInfo?, apn_name: String?) {}
    override fun getImsProfileApnResponse(info: SubsysResponseInfo?, apn_name: String?) {}
    override fun getImsProfileListResponse(info: SubsysResponseInfo?, profileIndex: ByteArray?) {}
    override fun getImsUssdEnabledResponse(info: SubsysResponseInfo?, imsUssdEnabled: Byte) {}
    override fun getLtePowerClassResponse(info: SubsysResponseInfo?, values: Char) {}
    override fun getModemBasebandVersionResponse(info: SubsysResponseInfo?, modemBaseband: String?) {}
    override fun getModemHeapInfoResponse(info: SubsysResponseInfo?, heapInfo: HeapInfo?) {}
    override fun getModemHeapListResponse(info: SubsysResponseInfo?, heapList: HeapList?) {}
    override fun getMotionStateResponse(info: SubsysResponseInfo?, motionStates: ByteArray?) {}
    override fun getNasSysInfoResponse(info: SubsysResponseInfo?, nasSysInfo: NasSysInfo?) {}
    override fun getNr5gBlerResponse(info: SubsysResponseInfo?, bler: Int) {}
    override fun getNr5gFullVoiceSupportResponse(info: SubsysResponseInfo?, voiceSupport: Byte) {}
    override fun getNrBandPreferResponse(info: SubsysResponseInfo?, preferredBands: ByteArray?) {}
    override fun getNrServingCellInfoResponse(info: SubsysResponseInfo?, result: NrCellInfo?) {}
    override fun getNrSupportResponse(info: SubsysResponseInfo?, status: Byte) {}
    override fun getNvBackupStatResponse(info: SubsysResponseInfo?, stat: NvBackupStatisticsType?) {}
    override fun getOperationModeResponse(info: SubsysResponseInfo?, mode: Int) {}
    override fun getPhySlotStateResponse(info: SubsysResponseInfo?, slotInfo: PhySlotStatus?) {}
    override fun getRfBandInfoResponse(info: SubsysResponseInfo?, activeBand: Int, active1xChannel: Int, activeEhrpdChannel: Int) {}
    override fun getRrcLogResponse(info: SubsysResponseInfo?, rrcState: RrcState?) {}
    override fun getSarDsiStateResponse(info: SubsysResponseInfo?, state: Int) {}
    override fun getSarRegionCodeResponse(info: SubsysResponseInfo?, code: Int) {}
    override fun getSarSnsDataResponse(info: SubsysResponseInfo?, data: Int) {}
    override fun getServingCellInfoResponse(info: SubsysResponseInfo?, result: CellInfo?) {}
    override fun getSimCardTypeResponse(info: SubsysResponseInfo?, type: Byte) {}
    override fun getSimPathResponse(info: SubsysResponseInfo?, simPath: Int) {}
    override fun getSimTrayStatusResponse(info: SubsysResponseInfo?, status: IntArray?) {}
    override fun getSimlockActivateTimeResponse(info: SubsysResponseInfo?, result: Long) {}
    override fun getSimlockCategoryDataResponse(info: SubsysResponseInfo?, categoryData: ByteArray?) {}
    override fun getSimlockComboTypeResponse(info: SubsysResponseInfo?, result: Int) {}
    override fun getSimlockCurrentRetryResponse(info: SubsysResponseInfo?, result: Int) {}
    override fun getSimlockDelayLockStateResponse(info: SubsysResponseInfo?, result: Byte) {}
    override fun getSimlockFactoryResetTimeResponse(info: SubsysResponseInfo?, result: Long) {}
    override fun getSimlockFeatureResponse(info: SubsysResponseInfo?, result: Int) {}
    override fun getSimlockFeeStateResponse(info: SubsysResponseInfo?, result: Byte) {}
    override fun getSimlockFuseStatusResponse(info: SubsysResponseInfo?, secBootStatus: Byte, fuseStatus: Byte) {}
    override fun getSimlockIsRegionVietnamResponse(info: SubsysResponseInfo?, result: Byte) {}
    override fun getSimlockLockStatusResponse(info: SubsysResponseInfo?, lockStatus: RsuSimlockLockStatus?) {}
    override fun getSimlockLockTypeResponse(info: SubsysResponseInfo?, result: Byte) {}
    override fun getSimlockLockmarkResponse(info: SubsysResponseInfo?, result: ByteArray?) {}
    override fun getSimlockMaxRetryResponse(info: SubsysResponseInfo?, result: Int) {}
    override fun getSimlockOperatorIdResponse(info: SubsysResponseInfo?, result: Byte) {}
    override fun getSimlockRsuModeResponse(info: SubsysResponseInfo?, mode: Int) {}
    override fun getSimlockSimStateResponse(info: SubsysResponseInfo?, result: Byte) {}
    override fun getSimlockUnlockStateResponse(info: SubsysResponseInfo?, result: Byte) {}
    override fun getSimlockVersionResponse(info: SubsysResponseInfo?, minVersion: Byte, maxVersion: Byte) {}
    override fun getSystemSelectionPreferenceResponse(info: SubsysResponseInfo?, preference: SystemSelectionPreference?) {}
    override fun getTestModeMaskResponse(info: SubsysResponseInfo?, result: Long) {}
    override fun getTxAdcResponse(info: SubsysResponseInfo?, result: Int) {}
    override fun getTxRxInfoResponse(info: SubsysResponseInfo?, txrxInfo: TxRxInfo?) {}
    override fun getVoiceConfigResponse(info: SubsysResponseInfo?, config: VoiceConfig?) {}
    override fun initRfPathResponse(info: SubsysResponseInfo?) {}
    override fun readEfsItemResponse(info: SubsysResponseInfo?, result: ByteArray?) {}
    override fun readNvResponse(info: SubsysResponseInfo?, result: ByteArray?) {}
    override fun refreshModemEfsResponse(info: SubsysResponseInfo?) {}
    override fun registerNr5gStatsEventResponse(info: SubsysResponseInfo?) {}
    override fun requireModemRebootResponse(info: SubsysResponseInfo?) {}
    override fun restoreNvBackupAllowedResponse(info: SubsysResponseInfo?, allowed: Byte) {}
    override fun restoreNvBackupResponse(info: SubsysResponseInfo?) {}
    override fun sendDciSyncReqAndRspResponse(info: SubsysResponseInfo?, rspBuf: ByteArray?) {}
    override fun sendScreenStateResponse(info: SubsysResponseInfo?) {}
    override fun setAclStateResponse(info: SubsysResponseInfo?) {}
    override fun setAntForceStateByRatResponse(info: SubsysResponseInfo?) {}
    override fun setAntMaxPowerResponse(info: SubsysResponseInfo?) {}
    override fun setAolScenarioResponse(info: SubsysResponseInfo?) {}
    override fun setAsdivFixPositionResponse(info: SubsysResponseInfo?) {}
    override fun setBandModeResponse(info: SubsysResponseInfo?) {}
    override fun setBarCellResponse(info: SubsysResponseInfo?) {}
    override fun setCABandComboResponse(info: SubsysResponseInfo?) {}
    override fun setCalibrationStateResponse(info: SubsysResponseInfo?) {}
    override fun setCapabilityResponse(info: SubsysResponseInfo?) {}
    override fun setCarrierOmacpFqdnResponse(info: SubsysResponseInfo?) {}
    override fun setComboRfTxInfoResponse(info: SubsysResponseInfo?) {}
    override fun setCyberSenseConfigPolicyResponse(info: SubsysResponseInfo?, result: Int) {}
    override fun setDiagnoseConfigResponse(info: SubsysResponseInfo?) {}
    override fun setFiveGSaNsaModeResponse(info: SubsysResponseInfo?) {}
    override fun setGameScenario2RfResponse(info: SubsysResponseInfo?) {}
    override fun setGpioStatusResponse(info: SubsysResponseInfo?) {}
    override fun setGsmPclPwrResponse(info: SubsysResponseInfo?) {}
    override fun setMotionConfigResponse(info: SubsysResponseInfo?) {}
    override fun setNr5gFullVoiceSupportResponse(info: SubsysResponseInfo?) {}
    override fun setNrBandPreferResponse(info: SubsysResponseInfo?) {}
    override fun setNvRestoreStateResponse(info: SubsysResponseInfo?, state: Byte) {}
    override fun setOosLpmCfgResponse(info: SubsysResponseInfo?) {}
    override fun setOperationModeResponse(info: SubsysResponseInfo?) {}
    override fun setPdcActivateResponse(info: SubsysResponseInfo?) {}
    override fun setPdcDeactivateResponse(info: SubsysResponseInfo?) {}
    override fun setRatAcqOrderResponse(info: SubsysResponseInfo?) {}
    override fun setRfDebugMaskResponse(info: SubsysResponseInfo?) {}
    override fun setRfTxInfoResponse(info: SubsysResponseInfo?) {}
    override fun setRtSarModeResponse(info: SubsysResponseInfo?) {}
    override fun setRxChainsNumberResponse(info: SubsysResponseInfo?) {}
    override fun setRxDiversityResponse(info: SubsysResponseInfo?) {}
    override fun setSarControlStateResponse(info: SubsysResponseInfo?) {}
    override fun setSarDsiStateResponse(info: SubsysResponseInfo?) {}
    override fun setSarRegionCodeResponse(info: SubsysResponseInfo?) {}
    override fun setSarSensorChannelResponse(info: SubsysResponseInfo?) {}
    override fun setSarStateResponse(info: SubsysResponseInfo?) {}
    override fun setSimPathResponse(info: SubsysResponseInfo?) {}
    override fun setSimlockAccumulatedTimeResponse(info: SubsysResponseInfo?) {}
    override fun setSimlockActivateTimeResponse(info: SubsysResponseInfo?) {}
    override fun setSimlockFactoryResetTimeResponse(info: SubsysResponseInfo?) {}
    override fun setSimlockFeeStateResponse(info: SubsysResponseInfo?) {}
    override fun setTestModeMaskResponse(info: SubsysResponseInfo?) {}
    override fun setTimeZoneResponse(info: SubsysResponseInfo?) {}
    override fun setUimPowerStatusResponse(info: SubsysResponseInfo?) {}
    override fun setUst5gUCConfigResponse(info: SubsysResponseInfo?) {}
    override fun setVoiceRoamingResponse(info: SubsysResponseInfo?) {}
    override fun testQlinkBlerResponse(info: SubsysResponseInfo?) {}
    override fun testQlinkPingResponse(info: SubsysResponseInfo?) {}
    override fun testQlinkSlaveIdResponse(info: SubsysResponseInfo?) {}
    override fun triggerModemCrashResponse(info: SubsysResponseInfo?) {}
    override fun unlockSimlockResponse(info: SubsysResponseInfo?) {}
    override fun updateKddiSimlockBlobResponse(info: SubsysResponseInfo?) {}
    override fun updateSimlockBlobResponse(info: SubsysResponseInfo?) {}
    override fun writeEfsItemResponse(info: SubsysResponseInfo?) {}
    override fun writeNvResponse(info: SubsysResponseInfo?) {}
    override fun getRRCStatusResponse(info: SubsysResponseInfo?, rrcStatus: Int) {}
    override fun setBandPreferResponse(info: SubsysResponseInfo?, result: Int) {}
    override fun getBandPreferResponse(info: SubsysResponseInfo?, preferredBands: ByteArray?) {}
    override fun clearStoredFrequencyResponse(info: SubsysResponseInfo?, result: Int) {}
    override fun setSidoIssueConfigResponse(info: SubsysResponseInfo?) {}
    override fun setLteAcqScanDuringLteResponse(info: SubsysResponseInfo?) {}
    override fun getNWLimitStatusResponse(info: SubsysResponseInfo?, limitState: Int) {}
    override fun setDeviceIdleModeResponse(info: SubsysResponseInfo?) {}
    override fun setDeviceDeepSleepResponse(info: SubsysResponseInfo?) {}
    override fun setGameSpaceModeResponse(info: SubsysResponseInfo?) {}
    override fun setIgnorePsPagingResponse(info: SubsysResponseInfo?) {}
    override fun getEfsSpaceInfoResponse(info: SubsysResponseInfo?, availBlocks: Int, blockSize: Int, totalBlocks: Int) {}
    override fun getSimHotswapStatusResponse(info: SubsysResponseInfo?, hotswapStatus: ByteArray?) {}
    override fun getRfBandwidthInfoResponse(info: SubsysResponseInfo?, lteBandwidth: Int, nrBandwidth: Int) {}
    override fun getRadioLinkInfoResponse(info: SubsysResponseInfo?, infoList: ByteArray?) {}
    override fun setMdmFeatureResponse(info: SubsysResponseInfo?) {}
    override fun resetModemConfigResponse(info: SubsysResponseInfo?) {}
    override fun setHstConfigResponse(info: SubsysResponseInfo?) {}
    override fun updateFoldScreenStatusResponse(info: SubsysResponseInfo?) {}
    override fun readSingleSimArrayResponse(info: SubsysResponseInfo?, singleSimData: ByteArray?) {}
    override fun setMipiOscFreqHopStateResponse(info: SubsysResponseInfo?) {}
    override fun setNecReportPeriodResponse(info: SubsysResponseInfo?) {}
    override fun setNecConfigResponse(info: SubsysResponseInfo?) {}
    override fun getNecDataResponse(info: SubsysResponseInfo?, data: ByteArray?) {}
    override fun setSimOverdueResponse(info: SubsysResponseInfo?) {}
    override fun setLpmScanConfigResponse(info: SubsysResponseInfo?) {}
    override fun getCalibrationStatusResponse(info: SubsysResponseInfo?, adjustStatus: ByteArray?) {}
    override fun setMcfgRfsParamsResponse(info: SubsysResponseInfo?, rfs_params_ver: Int) {}
    override fun getMcfgRfsParamsResponse(info: SubsysResponseInfo?, rfsParams: McfgRfsParams?) {}
    override fun trigerMcfgRemoteDiscoverResponse(info: SubsysResponseInfo?, mcfgRemoteDiscoverInfo: McfgRemoteDiscoverInfo?) {}
    override fun setRatScanResponse(info: SubsysResponseInfo?) {}
    override fun setImeiSvnResponse(info: SubsysResponseInfo?) {}
    override fun setUst5gUCBandConfigResponse(info: SubsysResponseInfo?) {}
    override fun setDssConfigResponse(info: SubsysResponseInfo?) {}
    override fun setDssAdditionalConfigResponse(info: SubsysResponseInfo?) {}
    override fun readDssConfigResponse(info: SubsysResponseInfo?, config: Int) {}
    override fun readNRCAConfigResponse(info: SubsysResponseInfo?, config: Int) {}
    override fun setWhiteSANRCAConfigResponse(info: SubsysResponseInfo?) {}
    override fun setBlackSANRCAConfigResponse(info: SubsysResponseInfo?) {}
    override fun setWhiteNSANRCAConfigResponse(info: SubsysResponseInfo?) {}
    override fun resetSANSAWhiteListResponse(info: SubsysResponseInfo?) {}
    override fun setTasForceIdxByRatResponse(info: SubsysResponseInfo?, forceIdx: IntArray?) {}
    override fun setMaxTxPowerResponse(info: SubsysResponseInfo?, txPowerConfig: IntArray?) {}
    override fun lockCellAndBandResponse(info: SubsysResponseInfo?) {}
    override fun unlockCellAndBandResponse(info: SubsysResponseInfo?) {}
    override fun setFastDormancyTimerResponse(info: SubsysResponseInfo?) {}
    override fun sendFastDormancyResponse(info: SubsysResponseInfo?) {}
    override fun setPSConformanceModeResponse(info: SubsysResponseInfo?) {}
    override fun queryFastDormancyModeResponse(info: SubsysResponseInfo?, mode: Int, profile: Int) {}
    override fun queryLegacyFastDormancyModeResponse(info: SubsysResponseInfo?, mode: Int) {}
    override fun setServiceModeResponse(info: SubsysResponseInfo?) {}
    override fun getServiceModeResponse(info: SubsysResponseInfo?, mode: Int) {}
    override fun setCentricModeResponse(info: SubsysResponseInfo?) {}
    override fun getCentricModeResponse(info: SubsysResponseInfo?, mode: Int) {}
    override fun setCaStatusResponse(info: SubsysResponseInfo?) {}
    override fun getCaStatusResponse(info: SubsysResponseInfo?, status: Int) {}
    override fun setSyncWithApResponse(info: SubsysResponseInfo?) {}
    override fun setBarCellOptResponse(info: SubsysResponseInfo?) {}
    override fun getTxAntennaResponse(info: SubsysResponseInfo?, antennas: IntArray?) {}
    override fun setNwRateLimitingDetectCfgResponse(info: SubsysResponseInfo?) {}
    override fun getNwRateLimitingInfoResponse(info: SubsysResponseInfo?, limitInfo: NwRateLimitingInfo?) {}
    override fun getHwidResponse(info: SubsysResponseInfo?, hwid: Int) {}
    override fun setRfTxPowerResponse(info: SubsysResponseInfo?) {}
    override fun performPlmnUnblockResponse(info: SubsysResponseInfo?) {}
    override fun performPlmnBlockResponse(info: SubsysResponseInfo?) {}
    override fun setSaPriorityResponse(info: SubsysResponseInfo?) {}
    override fun setPowerSavingStateResponse(info: SubsysResponseInfo?) {}
    override fun performLteAcqScanResponse(info: SubsysResponseInfo?) {}
    override fun plmnRatSelectionResponse(info: SubsysResponseInfo?) {}
    override fun mdmDispatchCaseResponse(info: SubsysResponseInfo?) {}
    override fun setImsRegTimerResponse(info: SubsysResponseInfo?) {}
    override fun setMicroMotionStateResponse(info: SubsysResponseInfo?) {}
    override fun performBgSearchResponse(info: SubsysResponseInfo?) {}
    override fun clearForbiddenTaiResponse(info: SubsysResponseInfo?) {}
    override fun setDataStallParamResponse(info: SubsysResponseInfo?) {}
    override fun setNfListResponse(info: SubsysResponseInfo?) {}
    override fun getNfListResponse(info: SubsysResponseInfo?) {}
    override fun setL2nrSelectionResponse(info: SubsysResponseInfo?) {}
    override fun setLteDubiousCellResponse(info: SubsysResponseInfo?) {}
    override fun setNrDubiousCellResponse(info: SubsysResponseInfo?) {}
    override fun setMsimSubModePrefResponse(info: SubsysResponseInfo?) {}
    override fun getMsimSubModeStateResponse(info: SubsysResponseInfo?, msimSubMode: Int, dsdaTxMode: Int) {}
    override fun getBwpConfigResponse(info: SubsysResponseInfo?, bwpState: Byte) {}
    override fun setBwpConfigResponse(info: SubsysResponseInfo?) {}
    override fun getNrcaConfigResponse(info: SubsysResponseInfo?, nrcaState: Byte) {}
    override fun setNrcaConfigResponse(info: SubsysResponseInfo?) {}
    override fun setBackgroundSearchResponse(info: SubsysResponseInfo?) {}
    override fun sendDeviceStateExtResponse(info: SubsysResponseInfo?) {}
    override fun setOosCfgResponse(info: SubsysResponseInfo?) {}
    override fun setQrxlvminCfgResponse(info: SubsysResponseInfo?) {}
    override fun setArrearageCfgResponse(info: SubsysResponseInfo?) {}
    override fun setSimTagResponse(info: SubsysResponseInfo?) {}
    override fun setQosReportPeriodResponse(info: SubsysResponseInfo?) {}
    override fun setQosConfigResponse(info: SubsysResponseInfo?) {}
    override fun getQosDataResponse(info: SubsysResponseInfo?, data: ByteArray?) {}
    override fun setDsdaPreferCustThreshResponse(info: SubsysResponseInfo?) {}
    override fun setIdcOffsetResponse(info: SubsysResponseInfo?) {}
    override fun writeNvItemResponse(info: SubsysResponseInfo?) {}
    override fun getLteCellInfoResponse(info: SubsysResponseInfo?, pci: Int, arfcn: Int, sib24Available: Boolean) {}
    override fun getNrcaInfoResponse(info: SubsysResponseInfo?, nrcaInfo: NrcaInfo?) {}
    override fun updateDcdcSleepStateResponse(info: SubsysResponseInfo?) {}
    override fun enableCellBarringResponse(info: SubsysResponseInfo?) {}
    override fun configCellBarringParamResponse(info: SubsysResponseInfo?) {}
    override fun setFeatureStateResponse(info: SubsysResponseInfo?) {}
    override fun getFeatureStateResponse(info: SubsysResponseInfo?, featureState: Int) {}
    override fun setRfTxInfo2Response(info: SubsysResponseInfo?) {}
    override fun setAtcTableInitResponse(info: SubsysResponseInfo?) {}
    override fun getTxCarkitResponse(info: SubsysResponseInfo?, antennas: ByteArray?) {}
    override fun getServingCellularCellInfoResponse(info: SubsysResponseInfo?, respInfo: CellInfos?) {}
    override fun startMetricsCollectResponse(info: SubsysResponseInfo?) {}
    override fun stopMetricsCollectResponse(info: SubsysResponseInfo?) {}
    override fun getMetricsDataResponse(info: SubsysResponseInfo?, data: ByteArray?) {}
    override fun setHyperUplinkModeResponse(info: SubsysResponseInfo?) {}
    override fun sendSceneModeResponse(info: SubsysResponseInfo?) {}
    override fun sendQoeScoreResponse(info: SubsysResponseInfo?) {}
    override fun setHyperUplinkParasResponse(info: SubsysResponseInfo?) {}
    override fun setSiteCellInfoResponse(info: SubsysResponseInfo?) {}
    override fun setModemIndicationPolicyResponse(info: SubsysResponseInfo?) {}
    override fun getModemIndicationPolicyResponse(info: SubsysResponseInfo?, policy: Array<PolicyConfig>?) {}
    override fun setAolStateResponse(info: SubsysResponseInfo?) {}
    override fun setImbStateResponse(info: SubsysResponseInfo?) {}
    override fun setMtplLimitStateResponse(info: SubsysResponseInfo?) {}
    override fun setAntFixStateResponse(info: SubsysResponseInfo?) {}
    override fun setAntBlockStateResponse(info: SubsysResponseInfo?) {}
    override fun setPowerBoostStateResponse(info: SubsysResponseInfo?) {}
    override fun getModemProtocolInfoResponse(info: SubsysResponseInfo?, data: ByteArray?) {}
    override fun getAtomDataResponse(info: SubsysResponseInfo?, data: ByteArray?) {}
    override fun loadAtomApplicationResponse(info: SubsysResponseInfo?) {}
    override fun unloadAtomApplicationResponse(info: SubsysResponseInfo?) {}
    override fun getAllCellularCellInfoResponse(info: SubsysResponseInfo?, servingCellInfo: CellInfos?, neighborCellInfos: NeighborCellInfos?) {}
    override fun setImsRtpInfoResponse(info: SubsysResponseInfo?) {}
    override fun setImsRtpThresholdResponse(info: SubsysResponseInfo?) {}
    override fun setRedirectOptSettingResponse(info: SubsysResponseInfo?) {}
    override fun setAttachPriorityResponse(info: SubsysResponseInfo?) {}
    override fun setPingpongBarSettingResponse(info: SubsysResponseInfo?) {}
    override fun performRetHighRatResponse(info: SubsysResponseInfo?) {}
    override fun getIndicationReportRecordsResponse(info: SubsysResponseInfo?, data: Array<IndReportRecord>?) {}
    override fun setCiwlanUiSwitchResponse(info: SubsysResponseInfo?) {}
    override fun getATCIStatusResponse(info: SubsysResponseInfo?, enable: Boolean) {}
    override fun enableATCIPortResponse(info: SubsysResponseInfo?) {}
    override fun setVonrRollbackCfgResponse(info: SubsysResponseInfo?) {}
    override fun satelliteFirmwareDownLoadResponse(info: SubsysResponseInfo?) {}
    override fun setEsimStatusResponse(info: SubsysResponseInfo?) {
        Log.d("OplusEsimController", "setEsimStatusResponse: error=${info?.error}")
    }
    override fun setCarrierLockBlobResponse(info: SubsysResponseInfo?) {}
    override fun setCarrierLockStatusResponse(info: SubsysResponseInfo?) {}
    override fun getCarrierLockStatusResponse(info: SubsysResponseInfo?, data: ByteArray?) {}
    override fun setRegionLockBlobResponse(info: SubsysResponseInfo?, data: ByteArray?) {}
    override fun setRegionLockStatusResponse(info: SubsysResponseInfo?, data: ByteArray?) {}
    override fun getRegionLockStatusResponse(info: SubsysResponseInfo?, data: ByteArray?) {}
    override fun writeEncryptedSerialIdResponse(info: SubsysResponseInfo?) {}
    override fun readRegionLockDataResponse(info: SubsysResponseInfo?, data: ByteArray?) {}
    override fun writeCarrierLockWhitelistResponse(info: SubsysResponseInfo?) {}
    override fun readCarrierLockWhitelistResponse(info: SubsysResponseInfo?, data: ByteArray?) {}
    override fun setAtomStatusResponse(info: SubsysResponseInfo?) {}
    override fun setImsAudioQualityThresholdResponse(info: SubsysResponseInfo?) {}
    override fun setPagingErrorCfgResponse(info: SubsysResponseInfo?) {}
    override fun setAdaptiveHandoverThresholdResponse(info: SubsysResponseInfo?) {}
    override fun setIndicationConfigResponse(info: SubsysResponseInfo?) {}
    override fun setEccListResponse(info: SubsysResponseInfo?) {}
    override fun getQrxlvminCfgResponse(info: SubsysResponseInfo?, qrxlvminCfg: QrxlvminRspInfo?) {}
    override fun setSmartIdleCfgResponse(info: SubsysResponseInfo?) {}
    override fun getPowerStatisticsResponse(info: SubsysResponseInfo?, powerStatistics: PowerStatisticsConfigs?) {}
    override fun sendScoreInfoResponse(info: SubsysResponseInfo?) {}
    override fun sendNetworkActionResponse(info: SubsysResponseInfo?) {}
    override fun rollbackNetworkActionResponse(info: SubsysResponseInfo?) {}
    override fun getNetworkActionStateResponse(info: SubsysResponseInfo?, clientId: Int, actionId: Int, actionState: Int) {}
    override fun getSceneModeResponse(info: SubsysResponseInfo?, scenes: Array<SceneMode>?) {}
    override fun hplmnBgSearchResponse(info: SubsysResponseInfo?) {}
    override fun getImsRtpRedunCapabilityResponse(info: SubsysResponseInfo?, capability: Int) {}
    override fun syncImsRtpRedunDataPathConfigResponse(info: SubsysResponseInfo?, result: Int) {}
    override fun exchangeImsRtpRedunPublicKeyResponse(info: SubsysResponseInfo?, keyRsp: ImsRtpRedunKeyRspInfo?) {}
    override fun setImsRtpRedunControlInfoResponse(info: SubsysResponseInfo?, controlRsp: ImsRtpRedunControlRspInfo?) {}
    override fun dumpDiagMdLogBufferResponse(info: SubsysResponseInfo?) {}
    override fun setMdLogBufferSizeResponse(info: SubsysResponseInfo?) {}
    override fun setTxPathFilterResponse(info: SubsysResponseInfo?) {}
    override fun setAtcTableDeInitResponse(info: SubsysResponseInfo?) {}
    override fun getModemFittingInfoResponse(info: SubsysResponseInfo?, mdInfo: ModemFittingInfo?) {}
    override fun getSatelliteImeiResponse(info: SubsysResponseInfo?, imei3: String?) {}
    override fun setRxBoostStateResponse(info: SubsysResponseInfo?) {}
    override fun getRxBoostStateResponse(info: SubsysResponseInfo?, stateInfo: RxBoostStateInfo?) {}
    override fun getSatelliteCalibrationDataStateResponse(info: SubsysResponseInfo?, state: SatelliteCalDataStatusType?) {}
    override fun setWifiQosInfoResponse(info: SubsysResponseInfo?) {}
    override fun getWifiQosInfoResponse(info: SubsysResponseInfo?, wifiQosRspInfo: Array<ModemWifiQosInfo>?) {}
    override fun registerFenceClientResponse(info: SubsysResponseInfo?, clientId: Int) {}
    override fun setFenceRecognizeStateResponse(info: SubsysResponseInfo?) {}
    override fun getFenceStateResponse(info: SubsysResponseInfo?, fenceStateInfo: Array<FenceStateInfo>?, state: Boolean) {}
    override fun triggerFenceLearningResponse(info: SubsysResponseInfo?, fenceId: Int) {}
    override fun sendWifiConnectInfoResponse(info: SubsysResponseInfo?) {}
    override fun sendFenceInfoResponse(info: SubsysResponseInfo?) {}
    override fun deregisterFenceClientResponse(info: SubsysResponseInfo?) {}
    override fun setCallScoreInfoResponse(info: SubsysResponseInfo?) {}
    override fun getCallScoreInfoResponse(info: SubsysResponseInfo?, score: CallCellScore?) {}
    override fun getInterfaceVersion(): Int = ISubsysRadioResponse.VERSION
    override fun getInterfaceHash(): String = ISubsysRadioResponse.HASH
}

private class EsimSubsysRadioIndication : ISubsysRadioIndication.Stub() {
    override fun radioCyberSenseCellInfoInd(type: Int, info: Array<CyberSenseHALCellInfo>?) {}
    override fun radioCyberSenseEventInd(type: Int, value: Int) {}
    override fun radioDtmfStartInd(type: Int, tone_type: Byte) {}
    override fun radioDtmfStopInd(type: Int, tone_type: Byte) {}
    override fun radioImsMessageInd(type: Int, imsMessage: ImsMessage?) {}
    override fun radioImsRtpStateInd(type: Int, rtpState: ImsRtpState?) {}
    override fun radioLteCaInfoInd(type: Int, ca_info: IntArray?) {}
    override fun radioLteRegDomainInd(type: Int, value: Int) {}
    override fun radioNecInd(type: Int, data: ByteArray?) {}
    override fun radioNonddsPagingInd() {}
    override fun radioNr5gDrxInd(type: Int, info: Nr5gDrxType?) {}
    override fun radioNr5gFreqTypeIndication(type: Int, nr5gFreqType: Int) {}
    override fun radioSimlockInfoChangedInd(type: Int, info: SimlockInfoType?) {}
    override fun radioSimlockStateChangedInd(type: Int, state: SimlockStateType?) {}
    override fun radioStateChanged(type: Int, state: Int) {}
    override fun radioSidoIssueInd(type: Int, params: IntArray?) {}
    override fun radioNrCapInd(type: Int, nr_cap: Byte) {}
    override fun radioNonddsNullPagingInd(type: Int, np_type: Byte, rat: Int, errcode: Int) {}
    override fun radioCssnfNumInd(type: Int, data: ByteArray?) {}
    override fun radioMipiOscFreqHopStateInd(type: Int, value: Int) {}
    override fun radioSimOverdueInd(type: Int, state: SimOverdueIndType?) {}
    override fun radioNwRateLimitingInd(type: Int, limitInfo: NwRateLimitingInfo?) {}
    override fun radioMdmTestInd(type: Int, data: Int) {}
    override fun radioMsimSubModeInd(type: Int, msimSubMode: Int, dsdaTxMode: Int) {}
    override fun radioQosPeriodReportInd(type: Int, data: ByteArray?) {}
    override fun radioQosEventReportInd(type: Int, data: ByteArray?) {}
    override fun radioLteCellInfoInd(type: Int, pci: Int, arfcn: Int, sib24_available: Boolean) {}
    override fun radioMccChangeInd(type: Int, roamInfo: MccChangeIndInfo?) {}
    override fun radioHstModeInd(type: Int, hstMode: Int) {}
    override fun radioServingCellInfoInd(type: Int, info: CellInfos?) {}
    override fun radioHyperUplinkStateInd(type: Int, sceneState: Int, hyperUplinkState: Int, hyperUplinkType: Int) {}
    override fun radioAtomDataInd(type: Int, data: ByteArray?) {}
    override fun radioLinkLatencyInfoInd(type: Int, ulLatency: Int) {}
    override fun radioImsRtpControlInd(type: Int, info: ImsRtpControlInfo?) {}
    override fun radioNasAccessBarringStatusInd(type: Int, nasAccessBarringStatusinfo: NasAccessBarringStatusInfo?) {}
    override fun radioNrcaInfoChangeInd(type: Int, nrcaInfo: NrcaInfo?) {}
    override fun radioVonrBackoffInfoChangeInd(type: Int, backoffInfo: VonrBackoffInfo?) {}
    override fun radioVonrRollbackInfoChangeInd(type: Int, rollbackInfo: VonrRollbackInfo?) {}
    override fun radioSingleSimChangeInd(type: Int, status: Byte) {}
    override fun radioCarrierLockStatusChangeInd(type: Int, data: ByteArray?) {}
    override fun radioRegionLockStatusChangeInd(type: Int, data: ByteArray?) {}
    override fun radioAlertEventInd(type: Int, alertId: Int, alertCause: Int, alertParam: IntArray?) {}
    override fun radioVodataEventInd(type: Int, vodataEventInfo: VodataEventInfo?) {}
    override fun radioAllCellInfoInd(type: Int, servingCellInfo: Array<CellInfos>?, neighborCellInfos: Array<NeighborCellInfos>?) {}
    override fun radioNetworkActionResultInd(type: Int, result: Array<ActionResult>?) {}
    override fun radioSceneModeInd(type: Int, scenes: Array<SceneMode>?) {}
    override fun radioSmartNetworkSelectInd(type: Int, smartNetworkSelectInfo: SmartNetworkSelectInfo?) {}
    override fun radioImsRtpRedunEventInd(type: Int, event: ImsRtpRedunEventInfo?) {}
    override fun rxBoostStateInd(type: Int, state: RxBoostStateInfo?) {}
    override fun radioRrcStatusChangeInd(type: Int, rrcInfo: RrcStatusIndInfo?) {}
    override fun radioFenceStateChangeInd(type: Int, fenceStateInfos: Array<FenceStateInfo>?) {}
    override fun radioFenceRemovedInd(type: Int, fenceRemovedInfos: Array<FenceRemovedInfo>?) {}
    override fun radioFenceMergedInd(type: Int, fenceMergedInfos: Array<FenceMergedInfo>?) {}
    override fun radioFenceCreateInd(type: Int, fenceCreatedInfo: FenceCreatedInfo?) {}
    override fun getInterfaceVersion(): Int = ISubsysRadioIndication.VERSION
    override fun getInterfaceHash(): String = ISubsysRadioIndication.HASH
}

class EsimController(private val context: Context) {
    private val hasSN220Chipset = SystemProperties.get(NFC_CONFIG_FILE_NAME_PROP).contains("SN220")

    private val oplusEsimService by lazy {
        IOplusEsim.Stub.asInterface(ServiceManager.getService("${IOplusEsim.DESCRIPTOR}/default"))
    }

    private val telephonyManager by lazy { context.getSystemService(TelephonyManager::class.java) }

    private val subsysRadioSerial = AtomicInteger(1)

    private val subsysRadio by lazy {
        ISubsysRadio.Stub.asInterface(
                ServiceManager.getService("${ISubsysRadio.DESCRIPTOR}/slot1")
            )
            ?.also {
                it.setCallback(EsimSubsysRadioResponse(), EsimSubsysRadioIndication())
            }
    }

    private fun notifyModemEsimStatus(state: Int) {
        try {
            subsysRadio?.setEsimStatus(subsysRadioSerial.getAndIncrement(), state)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to notify modem of eSIM status", e)
        }
    }

    private fun notifySimType(state: Int) {
        val newSimType =
            if (state != 0) {
                TelephonyManager.SIM_TYPE_EMBEDDED
            } else {
                TelephonyManager.SIM_TYPE_PHYSICAL
            }

        try {
            val currentMapping = telephonyManager?.simSlotMapping ?: return
            val newMapping =
                currentMapping.map {
                    if (it.physicalSlotIndex == ESIM_PHYSICAL_SLOT_INDEX) {
                        UiccSlotMapping(it.portIndex, it.physicalSlotIndex, it.logicalSlotIndex, newSimType)
                    } else {
                        it
                    }
                }

            telephonyManager?.setSimSlotMapping(newMapping)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to notify RIL of SIM type change", e)
        }
    }

    fun currentGpioState() = oplusEsimService?.esimGpio ?: 0

    fun toggleEsimState(state: Int) {
        val gpioState = currentGpioState()
        Log.d(TAG, "Current eSIM status = $gpioState")

        if (state == gpioState) {
            Log.d(TAG, "No need to change eSIM state")
            return
        }

        val newState = if (gpioState == 0) 1 else 0

        oplusEsimService?.setUimPower(0)

        if (hasSN220Chipset) {
            specialSetEsimGpio(newState)
            /* modem notify + setEsimGpio + setUimPower(1) done via SEService.OnConnectedListener */
        } else {
            notifyModemEsimStatus(newState)
            oplusEsimService?.setEsimGpio(newState)
            oplusEsimService?.setUimPower(1)
            notifySimType(newState)
        }
    }

    private fun specialSetEsimGpio(state: Int) {
        var seService: SEService? = null

        val listener =
            object : SEService.OnConnectedListener {
                override fun onConnected() {
                    Log.d(TAG, "SEService connected")

                    val service = seService ?: return

                    try {
                        val reader = service.readers.firstOrNull { it.name == "eSE1" }
                        val session = reader?.openSession()
                        val channel = session?.openLogicalChannel(null)

                        notifyModemEsimStatus(state)
                        oplusEsimService?.setEsimGpio(state)
                        oplusEsimService?.setUimPower(1)
                        notifySimType(state)

                        channel?.close()
                        session?.close()
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to open eSE session", e)
                    } finally {
                        try {
                            service.shutdown()
                        } catch (e: Exception) {
                            Log.e(TAG, "Failed to shutdown SEService", e)
                        }
                    }
                }
            }

        try {
            seService = SEService(context, Dispatchers.IO.asExecutor(), listener)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start SEService", e)
        }
    }

    companion object {
        private const val TAG = "OplusEsimController"

        private const val NFC_CONFIG_FILE_NAME_PROP = "persist.vendor.nfc.config_file_name"

        private const val ESIM_PHYSICAL_SLOT_INDEX = 1
    }
}
