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
 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Apex Map Rotations is a free to use, unmonotized android app that uses an unoffical Apex Legends API to bring you updates on the current maps for battle royal and arenas. You can set alarms or notifications to be alerted when map changes happen.
I made this app to get some experience using Views + XML for layout as opposed to Jetpack Compose, and to brush up on my skills with Adobe Illustrator,
Photoshop and other graphic utilities. All icons and animations are made in house. If you have an issues or feature requests send me and 
email at calebcookdev@gmail.com or leave it on the repository, I'd love to hear it! 😁
 </P>

## 🏗️ Libraries/Architecture 
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Apex Map Rotations is build using MVVM, with a single activity swapping out fragments. It features a repository layer that uses Retrofit to make request to the server. Its set up for dependency injection with hilt for the viewmodels, repository, and retrofit instance.
The viewmodels are bound to each fragment and the repository layer is a singleton that holds a SharedFlow which collects data from the api. The flow is converted to a public stateflow that is collect in each viewmodel. This also maintains a single source of truth for map data. The app uses Glide for image loading and caching. The animations and icons are build in Adobe Illustrator, animated in after effects, and exoprted as lottieanimations. Jetpack Datastore is used to persist local data. The app takes advantage of kotlin KTX and Coroutines. It also implements the new Splash Screen Api for android 12

## ▶️ Downloads 
- Go to Code -> Download ZIP
- Extract to ApexMapRotations-master
- Open Android Studio
- File -> Open -> ApexMapRotations-master
- Run from Android Studio on emulator or device

## ✅ Attributions  
- Thank you https://apexlegendsapi.com/#introduction for a clean easy to use API with good JSON responses.
- Thank you https://www.reddit.com/user/kurdan/ for the Error and Loading screen images 
- This app is not affiliated with or sponsored by Electronic Arts Inc. or its licensors.

## 📷 Screenshots
<P align="center">
 <img width="25%" height="25%" src = https://user-images.githubusercontent.com/49169067/174608266-c11e7b04-369c-4be4-86eb-112e9d462c97.png>
 <img width="25%" height="25%" src = https://user-images.githubusercontent.com/49169067/174608247-0d939ffc-6002-466b-963d-630fd4356ed1.png>
 <img width="25%" height="25%" src = https://user-images.githubusercontent.com/49169067/174608286-bc21f98b-9875-4d53-ab3c-9588ab0ec4a0.png>
</P>
<video src=https://user-images.githubusercontent.com/49169067/174610059-53ca9a5d-68de-4c9c-ae27-331a4def176c.mp4>
 



