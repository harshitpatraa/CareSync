const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");
const { serverTimestamp } = require(
  require.resolve("firebase/firestore", {
    paths: [require.resolve("@firebase/rules-unit-testing")],
  })
);

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_clinician_1";
const BOB_UID = "bob_clinician_2";

const [emulatorHost, emulatorPortStr] = (
  process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085"
).split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

function serverTs() {
  return serverTimestamp();
}

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

function validPatientPayload() {
  return {
    fullName: "Asha Devi",
    age: 29,
    gender: "Female",
    phone: "+91 98111 11111",
    villageOrWard: "Ward 4",
    bloodGroup: "B+",
    category: "PREGNANCY",
    riskLevel: "HIGH",
    critical: false,
    todayVisit: true,
    pendingVisit: false,
    lastVisitLabel: "Just now",
    nextFollowUpDate: "20 Nov 2026",
    allergies: "None",
    chronicConditions: "Anemia",
    familyHeadName: "Ramesh",
    familyMembersCount: 4,
    pregnancyWeeks: 28,
    expectedDueDate: "10 Jan 2027",
    childWeightKg: 0,
    vaccinationProgress: 100,
    notes: "Routine ANC visit",
    createdAt: serverTs(),
    updatedAt: serverTs(),
  };
}

test("Unauthenticated user: cannot read or write any user or patient document", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
  await assertFails(
    unauthDb.collection("users").doc(ALICE_UID).collection("patients").get()
  );
  await assertFails(
    unauthDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("patients")
      .doc("PT-1001")
      .set(validPatientPayload())
  );
});

test("Authenticated owner: can create and update their own user profile", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const profileRef = aliceDb.collection("users").doc(ALICE_UID);

  await assertSucceeds(
    profileRef.set({
      userId: ALICE_UID,
      displayName: "Dr. Priya Kulkarni",
      email: "priya@caresync.org",
      clinicName: "CareSync Hub Ward 4",
      workerId: "CS-CHW-409",
      district: "North Circle",
      role: "Senior CHO",
      createdAt: serverTs(),
      updatedAt: serverTs(),
    })
  );

  await assertSucceeds(
    profileRef.update({
      clinicName: "CareSync Hub Ward 5",
      updatedAt: serverTs(),
    })
  );
});

test("Shadow Update & Identity Spoofing: rejects ghost field or mismatched userId", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();

  // Ghost field isAdmin rejected
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      displayName: "Dr. Priya",
      clinicName: "Ward 4",
      workerId: "CS-1",
      isAdmin: true,
      createdAt: serverTs(),
      updatedAt: serverTs(),
    })
  );

  // Mismatched userId rejected
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).set({
      userId: BOB_UID,
      displayName: "Spoof",
      clinicName: "Ward 4",
      workerId: "CS-1",
      createdAt: serverTs(),
      updatedAt: serverTs(),
    })
  );
});

test("Authenticated owner: can CRUD own patients, vitals, health_records, notifications", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const userRef = aliceDb.collection("users").doc(ALICE_UID);

  const patientRef = userRef.collection("patients").doc("PT-1001");
  await assertSucceeds(patientRef.set(validPatientPayload()));
  await assertSucceeds(userRef.collection("patients").get());

  // Toggle critical action update
  await assertSucceeds(
    patientRef.update({
      critical: true,
      riskLevel: "CRITICAL",
      updatedAt: serverTs(),
    })
  );

  // Vitals create
  await assertSucceeds(
    userRef.collection("vitals").doc("VT-1").set({
      patientId: "PT-1001",
      systolicBp: 120,
      diastolicBp: 80,
      heartRate: 78,
      temperatureC: 37.1,
      spo2Percent: 98,
      respiratoryRate: 16,
      bloodSugarMgDl: 98,
      weightKg: 58.5,
      recordedAtLabel: "Just now",
      createdAt: serverTs(),
      updatedAt: serverTs(),
    })
  );

  // Health record create
  await assertSucceeds(
    userRef.collection("health_records").doc("HR-1").set({
      patientId: "PT-1001",
      patientName: "Asha Devi",
      recordType: "TIMELINE",
      title: "ANC Checkup",
      subtitle: "Normal progression",
      detail: "BP 120/80, fetal heart sounds normal",
      dateLabel: "Today",
      statusBadge: "Completed",
      createdAt: serverTs(),
      updatedAt: serverTs(),
    })
  );

  // Notification create & mark read
  const notifRef = userRef.collection("notifications").doc("NT-1");
  await assertSucceeds(
    notifRef.set({
      title: "Follow-up Due",
      message: "Schedule ANC week 30 check.",
      type: "FOLLOW_UP",
      timestampLabel: "Just now",
      read: false,
      patientId: "PT-1001",
      createdAt: serverTs(),
      updatedAt: serverTs(),
    })
  );
  await assertSucceeds(
    notifRef.update({
      read: true,
      updatedAt: serverTs(),
    })
  );
});

test("Cross-user isolation: Bob cannot read, list, create, update, or delete Alice's records", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    const adminUserRef = context.firestore().collection("users").doc(ALICE_UID);
    await adminUserRef.set({
      userId: ALICE_UID,
      displayName: "Dr. Priya",
      clinicName: "Ward 4",
      workerId: "CS-1",
      createdAt: serverTs(),
      updatedAt: serverTs(),
    });
    await adminUserRef.collection("patients").doc("PT-1001").set(validPatientPayload());
  });

  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const aliceUserRef = bobDb.collection("users").doc(ALICE_UID);

  await assertFails(aliceUserRef.get());
  await assertFails(aliceUserRef.collection("patients").get());
  await assertFails(aliceUserRef.collection("patients").doc("PT-1001").get());
  await assertFails(
    aliceUserRef.collection("patients").doc("PT-1002").set(validPatientPayload())
  );
  await assertFails(
    aliceUserRef.collection("patients").doc("PT-1001").update({
      critical: true,
      riskLevel: "CRITICAL",
      updatedAt: serverTs(),
    })
  );
  await assertFails(aliceUserRef.collection("patients").doc("PT-1001").delete());
});

test("Schema & Path validation: rejects invalid enum, negative age, or oversized ID", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const patientsCol = aliceDb.collection("users").doc(ALICE_UID).collection("patients");

  // Invalid category enum
  await assertFails(
    patientsCol.doc("PT-BAD-ENUM").set({
      ...validPatientPayload(),
      category: "INVALID_CATEGORY",
    })
  );

  // Invalid age
  await assertFails(
    patientsCol.doc("PT-BAD-AGE").set({
      ...validPatientPayload(),
      age: -5,
    })
  );

  // Oversized / invalid document ID
  await assertFails(
    patientsCol.doc("x".repeat(140)).set(validPatientPayload())
  );
});
