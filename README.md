
# Mapfit SDK for Android
<img  src="/assets/Android-banner.png"/>
<a href='https://bintray.com/mapfit/maven/android-sdk/_latestVersion'><img src='https://api.bintray.com/packages/mapfit/maven/android-sdk/images/download.svg'></a>

## Get your Mapfit API key
To use Mapfit Maps and services, your application will need an API key. You can get your API key for free [here](https://mapfit.com/getstarted).

## Install the Mapfit Android SDK
Make sure `JCenter` is included in your repositories scope inside project level `build.gradle` file as follows
```
allprojects {
    repositories {
        jcenter()
    }
}
```

In your module level `build.gradle` file, add depencency as follows.
```
dependencies {
    	implementation 'com.mapfit:android-sdk:2.0.1'
}
```

## Adding a map to your view
Inside your layout file, add `MapView` as follows

```xml
<com.mapfit.android.MapView
	        android:id="@+id/mapView"
	        android:layout_width="match_parent"
	        android:layout_height="match_parent" />
```

## Initializing Mapfit
Before using `MapView` or any other Mapfit services, you need to instantiate a `Mapfit` instance as follows

#### Kotlin
```kotlin
Mapfit.getInstance(context, YOUR_API_KEY) 

``` 

#### Java
```java
Mapfit.getInstance(context, YOUR_API_KEY);
```

## Setup MapView and add your first Marker
To show the map and interact with it, you must setup the `MapView` as follows

#### Kotlin
```kotlin
mapView.getMapAsync(onMapReadyCallback = object : OnMapReadyCallback {
    override fun onMapReady(mapfitMap: MapfitMap) {
    
        val position = LatLng(40.744023, -73.993150)
        val marker= mapfitMap.addMarker(position)
        
    }
})
```

#### Java
```java
mapView.getMapAsync(new OnMapReadyCallback() {
  	@Override
  	public void onMapReady(MapfitMap mapfitMap) {
    
      	LatLng position = new LatLng(40.744023, -73.993150);
      	Marker marker = mapfitMap.addMarker(position);
        
    }
});
```

If you are looking for detailed documentation, see [Mapfit Documentation](https://mapfit-android.readme.io/docs).


## License
Copyright (c) 2018 Mapfit, Inc.
All Rights Reserved.

