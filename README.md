<h1 align="center">Digital Zoo</h1>

<p align="center">
  <a href="https://opensource.org/licenses/MIT"><img alt="License" src="https://img.shields.io/badge/License-MIT-blue.svg"/></a>
  <a href="https://android-arsenal.com/api?level=21"><img alt="API" src="https://img.shields.io/badge/API-21%2B-brightgreen.svg?style=flat"/></a>
</p>

Digital Zoo is a small demo application based on MVVM architecture. Also features fetching data from the network, caching the data in the database via repository pattern and uses [this](https://github.com/k-arabadzhiev/Digital-Zoo-REST-API) repo as a backend service. 

# 🛠️ Tech stack & Open-source libraries
- Minimum SDK level 21
- [Kotlin](https://kotlinlang.org/), [Coroutines](https://github.com/Kotlin/kotlinx.coroutines) and [Flow](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/)
- [Hilt](https://dagger.dev/hilt/) for dependency injection.
- JetPack
  - Lifecycle - dispose of observing data when lifecycle state changes.
  - [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) - UI related data holder, lifecycle aware.
  - [LiveData](https://developer.android.com/topic/libraries/architecture/livedata) - lifecycle-aware observable data holder class. 
  - [Room](https://developer.android.com/jetpack/androidx/releases/room) Persistence - abstraction layer for local SQLite database.
  - [DataStore](https://developer.android.com/topic/libraries/architecture/datastore) - store user settings asynchronously with Kotlin Coroutines and Flow.
- Architecture
  - MVVM (Model - ViewModel - View) Architecture 
  - Repository pattern
- [Retrofit2](https://github.com/square/retrofit) - Type-safe HTTP client used to connect to the backend API. 
- [Moshi](https://github.com/square/moshi/) - JSON parser.
- [Glide](https://github.com/bumptech/glide) - loading images from the network.

# 🎬 Preview 
<p align="center">
<img src="previews/digital%20zoo%20demo.gif"/>
</p>

### 📅 TODO 🐌
- Add unit tests
- Improve logged-in user experience
- Disable collapsing toolbar expansion on details screen, but keep hide on scroll behaviour
- Pre-selected species & diet on update animal screen. 

# 📝 License
```
MIT License

Copyright (c) 2021 Kostadin Arabadzhiev

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.