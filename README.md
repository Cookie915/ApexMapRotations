# ApexMapRotations
<p align="center">
 <img width="250" height="250" src="https://user-images.githubusercontent.com/49169067/174446678-9b697331-e67d-4433-af9f-29af2dc602c8.png">
</p>

## ▶️ About 
Apex Map Rotations is a free to use, unmonotized android app that uses an unoffical Apex Legends API to bring you updates on the current maps in rotation
for battle royal and arenas. You can set alarms or notifications to be alerted when app changes happen.
I made this app to get some experience using Views + XML for layout as opposed to Jetpack Compose, and to brush up on my skills with Adobe Illustrator,
Photoshop and other graphic utilities. All icons and animations are made in house. If you have an issues or feature requests send me and 
email at calebcookdev@gmail.com or leave it on the repository, I'd love to hear it! 😁

## 🏗️ Libraries/Architecture 
Apex Map Rotations is build using MVVM, with a single activity swapping out fragments. It features a repository layer that uses Retrofit
to make request to the server. Its set up for dependency injection with hilt for the viewmodels, repository, and retrofit instance.
The viewmodels are bound to each fragment and the repository layer is a singleton that holds a private MutableStateFlow. The flow is converted to a public stateflow that is collect in each viewmodel. Althought its not best to keep a hot flow alive in the repository layer, I opted out of using a normal flow
to limit calls to the server and prevent hitting rate limits for GET requests, causing the flow to potentially return an error. This also maintains a single source of truth for map data. The app uses Glide for image loading and caching. The animations and icons are build in Adobe Illustrator, animated in after effects, and exoprted as lottieanimations. Jetpack Datastore is used to persist local data. The app takes advantage of kotlin KTX and Coroutines. It also implements the new Splash Screen Api for android 12

## ▶️ Downloads 
Currently working on play store release! 

## ✅ Attributions  
Thank you https://apexlegendsapi.com/#introduction for a clean easy to use API with good JSON responses. 






