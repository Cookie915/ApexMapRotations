<h1 align="center">Apex Map Rotations</h1>
 <p align="center">
  <img width="250" height="250" src="https://user-images.githubusercontent.com/49169067/174446678-9b697331-e67d-4433-af9f-29af2dc602c8.png">
 </p>

#### Table Of Contents
- [ℹ️ About](#ℹ%EF%B8%8F-about)
- [🏗️ Libraries/Arch](#%EF%B8%8F-librariesarchitecture)
- [▶️ Downloads](#%EF%B8%8F-downloads)
- [✅ Atrributions](#-attributions)
- [📷 Screenshots](#-screenshots)
## ℹ️ About
<p>
 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Apex Alarm is a free to use, unmonotized android app that uses an unoffical Apex Legends API to bring you updates on the current maps for battle royal and arenas. You can set alarms or notifications to be alerted when map changes happen.
I made this app to get some experience using Views + XML for layout as opposed to Jetpack Compose, and to brush up on my skills with Adobe Illustrator,
Photoshop and other graphic utilities. The app should support screen sizes from small 4 inch phones all the way to 10 inch tablets, landscape or portrait.  All icons and animations are made in house. If you have an issues or feature requests send me and 
email at calebcookdev@gmail.com or leave it on the repository, I'd love to hear it! 😁
 </P>

## 🏗️ Libraries/Architecture 
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Apex Map Rotations is build using MVVM, with a single activity swapping out fragments. It features a repository layer that uses Firebase to make request to the server. Its set up for dependency injection with hilt for the viewmodels, repository, and sharedPreferences instances.
The viewmodels are bound to the activity and the repository is a singleton that holds a SharedFlow which collects data from firestore. The flow is converted to a public stateflow that is collected in the viewmodels. The app uses Glide for image loading and caching. The animations and icons are build in Adobe Illustrator, animated in after effects, and exoprted as lottieanimations. SharedPreferences is used to persist local data. The app takes advantage of kotlin KTX and Coroutines. It also implements the new Splash Screen Api for android 12. On the cloud we have a Firestore database holding our mapdata. I have a cloud function that runs a small JS file to fetch the map data from the API. It takes the data and populates our firestore databases with only the stuff we need for the app. The function then schedules a task to run 1 second after nearest map change. The this task then reruns the original function. This keeps the data fresh, while minimizing firebase calls and avoiding rate limits on the Apex API. 

## ▶️ Downloads 
- Play store release pending review! Will update as soon as it becomes available!
- The master branch of this repository has the latest stable version of the app.
- The development branch may be unstable and is updated as I develope new features!

## ✅ Attributions  
- Thank you https://apexlegendsapi.com/#introduction for a clean easy to use API with good JSON responses.
- Thank you https://www.reddit.com/user/kurdan/ for the Error and Loading screen images 
- Thank you @Jamalater1269 for the beatiful background images
- This app is not affiliated with or sponsored by Electronic Arts Inc. or its licensors.

## 📷 Screenshots
<P align="center">
 <img width="25%" height="25%" src = https://user-images.githubusercontent.com/49169067/179357031-d5e3cd46-9949-4adb-9ada-bb768b26cc03.png>
 <img width="25%" height="25%" src = https://user-images.githubusercontent.com/49169067/179357051-eb39b1d8-94f5-4e89-a103-65e6f66416d9.png>
 <img width="25%" height="25%" src = https://user-images.githubusercontent.com/49169067/179357062-578f74d0-6fff-4aa2-b0b9-85a33aa986b3.png>
</P>
<P align="center">
 <img width="25%" height="25%" src = https://user-images.githubusercontent.com/49169067/179357088-1753bf69-d9c7-4e98-ac89-909585c74f49.png>
 <img width="25%" height="25%" src = https://user-images.githubusercontent.com/49169067/179357105-e3aa6204-7b1c-48cf-a099-588c96b51102.png>
 <img width="25%" height="25%" src = https://user-images.githubusercontent.com/49169067/179357118-530303c8-a88b-4fab-8311-76c7a3a60869.png>
</P>

<video src=https://user-images.githubusercontent.com/49169067/179357508-60afe5ec-3888-4dd3-a572-dd58bbaa6975.mp4>
