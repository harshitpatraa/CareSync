import React, { createContext, useContext, useState, useEffect } from 'react';
import { Patient, HealthRecord, ActivityItem, EmergencyContact, NearbyHealthCenter, NearbyClinician, WardOutbreakInsight } from '../types';
import { INITIAL_PATIENTS, INITIAL_RECORDS, INITIAL_ACTIVITIES, EMERGENCY_CONTACTS, NEARBY_CENTERS, NEARBY_CLINICIANS, WARD_SURVEILLANCE } from '../data/mockData';

interface AppContextType {
  patients: Patient[];
  records: HealthRecord[];
  activities: ActivityItem[];
  emergencyContacts: EmergencyContact[];
  nearbyCenters: NearbyHealthCenter[];
  nearbyClinicians: NearbyClinician[];
  wardOutbreak: WardOutbreakInsight;
  isOfflineOnly: boolean;
  setIsOfflineOnly: (val: boolean) => void;
  isDarkMode: boolean;
  setIsDarkMode: (val: boolean) => void;
  language: 'en' | 'hi';
  setLanguage: (lang: 'en' | 'hi') => void;
  addPatient: (patient: Omit<Patient, 'id' | 'updatedAt'>) => Patient;
  updatePatient: (patient: Patient) => void;
  deletePatient: (id: string) => void;
  toggleCriticalStatus: (id: string) => void;
  addHealthRecord: (record: Omit<HealthRecord, 'id'>) => HealthRecord;
  addActivity: (activity: Omit<ActivityItem, 'id' | 'timestamp'>) => void;
  triggerSos: (patientId?: string, reason?: string) => { success: boolean; message: string; notifiedClinicians: NearbyClinician[] };
  syncAllRecords: () => { syncedCount: number; message: string };
}

const AppContext = createContext<AppContextType | null>(null);

export const AppProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [patients, setPatients] = useState<Patient[]>(() => {
    const saved = localStorage.getItem('caresync_patients');
    return saved ? JSON.parse(saved) : INITIAL_PATIENTS;
  });

  const [records, setRecords] = useState<HealthRecord[]>(() => {
    const saved = localStorage.getItem('caresync_records');
    return saved ? JSON.parse(saved) : INITIAL_RECORDS;
  });

  const [activities, setActivities] = useState<ActivityItem[]>(() => {
    const saved = localStorage.getItem('caresync_activities');
    return saved ? JSON.parse(saved) : INITIAL_ACTIVITIES;
  });

  const [isOfflineOnly, setIsOfflineOnly] = useState(false);
  const [isDarkMode, setIsDarkMode] = useState(true);
  const [language, setLanguage] = useState<'en' | 'hi'>('en');

  useEffect(() => {
    localStorage.setItem('caresync_patients', JSON.stringify(patients));
  }, [patients]);

  useEffect(() => {
    localStorage.setItem('caresync_records', JSON.stringify(records));
  }, [records]);

  useEffect(() => {
    localStorage.setItem('caresync_activities', JSON.stringify(activities));
  }, [activities]);

  const addPatient = (pData: Omit<Patient, 'id' | 'updatedAt'>): Patient => {
    const newId = `PT-${1000 + patients.length + 1}`;
    const newPatient: Patient = {
      ...pData,
      id: newId,
      updatedAt: Date.now()
    };
    setPatients(prev => [newPatient, ...prev]);
    addActivity({
      title: `New Patient Registered: ${newPatient.fullName}`,
      description: `Category: ${newPatient.category} · Ward: ${newPatient.villageOrWard}. Saved to local vault.`,
      type: 'visit',
      patientId: newId,
      patientName: newPatient.fullName
    });
    return newPatient;
  };

  const updatePatient = (p: Patient) => {
    setPatients(prev => prev.map(item => item.id === p.id ? { ...p, updatedAt: Date.now() } : item));
  };

  const deletePatient = (id: string) => {
    setPatients(prev => prev.filter(item => item.id !== id));
  };

  const toggleCriticalStatus = (id: string) => {
    setPatients(prev => prev.map(item => {
      if (item.id === id) {
        const nextCrit = !item.isCritical;
        return {
          ...item,
          isCritical: nextCrit,
          riskLevel: nextCrit ? 'CRITICAL' : 'LOW',
          updatedAt: Date.now()
        };
      }
      return item;
    }));
  };

  const addHealthRecord = (rData: Omit<HealthRecord, 'id'>): HealthRecord => {
    const newRecord: HealthRecord = {
      ...rData,
      id: `REC-${2000 + records.length + 1}`
    };
    setRecords(prev => [newRecord, ...prev]);
    addActivity({
      title: `Record Added: ${newRecord.title}`,
      description: `${newRecord.recordType} created for ${newRecord.patientName}`,
      type: 'rx',
      patientId: newRecord.patientId,
      patientName: newRecord.patientName
    });
    return newRecord;
  };

  const addActivity = (act: Omit<ActivityItem, 'id' | 'timestamp'>) => {
    const newAct: ActivityItem = {
      ...act,
      id: `ACT-${Date.now()}`,
      timestamp: 'Just now'
    };
    setActivities(prev => [newAct, ...prev]);
  };

  const triggerSos = (patientId?: string, reason?: string) => {
    const patient = patientId ? patients.find(p => p.id === patientId) : undefined;
    const targetName = patient ? patient.fullName : 'Community Emergency Walk-in';
    const activeClinicians = NEARBY_CLINICIANS.filter(c => c.isAvailable);

    addActivity({
      title: `🚨 CRITICAL SOS DISPATCHED: ${targetName}`,
      description: `Reason: ${reason || 'Cardiac/Vitals Warning'}. Auto-alert sent to ${activeClinicians.length} nearest clinician(s) via 108 network.`,
      type: 'sos',
      patientId: patientId,
      patientName: targetName
    });

    if (patient) {
      updatePatient({
        ...patient,
        isCritical: true,
        riskLevel: 'CRITICAL',
        syncStatus: 'PENDING_UPLOAD'
      });
    }

    return {
      success: true,
      message: `Emergency SOS broadcasted! Alert dispatched to 108 Emergency Ambulance & ${activeClinicians[0]?.name || 'Dr. Arvind Mehta'} (ETA ${activeClinicians[0]?.etaMinutes || 4} mins).`,
      notifiedClinicians: activeClinicians
    };
  };

  const syncAllRecords = () => {
    const pendingCount = patients.filter(p => p.syncStatus === 'PENDING_UPLOAD').length;
    setPatients(prev => prev.map(p => ({ ...p, syncStatus: 'SYNCED' })));
    addActivity({
      title: 'Cloud Auto-Sync Completed',
      description: `All ${pendingCount || patients.length} local records synced with CareSync Cloud Firestore.`,
      type: 'sync'
    });
    return {
      syncedCount: pendingCount || patients.length,
      message: `Successfully synchronized ${pendingCount || patients.length} records to CareSync Cloud!`
    };
  };

  return (
    <AppContext.Provider
      value={{
        patients,
        records,
        activities,
        emergencyContacts: EMERGENCY_CONTACTS,
        nearbyCenters: NEARBY_CENTERS,
        nearbyClinicians: NEARBY_CLINICIANS,
        wardOutbreak: WARD_SURVEILLANCE,
        isOfflineOnly,
        setIsOfflineOnly,
        isDarkMode,
        setIsDarkMode,
        language,
        setLanguage,
        addPatient,
        updatePatient,
        deletePatient,
        toggleCriticalStatus,
        addHealthRecord,
        addActivity,
        triggerSos,
        syncAllRecords
      }}
    >
      {children}
    </AppContext.Provider>
  );
};

export const useApp = () => {
  const context = useContext(AppContext);
  if (!context) throw new Error('useApp must be used within AppProvider');
  return context;
};
