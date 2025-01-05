const admin = require('firebase-admin');
const fs = require('fs');

// Initialize Firebase Admin SDK
const serviceAccount = require('C:\\Users\\Adam Razali\\Documents\\AndroidStudioProjects\\GreenAura\\GreenAura\\JSON files\\ImportGoals\\green-aura-b6e67-firebase-adminsdk-9otzf-4a56a96fb6.json'); // path to the serviceAccountKey.json file
admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  databaseURL: 'https://green-aura-b6e67.firebaseio.com'
});

// Read your JSON file
const goalsData = JSON.parse(fs.readFileSync('C:\\Users\\Adam Razali\\Documents\\AndroidStudioProjects\\GreenAura\\GreenAura\\JSON files\\ImportGoals\\Goals.json', 'utf8'));

// Reference to Firestore
const db = admin.firestore();

// Import the data
async function importData() {
  const goalsCollection = db.collection('Goals');
  
  for (const goalId in goalsData) {
    const goal = goalsData[goalId];
    await goalsCollection.doc(goalId).set(goal);
    console.log(`Imported goal: ${goalId}`);
  }
}

importData()
  .then(() => console.log('Data imported successfully'))
  .catch((error) => console.error('Error importing data: ', error));
