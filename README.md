# Foursquare Search (MVP - Model-View-Presenter)


## Running the project

Before running the app, make sure to add following values to the `local.properties` file:

```
api.client=CLIENT
api.secret=SECRET
# version can be changed if needed
api.version=20180323
```

## Technologies:

* `Dagger Hilt` for Dependency Injection and easy to use

* `MvpPresenter`: I used `ViewModel` as a base of `MvpPresenter` as it's provided by the platform and because of the `Dagger Hilt` support. I implemented `MvpPresenter` by extending `ViewModel`. I tried not to over-engineer the solution, that's why I think solution is maintainable with current requirements and architecture. `MvpPresenter`. I wrote unit tests for the complex part of `SearchPresenter`. Presenter directly uses injected `LocationRepository` and `SearchVenuesUseCase`. I wrote some unit tests for `SearchVenuesUseCase` since it has page related calculation.

* `RxJava 2`: To use publish-subscribe pattern and make threading easy, I used `RxJava 2`. Mainly used `Observable` and `Single`. Besides those, I used `RxBinding` and `RxRelay2`.

* `Retrofit` and `Moshi`: used for fetching data from API and deserializing JSON. At work I use GSON as well but I have read often that `Moshi` is faster than `GSON`, that's why, I used it. I also wrote test for `FoursquareRequestInterceptorTest` which adds client id, secret and API version to all requests by default.

* Utilized `DiffUtil` to prevent unnecessary updates in `RecyclerView`

* `Paging 3`: it was first time I used new `Paging 3` library. I have used previously `Paging 2` but compared to that one, it was simpler to implement pagination using `Paging 3`.


## Some future to do:

* Unfortunately, I didn't have time to polish UI. UI definitely needs some polishing, extracting styles to make it more reusable. Also, it'd be nice to implement more explaining screen for the location permission.

* Implement offline-first approach

* Dark theme implementation

* UI tests for activity to make sure correct states are displayed 

* Change logo :D
  
* ...