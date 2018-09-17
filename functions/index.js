const fetch = require('node-fetch');
const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

exports.createDatabase = functions.https.onRequest((request, response) => {
    fetch('https://opentdb.com/api.php?amount=1&type=multiple')
    .then(res => res.json())
    .then((out) => {
        var rez = out.results[0];
        var category;
        switch (rez.category.substring(0,5)) {
            case 'Gener':
                category = 'General Knowledge'
                break;
            case 'Enter':
                category = 'Entertainment'
                break;
            case 'Scien':
                category = 'Science'
                break;
            case 'Sport':
                category = 'Sports'
                break;
            case 'Geogr':
                category = 'Geography'
                break;
            case 'Histo':
                category = 'History'
                break;
            case 'Polit':
                category = 'Politics'
                break;
            case 'Art':
                category = 'Art'
                break;
            case 'Celeb':
                category = 'Celebrities'
                break;
            case 'Anima':
                category = 'Animals'
                break;
            case 'Vehic':
                category = 'Vehicles' 
                break;
            case 'Mytho':
                category = 'Mythology'
                break;
        }
        return admin.database().ref('questions').child(category).push(rez).then((snapshot) => {
            // Redirect with 303 SEE OTHER to the URL of the pushed object in the Firebase console.
            return response.send(rez.category);
          });
    })
    .catch(err => { throw err });
});